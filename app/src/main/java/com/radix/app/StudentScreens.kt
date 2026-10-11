@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.radix.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private val groupColors = listOf(Color(0xFF8AB9F4),Color(0xFFEDA5C5),Color(0xFFB3D852),Color(0xFFBFA6F2),Color(0xFFE7BA76),Color(0xFF75C7B9),Color(0xFFB7C9F0),Color(0xFFE49D85))

@Composable internal fun WorkingSheet(input: String, from: Int, initialTarget: Int, notify: (String) -> Unit, dismiss: () -> Unit) {
    var target by remember { mutableIntStateOf(initialTarget) }
    val report = remember(input,from,target) { runCatching { BaseWorking.explain(input,from,target) }.getOrNull() }
    val clipboard=LocalClipboardManager.current
    val strings=LocalStrings.current
    val language=strings.languageCode
    ModalBottomSheet(onDismissRequest=dismiss, sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true), containerColor=panel()) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=30.dp)) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Text("Get the answer.\nGet the why.", fontFamily=Grotesk,fontSize=28.sp,lineHeight=30.sp,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f))
                IconButton(onClick=dismiss) {Icon(ImageVector.vectorResource(R.drawable.ic_close),"Close working")}
            }
            Text("$input · base $from",fontFamily=Mono,color=muted(),fontSize=12.sp,modifier=Modifier.padding(top=10.dp).horizontalScroll(rememberScrollState()))
            Row(Modifier.fillMaxWidth().padding(top=12.dp),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                listOf(2,8,10,16).forEach { base -> StudentChip(BaseConverter.shortName(base),target==base,{target=base},Modifier.weight(1f)) }
            }
            Text("Target base $target · ${BaseConverter.name(target)}",color=accent(),fontSize=12.sp,modifier=Modifier.padding(top=12.dp))
            Slider(value=target.toFloat(),onValueChange={target=it.roundToInt()},valueRange=2f..36f,steps=33,modifier=Modifier.semantics {contentDescription=strings.text("Target base from 2 to 36")})
            if(report==null) Text("Fix the source input before generating working.",color=MaterialTheme.colorScheme.error)
            else {
                Surface(shape=RoundedCornerShape(18.dp),color=MaterialTheme.colorScheme.surfaceVariant) {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("ANSWER · BASE $target",color=muted(),fontSize=10.sp,letterSpacing=1.sp)
                        Text(report.result.display,fontFamily=Mono,color=accent(),fontSize=28.sp,modifier=Modifier.padding(top=8.dp).horizontalScroll(rememberScrollState()))
                        Text(if(report.result.exact) "Exact" else "Truncated at 24 fractional places · approximate",color=muted(),fontSize=11.sp,modifier=Modifier.padding(top=7.dp))
                        TextButton(onClick={clipboard.setText(AnnotatedString(AppStrings.translateBlock(report.text(),language)));notify("Full working copied.")}) {Icon(ImageVector.vectorResource(R.drawable.ic_copy),null,modifier=Modifier.size(15.dp));Text("  Copy working")}
                    }
                }
                WorkHeading("01","Expand the place values","Positions run from the point: … 2, 1, 0, −1, −2 … . A–Z represent values 10–35. Work on the magnitude first.")
                WorkLines(report.places.map {it.line(from)})
                report.repeatFormula?.let { Text("Repeating tails use a geometric series:",color=muted(),fontSize=12.sp,modifier=Modifier.padding(vertical=12.dp));WorkLines(listOf(it)) }
                Text("Exact value: ${report.ratio}\nDecimal: ${BaseConverter.format(report.value,10).display}",fontFamily=Mono,fontSize=12.sp,color=accent(),lineHeight=20.sp,modifier=Modifier.padding(top=12.dp).horizontalScroll(rememberScrollState()))
                WorkHeading("02","Divide the integer part","Divide each quotient by $target. Read the remainders BOTTOM TO TOP.")
                WorkLines(report.divisions.map {it.line()})
                Text("Integer digits: ${report.reversedRemainders}",fontFamily=Mono,fontSize=12.sp,color=accent(),modifier=Modifier.padding(top=10.dp).horizontalScroll(rememberScrollState()))
                if(report.multiplications.isNotEmpty()) {
                    WorkHeading("03","Multiply the fractional part","Keep each integer digit; carry its fractional remainder. Read digits TOP TO BOTTOM—do not reverse them.")
                    WorkLines(report.multiplications.mapIndexed {i,step->"${i+1}. ${step.line()}"})
                    Text(when {report.cycleStart!=null->"The remainder repeats. The cycle starts at fractional step ${report.cycleStart+1}; put those digits in parentheses."; report.remaining.signum()!=0->"Stopped at 24 places before zero or a detected cycle. The ellipsis marks a truncated approximation.";else->"Remainder zero: the fraction terminates exactly."},color=muted(),fontSize=12.sp,lineHeight=19.sp,modifier=Modifier.padding(top=12.dp))
                }
                if(report.negative) Text("Restore the minus sign. This is signed magnitude, not fixed-width two's complement.",color=MaterialTheme.colorScheme.secondary,fontSize=12.sp,lineHeight=19.sp,modifier=Modifier.padding(top=18.dp))
                if(report.shortcut.isNotEmpty()) {WorkHeading("+","The bit-grouping shortcut","Align groups around the point. Integer padding goes LEFT; fractional padding goes RIGHT.");WorkLines(report.shortcut)}
            }
        }
    }
}
@Composable private fun WorkHeading(number: String,title: String,body: String) {
    val strings=LocalStrings.current
    Text("$number  ${strings.text(title)}",fontWeight=FontWeight.Bold,fontSize=17.sp,modifier=Modifier.padding(top=24.dp,bottom=8.dp))
    Text(body,color=muted(),fontSize=12.sp,lineHeight=19.sp,modifier=Modifier.padding(bottom=12.dp))
}
@Composable private fun WorkLines(lines: List<String>) {
    var page by remember(lines) {mutableIntStateOf(0)}
    val pages=maxOf(1,(lines.size+15)/16)
    Surface(shape=RoundedCornerShape(14.dp),color=MaterialTheme.colorScheme.surfaceVariant) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Column(Modifier.horizontalScroll(rememberScrollState())) {
                lines.drop(page*16).take(16).forEach {Text(it,fontFamily=Mono,fontSize=11.sp,lineHeight=20.sp,maxLines=1)}
            }
            if(pages>1) Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                TextButton(onClick={page--},enabled=page>0) {Text("Previous",fontSize=11.sp)}
                Text("${page+1}/$pages",fontFamily=Mono,fontSize=10.sp,color=muted(),modifier=Modifier.weight(1f))
                TextButton(onClick={page++},enabled=page<pages-1) {Text("Next",fontSize=11.sp)}
            }
        }
    }
}
@Composable private fun StudentChip(label: String, selected: Boolean, action: () -> Unit, modifier: Modifier=Modifier) {
    Surface(onClick=action,shape=RoundedCornerShape(12.dp),color=if(selected)Lime else MaterialTheme.colorScheme.surfaceVariant,contentColor=if(selected)Ink else muted(),modifier=modifier.heightIn(min=48.dp)) {
        Box(contentAlignment=Alignment.Center) {Text(label,fontSize=12.sp,fontWeight=FontWeight.Bold)}
    }
}

