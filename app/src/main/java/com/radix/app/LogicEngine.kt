package com.radix.app

/** Ideal Boolean/edge models only: no propagation delays, analog voltages or metastability. */
internal object LogicEngine {
    enum class Gate { AND, OR, NOT, NAND, NOR, XOR, XNOR, BUFFER }
    data class GateInfo(val gate: Gate, val description: String, val formula: String, val lesson: String)
    data class Node(val id: String, val gate: Gate, val refs: List<String>)
    data class Circuit(val id: String, val title: String, val inputs: List<String>, val outputs: Map<String,String>, val nodes: List<Node>, val formula: List<String>, val lesson: List<String>)
    data class Sequential(val id: String, val title: String, val inputs: List<String>, val width: Int, val clocked: Boolean, val rule: String, val lesson: List<String>)
    data class CircuitResult(val signals: Map<String,Boolean>, val outputs: Map<String,Boolean>)
    data class State(val bits: Int?, val clock: Boolean = false)
    data class Sample(val tick: Int, val clock: Boolean, val inputs: List<Boolean>, val outputs: List<Int?>)
    data class NextRow(val inputs: List<Boolean>, val before: Int, val after: Int?, val forbidden: Boolean)
    fun gate(gate: Gate, inputs: List<Boolean>): Boolean {
        require(inputs.size in 1..4 && ((gate in listOf(Gate.NOT,Gate.BUFFER)) == (inputs.size==1))) { "NOT/BUFFER need one input; other gates need 2–4." }
        return when(gate) {
            Gate.AND -> inputs.all { it }; Gate.OR -> inputs.any { it }
            Gate.NOT -> !inputs[0]; Gate.BUFFER -> inputs[0]
            Gate.NAND -> !inputs.all {it}; Gate.NOR -> !inputs.any {it}
            Gate.XOR -> inputs.count {it}%2==1; Gate.XNOR -> inputs.count {it}%2==0
        }
    }
    fun bits(code: Int, width: Int): List<Boolean> { require(width in 1..9 && code in 0 until (1 shl width)); return List(width) { code and (1 shl (width-1-it))!=0 } }
    fun code(inputs: List<Boolean>): Int = inputs.fold(0) { a,b -> (a shl 1) or if(b)1 else 0 }
    fun binary(value: Int?,width: Int): String = value?.toString(2)?.padStart(width,'0') ?: "X".repeat(width)
    fun evaluate(circuit: Circuit, values: List<Boolean>): CircuitResult {
        require(values.size==circuit.inputs.size)
        require(circuit.inputs.distinct().size==circuit.inputs.size)
        val signals=linkedMapOf<String,Boolean>();circuit.inputs.zip(values).forEach { (name,value)->signals[name]=value }
        circuit.nodes.forEach { node ->
            require(node.id !in signals) { "Duplicate net ${node.id}" }
            val inputs=node.refs.map { signals[it] ?: error("Undefined/forward net $it") }
            signals[node.id]=gate(node.gate,inputs)
        }
        return CircuitResult(signals,circuit.outputs.mapValues { (_,ref)->signals[ref] ?: error("Undefined output $ref") })
    }
    fun defaultInputs(spec: Sequential): List<Boolean> = when(spec.id) {
        "SR_NAND" -> listOf(true,true)
        "D_LATCH" -> listOf(true,false)
        "JK_FF" -> listOf(true,true)
        "T_FF" -> listOf(true)
        "D_FF" -> listOf(true)
        "COUNTER_3", "RING_4", "JOHNSON_4" -> listOf(true,false)
        "SHIFT_4" -> listOf(true,true,false)
        else -> List(spec.inputs.size) {false}
    }
    fun initial(spec: Sequential) = State(if(spec.id=="RING_4")1 else 0)
    fun forbidden(spec: Sequential, inputs: List<Boolean>): Boolean = when(spec.id) { "SR_NOR","SR_FF"->inputs[0]&&inputs[1];"SR_NAND"->!inputs[0]&&!inputs[1];else->false }
    fun update(spec: Sequential, previous: State, inputs: List<Boolean>, clock: Boolean): State {
        require(inputs.size==spec.inputs.size)
        require(previous.bits==null || previous.bits in 0 until (1 shl spec.width))
        val nextClock=if(spec.clocked)clock else false
        if(spec.clocked && (previous.clock || !clock)) return previous.copy(clock=nextClock)
        val q=previous.bits; val a=inputs[0]; val b=inputs.getOrElse(1){false}
        fun sr(set: Boolean,reset: Boolean): Int? = if(set&&reset)null else if(set)1 else if(reset)0 else q
        val next: Int? = when(spec.id) {
            "SR_NOR","SR_FF" -> sr(a,b)
            "SR_NAND" -> sr(!a,!b)
            "D_LATCH" -> if(b)if(a)1 else 0 else q
            "D_FF" -> if(a)1 else 0
            "JK_FF" -> when {a&&b->q?.xor(1);a->1;b->0;else->q}
            "T_FF" -> if(a)q?.xor(1) else q
            "COUNTER_3" -> if(b)0 else if(a)q?.let{(it+1)and 7} else q
            "SHIFT_4" -> if(inputs[2])0 else if(b)q?.let{((it shl 1)or if(a)1 else 0)and 15} else q
            "RING_4" -> if(b)1 else if(a)q?.let{((it shl 1)or(it shr 3))and 15} else q
            "JOHNSON_4" -> if(b)0 else if(a)q?.let{((it shl 1)or((it shr 3)xor 1))and 15} else q
            else -> error("Unknown sequential model ${spec.id}")
        }
        return State(next,nextClock)
    }
    /** Driven outputs during forbidden level-latch inputs are equal, not complementary. */
    fun outputs(spec: Sequential, state: State, inputs: List<Boolean>): List<Int?> {
        if(spec.id=="SR_NOR"&&forbidden(spec,inputs))return listOf(0,0)
        if(spec.id=="SR_NAND"&&forbidden(spec,inputs))return listOf(1,1)
        if(spec.width==1)return listOf(state.bits,state.bits?.xor(1))
        return (spec.width-1 downTo 0).map { bit->state.bits?.let{(it shr bit)and 1} }
    }
    fun nextRows(spec: Sequential): List<NextRow> = (0 until (1 shl spec.inputs.size)).flatMap { code ->
        val inputs=bits(code,spec.inputs.size)
        (0 until (1 shl spec.width)).map { before -> NextRow(inputs,before,update(spec,State(before),inputs,true).bits,forbidden(spec,inputs)) }
    }
}
