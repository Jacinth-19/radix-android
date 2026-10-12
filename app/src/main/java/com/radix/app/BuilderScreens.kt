package com.radix.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable private fun LabPage(first:String,second:String,eyebrow:String,subtitle:String,body:@Composable ColumnScope.()->Unit){
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=24.dp),horizontalAlignment=Alignment.CenterHorizontally){
        Column(Modifier.widthIn(max=980.dp).fillMaxWidth()){Hero(first,second,eyebrow,subtitle);body()}
    }
}
@Composable private fun LabCard(title:String,body:@Composable ColumnScope.()->Unit){
    Surface(shape=Round,color=panel(),border=BorderStroke(1.dp,outline()),modifier=Modifier.fillMaxWidth().padding(bottom=16.dp)){
        Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(11.dp)){if(title.isNotBlank())Text(title,fontSize=19.sp,fontWeight=FontWeight.Bold,fontFamily=Grotesk);body()}
    }
}
@Composable private fun LabNote(text:String,error:Boolean=false){Text(text,color=if(error)MaterialTheme.colorScheme.error else muted(),fontSize=12.sp,lineHeight=19.sp)}

// ---------------- Circuit builder ----------------
private val GATE_W=152.dp;private val GATE_H=104.dp;private val PIN=48.dp;private val PIN_GAP=4.dp
private fun pinCenters(count:Int,cy:Float,pinPx:Float,gapPx:Float):List<Float>{
    val total=count*pinPx+(count-1)*gapPx;val top=cy-total/2
    return (0 until count).map{top+pinPx/2+it*(pinPx+gapPx)}
}

