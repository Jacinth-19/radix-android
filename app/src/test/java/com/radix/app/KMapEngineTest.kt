package com.radix.app

import java.util.Random
import org.junit.Assert.*
import org.junit.Test

class KMapEngineTest {
    private data class Rule(val mask: Int, val bits: Int)
    /** Parse the visible expression, independently of the solver's cubes. */
    private fun rules(expression: String, n: Int, form: KMapEngine.Form): List<Rule> {
        val clauses = if (form == KMapEngine.Form.SOP) expression.split('+') else Regex("\\(([^()]*)\\)").findAll(expression).map { it.groupValues[1] }.toList()
        return clauses.map { clause ->
            var mask=0; var bits=0
            val literals=Regex("[A-F]'?").findAll(clause).map { it.value }.toList()
            assertEquals(clause.replace(" ", "").replace("+", ""), literals.joinToString(""))
            literals.forEach { literal ->
                val variable="ABCDEF".indexOf(literal[0]); assertTrue(variable in 0 until n)
                val bit=1 shl (n-1-variable); mask=mask or bit
                val complemented=literal.endsWith("'")
                val fixedOne=if(form==KMapEngine.Form.SOP) !complemented else complemented
                if(fixedOne) bits=bits or bit
            }
            Rule(mask,bits)
        }
    }
    /** Independent oracle: enumerate toroidal Gray-code rectangles, then 0/1 set-cover DP. */
    private fun referenceCost(n: Int, cells: List<Int>, form: KMapEngine.Form): Pair<Int,Int> {
        val target=if(form==KMapEngine.Form.SOP) 1 else 0
        var required=0; cells.indices.filter { cells[it]==target }.forEach { required=required or (1 shl it) }
        if(required==0) return 0 to 0
        val rows=1 shl (n/2); val cols=1 shl (n-n/2)
        val rectangles=mutableMapOf<Int,Int>()
        var h=1
        while(h<=rows) {
            var w=1
            while(w<=cols) {
                for(r in 0 until rows) for(c in 0 until cols) {
                    var all=0
                    for(dr in 0 until h) for(dc in 0 until w) {
                        val rr=(r+dr)%rows; val cc=(c+dc)%cols
                        val index=((rr xor (rr shr 1)) shl (n-n/2)) or (cc xor (cc shr 1))
                        all=all or (1 shl index)
                    }
                    if(cells.indices.none { all and (1 shl it)!=0 && cells[it]==1-target }) {
                        val cover=all and required
                        if(cover!=0) {
                            val literals=n-Integer.numberOfTrailingZeros(h)-Integer.numberOfTrailingZeros(w)
                            rectangles[cover]=minOf(rectangles[cover]?:Int.MAX_VALUE,literals)
                        }
                    }
                }
                w*=2
            }
            h*=2
        }
        val dp=mutableMapOf(0 to (0 to 0))
        rectangles.forEach { (cover,literals) ->
            dp.toList().forEach { (old,cost) ->
                val union=old or cover; val next=cost.first+1 to cost.second+literals; val prior=dp[union]
                if(prior==null || next.first<prior.first || next.first==prior.first && next.second<prior.second) dp[union]=next
            }
        }
        return dp.getValue(required)
    }
    private fun verify(n: Int, cells: List<Int>, minimum: Boolean = false) {
        for(form in KMapEngine.Form.entries) {
            val solution=KMapEngine.solve(n,cells,form)
            val parsed=if(solution.expression in listOf("0","1")) emptyList() else rules(solution.expression,n,form)
            for(index in cells.indices) {
                val visible=when(solution.expression) {
                    "0" -> false; "1" -> true
                    else -> parsed.any { index and it.mask == it.bits }.let { if(form==KMapEngine.Form.SOP) it else !it }
                }
                assertEquals("Cube/expression agreement n=$n form=$form index=$index",solution.evaluate(index),visible)
                if(cells[index]!=2) assertEquals("Truth table n=$n form=$form index=$index",cells[index]==1,visible)
            }
            solution.groups.forEach { group ->
                val members=group.cube.indices(1 shl n)
                assertTrue(members.isNotEmpty() && members.size and (members.size-1)==0)
                assertEquals(1 shl (n-group.cube.literals),members.size)
                assertTrue(members.all { cells[it] == 2 || cells[it] == if(form==KMapEngine.Form.SOP) 1 else 0 })
                assertTrue(members.any { cells[it] == if(form==KMapEngine.Form.SOP) 1 else 0 })
            }
            if(minimum) assertEquals("Independent rectangle optimum n=$n ${cells.joinToString("")}, $form",referenceCost(n,cells,form),solution.groups.size to solution.literalCount)
        }
    }
    @Test fun exhaustiveTwoVariableTernaryMaps() {
        for(code in 0 until 81) {var value=code; val cells=List(4) {(value%3).also {value/=3}};verify(2,cells,true)}
        println("2-variable ternary truth tables verified: 81, SOP and POS, with independent minimum-cover oracle.")
    }
    @Test fun exhaustiveThreeVariableTernaryMaps() {
        for(code in 0 until 6561) {var value=code; val cells=List(8) {(value%3).also {value/=3}};verify(3,cells,true)}
        println("3-variable ternary truth tables verified: 6561, SOP and POS, with independent minimum-cover oracle.")
    }
    @Test fun exhaustiveFourVariableBooleanMaps() {
        for(code in 0 until 65536) verify(4,List(16) { (code shr it) and 1 })
        println("4-variable Boolean truth tables verified: 65536, SOP and POS, via an independent visible-expression parser.")
    }
    @Test fun seededFourVariableDontCareMaps() {
        val random=Random(20260929)
        repeat(2000) { verify(4,List(16) {random.nextInt(3)},minimum=it<200) }
        println("Additional seeded 4-variable ternary tables verified: 2000; first 200 checked against the independent optimum oracle.")
    }
    @Test fun sixVariableMapsCoverAll64CellsAndParseVisibleExpressions() {
        val random=Random(606L)
        repeat(24) {
            val mask=random.nextInt(1 shl 6);val bits=random.nextInt(1 shl 6) and mask
            val cells=List(64){index->if(index and mask==bits)1 else 0}
            verify(6,cells)
        }
        val parity=List(64){Integer.bitCount(it)%2}
        verify(6,parity)
        println("Six-variable SOP/POS tables verified against independently parsed expressions, including parity and seeded cubes.")
    }
    @Test fun sixVariableWholeMapIsOne64CellGroupAcrossTheLongMaskBoundary() {
        val ones=KMapEngine.solve(6,List(64){1})
        assertEquals("1",ones.expression);assertEquals(1,ones.groups.size);assertEquals(64,ones.groups.single().cube.indices(64).size)
        assertEquals(64,java.lang.Long.bitCount(ones.groups.single().cube.members))
        assertTrue(KMapEngine.checkGroup(6,List(64){1},(0 until 64).toList(),KMapEngine.Form.SOP).valid)
        val zeros=KMapEngine.solve(6,List(64){0},KMapEngine.Form.POS)
        assertEquals("0",zeros.expression);assertEquals(64,zeros.groups.single().cube.indices(64).size)
    }
    @Test fun sigmaPiFunctionNotationKeepsMintermAndMaxtermMeaning() {
        val sigma=KMapEngine.fromNotation("F(A.B.C)=Σm(1,3,5)")
        assertEquals(KMapEngine.Notation(3,"ABC",KMapEngine.Form.SOP,listOf(0,1,0,1,0,1,0,0)),sigma)
        val pi=KMapEngine.fromNotation("F(A,B,C)=ΠM(0,2)")
        assertEquals(KMapEngine.Notation(3,"ABC",KMapEngine.Form.POS,listOf(0,1,0,1,1,1,1,1)),pi)
        val six=KMapEngine.fromNotation("F(A,B,C,D,E,F)=Σm(63)")
        assertEquals(KMapEngine.Notation(6,"ABCDEF",KMapEngine.Form.SOP,List(64){if(it==63)1 else 0}),six)
        assertThrows(IllegalArgumentException::class.java){KMapEngine.fromNotation("F(A,A)=Σm(0)")}
        assertThrows(IllegalArgumentException::class.java){KMapEngine.fromNotation("F(A,B)=Σm(4)")}
        assertThrows(IllegalArgumentException::class.java){KMapEngine.fromNotation("F(A,B)=Σm(1-3)")}
    }
    @Test fun beginnerExerciseCatalog() {
        assertEquals(24,KMapEngine.exercises.size)
        KMapEngine.exercises.forEach { verify(it.variables,it.cells(),true) }
    }
    @Test fun grayCodeOrderAndAxisWrapping() {
        assertEquals(listOf(0,1,3,2),KMapEngine.gray(2))
        assertEquals(listOf(0,1,3,2),(0..3).map {KMapEngine.cellAt(3,0,it)})
        assertEquals(listOf(0,4,12,8),(0..3).map {KMapEngine.cellAt(4,it,0)})
        assertEquals(listOf(0,1,3,2,6,7,5,4),KMapEngine.gray(3))
        for(bits in 1..3) {
            val labels=KMapEngine.gray(bits)
            labels.indices.forEach { assertEquals(1,Integer.bitCount(labels[it] xor labels[(it+1)%labels.size])) }
        }
    }
    @Test fun fourCornersMakeOneWrappedGroup() {
        val cells=List(16) {if(it in listOf(0,2,8,10)) 1 else 0}
        val solution=KMapEngine.solve(4,cells)
        assertEquals("B'D'",solution.expression);assertEquals(1,solution.groups.size);assertTrue(solution.groups[0].wraps)
        assertTrue(KMapEngine.checkGroup(4,cells,listOf(0,2,8,10),KMapEngine.Form.SOP).valid)
    }
    @Test fun overlapsAreAllowedAndEssential() {
        val result=KMapEngine.solve(2,listOf(0,1,1,1))
        assertEquals("A + B",result.expression);assertEquals(2,result.groups.size)
        assertEquals(2,result.groups.count {it.cube.contains(3)})
        assertTrue(result.groups.all {it.essential})
    }
    @Test fun rejectsDiagonalAndWrongSizedGroups() {
        val all=List(16) {1}
        assertFalse(KMapEngine.checkGroup(4,all,listOf(0,1,3),KMapEngine.Form.SOP).valid)
        assertFalse(KMapEngine.checkGroup(4,all,listOf(0,1,2,3,4,5),KMapEngine.Form.SOP).valid)
        assertFalse(KMapEngine.checkGroup(2,List(4){1},listOf(0,3),KMapEngine.Form.SOP).valid)
    }
    @Test fun rejectsOppositeOutputsAndAllXGroups() {
        assertFalse(KMapEngine.checkGroup(2,listOf(1,0,0,0),listOf(0,1),KMapEngine.Form.SOP).valid)
        assertFalse(KMapEngine.checkGroup(2,List(4){2},listOf(0,1),KMapEngine.Form.SOP).valid)
        assertFalse(KMapEngine.checkGroup(2,List(4){1},emptyList(),KMapEngine.Form.SOP).valid)
        assertFalse(KMapEngine.checkGroup(2,List(4){1},listOf(4),KMapEngine.Form.SOP).valid)
        assertTrue(KMapEngine.checkGroup(2,List(4){1},listOf(0),KMapEngine.Form.SOP).message.contains("grow"))
    }
    @Test fun inputListsValidateBoundsAndConflicts() {
        for(text in listOf("-1","4","1-3","A","1.5","99999999999999999")) assertTrue(KMapEngine.fromLists(2,text,"",KMapEngine.Form.SOP) is KMapEngine.Input.Invalid)
        assertTrue(KMapEngine.fromLists(4,"16","",KMapEngine.Form.SOP) is KMapEngine.Input.Invalid)
        assertTrue(KMapEngine.fromLists(3,"1,2","2",KMapEngine.Form.SOP) is KMapEngine.Input.Invalid)
        assertTrue(KMapEngine.fromLists(1,"","",KMapEngine.Form.SOP) is KMapEngine.Input.Invalid)
        assertEquals(listOf(0,1,1,0),(KMapEngine.fromLists(2,"1, 1, 2,","",KMapEngine.Form.SOP) as KMapEngine.Input.Valid).cells)
        assertEquals(listOf(1,0,1,1),(KMapEngine.fromLists(2,"1","",KMapEngine.Form.POS) as KMapEngine.Input.Valid).cells)
    }
    @Test fun constantsAndUnspecifiedFunctions() {
        assertEquals("1",KMapEngine.solve(4,List(16){1}).expression)
        assertEquals("0",KMapEngine.solve(4,List(16){0}).expression)
        assertEquals("0",KMapEngine.solve(4,List(16){0},KMapEngine.Form.POS).expression)
        assertEquals("1",KMapEngine.solve(4,List(16){1},KMapEngine.Form.POS).expression)
        assertEquals("0",KMapEngine.solve(2,List(4){2}).expression)
        assertEquals("1",KMapEngine.solve(2,List(4){2},KMapEngine.Form.POS).expression)
    }
    @Test fun complementRulesDifferBetweenForms() {
        assertEquals("A'",KMapEngine.solve(2,listOf(1,1,0,0)).expression)
        assertEquals("(A)",KMapEngine.solve(2,listOf(0,0,1,1),KMapEngine.Form.POS).expression)
        assertEquals("(B')",KMapEngine.solve(2,listOf(1,0,1,0),KMapEngine.Form.POS).expression)
        assertEquals("(A + B)",KMapEngine.solve(2,listOf(0,1,1,1),KMapEngine.Form.POS).expression)
    }
    @Test fun independentPublicExpressionEvaluator() {
        for(form in KMapEngine.Form.entries) {
            val result=KMapEngine.solve(4,List(16){if(it in listOf(0,2,8,10))1 else 0},form)
            for(i in 0..15) assertEquals(result.evaluate(i),KMapEngine.evaluateExpression(result.expression,4,i,form))
        }
    }
}
