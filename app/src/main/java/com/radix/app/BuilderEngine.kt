package com.radix.app

/** Drag-and-drop builder model: pure placement/wiring/validation/evaluation. Feedback-free only. */
internal object BuilderEngine {
    const val EXT_IN=-1;const val PROBE=-2;const val MAX_GATES=12;const val MAX_WIRES=28
    data class Placed(val id:Int,val gate:LogicEngine.Gate,val x:Float,val y:Float)
    data class Wire(val fromId:Int,val fromPin:Int,val toId:Int,val toPin:Int)
    data class Board(val gates:List<Placed>,val wires:List<Wire>,val ext:List<Boolean>)
    data class Eval(val gateOut:Map<Int,Boolean>,val probe:Boolean,val arrival:Map<Int,Int>,val probeNs:Int,val order:List<Int>)
    data class Table(val inputs:List<Int>,val rows:List<Pair<List<Boolean>,Boolean>>)
    fun arity(gate:LogicEngine.Gate):Int = if(gate==LogicEngine.Gate.NOT||gate==LogicEngine.Gate.BUFFER)1 else 2
    fun inputName(index:Int):String{require(index in 0..3){"External inputs are A–D."};return ('A'+index).toString()}
    /** Human-readable problems; empty means the board simulates. */
    fun validate(board:Board):List<String>{
        val problems=mutableListOf<String>()
        if(board.gates.size>MAX_GATES)problems+="Too many gates (maximum $MAX_GATES)."
        if(board.wires.size>MAX_WIRES)problems+="Too many wires (maximum $MAX_WIRES)."
        if(board.ext.size!=4)problems+="Four external inputs A–D are required."
        val ids=board.gates.map{it.id}
        if(ids.distinct().size!=ids.size)problems+="Duplicate gate placement."
        if(ids.any{it<0})problems+="Gate IDs must be non-negative."
        board.wires.forEachIndexed{i,w->
            when(w.fromId){
                EXT_IN->if(w.fromPin !in 0..3)problems+="Wire ${i+1}: external input must be A–D."
                PROBE->problems+="Wire ${i+1}: the probe is an output — it cannot drive a wire."
                else->{if(w.fromId !in ids)problems+="Wire ${i+1}: unknown source gate.";if(w.fromPin!=0)problems+="Wire ${i+1}: gates have one output pin."}
            }
            when(w.toId){
                PROBE->if(w.toPin!=0)problems+="Wire ${i+1}: the probe has one input."
                EXT_IN->problems+="Wire ${i+1}: external inputs cannot be driven."
                else->{val g=board.gates.firstOrNull{it.id==w.toId};if(g==null)problems+="Wire ${i+1}: unknown target gate.";else if(w.toPin !in 0 until arity(g.gate))problems+="Wire ${i+1}: pin out of range for ${g.gate}."}
            }
        }
        val targets=board.wires.groupBy{it.toId to it.toPin}
        targets.forEach{(key,list)->if(list.size>1){val (id,pin)=key;problems+=(if(id==PROBE)"The probe has ${list.size} drivers; keep exactly one." else "Gate $id pin ${pin+1} has ${list.size} drivers; keep exactly one.")}}
        board.gates.forEach{g->(0 until arity(g.gate)).forEach{p->if((g.id to p) !in targets)problems+="${g.gate} gate ${g.id} pin ${p+1} is unwired."}}
        if(board.wires.none{it.toId==PROBE})problems+="Wire one signal into the probe (Y) to observe an output."
        if(problems.isEmpty()&&hasCycle(board))problems+="Feedback loop detected — latches and flip-flops live in the Sequential tab."
        return problems
    }
    private fun dependencies(board:Board):Map<Int,List<Int>>{
        val incoming=board.wires.filter{it.toId>=0}.groupBy({it.toId},{it.fromId})
        return board.gates.associate{it.id to (incoming[it.id]?.filter{src->src>=0}?.distinct()?:emptyList())}
    }
    private fun hasCycle(board:Board):Boolean{
        val deps=dependencies(board)
        val remaining=deps.mapValues{it.value.toMutableList()}.toMutableMap();val done=mutableSetOf<Int>();var progress=true
        while(progress){progress=false;remaining.forEach{(id,needs)->if(id !in done&&needs.all{it in done}){done+=id;progress=true}}}
        return done.size<board.gates.size
    }
    fun topoOrder(board:Board):List<Int>{
        val deps=dependencies(board);val order=mutableListOf<Int>();val done=mutableSetOf<Int>()
        while(order.size<board.gates.size){val next=board.gates.map{it.id}.firstOrNull{it !in done&&deps[it].orEmpty().all{d->d in done}}?:error("Feedback loop");done+=next;order+=next}
        return order
    }
    fun evaluate(board:Board):Eval{
        val problems=validate(board);require(problems.isEmpty()){problems.first()}
        val incoming=board.wires.groupBy({it.toId to it.toPin},{it.fromId to it.fromPin})
        val out=mutableMapOf<Int,Boolean>();val arrival=mutableMapOf<Int,Int>();val order=topoOrder(board)
        fun signal(src:Pair<Int,Int>):Pair<Boolean,Int> = if(src.first==EXT_IN)board.ext[src.second] to 0 else out[src.first]!! to arrival[src.first]!!
        order.forEach{id->
            val g=board.gates.first{it.id==id};val pins=(0 until arity(g.gate)).map{signal(incoming[id to it]!!.single())}
            out[id]=LogicEngine.gate(g.gate,pins.map{it.first});arrival[id]=(pins.maxOfOrNull{it.second}?:0)+LiveSim.delayNs(g.gate)
        }
        val probeSrc=incoming[PROBE to 0]!!.single();val (value,ns)=signal(probeSrc)
        return Eval(out,value,arrival,ns,order)
    }
    fun usedInputs(board:Board):List<Int> = board.wires.filter{it.fromId==EXT_IN}.map{it.fromPin}.distinct().sorted()
    fun truthTable(board:Board):Table{
        val problems=validate(board).filterNot{it.startsWith("Wire one signal")};require(problems.isEmpty()){problems.first()}
        require(board.wires.any{it.toId==PROBE}){"Wire the probe first."}
        val used=usedInputs(board);val rows=(0 until (1 shl used.size)).map{code->
            val values=List(4){i->if(i in used)LogicEngine.bits(code,used.size)[used.indexOf(i)] else false}
            values.filterIndexed{i,_->i in used} to evaluate(board.copy(ext=values)).probe
        }
        return Table(used,rows)
    }
    // ---- Presets (positions are canvas fractions 0..1) ----
    private fun preset(gates:List<LogicEngine.Gate>,wires:List<Wire>):Board{
        // Keep the preset gates between the fixed input strip and the output probe.
        // Two columns also leave enough horizontal room on the portrait canvas.
        val rows=(gates.size+1)/2
        val placed=gates.mapIndexed{i,g->
            val x=if(i%2==0)0.28f else 0.72f
            val y=if(rows==1)0.5f else if(i/2==0)0.28f else 0.68f
            Placed(i,g,x,y)
        }
        return Board(placed,wires,List(4){false})
    }
    /** Add a gate in an unused canvas slot without covering the fixed I/O lanes. */
    fun addGate(board:Board,gate:LogicEngine.Gate):Board{
        require(board.gates.size<MAX_GATES){"The builder is limited to $MAX_GATES gates."}
        val id=(board.gates.maxOfOrNull{it.id}?:-1)+1
        val usedSlots=board.gates.map{((it.id%MAX_GATES)+MAX_GATES)%MAX_GATES}.toSet()
        val slot=(0 until MAX_GATES).firstOrNull{it !in usedSlots}?:error("No free gate slot.")
        val x=listOf(0.18f,0.5f,0.82f)[slot%3]
        // Four 104 dp rows fit between the 56 dp input and probe lanes on a 560 dp canvas.
        val y=listOf(0.2f,0.4f,0.6f,0.8f)[slot/3]
        return board.copy(gates=board.gates+Placed(id,gate,x,y))
    }
    /** XOR from four NANDs: classic teaching build. Inputs A,B; probe Y. */
    fun presetXorFromNand():Board = preset(listOf(LogicEngine.Gate.NAND,LogicEngine.Gate.NAND,LogicEngine.Gate.NAND,LogicEngine.Gate.NAND),listOf(
        Wire(EXT_IN,0,0,0),Wire(EXT_IN,1,0,1),Wire(EXT_IN,0,1,0),Wire(0,0,1,1),Wire(EXT_IN,1,2,0),Wire(0,0,2,1),Wire(1,0,3,0),Wire(2,0,3,1),Wire(3,0,PROBE,0)))
    /** Half adder: XOR sum + AND carry. Probe shows Sum; carry is gate 1's output. */
    fun presetHalfAdder():Board = preset(listOf(LogicEngine.Gate.XOR,LogicEngine.Gate.AND),listOf(
        Wire(EXT_IN,0,0,0),Wire(EXT_IN,1,0,1),Wire(EXT_IN,0,1,0),Wire(EXT_IN,1,1,1),Wire(0,0,PROBE,0)))
    /** 2:1 MUX from NOT/AND/OR: S=A selects D0=B, D1=C. */
    fun presetMux():Board = preset(listOf(LogicEngine.Gate.NOT,LogicEngine.Gate.AND,LogicEngine.Gate.AND,LogicEngine.Gate.OR),listOf(
        Wire(EXT_IN,0,0,0),Wire(0,0,1,0),Wire(EXT_IN,1,1,1),Wire(EXT_IN,0,2,0),Wire(EXT_IN,2,2,1),Wire(1,0,3,0),Wire(2,0,3,1),Wire(3,0,PROBE,0)))
    fun presets():List<Pair<String,Board>> = listOf("XOR from 4 NANDs" to presetXorFromNand(),"Half adder (probe = Sum)" to presetHalfAdder(),"2:1 MUX from gates" to presetMux())
}
