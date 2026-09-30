@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
package com.radix.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable internal fun LogicScreen(vm:RadixViewModel,notify:(String)->Unit){
    var chooser by remember {mutableStateOf(false)}
    var saveSession by remember{mutableStateOf(false)}
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=24.dp)){
        Hero("Small circuits.","Big ideas.","THE DIGITAL LOGIC STUDIO","Tap an input. Follow a wire. Understand the output.")
        Row(Modifier.fillMaxWidth().guideAnchor("logic-tabs"),horizontalArrangement=Arrangement.spacedBy(6.dp)){
            listOf("Gates","Combinational","Sequential").forEachIndexed{i,title->
                Surface(onClick={vm.logicSection=i;vm.persistLogic()},modifier=Modifier.weight(1f).heightIn(min=48.dp),shape=RoundedCornerShape(12.dp),color=if(vm.logicSection==i)Lime else MaterialTheme.colorScheme.surfaceVariant,contentColor=if(vm.logicSection==i)Ink else muted()){
                    Box(contentAlignment=Alignment.Center){Text(title,fontSize=10.sp,fontWeight=FontWeight.Bold)}
                }
            }
        }
        val title=when(vm.logicSection){0->vm.gateId;1->LogicCatalog.circuits.first{it.id==vm.circuitId}.title;else->LogicCatalog.sequential.first{it.id==vm.sequentialId}.title}
        OutlinedButton(onClick={chooser=true},modifier=Modifier.fillMaxWidth().padding(top=12.dp).heightIn(min=52.dp).guideAnchor("logic-choice"),shape=RoundedCornerShape(16.dp)){
            Text(title,fontSize=16.sp,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f));Text("Change ▾",fontSize=11.sp)
        }
        Spacer(Modifier.height(12.dp))
        HistoryTools(vm.logicCanUndo,vm.logicCanRedo,vm::undoLogic,vm::redoLogic)
        OutlinedButton(onClick={saveSession=true},modifier=Modifier.fillMaxWidth().heightIn(min=if(vm.largeControls)60.dp else 48.dp),shape=RoundedCornerShape(12.dp)){Text("Save session · ${vm.sessions.size}/24",fontSize=12.sp)}
        when(vm.logicSection){0->GatesPanel(vm,notify);1->CombinationalPanel(vm,notify);else->SequentialPanel(vm,notify)}
        Text("Ideal digital models, plus an optional teaching live-timing layer (fixed gate delays, glitch replay, auto clock). No electrical voltage, setup/hold violation or metastability simulation.",color=muted(),fontSize=10.sp,lineHeight=17.sp,modifier=Modifier.padding(top=18.dp))
    }
    if(saveSession)SaveSessionDialog(vm,notify){saveSession=false}
    if(chooser)ModalBottomSheet(onDismissRequest={chooser=false},containerColor=panel(),sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true)){
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=24.dp)){
            Text("Choose your next aha.",fontFamily=Grotesk,fontSize=26.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(bottom=16.dp))
            val items=when(vm.logicSection){0->LogicCatalog.gates.map{it.gate.name to it.description};1->LogicCatalog.circuits.map{it.id to it.title};else->LogicCatalog.sequential.map{it.id to it.title}}
            items.forEach{(id,description)->
                Surface(onClick={when(vm.logicSection){0->vm.chooseGate(id);1->vm.chooseCircuit(id);else->vm.chooseSequential(id)};chooser=false},shape=RoundedCornerShape(14.dp),color=MaterialTheme.colorScheme.surfaceVariant,modifier=Modifier.fillMaxWidth().padding(bottom=8.dp)){
                    Column(Modifier.padding(16.dp)){Text(if(vm.logicSection==0)id else description,fontSize=14.sp,fontWeight=FontWeight.Bold);if(vm.logicSection==0)Text(description,fontSize=11.sp,color=muted(),modifier=Modifier.padding(top=5.dp))}
                }
            }
        }
    }
}
@Composable private fun LogicCard(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit){
    Surface(shape=RoundedCornerShape(22.dp),color=panel(),border=BorderStroke(1.dp,outline()),modifier=modifier.fillMaxWidth().padding(top=14.dp)) {Column(Modifier.padding(16.dp),content=content)}
}
@Composable private fun BitControls(names:List<String>,values:List<Boolean>,action:(Int)->Unit){
    val haptic=LocalHapticFeedback.current
    FlowRow(Modifier.fillMaxWidth().guideAnchor("logic-inputs"),horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp),maxItemsInEachRow=4){
        names.forEachIndexed{i,name->
            Surface(onClick={action(i);haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)},shape=RoundedCornerShape(14.dp),color=if(values[i])Lime else MaterialTheme.colorScheme.surfaceVariant,contentColor=if(values[i])Ink else MaterialTheme.colorScheme.onSurface,
                modifier=Modifier.widthIn(min=if(LocalLargeStudyControls.current)64.dp else 58.dp).heightIn(min=if(LocalLargeStudyControls.current)78.dp else 64.dp).semantics{contentDescription="Input $name ${if(values[i])1 else 0}. Tap to toggle."}){
                Column(Modifier.padding(horizontal=12.dp,vertical=8.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(name,fontFamily=Mono,fontSize=11.sp);Text(if(values[i])"1"else"0",fontFamily=Mono,fontSize=23.sp,fontWeight=FontWeight.Bold)}
            }
        }
    }
}
@Composable private fun LogicOutputs(names:List<String>,values:List<Int?>){
    FlowRow(Modifier.fillMaxWidth().padding(top=16.dp).guideAnchor("logic-outputs"),horizontalArrangement=Arrangement.spacedBy(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
        names.forEachIndexed{i,name->Column {Text(name,color=muted(),fontFamily=Mono,fontSize=10.sp);Text(values[i]?.toString()?:"X",fontFamily=Mono,fontWeight=FontWeight.Bold,fontSize=30.sp,color=if(values[i]==null)MaterialTheme.colorScheme.secondary else accent())}}
    }
}
@Composable private fun LessonLines(lines:List<String>){lines.forEach {Text(it,color=muted(),fontSize=12.sp,lineHeight=20.sp,modifier=Modifier.padding(top=10.dp))}}
@Composable private fun CopyTable(text:String,notify:(String)->Unit){val clipboard=LocalClipboardManager.current;TextButton(onClick={clipboard.setText(AnnotatedString(text));notify("Complete truth/state table copied.")},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)){Text("Copy complete table",fontSize=12.sp)}}
@Composable private fun TruthGrid(headers:List<String>,rows:List<List<String>>,current:Int,source:String){
    var page by remember(source){mutableIntStateOf(if(current>=0)current/16 else 0)}
    val pages=maxOf(1,(rows.size+15)/16)
    page=page.coerceIn(0,pages-1)
    Column(Modifier.fillMaxWidth().guideAnchor("logic-table")){
        Text("${rows.size} ROWS · ALL INPUT COMBINATIONS",fontSize=9.sp,letterSpacing=1.sp,color=muted(),modifier=Modifier.padding(top=12.dp,bottom=10.dp))
        Column(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())){
            Row {headers.forEach {Text(it,fontFamily=Mono,fontSize=10.sp,color=muted(),modifier=Modifier.width(if(it=="Status")88.dp else if(it.length>4)58.dp else 42.dp).padding(vertical=9.dp))}}
            rows.drop(page*16).take(16).forEachIndexed{i,row->
                val active=current==page*16+i
                Surface(color=if(active)MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,shape=RoundedCornerShape(6.dp)){
                    Row {row.forEachIndexed{j,value->Text(value,fontFamily=Mono,fontSize=11.sp,color=if(active)accent()else MaterialTheme.colorScheme.onSurface,modifier=Modifier.width(if(headers[j]=="Status")88.dp else if(headers[j].length>4)58.dp else 42.dp).padding(vertical=8.dp))}}
                }
            }
        }
        if(pages>1)Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){TextButton(onClick={page--},enabled=page>0){Text("Previous",fontSize=11.sp)};Text("${page+1}/$pages",fontFamily=Mono,fontSize=10.sp,modifier=Modifier.weight(1f));TextButton(onClick={page++},enabled=page<pages-1){Text("Next",fontSize=11.sp)}}
        if(current>=0&&current/16!=page)TextButton(onClick={page=current/16}){Text("Show current input row",fontSize=11.sp)}
    }
}
private fun tableText(title:String,headers:List<String>,rows:List<List<String>>)=title+"\n"+headers.joinToString("\t")+"\n"+rows.joinToString("\n"){it.joinToString("\t")}
@Composable private fun GatesPanel(vm:RadixViewModel,notify:(String)->Unit){
    val g=LogicEngine.Gate.valueOf(vm.gateId);val info=LogicCatalog.gates.first{it.gate==g};val n=vm.gateWidth
    val names=(0 until n).map{('A'+it).toString()};val out=LogicEngine.gate(g,vm.gateInputs)
    val rows=remember(g,n){(0 until(1 shl n)).map{c->val i=LogicEngine.bits(c,n);i.map{if(it)"1"else"0"}+if(LogicEngine.gate(g,i))"1"else"0"}}
    LogicCard {
        Text(info.description,fontSize=13.sp,color=muted())
        if(n>1)Row(Modifier.fillMaxWidth().padding(vertical=12.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){(2..4).forEach{w->OutlinedButton(onClick={vm.chooseGateWidth(w)},modifier=Modifier.weight(1f),border=BorderStroke(if(n==w)2.dp else 1.dp,if(n==w)accent()else outline()),shape=RoundedCornerShape(12.dp)){Text("$w inputs",fontSize=10.sp)}}}else Spacer(Modifier.height(12.dp))
        BitControls(names,vm.gateInputs,vm::toggleGateInput);LogicOutputs(listOf("Y"),listOf(if(out)1 else 0))
        Box(Modifier.fillMaxWidth().guideAnchor("logic-diagram")){GateDiagram(g,vm.gateInputs,out)}
        Text(if(n<=2)info.formula else when(g){LogicEngine.Gate.XOR->"Y = ${names.joinToString(" ⊕ ")}";LogicEngine.Gate.XNOR->"Y = (${names.joinToString(" ⊕ ")})′";else->"${g.name}: ${if(g in listOf(LogicEngine.Gate.AND,LogicEngine.Gate.NAND))"all inputs"else"any input"}${if(g in listOf(LogicEngine.Gate.NAND,LogicEngine.Gate.NOR))", then invert"else""}"},fontFamily=Mono,fontSize=13.sp,color=accent())
        LessonLines(listOf(info.lesson,"Inversion bubbles mean NOT; the extra curved input line identifies XOR/XNOR."))
    }
    LogicCard {
        Text("Truth table",fontSize=20.sp,fontWeight=FontWeight.Bold)
        TruthGrid(names+"Y",rows,LogicEngine.code(vm.gateInputs),"gate-${g.name}-$n")
        CopyTable(tableText("${g.name} · $n inputs",names+"Y",rows),notify)
        if(n in 2..4)TextButton(onClick={vm.logicToKMap(n,rows.map{it.last().toInt()})},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)){Text("Simplify this gate in K-maps",fontSize=12.sp)}
    }
}
@Composable private fun CombinationalPanel(vm:RadixViewModel,notify:(String)->Unit){
    val c=LogicCatalog.circuits.first{it.id==vm.circuitId};val result=remember(c,vm.circuitInputs){LogicEngine.evaluate(c,vm.circuitInputs)}
    var expanded by remember(c.id){mutableStateOf(true)};var output by remember(c.id){mutableStateOf(c.outputs.keys.first())}
    val headers=c.inputs+c.outputs.keys
    val rows=remember(c.id){(0 until(1 shl c.inputs.size)).map{code->val i=LogicEngine.bits(code,c.inputs.size);i.map{if(it)"1"else"0"}+LogicEngine.evaluate(c,i).outputs.values.map{if(it)"1"else"0"}}}
    LogicCard {
        Text("No memory: outputs follow the current inputs.",color=muted(),fontSize=12.sp,lineHeight=19.sp,modifier=Modifier.padding(bottom=12.dp))
        BitControls(c.inputs,vm.circuitInputs,vm::toggleCircuitInput);LogicOutputs(c.outputs.keys.toList(),result.outputs.values.map{if(it)1 else 0})
        TextButton(onClick={expanded=!expanded},modifier=Modifier.heightIn(min=48.dp)){Text(if(expanded)"Switch to phone-friendly block view"else"Explore the gate-level diagram",fontSize=11.sp)}
        Box(Modifier.fillMaxWidth().guideAnchor("logic-diagram")){CircuitDiagram(c,result,expanded)}
        if(expanded)Text("Swipe ↔ to follow all gates. Crossing wires connect only at dotted pin junctions.",fontSize=10.sp,color=muted(),lineHeight=17.sp)
        c.formula.forEach{Text(it,fontFamily=Mono,fontSize=12.sp,color=accent(),modifier=Modifier.padding(top=8.dp))};LessonLines(c.lesson)
    }
    LogicCard {
        Text("Truth table",fontSize=20.sp,fontWeight=FontWeight.Bold)
        TruthGrid(headers,rows,LogicEngine.code(vm.circuitInputs),c.id);CopyTable(tableText(c.title,headers,rows),notify)
        if(c.inputs.size in 2..4){
            Text("K-map variables: ${c.inputs.mapIndexed{i,name->"${'A'+i}=$name"}.joinToString(", ")}",fontSize=10.sp,color=muted())
            Row(Modifier.horizontalScroll(rememberScrollState())){c.outputs.keys.forEach{name->FilterChip(selected=name==output,onClick={output=name},label={Text(name,fontSize=11.sp)},modifier=Modifier.padding(end=6.dp))}}
            TextButton(onClick={val index=c.outputs.keys.indexOf(output);vm.logicToKMap(c.inputs.size,rows.map{it[c.inputs.size+index].toInt()})},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)){Text("Open $output in K-maps",fontSize=12.sp)}
        }else Text("This circuit has more than four inputs; K-maps support up to four variables.",fontSize=10.sp,color=muted(),lineHeight=17.sp)
    }
}
@Composable private fun SequentialPanel(vm:RadixViewModel,notify:(String)->Unit){
    val s=LogicCatalog.sequential.first{it.id==vm.sequentialId};val state=vm.sequenceState;val values=LogicEngine.outputs(s,state,vm.sequentialInputs)
    var expanded by remember(s.id){mutableStateOf(true)}
    val names=if(s.width==1)listOf("Q","Q′")else(s.width-1 downTo 0).map{"Q$it"}
    val rawRows=remember(s.id){LogicEngine.nextRows(s)}
    val rows=remember(s.id){rawRows.map{row->row.inputs.map{if(it)"1"else"0"}+listOf(LogicEngine.binary(row.before,s.width),LogicEngine.binary(row.after,s.width),if(row.forbidden)"forbidden"else"valid")}}
    val headers=s.inputs+listOf("Q(now)","Q(next)","Status")
    LogicCard {
        Text(if(s.clocked)"EDGE-TRIGGERED · ↑CLK"else"LEVEL-SENSITIVE · NO CLOCK",fontSize=10.sp,color=accent(),letterSpacing=.6.sp,modifier=Modifier.padding(bottom=12.dp))
        BitControls(s.inputs,vm.sequentialInputs,vm::toggleSequentialInput);LogicOutputs(names,values)
        if(s.clocked)Column(Modifier.fillMaxWidth().padding(top=12.dp).guideAnchor("logic-clock")){
            Text("CLK = ${if(state.clock)1 else 0}  ·  memory ${LogicEngine.binary(state.bits,s.width)}",fontFamily=Mono,fontSize=12.sp,color=accent())
            Row(Modifier.fillMaxWidth().padding(top=10.dp),horizontalArrangement=Arrangement.spacedBy(6.dp)){
                OutlinedButton(onClick={vm.setSequenceClock(true)},enabled=!state.clock,modifier=Modifier.weight(1f),shape=RoundedCornerShape(12.dp)){Text("↑ Rise",fontSize=11.sp)}
                OutlinedButton(onClick={vm.setSequenceClock(false)},enabled=state.clock,modifier=Modifier.weight(1f),shape=RoundedCornerShape(12.dp)){Text("↓ Fall",fontSize=11.sp)}
                Button(onClick=vm::pulseSequence,modifier=Modifier.weight(1f),shape=RoundedCornerShape(12.dp)){Text("Pulse",fontSize=11.sp)}
            }
            LiveClockRow(vm,s)
        }
        if(state.bits==null)Text(if(LogicEngine.forbidden(s,vm.sequentialInputs)&&!s.clocked)"Forbidden inputs force equal physical outputs. Stored memory is indeterminate; a valid set/reset recovers it."else"Memory is indeterminate (X). Use a valid set/reset or capture a known D value to recover.",fontSize=12.sp,color=MaterialTheme.colorScheme.error,lineHeight=19.sp,modifier=Modifier.padding(top=14.dp))
        TextButton(onClick={expanded=!expanded},modifier=Modifier.heightIn(min=48.dp)){Text(if(expanded)"Switch to phone-friendly block view"else"See inside the circuit",fontSize=11.sp)}
        if(expanded)Text("Inside view: grey nets are structure-only; colored nets carry live values. Crossing wires connect only at dotted pin junctions.",fontSize=10.sp,color=muted(),lineHeight=17.sp)
        if(s.id in listOf("SR_NOR","SR_NAND")){
            OutlinedButton(onClick=vm::releaseLatchInputs,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)){Text("Release both to hold",fontSize=12.sp)}
            Text("Models simultaneous release. Changing only one input first can recover a known set/reset state.",color=muted(),fontSize=10.sp,lineHeight=17.sp,modifier=Modifier.padding(top=7.dp))
        }
        Box(Modifier.fillMaxWidth().guideAnchor("logic-diagram")){SequentialDiagram(s,state,vm.sequentialInputs,values,expanded)}
        Text(s.rule,fontFamily=Mono,fontSize=12.sp,color=accent(),lineHeight=20.sp)
        LessonLines(s.lesson)
        TextButton(onClick=vm::resetSequence,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)){Text("Initialize simulation",fontSize=12.sp)}
        Text("Initialization sets a known teaching state; it does not model real hardware power-up.",fontSize=10.sp,color=muted(),lineHeight=17.sp)
    }
    LogicCard(Modifier.guideAnchor("logic-timing")){
        Text("See memory over time",fontWeight=FontWeight.Bold,fontSize=18.sp)
        Text("Each column is an action sample, not elapsed time. Swipe ↔ for the latest 12 samples. X means unknown.",fontSize=11.sp,color=muted(),lineHeight=18.sp,modifier=Modifier.padding(top=10.dp,bottom=8.dp))
        TimingDiagram(s,vm.sequenceHistory)
    }
    LogicCard {
        Text("Next-state table",fontSize=20.sp,fontWeight=FontWeight.Bold)
        Text(if(s.clocked)"Q(next) applies on a rising edge. At other times Q holds."else"Level-sensitive: enabled inputs can change Q immediately.",fontSize=11.sp,color=muted(),lineHeight=18.sp,modifier=Modifier.padding(top=8.dp))
        TruthGrid(headers,rows,-1,"state-${s.id}");CopyTable(tableText(s.title+" · "+if(s.clocked)"on rising edge"else"level-sensitive",headers,rows),notify)
    }
}
@Composable private fun LiveClockRow(vm:RadixViewModel,s:LogicEngine.Sequential){
    LaunchedEffect(vm.livePlaying,vm.liveSpeed){while(vm.livePlaying){delay(1000L/vm.liveSpeed);vm.pulseSequence()}}
    val ticks=remember(s.id,vm.sequentialInputs,vm.livePeriodNs,vm.sequenceHistory){LiveSim.runClock(s,vm.sequentialInputs,vm.livePeriodNs,2)}
    Column(Modifier.fillMaxWidth().padding(top=12.dp)){
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){
            Button(onClick={vm.livePlaying=!vm.livePlaying},modifier=Modifier.weight(1f).heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)){Text(if(vm.livePlaying)"⏸ Stop live clock"else"▶ Live clock",fontSize=11.sp)}
            listOf(1,2,4).forEach{speed->OutlinedButton(onClick={vm.chooseLiveSpeed(speed)},border=BorderStroke(if(vm.liveSpeed==speed)2.dp else 1.dp,if(vm.liveSpeed==speed)accent()else outline()),shape=RoundedCornerShape(12.dp),modifier=Modifier.heightIn(min=48.dp)){Text("${speed}×",fontSize=11.sp)}}
        }
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.padding(top=8.dp)){
            Text("Period",fontSize=11.sp,color=muted())
            listOf(100,200,1000).forEach{p->OutlinedButton(onClick={vm.setLivePeriod(p)},border=BorderStroke(if(vm.livePeriodNs==p)2.dp else 1.dp,if(vm.livePeriodNs==p)accent()else outline()),shape=RoundedCornerShape(12.dp),modifier=Modifier.heightIn(min=44.dp)){Text("${p}ns",fontSize=10.sp)}}
        }
        Text("Nanosecond timeline · Q sampled at edges",fontFamily=Mono,fontSize=10.sp,color=muted(),modifier=Modifier.padding(top=10.dp))
        ticks.forEach{t->Text("t=${t.t}ns  CLK=${if(t.clock)1 else 0}  Q=${t.outputs.joinToString(""){it?.toString()?:"X"}}",fontFamily=Mono,fontSize=11.sp,lineHeight=19.sp)}
        Text("The auto clock pulses this same ideal model; the diagram above grows live. Teaching delays only.",fontSize=10.sp,color=muted(),lineHeight=16.sp,modifier=Modifier.padding(top=6.dp))
    }
}
@Composable internal fun MoreScreen(vm:RadixViewModel,help:()->Unit){
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=24.dp)){
        Hero("A little extra.","A lot to explore.","YOUR LEARNING TOOLBOX","Everything offline. Everything within a tap.")
        val entries=listOf(
            Triple("Circuit builder","Drag gates, wire them, simulate with delays",{vm.tab=13}),
            Triple("IEEE-754 float lab","Sign, exponent, fraction — every float decoded",{vm.tab=14}),
            Triple("Leaderboard & rewards","Level ${vm.gameLevel} · ${vm.gameXp} XP · ${vm.gameProfiles.size} players",{vm.tab=15}),
            Triple("Boolean expression lab","Tables, SOP/POS and expression equivalence",{vm.tab=6}),
            Triple("Boolean laws lab","Every law verified live on every row",{vm.tab=11}),
            Triple("Base arithmetic lab","+ − × ÷ in any base, with column working",{vm.tab=12}),
            Triple("Number representations","One’s & two’s complement, widths, Gray and BCD",{vm.tab=7}),
            Triple("Logic practice hub","${PracticeCatalog.questions.size} leveled cards · ${vm.practiceProgress.size} mastered",{vm.tab=8}),
            Triple("Circuit checkpoints","${vm.sessions.size} named inputs/clock/memory sessions",{vm.tab=9}),
            Triple("Saved conversions","${vm.saved.size} saved numbers",{vm.tab=1}),
            Triple("The 8-bit playground","Build a byte, one switch at a time",{vm.tab=2}),
            Triple("Settings, reminders & backup","Theme, language, notifications and portable study data",{vm.tab=10}),
            Triple("Visual helper & app tour","Illustrated lessons and replayable highlights",help)
        )
        BoxWithConstraints(Modifier.fillMaxWidth()){
            val wide=maxWidth>=760.dp
            @Composable fun card(index:Int,title:String,detail:String,action:()->Unit,modifier:Modifier){
                Surface(onClick=action,shape=RoundedCornerShape(18.dp),color=panel(),border=BorderStroke(1.dp,outline()),modifier=modifier.padding(bottom=12.dp).heightIn(min=86.dp).then(if(index==entries.size-1)Modifier.guideAnchor("more-help")else Modifier)){
                    Row(Modifier.padding(18.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(title,fontWeight=FontWeight.Bold,fontSize=15.sp);Text(detail,color=muted(),fontSize=11.sp,lineHeight=17.sp,modifier=Modifier.padding(top=5.dp))};Text("→",color=accent(),fontSize=24.sp)}
                }
            }
            if(wide)entries.withIndex().chunked(2).forEach{row->Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){row.forEach{(i,e)->card(i,e.first,e.second,e.third,Modifier.weight(1f))};if(row.size<2)Spacer(Modifier.weight(1f))}}
            else entries.forEachIndexed{i,(title,detail,action)->card(i,title,detail,action,Modifier.fillMaxWidth())}
        }
        Text("Radix 6.0 · no ads, accounts or Internet permission",color=muted(),fontSize=11.sp,modifier=Modifier.padding(top=12.dp))
    }
}
