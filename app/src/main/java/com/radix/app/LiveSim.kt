package com.radix.app

/** Real-time teaching simulation: gate delays, transport-delay settle, glitch + clock runs. */
internal object LiveSim {
    fun delayNs(gate:LogicEngine.Gate):Int = when(gate){LogicEngine.Gate.AND,LogicEngine.Gate.OR->4;LogicEngine.Gate.NAND,LogicEngine.Gate.NOR->3;LogicEngine.Gate.XOR,LogicEngine.Gate.XNOR->6;LogicEngine.Gate.NOT,LogicEngine.Gate.BUFFER->2}
    data class Event(val t:Int,val label:String,val value:Boolean)
    data class Settle(val events:List<Event>,val final:Boolean,val glitched:Boolean,val settleNs:Int,val probeEvents:List<Event>)
    /** Flip external input [changed] at t=0 and replay every output change in nanoseconds (transport delay: output at t sees inputs at t−delay, so uneven paths visibly glitch). */
    fun settle(board:BuilderEngine.Board,changed:Int):Settle{
        require(changed in 0..3){"External inputs are A–D."}
        val base=BuilderEngine.evaluate(board)
        val target=BuilderEngine.evaluate(board.copy(ext=board.ext.mapIndexed{i,v->if(i==changed)!v else v}))
        val incoming=board.wires.groupBy({it.toId to it.toPin},{it.fromId to it.fromPin})
        val gateOf=board.gates.associateBy{it.id}
        // Net timelines: initial value + transitions (time, value just after).
        val initial=mutableMapOf<Pair<Int,Int>,Boolean>()
        val lines=mutableMapOf<Pair<Int,Int>,MutableList<Pair<Int,Boolean>>>()
        board.ext.forEachIndexed{i,v->initial[BuilderEngine.EXT_IN to i]=v;lines[BuilderEngine.EXT_IN to i]=if(i==changed)mutableListOf(0 to !v) else mutableListOf()}
        base.gateOut.forEach{(id,v)->initial[id to 0]=v}
        fun valueAfter(net:Pair<Int,Int>,t:Int):Boolean{var v=initial[net]!!;lines[net].orEmpty().forEach{(time,value)->if(time<=t)v=value};return v}
        BuilderEngine.topoOrder(board).forEach{id->
            val g=gateOf[id]!!;val pins=(0 until BuilderEngine.arity(g.gate)).map{incoming[id to it]!!.single()}
            val times=pins.flatMap{lines[it].orEmpty().map{tr->tr.first}}.distinct().sorted()
            val merged=mutableListOf<Pair<Int,Boolean>>();var running=initial[id to 0]!!
            times.forEach{t->
                val out=LogicEngine.gate(g.gate,pins.map{valueAfter(it,t)});val at=t+delayNs(g.gate)
                if(merged.isNotEmpty()&&merged.last().first==at)merged[merged.lastIndex]=at to out
                else if(out!=running){merged+=at to out;running=out}
            }
            // Collapse a same-instant overwrite that returns to the running value.
            lines[id to 0]=merged.filterIndexed{i,tr->i==0||tr.second!=merged[i-1].second}.toMutableList()
        }
        val events=board.gates.flatMap{g->lines[g.id to 0].orEmpty().map{Event(it.first,"${g.gate} #${g.id+1}",it.second)}}.sortedWith(compareBy({it.t},{it.label}))
        val probeSrc=incoming[BuilderEngine.PROBE to 0]!!.single()
        val probeEvents=lines[probeSrc].orEmpty().map{Event(it.first,"Y",it.second)}
        return Settle(events,target.probe,probeEvents.size>=2,events.maxOfOrNull{it.t}?:0,probeEvents)
    }
    data class Tick(val t:Int,val clock:Boolean,val state:Int?,val outputs:List<Int?>)
    /** Square-wave clock run for a sequential model; samples edges and midpoints in ns. */
    fun runClock(spec:LogicEngine.Sequential,inputs:List<Boolean>,periodNs:Int,cycles:Int):List<Tick>{
        require(periodNs in 10..10000&&cycles in 1..16){"Period 10–10000 ns, 1–16 cycles."}
        require(inputs.size==spec.inputs.size){"Wrong input count."}
        var state=LogicEngine.initial(spec);val ticks=mutableListOf<Tick>()
        ticks+=Tick(0,false,state.bits,LogicEngine.outputs(spec,state,inputs))
        repeat(cycles){c->
            val rise=c*periodNs+periodNs/2;val fall=(c+1)*periodNs
            if(spec.clocked)state=LogicEngine.update(spec,state,inputs,true)
            ticks+=Tick(rise,spec.clocked,state.bits,LogicEngine.outputs(spec,state,inputs))
            if(spec.clocked)state=LogicEngine.update(spec,state,inputs,false)
            ticks+=Tick(fall,false,state.bits,LogicEngine.outputs(spec,state,inputs))
        }
        return ticks
    }
    fun waveRow(ticks:List<Tick>,pick:(Tick)->String):String = ticks.joinToString(" "){pick(it)}
}
