package com.radix.app

import java.math.BigInteger
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class BaseArithmeticTest {
    private fun big(s:String,base:Int)=BigInteger(s,base)
    @Test fun knownAdditions(){
        assertEquals("100",BaseArithmetic.compute("FF","1",16,'+').result)
        assertEquals("10010",BaseArithmetic.compute("1011","0111",2,'+').result)
        assertEquals("3F",BaseArithmetic.compute("2A","15",16,'+').result)
        assertEquals("0",BaseArithmetic.compute("ABC","-ABC",16,'+').result)
        assertEquals("-5",BaseArithmetic.compute("-2","-3",10,'+').result)
    }
    @Test fun knownSubtractions(){
        assertEquals("100",BaseArithmetic.compute("1011","0111",2,'-').result)
        assertEquals("23",BaseArithmetic.compute("52","27",8,'-').result)
        assertEquals("0",BaseArithmetic.compute("FF","FF",16,'-').result)
        assertEquals("-1",BaseArithmetic.compute("0","1",10,'-').result)
    }
    @Test fun knownMultiplyDivide(){
        assertEquals("1111",BaseArithmetic.compute("101","11",2,'*').result)
        assertEquals("-372",BaseArithmetic.compute("2A","-15",16,'×').result)
        val d=BaseArithmetic.compute("1100","100",2,'/')
        assertEquals("11",d.quotient);assertEquals("0",d.remainder)
        val d2=BaseArithmetic.compute("1A","4",16,'÷')
        assertEquals(big("1A",16).divide(big("4",16)).toString(16).uppercase(),d2.quotient)
        assertEquals("2",BaseArithmetic.compute("7","3",10,'/').quotient)
        assertEquals("1",BaseArithmetic.compute("7","3",10,'/').remainder)
        assertEquals("-3",BaseArithmetic.compute("-7","2",10,'/').quotient)
        assertEquals("-1",BaseArithmetic.compute("-7","2",10,'/').remainder)
    }
    @Test fun rejectsBadInput(){
        assertThrows(IllegalArgumentException::class.java){BaseArithmetic.compute("2","1",2,'+')}
        assertThrows(IllegalArgumentException::class.java){BaseArithmetic.compute("","1",10,'+')}
        assertThrows(IllegalArgumentException::class.java){BaseArithmetic.compute("1.5","1",10,'+')}
        assertThrows(IllegalArgumentException::class.java){BaseArithmetic.compute("1","0",10,'/')}
        assertThrows(IllegalArgumentException::class.java){BaseArithmetic.compute("1","1",1,'+')}
        assertThrows(IllegalArgumentException::class.java){BaseArithmetic.compute("1","1",37,'+')}
        assertThrows(IllegalArgumentException::class.java){BaseArithmetic.compute("1","1",10,'%')}
    }
    @Test fun randomizedCrossCheckAgainstBigInteger(){
        val rnd=Random(5);val digits="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        fun num(base:Int):String{val len=rnd.nextInt(1,11);val s=(1..len).map{digits[rnd.nextInt(base)]}.joinToString("");return (if(rnd.nextBoolean()&&s.any{it!='0'})"-"else "")+s}
        var checked=0
        repeat(400){
            val base=listOf(2,3,5,8,10,12,16,20,36).random(rnd)
            val op=listOf('+','-','*','/').random(rnd)
            var a=num(base);var b=num(base)
            if(op=='/'&&b.trimStart('-','0').isEmpty())b="1"
            val r=BaseArithmetic.compute(a,b,base,op)
            val va=big(a.trimStart('+').ifEmpty{"0"}.let{if(it=="-"||it=="") "0" else it},base)
            val vb=big(b.trimStart('+').ifEmpty{"0"}.let{if(it=="-"||it=="") "0" else it},base)
            val expected=when(op){'+'->va+vb;'-'->va-vb;else->va* vb}
            if(op=='/'){assertEquals(va.divide(vb).toString(base).uppercase(),r.quotient);assertEquals(va.remainder(vb).toString(base).uppercase(),r.remainder)}
            else assertEquals(expected.toString(base).uppercase(),r.result)
            assertTrue(r.steps.isNotEmpty())
            checked++
        }
        assertEquals(400,checked)
    }
    @Test fun referenceTableIsCorrect(){
        val t=BaseArithmetic.referenceTable()
        assertEquals(16,t.size)
        assertEquals(listOf("0","0","0","0000"),t[0])
        assertEquals(listOf("15","17","F","1111"),t[15])
        t.forEachIndexed{i,row->assertEquals(i.toString(),row[0]);assertEquals(i.toString(8),row[1]);assertEquals(i.toString(16).uppercase(),row[2]);assertEquals(i.toString(2).padStart(4,'0'),row[3])}
    }
}
