package com.radix.app

import org.junit.Assert.*
import org.junit.Test

class LogicEngineTest {
    private fun expectedCircuit(c: LogicEngine.Circuit, i: List<Boolean>): List<Boolean> {
        val v=c.inputs.zip(i.map{if(it)1 else 0}).toMap();fun n(name:String)=v.getValue(name)
        val a=nIf(v,"A");val b=nIf(v,"B")
        val output:List<Int> = when(c.id) {
            "half-adder" -> listOf((a+b)%2,(a+b)/2)
            "full-adder" -> (a+b+n("Cin")).let {listOf(it%2,it/2)}
            "half-subtractor" -> (a-b).let {listOf(it and 1,if(it<0)1 else 0)}
            "full-subtractor" -> (a-b-n("Bin")).let {listOf(it and 1,if(it<0)1 else 0)}
            "mux-2" -> listOf(n("D${n("S")}"))
            "mux-4" -> listOf(n("D${2*n("S1")+n("S0")}"))
            "demux-2" -> List(2){if(it==n("S"))n("D")else 0}
            "decoder-2" -> List(4){if(n("EN")==1&&it==2*a+b)1 else 0}
            "encoder-4" -> {val highest=(3 downTo 0).firstOrNull{n("D$it")==1};listOf((highest?:0)/2,(highest?:0)%2,if(highest==null)0 else 1)}
            "compare-1" -> listOf(if(a<b)1 else 0,if(a==b)1 else 0,if(a>b)1 else 0)
            "compare-2" -> {val aa=2*n("A1")+n("A0");val bb=2*n("B1")+n("B0");listOf(if(aa<bb)1 else 0,if(aa==bb)1 else 0,if(aa>bb)1 else 0)}
            "ripple-4" -> {val aa=(0..3).sumOf{n("A$it")*(1 shl it)};val bb=(0..3).sumOf{n("B$it")*(1 shl it)};val sum=aa+bb+n("Cin");(3 downTo 0).map{(sum shr it)and 1}+listOf(sum/16)}
            else -> error(c.id)
        }
        return output.map{it==1}
    }
    private fun nIf(v:Map<String,Int>,name:String)=v[name]?:0
    @Test fun everyGateTruthTableAndArity() {
        var rows=0
        for(g in LogicEngine.Gate.entries)for(n in if(g in listOf(LogicEngine.Gate.NOT,LogicEngine.Gate.BUFFER))listOf(1)else listOf(2,3,4))for(code in 0 until(1 shl n)){
            val i=LogicEngine.bits(code,n);val ones=Integer.bitCount(code)
            val expected=when(g){LogicEngine.Gate.AND->ones==n;LogicEngine.Gate.OR->ones>0;LogicEngine.Gate.NOT->ones==0;LogicEngine.Gate.BUFFER->ones==1;LogicEngine.Gate.NAND->ones<n;LogicEngine.Gate.NOR->ones==0;LogicEngine.Gate.XOR->ones%2==1;LogicEngine.Gate.XNOR->ones%2==0}
            assertEquals(expected,LogicEngine.gate(g,i));assertEquals(code,LogicEngine.code(i));rows++
        };assertEquals(172,rows)
    }
    @Test fun allCombinationalTablesUseIndependentArithmeticOracle(){
        var rows=0;assertEquals(12,LogicCatalog.circuits.size)
        LogicCatalog.circuits.forEach { c ->for(code in 0 until(1 shl c.inputs.size)){
            val i=LogicEngine.bits(code,c.inputs.size);val result=LogicEngine.evaluate(c,i)
            assertEquals("${c.id} $code",expectedCircuit(c,i),result.outputs.values.toList());rows++
        }};assertEquals(656,rows)
    }
    @Test fun decoderComparatorEncoderProperties(){
        for(id in listOf("compare-1","compare-2")){val c=LogicCatalog.circuits.first{it.id==id};for(code in 0 until(1 shl c.inputs.size))assertEquals(1,LogicEngine.evaluate(c,LogicEngine.bits(code,c.inputs.size)).outputs.values.count{it})}
        val enc=LogicCatalog.circuits.first{it.id=="encoder-4"}
        assertEquals(listOf(false,false,false),LogicEngine.evaluate(enc,listOf(false,false,false,false)).outputs.values.toList())
        assertEquals(listOf(false,false,true),LogicEngine.evaluate(enc,listOf(false,false,false,true)).outputs.values.toList())
        assertEquals(listOf(true,true,true),LogicEngine.evaluate(enc,List(4){true}).outputs.values.toList())
    }
    private fun expectedSeq(s:LogicEngine.Sequential,q:Int?,i:List<Boolean>):Int?{
        val a=if(i[0])1 else 0;val b=if(i.getOrElse(1){false})1 else 0
        fun sr(set:Int,reset:Int):Int?=if(set+reset==2)null else if(set==1)1 else if(reset==1)0 else q
        return when(s.id){
            "SR_NOR","SR_FF"->sr(a,b);"SR_NAND"->sr(1-a,1-b)
            "D_LATCH"->if(b==1)a else q;"D_FF"->a
            "JK_FF"->if(a==0&&b==0)q else if(a==0)0 else if(b==0)1 else q?.let{1-it}
            "T_FF"->if(a==0)q else q?.let{1-it}
            "COUNTER_3"->if(b==1)0 else if(a==0)q else q?.let{(it+1)%8}
            "SHIFT_4"->if(i[2])0 else if(b==0)q else q?.let{(it*2+a)%16}
            "RING_4"->if(b==1)1 else if(a==0)q else q?.let{(it*2)%16+it/8}
            "JOHNSON_4"->if(b==1)0 else if(a==0)q else q?.let{(it*2)%16+(1-it/8)}
            else->error(s.id)
        }
    }
    @Test fun allSequentialTransitionsAndFourClockCombinations(){
        assertEquals(11,LogicCatalog.sequential.size);var states=0
        LogicCatalog.sequential.forEach { s->
            for(code in 0 until(1 shl s.inputs.size))for(q in 0 until(1 shl s.width)){
                val i=LogicEngine.bits(code,s.inputs.size)
                for(oldClock in listOf(false,true))for(clock in listOf(false,true)){
                    val result=LogicEngine.update(s,LogicEngine.State(q,oldClock),i,clock)
                    val capture=!s.clocked||(!oldClock&&clock)
                    assertEquals("${s.id} q=$q input=$code clocks=$oldClock/$clock",if(capture)expectedSeq(s,q,i)else q,result.bits)
                    assertEquals(s.clocked&&clock,result.clock);states++
                }
            }
        };assertEquals(1344,states)
    }
    @Test fun nextStateTablesAndUnknownRecovery(){
        var rows=0
        LogicCatalog.sequential.forEach {s->
            for(row in LogicEngine.nextRows(s)){assertEquals(expectedSeq(s,row.before,row.inputs),row.after);rows++}
            for(code in 0 until(1 shl s.inputs.size)){val i=LogicEngine.bits(code,s.inputs.size);assertEquals(expectedSeq(s,null,i),LogicEngine.update(s,LogicEngine.State(null),i,true).bits)}
        };assertEquals(336,rows)
    }
    @Test fun latchIsTransparentButFlipFlopIsEdgeTriggered(){
        val latch=LogicCatalog.sequential.first{it.id=="D_LATCH"};val ff=LogicCatalog.sequential.first{it.id=="D_FF"}
        assertEquals(1,LogicEngine.update(latch,LogicEngine.State(0),listOf(true,true),false).bits)
        assertEquals(0,LogicEngine.update(latch,LogicEngine.State(0),listOf(true,false),false).bits)
        assertEquals(0,LogicEngine.update(ff,LogicEngine.State(0,true),listOf(true),true).bits)
        assertEquals(0,LogicEngine.update(ff,LogicEngine.State(0,true),listOf(true),false).bits)
        assertEquals(1,LogicEngine.update(ff,LogicEngine.State(0),listOf(true),true).bits)
    }
    @Test fun forbiddenNorAndNandOutputsDoNotPretendComplementarity(){
        val nor=LogicCatalog.sequential.first{it.id=="SR_NOR"};val nand=LogicCatalog.sequential.first{it.id=="SR_NAND"}
        val n=LogicEngine.update(nor,LogicEngine.State(1),listOf(true,true),false)
        assertNull(n.bits);assertEquals(listOf(0,0),LogicEngine.outputs(nor,n,listOf(true,true)))
        val released=LogicEngine.update(nor,n,listOf(false,false),false);assertNull(released.bits);assertEquals(listOf(null,null),LogicEngine.outputs(nor,released,listOf(false,false)))
        assertEquals(1,LogicEngine.update(nor,released,listOf(true,false),false).bits)
        val p=LogicEngine.update(nand,LogicEngine.State(0),listOf(false,false),false)
        assertNull(p.bits);assertEquals(listOf(1,1),LogicEngine.outputs(nand,p,listOf(false,false)))
    }
    @Test fun counterWrapEnableAndSynchronousReset(){
        val s=LogicCatalog.sequential.first{it.id=="COUNTER_3"}
        assertEquals(0,LogicEngine.update(s,LogicEngine.State(7),listOf(true,false),true).bits)
        assertEquals(5,LogicEngine.update(s,LogicEngine.State(5),listOf(false,false),true).bits)
        assertEquals(5,LogicEngine.update(s,LogicEngine.State(5),listOf(true,true),false).bits)
        assertEquals(0,LogicEngine.update(s,LogicEngine.State(5),listOf(false,true),true).bits)
    }
    @Test fun shiftRingAndJohnsonSequences(){
        val shift=LogicCatalog.sequential.first{it.id=="SHIFT_4"};var q=LogicEngine.initial(shift)
        for(d in listOf(true,false,true,true)){q=LogicEngine.update(shift,q.copy(clock=false),listOf(d,true,false),true)};assertEquals(11,q.bits)
        for((id,expected)in listOf("RING_4" to listOf(1,2,4,8,1),"JOHNSON_4" to listOf(0,1,3,7,15,14,12,8,0))){
            val s=LogicCatalog.sequential.first{it.id==id};var st=LogicEngine.initial(s);val actual=mutableListOf(st.bits)
            repeat(expected.size-1){st=LogicEngine.update(s,st.copy(clock=false),listOf(true,false),true);actual.add(st.bits)};assertEquals(expected,actual)
        }
    }
    @Test fun invalidDimensionsAndNetlistsFail(){
        assertThrows(IllegalArgumentException::class.java){LogicEngine.gate(LogicEngine.Gate.NOT,listOf(true,false))}
        assertThrows(IllegalArgumentException::class.java){LogicEngine.gate(LogicEngine.Gate.AND,listOf(true))}
        assertThrows(IllegalArgumentException::class.java){LogicEngine.bits(512,9)}
        val c=LogicCatalog.circuits.first()
        assertThrows(IllegalArgumentException::class.java){LogicEngine.evaluate(c,listOf(true))}
        assertThrows(IllegalStateException::class.java){LogicEngine.evaluate(c.copy(nodes=listOf(LogicEngine.Node("x",LogicEngine.Gate.NOT,listOf("missing")))),listOf(false,false))}
    }
}
