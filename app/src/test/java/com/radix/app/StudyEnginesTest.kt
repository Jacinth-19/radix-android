package com.radix.app

import java.math.BigInteger
import java.util.Random
import org.junit.Assert.*
import org.junit.Test

class StudyEnginesTest {
    @Test fun expressionOperatorsAndAliases(){
        val aliases=listOf("AB","A B","A AND B","A*B","A.B","A·B","A&B","A&&B")
        aliases.forEach{assertEquals(listOf(0,0,0,1),ExpressionEngine.report(it,2).cells)}
        listOf("A+B","A OR B","A|B","A||B").forEach{assertEquals(listOf(0,1,1,1),ExpressionEngine.report(it,2).cells)}
        listOf("A'","A′","A’","NOT A","!A","~A").forEach{assertEquals(listOf(1,1,0,0),ExpressionEngine.report(it,2).cells)}
        listOf("A XOR B","A^B","A⊕B").forEach{assertEquals(listOf(0,1,1,0),ExpressionEngine.report(it,2).cells)}
        assertEquals(listOf(1,1,1,0),ExpressionEngine.report("A NAND B",2).cells)
        assertEquals(listOf(1,0,0,0),ExpressionEngine.report("A NOR B",2).cells)
        assertEquals(listOf(1,0,0,1),ExpressionEngine.report("A XNOR B",2).cells)
    }
    @Test fun expressionPrecedenceAdjacencyAndParentheses(){
        assertTrue(ExpressionEngine.compare("A+BC","A OR (B AND C)",3).equivalent)
        assertTrue(ExpressionEngine.compare("A(B+C)","AB+AC",3).equivalent)
        assertTrue(ExpressionEngine.compare("A XOR B AND C OR D","(A XOR (B AND C)) OR D",4).equivalent)
        assertFalse(ExpressionEngine.compare("A+B*C","(A+B)*C",3).equivalent)
        assertTrue(ExpressionEngine.compare("A NAND B NAND C","(A NAND B) NAND C",3).equivalent)
    }
    @Test fun expressionEquivalenceAndCounterexamples(){
        for(n in 2..6){assertTrue(ExpressionEngine.compare("!(A B)","A'+B'",n).equivalent);assertTrue(ExpressionEngine.compare("A XOR B","A'B+AB'",n).equivalent)}
        assertEquals(listOf(1,2),ExpressionEngine.compare("AB","A+B",2).mismatches)
        assertEquals("1",ExpressionEngine.report("1 OR AB",2).sop);assertEquals("0",ExpressionEngine.report("0 AND (A+B)",2).pos)
        assertEquals("(A + B)(A' + B')",ExpressionEngine.report("A XOR B",2).pos)
        val parity=ExpressionEngine.report("A XOR B",2);assertEquals(listOf(1,2),parity.minterms);assertEquals(listOf(0,3),parity.maxterms);assertEquals("Σm(1,2)",parity.sigmaM);assertEquals("ΠM(0,3)",parity.piM)
        val six=ExpressionEngine.report("A XOR F",6);assertEquals(64,six.cells.size);assertEquals(32,six.minterms.size);assertEquals(32,six.maxterms.size);assertTrue(ExpressionEngine.compare("A XOR F","A'F+AF'",6).equivalent);assertEquals(listOf('A','B','C','D','E','F'),six.names)
    }
    @Test fun expressionInvalidAndBoundedInputs(){
        for(s in listOf("","A+","AND A","(A+B","A B)","A, B","E","A;alert(1)","2","NOT"))assertThrows(IllegalArgumentException::class.java){ExpressionEngine.parse(s,2)}
        assertThrows(IllegalArgumentException::class.java){ExpressionEngine.parse("C",2)}
        assertThrows(IllegalArgumentException::class.java){ExpressionEngine.parse("F",2)}
        assertThrows(IllegalArgumentException::class.java){ExpressionEngine.parse("A XOR F",5)}
        assertThrows(IllegalArgumentException::class.java){ExpressionEngine.parse("A".repeat(257),2)}
        assertThrows(IllegalArgumentException::class.java){ExpressionEngine.parse("(".repeat(33)+"A"+")".repeat(33),2)}
        assertThrows(IllegalArgumentException::class.java){ExpressionEngine.parse("A",1)}
    }
    private data class Example(val text:String,val eval:(Int)->Boolean)
    @Test fun seededExpressionTreesUseIndependentBooleanOracle(){
        val r=Random(401L);var rows=0
        for(n in 2..6){
            fun example(depth:Int):Example {
                if(depth==0||r.nextInt(5)==0){val v=r.nextInt(n);return Example(('A'+v).toString()){code->(code shr(n-v-1))%2==1}}
                val a=example(depth-1);if(r.nextInt(5)==0)return Example("NOT (${a.text})"){!a.eval(it)}
                val b=example(depth-1);val op=r.nextInt(6);val name=listOf("AND","OR","XOR","NAND","NOR","XNOR")[op]
                return Example("(${a.text} $name ${b.text})"){code->val x=a.eval(code);val y=b.eval(code);when(op){0->x&&y;1->x||y;2->x!=y;3->!(x&&y);4->!(x||y);else->x==y}}
            }
            repeat(120){val e=example(3);val actual=ExpressionEngine.report(e.text,n).cells;for(code in 0 until(1 shl n)){assertEquals(e.text,if(e.eval(code))1 else 0,actual[code]);rows++}}
        };assertEquals(14880,rows)
    }
    @Test fun representationsCoverEverySmallWidthWord(){
        for(width in listOf(4,8,16)){val max=1 shl width;for(v in 0 until max){val r=RepresentationEngine.report(v.toString(),width,false);assertEquals(v.toString(2).padStart(width,'0'),r.word);assertEquals(v.toString(),r.unsigned);assertEquals((if(v>=max/2)v-max else v).toString(),r.signed);assertEquals(v.toString(16).uppercase().padStart(width/4,'0'),r.hex)}}
    }
    @Test fun signedEndpointsAndNoSilentWrapping(){
        for(width in RepresentationEngine.widths){val m=BigInteger.ONE.shiftLeft(width-1)
            for(v in listOf(m.negate(),BigInteger.valueOf(-1),BigInteger.ZERO,m-BigInteger.ONE)){val r=RepresentationEngine.report(v.toString(),width,true);assertEquals(v.toString(),r.signed)}
            for(v in listOf(m.negate()-BigInteger.ONE,m))assertThrows(IllegalArgumentException::class.java){RepresentationEngine.report(v.toString(),width,true)}
            assertThrows(IllegalArgumentException::class.java){RepresentationEngine.report("-1",width,false)}
            assertThrows(IllegalArgumentException::class.java){RepresentationEngine.report(BigInteger.ONE.shiftLeft(width).toString(),width,false)}
        }
        assertEquals("11110011",RepresentationEngine.report("-13",8,true).word)
        assertEquals("FFFFFFFFFFFFFFFF",RepresentationEngine.report("18446744073709551615",64,false).hex)
        assertEquals("-1",RepresentationEngine.report("1".repeat(64),64,true,"bits").signed)
    }
    @Test fun grayRoundTripAndNeighbors(){
        for(v in 0 until 65536){val n=BigInteger.valueOf(v.toLong());val g=RepresentationEngine.grayEncode(n);assertEquals(n,RepresentationEngine.grayDecode(g));if(v>0)assertEquals(1,g.xor(RepresentationEngine.grayEncode(n-BigInteger.ONE)).bitCount())}
        val n=BigInteger("18446744073709551615");assertEquals(n,RepresentationEngine.grayDecode(RepresentationEngine.grayEncode(n)))
        assertEquals("0100",RepresentationEngine.report("0110",4,false,"gray").word)
    }
    @Test fun bcdAndShortBinaryWordsAreNotConfused(){
        assertEquals("0001 0010",RepresentationEngine.bcd(BigInteger.valueOf(12)))
        assertEquals("0000",RepresentationEngine.bcd(BigInteger.ZERO))
        assertNull(RepresentationEngine.report("-13",8,true).bcd)
        val r=RepresentationEngine.report("1111",8,true,"bits");assertEquals("00001111",r.word);assertEquals("15",r.signed);assertTrue(r.steps.any{it.contains("ZERO-padded")})
        assertEquals("-1",RepresentationEngine.report("1111",4,true,"bits").signed)
    }
    @Test fun representationInvalidInputs(){
        for(s in listOf("","1.5","0xFF","1e2","abc"))assertThrows(IllegalArgumentException::class.java){RepresentationEngine.report(s,8,false)}
        for(s in listOf("","1020","111111111","−1"))assertThrows(IllegalArgumentException::class.java){RepresentationEngine.report(s,8,false,"bits")}
        assertThrows(IllegalArgumentException::class.java){RepresentationEngine.report("0",3,false)}
        assertThrows(IllegalArgumentException::class.java){RepresentationEngine.report("0",8,false,"bad")}
    }
    @Test fun atomicHistoryBranchingAndBounds(){
        val h=StudyHistory<Int>(3);assertNull(h.undo(0));h.record(0,0);assertFalse(h.canUndo)
        for(i in 0..4)h.record(i,i+1)
        assertEquals(4,h.undo(5));assertEquals(3,h.undo(4));assertEquals(2,h.undo(3));assertNull(h.undo(2))
        assertEquals(3,h.redo(2));h.record(3,7);assertFalse(h.canRedo);assertEquals(3,h.undo(7));assertEquals(7,h.redo(3));h.clear();assertFalse(h.canUndo)
    }
    private fun fixture()=StudyBackup(listOf(StudySaved(1,"FF",16)),listOf(CircuitSession("session-1","D memory","sequential","D_FF",1,listOf(false),1,true)),setOf("gate-and"),StudyMap(3,listOf(0,1,2,0,1,0,0,1),"POS"),StudyExpression(),StudyRepresentation(),StudySettings(),StudyConverter("255",10,16,42,true))
    @Test fun backupRoundTripAndCrossPlatformFields(){val b=fixture();assertEquals(b,StudyData.decode(StudyData.encode(b)));assertEquals(b.sessions,StudyData.sessions(StudyData.sessionText(b.sessions)));assertEquals(b.progress,StudyData.progress(StudyJson.write(b.progress.toList())));val wide=b.copy(kmap=StudyMap(6,List(64){if(it==63)1 else 0},"POS"),expression=StudyExpression(6,"A XOR F","A XOR F"));assertEquals(wide,StudyData.decode(StudyData.encode(wide)))}
    @Test fun backupRejectsBadDimensionsModelsAndDuplicates(){
        val b=fixture();assertThrows(IllegalArgumentException::class.java){StudyData.decode(StudyData.encode(b.copy(kmap=StudyMap(4,listOf(0),"SOP"))))}
        assertThrows(IllegalArgumentException::class.java){StudyData.decode(StudyData.encode(b.copy(saved=b.saved+b.saved)))}
        assertThrows(IllegalArgumentException::class.java){StudyData.decode(StudyData.encode(b.copy(progress=setOf("unknown"))))}
        for(s in listOf(b.sessions[0].copy(model="missing"),b.sessions[0].copy(bits=2),b.sessions[0].copy(inputs=listOf(true,false)),b.sessions[0].copy(name="bad\nname"),CircuitSession("s","Bad","sequential","SR_NOR",1,listOf(true,true),1,false)))assertThrows(IllegalArgumentException::class.java){StudyData.session(StudyData.sessionMap(s))}
        assertThrows(IllegalArgumentException::class.java){StudyData.decode(StudyData.encode(b).replace("\"version\":1","\"version\":2"))}
    }
    @Test fun allCatalogSessionsValidateAndUnknownMemorySurvives(){
        LogicEngine.Gate.entries.forEach{g->val n=if(g in listOf(LogicEngine.Gate.NOT,LogicEngine.Gate.BUFFER))1 else 4;val s=CircuitSession("s",g.name,"gates",g.name,n,List(n){true});assertEquals(s,StudyData.session(StudyData.sessionMap(s)))}
        LogicCatalog.circuits.forEach{c->val s=CircuitSession("s",c.title,"combinational",c.id,c.inputs.size,List(c.inputs.size){false});assertEquals(s,StudyData.session(StudyData.sessionMap(s)))}
        LogicCatalog.sequential.forEach{s->val saved=CircuitSession("s",s.title,"sequential",s.id,s.width,LogicEngine.defaultInputs(s),null,false);assertEquals(saved,StudyData.session(StudyData.sessionMap(saved)))}
    }
    @Test fun jsonStringsBoundsAndMaliciousInput(){
        val value=mapOf("text" to "Name \"quoted\" \\ newline\nemoji 🧠","items" to listOf(1,true,null,"\t"));assertEquals(value.mapValues{if(it.key=="items")listOf(1L,true,null,"\t")else it.value},StudyJson.parse(StudyJson.write(value)))
        for(text in listOf("{} trailing","{\"a\":1,\"a\":2}","[1,]","{\"a\":}","NaN","01","\"bad\ntext\"","[".repeat(17)+"0"+"]".repeat(17)))assertThrows(IllegalArgumentException::class.java){StudyJson.parse(text)}
        assertThrows(IllegalArgumentException::class.java){StudyJson.parse(" ".repeat(262145))}
        assertEquals(1.0,StudyJson.parse("1.0"))
    }
    @Test fun practiceCatalogIsUniqueAndComplete(){assertEquals(108,PracticeCatalog.questions.size);assertEquals(108,PracticeCatalog.questions.map{it.id}.distinct().size);assertEquals(setOf("Gates","Circuits","Memory","Representations","Expressions","Number bases","Arithmetic","Laws","K-maps","Floats","Builder"),PracticeCatalog.questions.map{it.category}.toSet());assertEquals(listOf(31,52,25),(0..2).map{t->PracticeCatalog.questions.count{it.tier==t}});PracticeCatalog.questions.forEach{assertTrue(it.answer in it.choices.indices);assertEquals(it.choices.size,it.choices.distinct().size);assertTrue(it.hint.isNotBlank()&&it.explanation.isNotBlank());assertTrue(it.tier in 0..2)}}
    @Test fun browserBackupFixtureDecodesAndNativeExportMatches(){
        val dir=java.io.File("../design")
        val b=StudyData.decode(java.io.File(dir,"study-backup-browser-fixture.json").readText())
        assertEquals(fixture().saved,b.saved);assertEquals(fixture().sessions,b.sessions)
        assertEquals(fixture().progress,b.progress);assertEquals(fixture().kmap,b.kmap)
        assertEquals(fixture().converter,b.converter);assertEquals("A'B+AB'",b.expression.left)
        assertTrue(ExpressionEngine.compare(b.expression.left,b.expression.right,b.expression.variables).equivalent)
        assertEquals(b,StudyData.decode(StudyData.encode(b)))
        java.io.File(dir,"study-backup-native-fixture.json").writeText(StudyData.encode(b))
    }
    @Test fun sessionValidationCoversEveryModelAndUnaryWidth(){
        var count=0
        LogicEngine.Gate.entries.forEach{g->val w=if(g.name in listOf("NOT","BUFFER"))1 else 4
            val s=CircuitSession("s",g.name,"gates",g.name,w,List(w){true})
            assertEquals(s,StudyData.session(StudyData.sessionMap(s)));count++}
        LogicCatalog.circuits.forEach{c->val s=CircuitSession("s",c.title,"combinational",c.id,c.inputs.size,List(c.inputs.size){false})
            assertEquals(s,StudyData.session(StudyData.sessionMap(s)));count++}
        LogicCatalog.sequential.forEach{c->val s=CircuitSession("s",c.title,"sequential",c.id,c.width,LogicEngine.defaultInputs(c),null,false)
            assertEquals(s,StudyData.session(StudyData.sessionMap(s)));count++}
        assertEquals(42,count)
    }
    @Test fun bcdDigitPackingCoversTenThousandDecimalValues(){
        for(v in 0 until 10000){val digits=RepresentationEngine.bcd(BigInteger.valueOf(v.toLong())).split(" ")
            assertEquals(v.toString(),digits.joinToString(""){it.toInt(2).toString()})
            assertTrue(digits.all{it.length==4&&it.toInt(2)<10})}
    }

}