@Composable internal fun BuilderScreen(vm:RadixViewModel,notify:(String)->Unit){
    val haptic=LocalHapticFeedback.current;val clipboard=LocalClipboardManager.current;val strings=LocalStrings.current
    val board=vm.builderBoard
    val problems=remember(board){BuilderEngine.validate(board)}
    val eval=remember(board){runCatching{BuilderEngine.evaluate(board)}.getOrNull()}
    var pulse by remember(board.gates,board.wires,board.ext){mutableStateOf<Int?>(null)}
    val settle=pulse?.let{runCatching{LiveSim.settle(board,it)}.getOrNull()}
    LabPage("Place gates.","Wire the idea.","THE CIRCUIT BUILDER","Drag, connect, simulate — feedback-free, always explainable."){
        LabNote(strings.landscapeBuilderHint)
        LabCard("Start from a working build"){
            BuilderEngine.presets().forEachIndexed{i,(name,_)->
                OutlinedButton(onClick={vm.builderLoadPreset(i);pulse=null;haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp),border=BorderStroke(if(vm.builderPreset==i)2.dp else 1.dp,if(vm.builderPreset==i)accent()else outline()),shape=RoundedCornerShape(12.dp)){Text(name,fontSize=12.sp)}
            }
            OutlinedButton(onClick={vm.builderClear();pulse=null},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)){Text("Clear the canvas",fontSize=12.sp)}
        }
        LabCard("Gate palette · tap to place"){
            val atLimit=board.gates.size>=BuilderEngine.MAX_GATES
            FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                LogicEngine.Gate.entries.forEach{g->
                    Button(onClick={vm.builderAdd(g);pulse=null;haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)},enabled=!atLimit,shape=RoundedCornerShape(12.dp),modifier=Modifier.heightIn(min=48.dp)){Text(g.name,fontSize=11.sp)}
                }
            }
            LabNote("Binary gates take 2 inputs here; chain them for wider functions. NOT and BUFFER take 1.")
            if(atLimit)LabNote("Maximum 12 gates; remove one to make room.")
        }
        LabCard("Canvas · ${board.gates.size}/${BuilderEngine.MAX_GATES} gates"){
            Row(verticalAlignment=Alignment.CenterVertically){
                Column(Modifier.weight(1f)){Text(if(vm.builderWiring)"Wiring mode: ON"else"Wiring mode: OFF",fontSize=14.sp,fontWeight=FontWeight.Bold);LabNote(if(vm.builderWiring)"Tap a glowing output, then an input (or the probe)."else"Drag gates to arrange. Tap an input pin to unwire it.")}
                Switch(checked=vm.builderWiring,onCheckedChange={vm.builderSetWiring(it)},modifier=Modifier.semantics{contentDescription=strings.text("Toggle wiring mode")})
            }
            BuilderCanvas(vm,eval,notify)
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                (0 until 4).forEach{i->
                    val on=board.ext[i]
                    Surface(onClick={vm.builderToggleExt(i);pulse=null;haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)},shape=RoundedCornerShape(12.dp),color=if(on)Lime else MaterialTheme.colorScheme.surfaceVariant,contentColor=if(on)Ink else MaterialTheme.colorScheme.onSurface,modifier=Modifier.weight(1f).heightIn(min=56.dp).semantics{contentDescription=strings.text("External input ${BuilderEngine.inputName(i)}, ${if(on)1 else 0}. Tap to toggle.")}){
                        Column(Modifier.padding(6.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Text(BuilderEngine.inputName(i),fontFamily=Mono,fontSize=11.sp);Text(if(on)"1"else"0",fontFamily=Mono,fontSize=20.sp,fontWeight=FontWeight.Bold)}
                    }
                }
            }
        }
        if(problems.isNotEmpty())LabCard("Finish the wiring"){problems.forEach{LabNote("• $it",true)}}
        eval?.let{e->
            LabCard("Live simulation"){
                Text("Probe Y = ${if(e.probe)1 else 0}",fontFamily=Mono,fontSize=30.sp,fontWeight=FontWeight.Bold,color=accent())
                LabNote("Critical path: ${e.probeNs} ns through ${e.order.size} gate${if(e.order.size==1)""else"s"}. Delays are teaching values (NOT 2 ns, NAND/NOR 3 ns, AND/OR 4 ns, XOR/XNOR 6 ns), not silicon measurements.")
                e.order.forEach{id->val g=board.gates.first{it.id==id};Text("${g.gate} #${id+1} → ${if(e.gateOut[id]==true)1 else 0} @ ${e.arrival[id]} ns",fontFamily=Mono,fontSize=11.sp,color=muted())}
            }
            LabCard("Pulse an input · watch for glitches"){
                val used=BuilderEngine.usedInputs(board)
                if(used.isEmpty())LabNote("Wire an external input first.")
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){used.forEach{i->OutlinedButton(onClick={pulse=i},modifier=Modifier.weight(1f).heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)){Text("Pulse ${BuilderEngine.inputName(i)}",fontSize=11.sp)}}}
                settle?.let{s->
                    Text(if(s.glitched)"⚡ Glitch: the probe flickers before settling."else"Clean: the probe changes once, then holds.",color=if(s.glitched)MaterialTheme.colorScheme.error else accent(),fontSize=13.sp,fontWeight=FontWeight.Bold)
                    Text("Settles in ${s.settleNs} ns · final Y = ${if(s.final)1 else 0}",fontFamily=Mono,fontSize=11.sp,color=muted())
                    s.events.forEach{Text("t=${it.t} ns  ${it.label} → ${if(it.value)1 else 0}",fontFamily=Mono,fontSize=11.sp,lineHeight=18.sp)}
                }
            }
            LabCard("Your circuit's truth table"){
                val table=remember(board){runCatching{BuilderEngine.truthTable(board)}.getOrNull()}
                if(table==null)LabNote("Complete the wiring to generate the table.")
                else{
                    Text("${table.rows.size} rows over ${table.inputs.joinToString{BuilderEngine.inputName(it)}}",fontFamily=Mono,fontSize=11.sp,color=muted())
                    Column(Modifier.horizontalScroll(rememberScrollState())){table.rows.forEach{(ins,out)->Text(ins.joinToString("  "){if(it)"1"else"0"}+"   →   ${if(out)1 else 0}",fontFamily=Mono,fontSize=12.sp,lineHeight=20.sp)}}
                    TextButton(onClick={clipboard.setText(AnnotatedString(AppStrings.translateBlock("Builder truth table\n"+table.inputs.joinToString("\t"){BuilderEngine.inputName(it)}+"\tY\n"+table.rows.joinToString("\n"){(ins,out)->ins.joinToString("\t"){if(it)"1"else"0"}+"\t${if(out)1 else 0}"},strings.languageCode)));notify("Truth table copied.")},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)){Text("Copy table",fontSize=12.sp)}
                }
            }
        }
    }
}