@Composable internal fun KMapScreen(vm: RadixViewModel,notify:(String)->Unit) {
    val clipboard=LocalClipboardManager.current;val haptic=LocalHapticFeedback.current;val context=LocalContext.current;val strings=LocalStrings.current
    var notation by remember {mutableStateOf("")};var notationError by remember {mutableStateOf<String?>(null)}
    val solution=remember(vm.kVariables,vm.kNames,vm.kCells,vm.kForm,vm.kError) {if(vm.kError==null)KMapEngine.solve(vm.kVariables,vm.kCells,vm.kForm,vm.kNames)else null}
    var highlight by remember {mutableIntStateOf(-1)}
    var manual by remember {mutableStateOf(false)}
    var selected by remember {mutableStateOf(setOf<Int>())}
    var groupCheck by remember {mutableStateOf<KMapEngine.GroupCheck?>(null)}
    var showInputs by remember {mutableStateOf(false)}
    var showTruth by remember {mutableStateOf(false)}
    var showGroups by remember {mutableStateOf(true)}
    var showIndices by remember {mutableStateOf(true)}
    var savedOpen by remember {mutableStateOf(false)}
    var examples by remember {mutableStateOf(false)}
    var exercise by remember {mutableStateOf<KMapEngine.Exercise?>(null)}
    var revealed by remember {mutableStateOf(true)}
    fun fresh() {highlight=-1;selected=emptySet();groupCheck=null;exercise=null;revealed=true;manual=false}
    val visible=if(revealed)solution else null
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=24.dp)) {
        Hero("Make logic.","Look simple.","KARNAUGH MAPS, MADE FRIENDLY","Gray-code neighbors. Bigger groups. Smaller expressions.")
        HistoryTools(vm.mapCanUndo,vm.mapCanRedo,{vm.undoMap();fresh()},{vm.redoMap();fresh()})
        Surface(shape=Round,color=panel(),border=BorderStroke(1.dp,outline())) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Text("YOUR LOGIC PLAYGROUND",fontSize=9.sp,letterSpacing=1.sp,color=muted(),modifier=Modifier.weight(1f))
                    TextButton(onClick={examples=true}) {Text("24 examples",fontSize=11.sp)}
                }
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {(2..6).forEach {n->StudentChip("$n variables",vm.kVariables==n,{vm.selectKVariables(n);fresh()},Modifier.width(78.dp))}}
                if(vm.kVariables>=5)Text(strings.landscapeKmapHint,fontSize=10.sp,color=muted(),lineHeight=16.sp,modifier=Modifier.padding(top=6.dp))
                Row(Modifier.padding(top=8.dp),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    listOf(Triple("XOR · 2-var",2,"1,2"),Triple("Majority · 3-var",3,"3,5,6,7"),Triple("Primes · 4-var",4,"2,3,5,7,11,13")).forEach{(name,n,terms)->
                        StudentChip(name,false,{vm.selectKVariables(n);vm.updateKTerms(terms);vm.updateKDontCare("");fresh()},Modifier.weight(1f))}
                }
                Row(Modifier.padding(top=8.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    listOf(KMapEngine.Form.SOP to "SOP · 1s",KMapEngine.Form.POS to "POS · 0s").forEach {(form,label)->StudentChip(label,vm.kForm==form,{vm.selectKForm(form);fresh()},Modifier.weight(1f))}
                }
                Row(Modifier.padding(top=6.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    StudentChip(if(showGroups)"Groups · on"else"Show groups",showGroups,{showGroups=!showGroups},Modifier.weight(1f))
                    StudentChip(if(showIndices)"Indices · on"else"Show indices",showIndices,{showIndices=!showIndices},Modifier.weight(1f))
                }
                OutlinedTextField(value=notation,onValueChange={notation=it.take(512);notationError=null},label={Text("Example: F(A.B.C.D.E.F)=Σm(1,3,5,7) or ΠM(...)")},modifier=Modifier.fillMaxWidth().padding(top=10.dp),textStyle=LocalTextStyle.current.copy(fontFamily=Mono),minLines=1,maxLines=2)
                notationError?.let{Text(it,color=MaterialTheme.colorScheme.error,fontSize=11.sp,lineHeight=17.sp)}
                OutlinedButton(
                    onClick = {
                        val result = runCatching {
                            KMapEngine.fromNotation(notation)
                        }
                        result.onSuccess { parsed ->
                            vm.logicToKMap(parsed.variables, parsed.cells, parsed.form, parsed.names)
                            notation = ""
                            notationError = null
                            fresh()
                            notify("${parsed.form} function opened in K-maps.")
                        }.onFailure { failure ->
                            notationError = failure.message ?: "Check function notation."
                        }
                    },
                    enabled = notation.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                ) { Text("Load Σm / ΠM function", fontSize = 12.sp) }
                Text(if(vm.kForm==KMapEngine.Form.SOP) "Group 1s. Xs may enlarge groups." else "Group 0s. Fixed literals use the opposite complement rule.",fontSize=12.sp,color=muted(),modifier=Modifier.padding(top=12.dp))
                TextButton(onClick={showInputs=!showInputs},modifier=Modifier.fillMaxWidth()) {Text(if(showInputs)"Hide input lists" else "Edit ${if(vm.kForm==KMapEngine.Form.SOP)"minterms (1s)" else "maxterms (0s)"} and don't-cares",fontSize=11.sp)}
                if(showInputs) {
                    val keyboard=KeyboardOptions(keyboardType=KeyboardType.Ascii,autoCorrectEnabled=false,imeAction=ImeAction.Done)
                    OutlinedTextField(value=vm.kTerms,onValueChange={vm.updateKTerms(it);highlight=-1;exercise=null;revealed=true;selected=emptySet();groupCheck=null;manual=false},singleLine=true,label={Text(if(vm.kForm==KMapEngine.Form.SOP)"Minterms Σm · required 1s" else "Maxterms ΠM · required 0s")},keyboardOptions=keyboard,modifier=Modifier.fillMaxWidth(),isError=vm.kError!=null)
                    OutlinedTextField(value=vm.kDontCare,onValueChange={vm.updateKDontCare(it);highlight=-1;exercise=null;revealed=true;selected=emptySet();groupCheck=null;manual=false},singleLine=true,label={Text("Don't-cares d · optional Xs")},keyboardOptions=keyboard,modifier=Modifier.fillMaxWidth().padding(top=8.dp),isError=vm.kError!=null)
                    Text("Indices 0–${(1 shl vm.kVariables)-1}. A is the MSB. Duplicates are ignored; ranges are not supported.",fontSize=10.sp,color=muted(),lineHeight=16.sp,modifier=Modifier.padding(top=8.dp,bottom=12.dp))
                }
                vm.kError?.let {Text("Fix the lists before solving: $it",color=MaterialTheme.colorScheme.error,fontSize=12.sp,lineHeight=18.sp,modifier=Modifier.padding(vertical=10.dp))}
                Text("1s: ${vm.kTerms.ifBlank{"none"}} · Xs: ${vm.kDontCare.ifBlank{"none"}}",fontFamily=Mono,fontSize=11.sp,color=muted(),modifier=Modifier.padding(top=10.dp).horizontalScroll(rememberScrollState()))
                if(solution==null) Text("Results paused · invalid input",color=muted(),fontSize=13.sp)
                else if(!revealed) Button(onClick={revealed=true},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)) {Text("Reveal worked solution")}
                else {
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("F(${vm.kNames.toList().joinToString(", ")}) · ${vm.kForm}",fontSize=10.sp,color=muted())
                            Text(solution.expression.replace("'","′"),fontFamily=Mono,fontSize=28.sp,color=accent(),modifier=Modifier.padding(top=8.dp).horizontalScroll(rememberScrollState()))
                        }
                        IconButton(onClick={clipboard.setText(AnnotatedString(solution.expression));notify("Simplified expression copied.")}) {Icon(ImageVector.vectorResource(R.drawable.ic_copy),"Copy simplified expression",modifier=Modifier.size(18.dp))}
                    }
                    Text("${solution.groups.size} ${if(vm.kForm==KMapEngine.Form.SOP)"product terms" else "sum clauses"} · ${solution.literalCount} literals · exact minimum",fontSize=10.sp,color=muted(),modifier=Modifier.padding(top=8.dp,bottom=16.dp))
                }
                KMapGrid(vm,visible,highlight,manual,selected,showGroups,showIndices) { index ->
                    if(manual) {selected=if(index in selected)selected-index else selected+index;groupCheck=null}
                    else {vm.cycleKCell(index);exercise=null;revealed=true;highlight=-1}
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {Text(if(manual)"Tap to select; values stay fixed." else "Tap: 0 → 1 → X → 0",fontSize=10.sp,color=muted(),modifier=Modifier.weight(1f));TextButton(onClick={vm.resetK();fresh()}) {Text("Reset to 0",fontSize=11.sp)}}
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick={manual=!manual;selected=emptySet();groupCheck=null},modifier=Modifier.weight(1f).heightIn(min=48.dp),shape=RoundedCornerShape(12.dp),enabled=vm.kError==null) {Text(if(manual)"Exit group check" else "Check a group",fontSize=11.sp)}
                    OutlinedButton(onClick={showTruth=!showTruth},modifier=Modifier.weight(1f).heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)) {Text("Truth table",fontSize=11.sp)}
                }
                if(manual) {
                    Text("Select an adjacent power-of-two group (1–${1 shl vm.kVariables} cells), then check it.",fontSize=12.sp,color=muted(),lineHeight=18.sp,modifier=Modifier.padding(top=12.dp))
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Text("${selected.size} selected",fontFamily=Mono,fontSize=11.sp,modifier=Modifier.weight(1f))
                        TextButton(onClick={selected=emptySet();groupCheck=null}) {Text("Clear",fontSize=11.sp)}
                        Button(onClick={groupCheck=KMapEngine.checkGroup(vm.kVariables,vm.kCells,selected,vm.kForm)},enabled=selected.isNotEmpty(),shape=RoundedCornerShape(12.dp)) {Text("Check",fontSize=11.sp)}
                    }
                    groupCheck?.let {Text(it.message,color=if(it.valid)accent()else MaterialTheme.colorScheme.error,fontSize=12.sp,lineHeight=18.sp,modifier=Modifier.padding(top=10.dp))}
                }
                if(showTruth) {
                    HorizontalDivider(color=outline(),modifier=Modifier.padding(vertical=14.dp))
                    Text("${vm.kNames}   index   input   simplified",fontFamily=Mono,fontSize=11.sp,color=muted())
                    vm.kCells.forEachIndexed {i,value->Text("${KMapEngine.binary(i,vm.kVariables)}     ${i.toString().padStart(2)}      ${if(value==2)"X"else value}      ${visible?.evaluate(i)?.let {if(it)1 else 0}?:"—"}${if(value==2)" (optional)"else""}",fontFamily=Mono,fontSize=11.sp,lineHeight=21.sp)}
                    Text("Only specified 0/1 cells must match. Xs impose no output requirement.",color=muted(),fontSize=10.sp,lineHeight=16.sp,modifier=Modifier.padding(top=8.dp))
                }
            }
        }
        exercise?.let {item->
            Surface(shape=RoundedCornerShape(18.dp),color=MaterialTheme.colorScheme.surfaceVariant,modifier=Modifier.padding(top=16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("BEGINNER CHALLENGE",color=accent(),fontSize=10.sp,letterSpacing=1.sp)
                    Text(item.title,fontSize=18.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=8.dp))
                    Text(item.hint,color=muted(),fontSize=12.sp,lineHeight=19.sp,modifier=Modifier.padding(top=8.dp))
                    if(revealed)Text(item.lesson,color=accent(),fontSize=12.sp,lineHeight=19.sp,modifier=Modifier.padding(top=8.dp))
                }
            }
        }
        visible?.let {answer->
            Text("WHY THIS ANSWER WORKS",color=muted(),fontSize=10.sp,letterSpacing=1.2.sp,modifier=Modifier.padding(top=24.dp,bottom=10.dp))
            Text(answer.note,color=muted(),fontSize=12.sp,lineHeight=18.sp)
            if(answer.groups.isNotEmpty()) {
                Text("SELECTED PRIME ${if(vm.kForm==KMapEngine.Form.SOP)"IMPLICANTS"else"IMPLICATES"} · ${answer.groups.size}",color=muted(),fontSize=10.sp,letterSpacing=1.sp,modifier=Modifier.padding(top=12.dp))
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top=8.dp),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick={highlight=-1}) {Text("All groups",fontSize=11.sp)}
                    answer.groups.forEachIndexed {i,group->
                        val groupTerm=(if(vm.kForm==KMapEngine.Form.POS)"(${group.term})"else group.term).replace("'","′")
                        OutlinedButton(onClick={highlight=if(highlight==i)-1 else i},border=BorderStroke(if(highlight==i)2.dp else 1.dp,groupColors[i%groupColors.size]),shape=RoundedCornerShape(12.dp)) {Text("G${i+1} · $groupTerm",fontFamily=Mono,fontSize=10.sp,maxLines=1)}
                    }
                }
                answer.groups.forEachIndexed {i,group->
                    Surface(shape=RoundedCornerShape(18.dp),color=panel(),border=BorderStroke(1.dp,groupColors[i%groupColors.size].copy(alpha=0.7f)),modifier=Modifier.padding(top=10.dp)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp)) {
                            Text("G${i+1}   ${(if(vm.kForm==KMapEngine.Form.POS)"(${group.term})"else group.term).replace("'","′")}",fontFamily=Mono,fontSize=17.sp,fontWeight=FontWeight.Bold)
                            Text("Cells ${group.cube.indices(1 shl vm.kVariables).joinToString(", ")} · ${java.lang.Long.bitCount(group.cube.members)} cells${if(group.wraps)" · edge wrap"else""}${if(group.essential)" · essential"else""}",color=muted(),fontSize=10.sp,lineHeight=16.sp,modifier=Modifier.padding(top=8.dp))
                            group.explanation.forEach {Text(it,color=muted(),fontSize=12.sp,lineHeight=20.sp,modifier=Modifier.padding(top=4.dp))}
                            Text(if(group.essential)"At least one required cell has no other maximal covering group."else"Chosen to finish a minimum cover. Overlap is allowed.",color=muted(),fontSize=10.sp,lineHeight=16.sp,modifier=Modifier.padding(top=8.dp))
                        }
                    }
                }
            } else Text("No groups are needed for this constant function.",color=muted(),fontSize=12.sp,modifier=Modifier.padding(top=12.dp))
            val savedDuplicate=vm.savedExpressions.any{it.variables==vm.kVariables&&it.expression==answer.expression.trim()&&it.form==vm.kForm.name}
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth().padding(top=12.dp)) {
                OutlinedButton(onClick={clipboard.setText(AnnotatedString(AppStrings.translateBlock(kWorking(vm,answer),strings.languageCode)));notify("K-map working copied.")},modifier=Modifier.weight(1f).heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)) {Icon(ImageVector.vectorResource(R.drawable.ic_copy),null,modifier=Modifier.size(16.dp));Text("  Copy working",fontSize=11.sp)}
                Button(onClick={
                    val added=runCatching{vm.saveExpressionSolution(StudyExpression(vm.kVariables,answer.expression,"",vm.kForm.name),vm.kForm)}.getOrElse{notify(it.message?:"Could not save this solution.");null}
                    if(added!=null)notify(if(added)"K-map formula saved in Boolean solutions and backups."else"That formula was already saved; it was moved to the top.")
                },enabled=vm.savedExpressions.size<50||savedDuplicate,modifier=Modifier.weight(1f).heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)){Text("Save solution",fontSize=11.sp)}
            }
            TextButton(onClick={savedOpen=true},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)){Text("Saved Boolean solutions · ${vm.savedExpressions.size}",fontSize=11.sp)}
            Text("Save keeps the minimized formula and form; Copy working includes the groups, rules and full table.",fontSize=10.sp,color=muted(),lineHeight=16.sp)
            OutlinedButton(onClick={ShareCards.shareKMap(context,"F(${vm.kNames})",vm.kVariables,vm.kCells,vm.kForm.name,answer.expression.replace("'","′"),answer.note);notify("K-map solution shared as an image.")},modifier=Modifier.fillMaxWidth().heightIn(min=48.dp),shape=RoundedCornerShape(12.dp)) {Text("Share solution image",fontSize=12.sp)}
        }
        Surface(shape=RoundedCornerShape(18.dp),color=panel(),border=BorderStroke(1.dp,outline()),modifier=Modifier.padding(top=18.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("The five grouping rules",fontSize=16.sp,fontWeight=FontWeight.Bold)
                Text("1. Gray order is 00, 01, 11, 10.\n2. Group powers of two, not 3 or 6.\n3. Use rectangles, not diagonal-only pairs.\n4. Opposite edges touch; four corners can wrap.\n5. Overlap is allowed; Xs are optional.",fontSize=12.sp,color=muted(),lineHeight=22.sp,modifier=Modifier.padding(top=10.dp))
                Text("SOP: fixed 0 → A', fixed 1 → A.\nPOS: fixed 0 → A, fixed 1 → A'.\n+ is OR, adjacency is AND, ' is NOT.\nSwitching SOP/POS preserves the truth table.",fontSize=11.sp,color=accent(),lineHeight=20.sp,modifier=Modifier.padding(top=12.dp))
            }
        }
    }
    if(savedOpen)SavedExpressionSheet(vm,notify){savedOpen=false}
    if(examples) ModalBottomSheet(onDismissRequest={examples=false},containerColor=panel(),sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true)) {
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=28.dp)) {
            Text("Little aha moments.",fontFamily=Grotesk,fontSize=27.sp,fontWeight=FontWeight.Bold)
            Text("24 beginner cases. Try the groups, then reveal the worked solution.",fontSize=12.sp,color=muted(),lineHeight=18.sp,modifier=Modifier.padding(vertical=12.dp))
            KMapEngine.exercises.forEachIndexed {i,item->
                Surface(onClick={vm.loadKExercise(item);exercise=item;revealed=false;highlight=-1;manual=false;selected=emptySet();groupCheck=null;examples=false},color=MaterialTheme.colorScheme.surfaceVariant,shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth().padding(bottom=8.dp)) {
                    Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically) {
                        Text((i+1).toString().padStart(2,'0'),fontFamily=Mono,color=accent(),fontSize=16.sp,modifier=Modifier.width(38.dp))
                        Column(Modifier.weight(1f)) {Text(item.title,fontSize=13.sp,fontWeight=FontWeight.Bold);Text("${item.variables} variables · ${item.form}${if(item.dc.isNotEmpty())" · don't-cares"else""}",color=muted(),fontSize=10.sp,modifier=Modifier.padding(top=4.dp))}
                        Icon(ImageVector.vectorResource(R.drawable.ic_arrow),null,modifier=Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable private fun KMapGrid(vm: RadixViewModel, solution: KMapEngine.Solution?, highlight: Int, manual: Boolean, selected: Set<Int>, showGroups:Boolean, showIndices:Boolean, action:(Int)->Unit) {
    val strings=LocalStrings.current
    val n=vm.kVariables;val rows=KMapEngine.gray(KMapEngine.rowBits(n));val cols=KMapEngine.gray(KMapEngine.columnBits(n))
    BoxWithConstraints(Modifier.fillMaxWidth().padding(top=10.dp).guideAnchor("kmap")) {
        val gridWidth=if(n>=5)maxOf(maxWidth,548.dp)else maxWidth
        Column(Modifier.horizontalScroll(rememberScrollState())) {
        Column(Modifier.width(gridWidth)) {
        Row(horizontalArrangement=Arrangement.spacedBy(6.dp),verticalAlignment=Alignment.CenterVertically) {
            Text("${vm.kNames.take(KMapEngine.rowBits(n))}/${vm.kNames.drop(KMapEngine.rowBits(n))}",fontFamily=Mono,fontSize=9.sp,color=muted(),modifier=Modifier.width(28.dp))
            cols.forEach {label->Box(Modifier.weight(1f).height(26.dp),contentAlignment=Alignment.Center){Text(KMapEngine.binary(label,KMapEngine.columnBits(n)),fontFamily=Mono,fontSize=12.sp,color=muted())}}
        }
        rows.forEachIndexed {r,label->
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp),verticalAlignment=Alignment.CenterVertically,modifier=Modifier.padding(bottom=6.dp)) {
                Text(KMapEngine.binary(label,KMapEngine.rowBits(n)),fontFamily=Mono,fontSize=12.sp,color=muted(),modifier=Modifier.width(28.dp))
                cols.forEachIndexed {c,_->
                    val index=KMapEngine.cellAt(n,r,c);val value=vm.kCells[index]
                    val groups=if(showGroups)solution?.groups?.indices?.filter {solution.groups[it].cube.contains(index)&&(highlight<0||highlight==it)}?:emptyList()else emptyList()
                    val marked=index in selected
                    Surface(onClick={action(index)},enabled=vm.kError==null,shape=RoundedCornerShape(12.dp),color=if(marked)Lime.copy(alpha=0.2f)else MaterialTheme.colorScheme.surfaceVariant,
                        modifier=Modifier.weight(1f).heightIn(min=76.dp).semantics {
                            contentDescription=strings.text("${if(vm.kForm==KMapEngine.Form.SOP)"Minterm m"else"Maxterm M"}$index, ${vm.kNames.mapIndexed {i,ch->"$ch=${KMapEngine.binary(index,n)[i]}"}.joinToString(", ")}, output ${if(value==2)"don't-care"else value}. ${if(manual)"Select group cell"else"Tap to cycle"}")
                            stateDescription=strings.text(if(marked)"Selected"else"Not selected")
                        }.drawWithContent {
                            drawContent()
                            val strokes=if(manual&&marked)listOf(Lime)else if(manual)emptyList()else groups.take(4).map {groupColors[it%groupColors.size]}
                            strokes.forEachIndexed {i,color->val inset=(2+i*2).dp.toPx();drawRoundRect(color,Offset(inset,inset),Size(size.width-inset*2,size.height-inset*2),CornerRadius(10.dp.toPx()),style=Stroke(1.5.dp.toPx()))}
                        }) {
                        Column(Modifier.padding(vertical=7.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
                            if(showIndices)Text("${if(vm.kForm==KMapEngine.Form.SOP)"m"else"M"}$index",fontFamily=Mono,fontSize=9.sp,color=muted())
                            Text(if(value==2)"X"else value.toString(),fontFamily=Mono,fontSize=25.sp,fontWeight=FontWeight.Bold,color=if(value==1)accent()else if(value==2)MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface)
                            Text(if(groups.isNotEmpty()&&!manual)groups.joinToString(" "){"G${it+1}"}else KMapEngine.binary(index,n),fontFamily=Mono,fontSize=8.sp,color=muted(),maxLines=1)
                        }
                    }
                }
            }
        }
    }
        }
    }
}
private fun kWorking(vm: RadixViewModel, solution: KMapEngine.Solution): String = buildString {
    appendLine("F(${vm.kNames}) = ${solution.expression} · ${vm.kForm}")
    appendLine("Selected prime ${if(vm.kForm==KMapEngine.Form.SOP)"implicants"else"implicates"} (${solution.groups.size})")
    appendLine("Gray order: 00, 01, 11, 10. The leftmost listed variable is the most significant bit.")
    appendLine("Required ${if(vm.kForm==KMapEngine.Form.SOP)"1s"else"0s"}: ${vm.kTerms}; don't-cares: ${vm.kDontCare}")
    solution.groups.forEachIndexed {i,group->appendLine("G${i+1}: cells ${group.cube.indices(1 shl vm.kVariables).joinToString(",")} → ${group.term}${if(group.wraps)" (edge wrap)"else""}");group.explanation.forEach {appendLine(it)}}
    appendLine(solution.note)
    appendLine("Truth table: ${vm.kNames} / input / simplified")
    vm.kCells.forEachIndexed {i,v->appendLine("${KMapEngine.binary(i,vm.kVariables)} / ${if(v==2)"X"else v} / ${if(solution.evaluate(i))1 else 0}${if(v==2)" (optional)"else""}")}
}
