@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
package com.radix.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

internal val LocalGuideAnchors=staticCompositionLocalOf<MutableMap<String,Rect>?>{null}
internal val LocalGuideTarget=staticCompositionLocalOf<String?>{null}
@Composable internal fun Modifier.guideAnchor(id:String):Modifier{
    val registry=LocalGuideAnchors.current;val active=LocalGuideTarget.current==id;val bring=remember{BringIntoViewRequester()}
    DisposableEffect(id,registry){onDispose{registry?.remove(id)}}
    LaunchedEffect(active){if(active){delay(260);bring.bringIntoView()}}
    return this.bringIntoViewRequester(bring).onGloballyPositioned{c->val rect=c.boundsInRoot();if(registry?.get(id)!=rect)registry?.set(id,rect)}
}
internal data class GuideStep(val tab:Int,val section:Int?,val anchor:String,val title:String,val text:String)
internal val guideSteps=listOf(
    GuideStep(0,null,"number","One number is all it takes.","Use Next to follow the highlights, then try the controls. Enter a signed integer, fraction or repeating number; results update instantly."),
    GuideStep(0,null,"source-base","Choose what the digits mean.","Select the SOURCE base before reading the result. Changing it reinterprets the same text; More offers every base 2–36."),
    GuideStep(0,null,"working","Get the answer and the why.","Open Show the working for powers, division and fractional multiplication. The arithmetic lab also supports exact +, −, × and ÷ in binary, denary and other bases. Choose a target base and copy all steps."),
    GuideStep(3,null,"kmap","Make a bigger group.","Tap cells through 0 → 1 → X. Gray-code neighbors include opposite edges. Use powers of two; diagonal-only pairs are not groups."),
    GuideStep(4,0,"logic-choice","Meet every standard gate.","Change the gate to AND, OR, NOT, NAND, NOR, XOR, XNOR or BUFFER. Multi-input gates support 2–4 inputs."),
    GuideStep(4,0,"logic-inputs","Your finger is the switch.","Tap a labeled input to flip 0/1. Highlighted wires carry 1; the output and truth table follow immediately."),
    GuideStep(4,0,"logic-table","A table tells the whole story.","Every input combination is listed. The current row is highlighted. Copy the full table or send a function with up to six inputs to K-maps."),
    GuideStep(4,1,"logic-diagram","Build ideas from gates.","Explore adders, subtractors, selectors, encoders and comparators, plus the live BCD-to-seven-segment decoder. Rotate to landscape for wider diagrams; portrait remains swipeable."),
    GuideStep(4,2,"logic-outputs","Now, give the circuit memory.","Latches respond to levels; flip-flops capture on rising edges. Q is stored state. X means unknown, not a disguised zero."),
    GuideStep(4,2,"logic-clock","A rising edge is the moment.","Clocked models have Rise, Fall and Pulse controls. A pulse captures once. Holding CLK high is not another rising edge. Latches have no clock controls."),
    GuideStep(4,2,"logic-timing","See the past, not just now.","The waveform records your latest actions, not real elapsed time. Follow CLK, inputs and Q; X marks indeterminate memory."),
    GuideStep(5,null,"more-help","Come back any time.","More is grouped into Learn & track progress, Number tools, Logic & circuit labs, Progress & rewards, and Quick tools. History, practice and the learning path are direct entries; Settings and the helper are easy to find. Everything works offline.")
)
@Composable internal fun GuideOverlay(index:Int,anchors:Map<String,Rect>,next:()->Unit,previous:()->Unit,close:()->Unit){
    BackHandler(onBack=close)
    val step=guideSteps[index]
    val strings=LocalStrings.current
    BoxWithConstraints(Modifier.fillMaxSize().clickable(onClick={}).semantics{contentDescription=strings.text("Visual app tour, step ${index+1} of ${guideSteps.size}")}){
        val density=androidx.compose.ui.platform.LocalDensity.current
        val rect=anchors[step.anchor]?:if(step.anchor=="logic-clock")anchors["logic-choice"]else null
        val width=with(density){maxWidth.toPx()};val height=with(density){maxHeight.toPx()}
        val shown=rect?.let{r->val left=(r.left-6).coerceIn(0f,width);val top=(r.top-6).coerceIn(52*density.density,height-80*density.density);Rect(left,top,(r.right+6).coerceIn(left,width),minOf(r.bottom+6,top+height*.4f,height-64*density.density))}
        Canvas(Modifier.fillMaxSize()){
            val mask=Path().apply{fillType=PathFillType.EvenOdd;addRect(Rect(0f,0f,size.width,size.height));shown?.let{addRoundRect(RoundRect(it,CornerRadius(14.dp.toPx())))}}
            drawPath(mask,Color.Black.copy(alpha=.79f))
            shown?.let{drawRoundRect(Lime,topLeft=it.topLeft,size=it.size,cornerRadius=CornerRadius(14.dp.toPx()),style=androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx()))}
        }
        val above=shown!=null&&shown.top>height*.48f
        Surface(shape=RoundedCornerShape(22.dp),color=panel(),border=BorderStroke(1.dp,Lime.copy(alpha=.6f)),modifier=Modifier.align(if(above)Alignment.TopCenter else Alignment.BottomCenter).padding(horizontal=16.dp).padding(top=if(above)64.dp else 0.dp,bottom=if(above)0.dp else 24.dp).widthIn(max=520.dp).heightIn(max=maxHeight-96.dp)){
            Column(Modifier.verticalScroll(rememberScrollState()).padding(18.dp)){
                Row(verticalAlignment=Alignment.CenterVertically){Text("VISUAL HELPER · ${index+1}/${guideSteps.size}",color=accent(),fontSize=10.sp,letterSpacing=1.sp,modifier=Modifier.weight(1f));TextButton(onClick=close){Text("Skip",fontSize=11.sp)}}
                Text(step.title,fontFamily=Grotesk,fontWeight=FontWeight.Bold,fontSize=22.sp,lineHeight=26.sp)
                Text(step.text,color=muted(),fontSize=12.sp,lineHeight=19.sp,modifier=Modifier.padding(top=10.dp,bottom=14.dp))
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)){
                    OutlinedButton(onClick=previous,enabled=index>0,modifier=Modifier.weight(1f).heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)){Text("Back")}
                    Button(onClick=next,modifier=Modifier.weight(1f).heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)){Text(if(index==guideSteps.lastIndex)"Let's explore"else"Next →")}
                }
            }
        }
    }
}
@Composable internal fun IllustratedHelper(dismiss:()->Unit,start:()->Unit){
    ModalBottomSheet(onDismissRequest=dismiss,containerColor=panel(),sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true)){
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=28.dp)){
            Text("A little help.\nA lot of aha.",fontFamily=Grotesk,fontSize=29.sp,lineHeight=32.sp,fontWeight=FontWeight.Bold)
            Text("Take a guided tour of the core tools. This built-in helper works offline. It is not a chat or network service. The app follows the system language when English, French, Spanish or Portuguese is supported; otherwise it uses English.",color=muted(),fontSize=12.sp,lineHeight=19.sp,modifier=Modifier.padding(vertical=12.dp))
            Button(onClick=start,modifier=Modifier.fillMaxWidth().heightIn(min=52.dp),shape=RoundedCornerShape(14.dp)){Text("Start the visual app tour")}
            HelpCard("01","Convert without waiting","Choose a source base, type your number, then read or copy a result. Recent valid conversions appear in History; the arithmetic lab handles exact binary and denary operations too."){
                Row(Modifier.fillMaxWidth().padding(vertical=15.dp),horizontalArrangement=Arrangement.SpaceEvenly){Text("255₁₀",fontFamily=Mono,fontSize=25.sp,color=accent());Text("→",fontSize=25.sp);Text("FF₁₆",fontFamily=Mono,fontSize=25.sp,color=MaterialTheme.colorScheme.secondary)}
            }
            HelpCard("02","Show the working","Open the sheet under the results. Divide the integer part; read remainders bottom-to-top. Fractional digits stay top-to-bottom."){
                Text("13 ÷ 2 = 6, r1\n 6 ÷ 2 = 3, r0\n 3 ÷ 2 = 1, r1\n 1 ÷ 2 = 0, r1\n↑ read upward → 1101₂",fontFamily=Mono,fontSize=12.sp,lineHeight=22.sp,color=accent(),modifier=Modifier.padding(14.dp))
            }
            HelpCard("03","K-maps are neighbor puzzles","Choose 2–6 distinct A–Z variables. Enter F(A.B.C.D.E.F)=Σm(...) or ΠM(...); SOP groups 1s and POS groups 0s. Toggle group outlines and indices, then tap a term-labelled G button to highlight its cells and prime implicant/implicate explanation. Copy all working or save the minimized formula to Saved Boolean solutions.") {
                Column(Modifier.padding(12.dp)) {
                    listOf(listOf("0","1"),listOf("1","1")).forEach { row ->
                        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                            row.forEach { value ->
                                Surface(shape=RoundedCornerShape(10.dp),color=MaterialTheme.colorScheme.surfaceVariant,border=BorderStroke(1.dp,accent()),modifier=Modifier.weight(1f).padding(bottom=8.dp)) {
                                    Box(Modifier.height(46.dp),contentAlignment=Alignment.Center) {Text(value,fontFamily=Mono,color=accent(),fontSize=23.sp)}
                                }
                            }
                        }
                    }
                }
            }
            HelpCard("04","Gates are switches with rules","Open Logic → Gates. Tap an input; follow the colored wire and the highlighted truth-table row. NOT bubbles invert the result."){GateDiagram(LogicEngine.Gate.AND,listOf(true,false),false)}
            HelpCard("05","Combine gates into useful circuits","Choose Combinational. Explore MUX/DEMUX, encoders, decoders, comparators, parity and BCD circuits. The BCD decoder has four 8-4-2-1 inputs and a live seven-segment display. Use block view on a small screen; rotate to landscape for the full circuit lab and widest gate diagrams."){
                val c=LogicCatalog.circuits.first{it.id=="full-adder"};CircuitDiagram(c,LogicEngine.evaluate(c,listOf(true,true,true)),false)
            }
            HelpCard("06","Give logic a memory","Choose Sequential. Latches follow levels; flip-flops capture only ↑CLK. Try Rise/Fall/Pulse and watch the action trace. A forbidden SR condition creates indeterminate memory—not zero."){
                BlockDiagram("D flip-flop",listOf("D"),listOf("Q","Q′"),listOf(1),listOf(0,1),true,false)
            }
            HelpCard("07","Keep, replay and explore","More is grouped into Learn & track progress, Number tools, Logic & circuit labs, Progress & rewards, and Quick tools. History, practice and the learning path are direct entries; Settings and the helper are easy to find. Saved items and drafts stay on this device; circuit traces are teaching sessions, not hardware measurements."){
                Row(Modifier.fillMaxWidth().padding(vertical=14.dp),horizontalArrangement=Arrangement.SpaceEvenly){listOf("Settings","History","Progress").forEach{Text(it,color=accent(),fontFamily=Mono,fontSize=14.sp)}}
            }
            HelpCard("08","Write logic, then prove it","More → Logic & circuit labs → Expression-to-circuit lab accepts A–Z (up to 6 distinct inputs). Type apostrophe/prime or ! for NOT, + for OR, . or adjacency for AND, XOR/^/⊕, XNOR/⊙ and parentheses. Change inputs live, inspect the generated gate circuit and truth table, then copy or save your work.") {Text("A′B + AB′  ≡  A XOR B\n3 inputs → 8 rows (0–7)\n4 inputs → 16 rows (0–15)",fontFamily=Mono,fontSize=12.sp,lineHeight=23.sp,color=accent(),modifier=Modifier.padding(vertical=14.dp))}
            HelpCard("09","A width changes the meaning","More → Number representations is separate from ordinary signed-magnitude conversion. Select a width, decimal/binary/Gray input and signed interpretation. Gray and BCD are codes, not ordinary positional binary.") {Text("−13 at 8 bits\n00001101 → invert → +1\n11110011 = unsigned 243",fontFamily=Mono,fontSize=12.sp,lineHeight=23.sp,color=accent(),modifier=Modifier.padding(vertical=14.dp))}
            HelpCard("10","Try, hint, check, understand","More → Practice & self-tests has ${PracticeCatalog.questions.size} objective cards across Easy, Intermediate and Advanced, plus written subjective self-checks. Choose an answer, use a hint, or compare your written reasoning with a model solution. Mastery and practice progress are tracked; missed days are never punished.") {Row(Modifier.fillMaxWidth().padding(vertical=14.dp),horizontalArrangement=Arrangement.SpaceEvenly){Text("Try",color=accent());Text("→ Hint",color=accent());Text("→ Check",color=accent())}}
            HelpCard("11","Keep a safe checkpoint","Logic has Undo/Redo and Save session. More → Circuit checkpoints restores inputs, clock and memory with a fresh trace. Settings lets you control haptics, larger study controls, daily reminders, streak celebrations and reminder tips. Portable backups are validated before replacing data; keys/passwords are excluded.") {Text("Undo ↶  •  Save  •  Restore ↷\nExport → Validate → Confirm",fontFamily=Mono,fontSize=12.sp,lineHeight=24.sp,color=accent(),modifier=Modifier.padding(vertical=14.dp))}
            TextButton(onClick=dismiss,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)){Text("Ready to explore")}
        }
    }
}
@Composable private fun HelpCard(number:String,title:String,body:String,visual:@Composable ()->Unit){
    val strings=LocalStrings.current
    Surface(shape=RoundedCornerShape(20.dp),color=MaterialTheme.colorScheme.surfaceVariant,modifier=Modifier.fillMaxWidth().padding(top=15.dp)){
        Column(Modifier.padding(16.dp)){
            Text("$number  ${strings.text(title)}",fontWeight=FontWeight.Bold,fontSize=17.sp,lineHeight=23.sp)
            visual();Text(body,color=muted(),fontSize=12.sp,lineHeight=19.sp,modifier=Modifier.padding(top=9.dp))
        }
    }
}