@Composable private fun BuilderCanvas(vm:RadixViewModel,eval:BuilderEngine.Eval?,notify:(String)->Unit){
    val strings=LocalStrings.current
    val haptic=LocalHapticFeedback.current;val density=LocalDensity.current
    BoxWithConstraints(Modifier.fillMaxWidth().height(560.dp).clip(RoundedCornerShape(18.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.45f))) {
        val canvasWidth=maxOf(maxWidth,520.dp)
        val wPx=with(density){canvasWidth.toPx()};val hPx=with(density){maxHeight.toPx()}
        val gwPx=with(density){GATE_W.toPx()};val ghPx=with(density){GATE_H.toPx()};val pinPx=with(density){PIN.toPx()};val gapPx=with(density){PIN_GAP.toPx()}
        val minGateX=(gwPx/2f/wPx).coerceIn(0f,0.5f);val maxGateX=1f-minGateX
        val minGateY=(with(density){60.dp.toPx()}+ghPx/2f)/hPx;val maxGateY=1f-minGateY
        Row(Modifier.fillMaxSize().horizontalScroll(rememberScrollState())) {
            Box(Modifier.width(canvasWidth).fillMaxHeight()) {
                val board=vm.builderBoard
                fun gateCenter(g:BuilderEngine.Placed)=Offset(g.x.coerceIn(minGateX,maxGateX)*wPx,g.y.coerceIn(minGateY,maxGateY)*hPx)
        fun extDot(i:Int)=Offset((i+0.5f)/4*wPx,56.dp.value*density.density)
        val probeDot=Offset(wPx/2,hPx-56.dp.value*density.density)
        fun outDot(id:Int):Offset = if(id==BuilderEngine.EXT_IN)Offset.Zero else {val c=gateCenter(board.gates.first{it.id==id});Offset(c.x+gwPx/2,c.y)}
        fun inDot(id:Int,pin:Int):Offset{
            if(id==BuilderEngine.PROBE)return probeDot
            val g=board.gates.first{it.id==id};val c=gateCenter(g)
            return Offset(c.x-gwPx/2,pinCenters(BuilderEngine.arity(g.gate),c.y,pinPx,gapPx)[pin])
        }
        fun signal(id:Int,pin:Int):Boolean? = when(id){BuilderEngine.EXT_IN->board.ext.getOrNull(pin);BuilderEngine.PROBE->eval?.probe;else->eval?.gateOut?.get(id)}
        val lime=if(MaterialTheme.colorScheme.background==Ink)Lime else accent()
        Canvas(Modifier.fillMaxSize()){
            board.wires.forEach{w->
                val a=if(w.fromId==BuilderEngine.EXT_IN)extDot(w.fromPin)else outDot(w.fromId)
                val b=inDot(w.toId,w.toPin);val live=signal(w.fromId,w.fromPin)==true&&eval!=null
                drawLine(if(live)lime else Color.Gray,a,b,strokeWidth=3.dp.toPx())
                drawCircle(if(live)lime else Color.Gray,4.dp.toPx(),a);drawCircle(if(live)lime else Color.Gray,4.dp.toPx(),b)
            }
        }
        // External inputs (fixed top row).
        (0 until 4).forEach{i->
            val pending=vm.builderPending==Pair(BuilderEngine.EXT_IN,i)
            Surface(onClick={
                if(vm.builderWiring){vm.builderTapSource(BuilderEngine.EXT_IN,i)}
                else{vm.builderToggleExt(i)};haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },shape=RoundedCornerShape(10.dp),color=if(board.ext[i])Lime else panel(),contentColor=if(board.ext[i])Ink else MaterialTheme.colorScheme.onSurface,
                border=BorderStroke(if(pending)3.dp else 1.dp,if(pending)accent()else outline()),
                modifier=Modifier.offset{IntOffset(((i+0.5f)/4*wPx-32.dp.value*density.density).roundToInt(),(8.dp.value*density.density).roundToInt())}.size(64.dp,48.dp).semantics{contentDescription=strings.text("External input ${BuilderEngine.inputName(i)}, ${if(board.ext[i])1 else 0}")}){
                Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Text(BuilderEngine.inputName(i),fontFamily=Mono,fontSize=10.sp);Text(if(board.ext[i])"1"else"0",fontFamily=Mono,fontSize=16.sp,fontWeight=FontWeight.Bold)}
            }
        }
        // Gates (draggable).
        board.gates.forEach{g->
            val arity=BuilderEngine.arity(g.gate);val out=eval?.gateOut?.get(g.id)
            val center=gateCenter(g)
            Box(Modifier.offset{IntOffset((center.x-gwPx/2).roundToInt(),(center.y-ghPx/2).roundToInt())}.size(GATE_W,GATE_H)
                .pointerInput(g.id,wPx,hPx){detectDragGestures(onDrag={change,amount->change.consume();val cur=vm.builderBoard.gates.first{it.id==g.id};val current=gateCenter(cur);vm.builderMove(g.id,((current.x+amount.x)/wPx).coerceIn(minGateX,maxGateX),((current.y+amount.y)/hPx).coerceIn(minGateY,maxGateY))})}){
                Surface(shape=RoundedCornerShape(12.dp),color=panel(),border=BorderStroke(1.dp,outline()),modifier=Modifier.fillMaxSize()){
                    Row(Modifier.fillMaxSize().padding(4.dp),verticalAlignment=Alignment.CenterVertically){
                        Column(Modifier.width(PIN),verticalArrangement=Arrangement.Center,horizontalAlignment=Alignment.CenterHorizontally){
                            (0 until arity).forEach{p->
                                val wired=board.wires.any{it.toId==g.id&&it.toPin==p}
                                Surface(onClick={
                                    if(vm.builderWiring){if(vm.builderPending!=null)vm.builderTapTarget(g.id,p)else notify("Pick a glowing output first.")}
                                    else if(wired){vm.builderDeleteWireTo(g.id,p);notify("Wire removed.")}
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                },shape=RoundedCornerShape(8.dp),color=if(wired)accent().copy(alpha=0.25f)else MaterialTheme.colorScheme.surfaceVariant,border=BorderStroke(1.dp,outline()),modifier=Modifier.size(PIN).padding(1.dp).semantics{contentDescription=strings.text("${g.gate} input ${p+1}, ${if(wired)"wired"else"empty"}")}){
                                    Box(contentAlignment=Alignment.Center){Text((p+1).toString(),fontFamily=Mono,fontSize=9.sp)}
                                }
                                if(p<arity-1)Spacer(Modifier.height(PIN_GAP))
                            }
                        }
                        Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally){
                            Text(g.gate.name,fontSize=10.sp,fontWeight=FontWeight.Bold,maxLines=1)
                            Text(out?.let{if(it)"1"else"0"}?:"–",fontFamily=Mono,fontSize=18.sp,fontWeight=FontWeight.Bold,color=if(out==true)accent()else muted())
                            TextButton(onClick={vm.builderDelete(g.id);notify("${g.gate} removed.")},contentPadding=PaddingValues(0.dp),modifier=Modifier.height(22.dp)){Text("× remove",fontSize=8.sp)}
                        }
                        Surface(onClick={
                            if(vm.builderWiring)vm.builderTapSource(g.id,0)else notify("Turn wiring mode ON, then tap this output.")
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },shape=RoundedCornerShape(8.dp),color=if(vm.builderPending==Pair(g.id,0))Lime else if(out==true)accent().copy(alpha=0.3f)else MaterialTheme.colorScheme.surfaceVariant,border=BorderStroke(1.dp,outline()),modifier=Modifier.size(PIN).semantics{contentDescription=strings.text("${g.gate} output")}){
                            Box(contentAlignment=Alignment.Center){Text("Y",fontFamily=Mono,fontSize=10.sp,fontWeight=FontWeight.Bold)}
                        }
                    }
                }
            }
        }
        // Probe (fixed bottom).
        Surface(onClick={
            if(vm.builderWiring){if(vm.builderPending!=null)vm.builderTapTarget(BuilderEngine.PROBE,0)else notify("Pick a glowing output first.")}
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        },shape=RoundedCornerShape(12.dp),color=if(eval?.probe==true)Lime else panel(),contentColor=if(eval?.probe==true)Ink else MaterialTheme.colorScheme.onSurface,border=BorderStroke(1.dp,outline()),
            modifier=Modifier.offset{IntOffset((wPx/2-48.dp.value*density.density).roundToInt(),(hPx-56.dp.value*density.density).roundToInt())}.size(96.dp,48.dp).semantics{contentDescription=strings.text("Output probe, ${if(eval?.probe==true)1 else 0}")}){
            Box(contentAlignment=Alignment.Center){Text("Y = ${eval?.probe?.let{if(it)1 else 0}?:"–"}",fontFamily=Mono,fontSize=14.sp,fontWeight=FontWeight.Bold)}
        }
            }
        }
    }
}

