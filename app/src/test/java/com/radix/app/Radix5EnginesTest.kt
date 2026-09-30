package com.radix.app

import org.junit.Assert.*
import org.junit.Test

class Radix5EnginesTest {
    private fun holds(left:String,right:String,vars:Int){assertTrue("$left == $right",ExpressionEngine.compare(left,right,vars).equivalent)}
    @Test fun booleanLawsHoldOnEveryRow(){
        holds("A+0","A",2);holds("A1","A",2);holds("A+1","1",2);holds("A0","0",2)
        holds("A+A","A",2);holds("AA","A",2);holds("A+A'","1",2);holds("AA'","0",2);holds("(A')'","A",2)
        holds("A+B","B+A",2);holds("AB","BA",2);holds("(A+B)+C","A+(B+C)",3);holds("(AB)C","A(BC)",3)
        holds("A(B+C)","AB+AC",3);holds("A+BC","(A+B)(A+C)",3)
        holds("A+AB","A",2);holds("A(A+B)","A",2)
        holds("(A+B)'","A'B'",2);holds("(AB)'","A'+B'",2)
        holds("A XOR B","A'B+AB'",2);holds("A XNOR B","(A XOR B)'",2)
        // Symbols parse identically to words.
        holds("A⊕B","A XOR B",2);holds("A⊙B","A XNOR B",2);holds("A′","A'",2);holds("¬A","NOT A",2)
        // A negative control: these must NOT be equivalent.
        assertFalse(ExpressionEngine.compare("A+B","AB",2).equivalent)
        assertFalse(ExpressionEngine.compare("A XOR B","A XNOR B",2).equivalent)
    }
    @Test fun onesComplementIsCorrect(){
        val r=RepresentationEngine.report("-13",8,true)
        assertEquals("11110011",r.word);assertEquals("00001100",r.ones);assertEquals("-12",r.onesSigned)
        val p=RepresentationEngine.report("13",8,true)
        assertEquals("11110010",p.ones);assertEquals("13",p.onesSigned)
        val z=RepresentationEngine.report("255",8,false)
        assertEquals("00000000",z.ones);assertEquals("-0",z.onesSigned)
        for(v in 0 until 16){
            val w=RepresentationEngine.report(v.toString(),4,false)
            val inv=v.toString(2).padStart(4,'0').map{if(it=='1')'0' else '1'}.joinToString("")
            assertEquals(inv,w.ones)
        }
    }
    @Test fun practiceCatalogHasTiersAndBreadth(){
        val qs=PracticeCatalog.questions
        assertEquals(108,qs.size)
        assertEquals(108,qs.map{it.id}.toSet().size)
        assertTrue(qs.all{it.tier in 0..2})
        (0..2).forEach{t->assertTrue("tier $t",qs.count{it.tier==t}>=20)}
        qs.forEach{q->assertTrue(q.choices.size in 2..4);assertEquals(q.choices.size,q.choices.toSet().size);assertTrue(q.answer in q.choices.indices);assertTrue(q.category.isNotBlank())}
        assertEquals(14,qs.count{it.id.startsWith("exam-")})
        assertEquals(9,qs.count{it.id.startsWith("gh-")})
        val byId=qs.associateBy{it.id}
        // Spot-check freshly computed answers (not just stored indices).
        assertEquals("100",BaseArithmetic.compute("FF","1",16,'+').result)
        assertEquals(byId.getValue("exam-arith-hex").choices[0],"100")
        assertEquals("3F",BaseArithmetic.compute("2A","15",16,'+').result)
        assertEquals("23",BaseArithmetic.compute("52","27",8,'-').result)
        assertEquals("1111",BaseArithmetic.compute("101","11",2,'*').result)
        assertEquals("11",BaseArithmetic.compute("1100","100",2,'/').quotient)
    }
    @Test fun appStringsCoverBothLanguages(){
        val en=AppStrings.forLang("en");val fr=AppStrings.forLang("fr")
        assertEquals("Convert",en.tabConvert);assertEquals("Convertir",fr.tabConvert)
        assertEquals(en.tabConvert,AppStrings.forLang("xx").tabConvert)
        assertTrue(en.whatsNewItems.size>=5&&fr.whatsNewItems.size>=5)
        assertTrue(en.tourConvertTitle.isNotBlank()&&fr.tourConvertTitle.isNotBlank())
        assertTrue(en.reminderTime(19,5).contains("19:05"))
        assertTrue(fr.reminderTime(7,30).contains("7:30")||fr.reminderTime(7,30).contains("07:30"))
    }
}