// ---------------- IEEE-754 float lab ----------------
@Composable internal fun FloatScreen(vm:RadixViewModel,notify:(String)->Unit){
    val clipboard=LocalClipboardManager.current
    val language=LocalStrings.current.languageCode
    val report=remember(vm.floatInput,vm.floatWidth,vm.floatMode){runCatching{
        when(vm.floatMode){"dec"->FloatLab.encode(vm.floatInput,vm.floatWidth);"bits"->FloatLab.decode(FloatLab.parseBits(vm.floatInput,vm.floatWidth),vm.floatWidth);else->FloatLab.decode(FloatLab.parseHex(vm.floatInput,vm.floatWidth),vm.floatWidth)}
    }}
    LabPage("Fractions fly.","Bits explain.","THE IEEE-754 FLOAT LAB","Sign, exponent, fraction — every float, decoded."){
        LabCard("Your float"){
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf(32 to "Single · 32-bit",64 to "Double · 64-bit").forEach{(w,label)->OutlinedButton(onClick={vm.setFloat(w,vm.floatMode,vm.floatInput)},modifier=Modifier.weight(1f).heightIn(min=48.dp),border=BorderStroke(if(vm.floatWidth==w)2.dp else 1.dp,if(vm.floatWidth==w)accent()else outline()),shape=RoundedCornerShape(12.dp)){Text(label,fontSize=11.sp)}}}
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf("dec" to "Decimal", "bits" to "Bits", "hex" to "Hex").forEach{(m,label)->OutlinedButton(onClick={vm.setFloat(vm.floatWidth,m,when(m){"dec"->"0.1";"bits"->if(vm.floatWidth==32)"00111101110011001100110011001101"else "0".repeat(64);else->if(vm.floatWidth==32)"3DCCCCCD"else"0".repeat(16)})},modifier=Modifier.weight(1f).heightIn(min=48.dp),border=BorderStroke(if(vm.floatMode==m)2.dp else 1.dp,if(vm.floatMode==m)accent()else outline()),shape=RoundedCornerShape(12.dp)){Text(label,fontSize=11.sp)}}}
            OutlinedTextField(value=vm.floatInput,onValueChange={vm.setFloat(vm.floatWidth,vm.floatMode,it)},label={Text(when(vm.floatMode){"dec"->"Decimal, inf, −inf or nan";"bits"->"${vm.floatWidth} binary digits";else->"${vm.floatWidth/4} hex digits"})},modifier=Modifier.fillMaxWidth(),textStyle=LocalTextStyle.current.copy(fontFamily=Mono),minLines=1,maxLines=2)
            FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf("1.0","-2.5","0.1","inf","nan").forEach{p->TextButton(onClick={vm.setFloat(vm.floatWidth,"dec",p)}){Text(p,fontFamily=Mono,fontSize=12.sp)}}}
            report.exceptionOrNull()?.let{LabNote(it.message?:"Check the input.",true)}
        }
        report.getOrNull()?.let{r->
            LabCard("Decoded · ${r.kind}"){
                Text(r.valueText,fontFamily=Mono,fontSize=30.sp,fontWeight=FontWeight.Bold,color=accent(),modifier=Modifier.horizontalScroll(rememberScrollState()))
                Text("HEX ${r.hex.chunked(4).joinToString(" ")}",fontFamily=Mono,fontSize=13.sp,color=muted())
                LabNote("SIGN ${r.sign}  ·  EXPONENT ${r.expBits}₂ = ${r.expRaw} (bias ${r.bias}${r.expValue?.let{", true $it"}?:""})  ·  FRACTION ${r.fracBits.length} bits")
                Box(Modifier.horizontalScroll(rememberScrollState())){Text(r.bits.chunked(4).joinToString(" "),fontFamily=Mono,fontSize=13.sp,lineHeight=22.sp)}
            }
            LabCard("Show the working"){r.steps.forEachIndexed{i,s->LabNote("${i+1}. $s")}
                TextButton(onClick={clipboard.setText(AnnotatedString(AppStrings.translateBlock("${r.width}-bit float\nInput: ${vm.floatInput}\nBits: ${r.bits}\nHex: ${r.hex}\n${r.kind} = ${r.valueText}\n"+r.steps.joinToString("\n"),language)));notify("Float working copied.")},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)){Text("Copy working",fontSize=12.sp)}
            }
        }
        LabNote("Why 0.1 + 0.2 ≠ 0.3: most decimal fractions repeat forever in binary, so the nearest float is stored, not the exact value.")
    }
}

// ---------------- Leaderboard ----------------
@Composable internal fun LeaderboardScreen(vm:RadixViewModel,notify:(String)->Unit){
    val context=LocalContext.current
    var name by remember{mutableStateOf("")};var error by remember{mutableStateOf<String?>(null)}
    val ranked=remember(vm.gameProfiles){GameEngine.rank(vm.gameProfiles)}
    LabPage("Earn XP.","Top the table.","LEADERBOARD & REWARDS","One device, up to 8 players. Streaks never punish a missed day."){
        LabCard("Now playing · ${vm.gameProfiles.getOrNull(vm.gameActive)?.name?:""}"){
            Text("Level ${vm.gameLevel} ${GameEngine.levelName(vm.gameLevel)}",fontSize=22.sp,fontWeight=FontWeight.Bold,fontFamily=Grotesk)
            val (into,needed)=GameEngine.progressToNext(vm.gameXp)
            LinearProgressIndicator(progress={into.toFloat()/needed},modifier=Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(6.dp)))
            LabNote("${vm.gameXp} XP · $into/$needed to Level ${vm.gameLevel+1}")
            GameEngine.scoreLines(vm.gameProfiles.getOrNull(vm.gameActive)?.name?:"",vm.gameXp,vm.practiceProgress.size,PracticeCatalog.questions.size,vm.streakDays).forEach{LabNote(it)}
            Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){
                OutlinedButton(onClick={ShareCards.shareScore(context,vm.gameProfiles.getOrNull(vm.gameActive)?.name?:"Player",vm.gameXp,vm.practiceProgress.size,PracticeCatalog.questions.size,vm.streakDays);notify("Score card shared as an image.")},modifier=Modifier.weight(1f).heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)){Text("Share score card",fontSize=11.sp)}
            }
        }
        LabCard("Household table · same device"){
            ranked.forEachIndexed{i,p->
                val active=vm.gameProfiles.indexOf(p)==vm.gameActive
                Surface(onClick={vm.selectProfile(vm.gameProfiles.indexOf(p))},shape=RoundedCornerShape(14.dp),color=if(active)accent().copy(alpha=0.12f)else MaterialTheme.colorScheme.surfaceVariant,border=BorderStroke(if(active)2.dp else 1.dp,if(active)accent()else outline()),modifier=Modifier.fillMaxWidth()){
                    Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){
                        Text("#${i+1}",fontFamily=Mono,fontSize=16.sp,color=accent(),modifier=Modifier.width(44.dp))
                        Column(Modifier.weight(1f)){Text(p.name,fontSize=14.sp,fontWeight=FontWeight.Bold);Text("Level ${GameEngine.level(p.xp)} ${GameEngine.levelName(GameEngine.level(p.xp))} · ${p.xp} XP",fontSize=11.sp,color=muted())}
                        if(vm.gameProfiles.size>1)TextButton(onClick={runCatching{vm.deleteProfile(vm.gameProfiles.indexOf(p))}.onFailure{notify(it.message?:"Cannot delete.")}}){Text("Remove",fontSize=10.sp)}
                    }
                }
            }
            OutlinedTextField(value=name,onValueChange={name=it;error=null},label={Text("New player name")},modifier=Modifier.fillMaxWidth(),singleLine=true)
            error?.let{LabNote(it,true)}
            Button(onClick={runCatching{vm.addProfile(name)}.fold(onSuccess={name="";notify("Player joined the table.")},onFailure={error=it.message})},enabled=vm.gameProfiles.size<8,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)){Text("Add player (${vm.gameProfiles.size}/8)",fontSize=12.sp)}
        }
        LabNote("XP: Easy 10, Intermediate 20, Advanced 30 per mastered card, plus up to +14 for long streaks. Levels grow quadratically — early wins come fast.")
    }
}
