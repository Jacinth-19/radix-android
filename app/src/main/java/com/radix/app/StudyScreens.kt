@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
package com.radix.app

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal val LocalLargeStudyControls=staticCompositionLocalOf{false}
@Composable private fun actionHeight()=if(LocalLargeStudyControls.current)60.dp else 48.dp
@Composable private fun StudyPage(first:String,second:String,eyebrow:String,subtitle:String,body:@Composable ColumnScope.()->Unit){
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=24.dp),horizontalAlignment=Alignment.CenterHorizontally){
        Column(Modifier.widthIn(max=980.dp).fillMaxWidth()){Hero(first,second,eyebrow,subtitle);body()}
    }
}
@Composable private fun StudyCard(title:String,body:@Composable ColumnScope.()->Unit){
    Surface(shape=Round,color=panel(),border=BorderStroke(1.dp,outline()),modifier=Modifier.fillMaxWidth().padding(bottom=16.dp)){
        Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(11.dp)){if(title.isNotBlank())Text(title,fontSize=19.sp,fontWeight=FontWeight.Bold,fontFamily=Grotesk);body()}
    }
}
@Composable internal fun HistoryTools(canUndo:Boolean,canRedo:Boolean,undo:()->Unit,redo:()->Unit){
    val haptic=LocalHapticFeedback.current
    Row(Modifier.fillMaxWidth().padding(bottom=12.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)){
        OutlinedButton(onClick={undo();haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)},enabled=canUndo,shape=RoundedCornerShape(12.dp),modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("↶ Undo",fontSize=12.sp)}
        OutlinedButton(onClick={redo();haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)},enabled=canRedo,shape=RoundedCornerShape(12.dp),modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("↷ Redo",fontSize=12.sp)}
    }
}
@Composable private fun StudyNote(text:String,error:Boolean=false){Text(text,color=if(error)MaterialTheme.colorScheme.error else muted(),fontSize=12.sp,lineHeight=19.sp)}
@Composable private fun StudyValue(label:String,value:String){Column{Text(label,color=muted(),fontSize=10.sp,letterSpacing=1.sp);Text(value,fontFamily=Mono,fontSize=23.sp,lineHeight=31.sp,color=accent(),modifier=Modifier.fillMaxWidth())}}

@Composable internal fun ExpressionScreen(vm:RadixViewModel,notify:(String)->Unit){
    val s=vm.expression;val clipboard=LocalClipboardManager.current;val haptic=LocalHapticFeedback.current
    var bits by remember(s.variables){mutableStateOf(List(s.variables){it==0})}
    val report=remember(s.left,s.variables){runCatching{ExpressionEngine.report(s.left,s.variables)}}
    val comparison=remember(s.left,s.right,s.variables){if(s.right.isBlank())null else runCatching{ExpressionEngine.compare(s.left,s.right,s.variables)}}
    StudyPage("Write logic.","Prove the idea.","THE BOOLEAN EXPRESSION LAB","Real truth tables. Both forms. No guesswork."){
        StudyCard("Your expression"){
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)){(2..6).forEach{n->OutlinedButton(onClick={vm.setExpression(s.copy(variables=n))},modifier=Modifier.width(70.dp).heightIn(min=actionHeight())){Text("$n vars",fontSize=11.sp,color=if(n==s.variables)accent()else muted())}}}
            OutlinedTextField(value=s.left,onValueChange={vm.setExpression(s.copy(left=it))},label={Text("Expression F")},textStyle=LocalTextStyle.current.copy(fontFamily=Mono),modifier=Modifier.fillMaxWidth(),minLines=2,maxLines=4)
            FlowRow(horizontalArrangement=Arrangement.spacedBy(7.dp),verticalArrangement=Arrangement.spacedBy(5.dp)){
                listOf("A","B","C","D","E","F","′"," + "," · "," ⊕ "," ⊙ ","(",")","0","1").forEach{token->OutlinedButton(onClick={vm.setExpression(s.copy(left=s.left+token))},enabled=token.length!=1||token[0] !in 'A'..'F'||token[0]-'A'<s.variables,modifier=Modifier.heightIn(min=actionHeight()),contentPadding=PaddingValues(horizontal=12.dp)){Text(token.trim(),fontFamily=Mono,fontSize=12.sp)}}
            }
            StudyNote("Buttons append symbols: ⊕ is XOR, ⊙ is XNOR, ′ is NOT. Precedence: NOT first; AND (including adjacency) next; XOR/XNOR next; OR last. NAND/NOR/XNOR sit at their corresponding level and associate left-to-right.")
            FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf("XOR" to "A'B + AB'","De Morgan" to "!(A B)","Distribute" to "A(B+C)").forEach{(name,text)->TextButton(onClick={vm.setExpression(s.copy(left=text,variables=if(name=="Distribute")maxOf(3,s.variables)else s.variables))},modifier=Modifier.heightIn(min=actionHeight())){Text(name,fontSize=12.sp)}};TextButton(onClick={vm.setExpression(s.copy(left=""))},modifier=Modifier.heightIn(min=actionHeight())){Text("Clear",fontSize=12.sp)}}
            report.exceptionOrNull()?.let{StudyNote(it.message?:"Check the expression.",true)}
        }
        report.getOrNull()?.let{r->
            StudyCard("Follow the live function"){
                Row(horizontalArrangement=Arrangement.spacedBy(9.dp)) {
                    bits.forEachIndexed { i,v ->
                        FilledTonalButton(onClick={bits=bits.mapIndexed{j,b->if(i==j)!b else b};haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)},modifier=Modifier.weight(1f).heightIn(min=if(vm.largeControls)78.dp else 64.dp),shape=RoundedCornerShape(13.dp)) {
                            Column(horizontalAlignment=Alignment.CenterHorizontally) {
                                Text(('A'+i).toString(),fontSize=10.sp)
                                Text(if(v)"1"else"0",fontFamily=Mono,fontSize=25.sp)
                            }
                        }
                    }
                }
                StudyValue("F · CURRENT OUTPUT",r.cells[LogicEngine.code(bits)].toString());StudyNote("Parsed as ${r.normalized}")
                StudyValue("MINIMUM SOP",r.sop);StudyValue("MINIMUM POS",r.pos);StudyValue("MINTERMS · Σm",r.sigmaM);StudyValue("MAXTERMS · ΠM",r.piM)
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){Button(onClick={vm.logicToKMap(s.variables,r.cells,KMapEngine.Form.SOP)},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("SOP K-map",fontSize=11.sp)};OutlinedButton(onClick={vm.logicToKMap(s.variables,r.cells,KMapEngine.Form.POS)},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("POS K-map",fontSize=11.sp)}}
            }
            StudyCard("Complete truth table"){
                Row(Modifier.fillMaxWidth()){Text("INPUTS",color=muted(),fontSize=10.sp,modifier=Modifier.weight(1f));Text("F",color=muted(),fontSize=10.sp)}
                r.cells.forEachIndexed{code,value->Row(Modifier.fillMaxWidth().background(if(code==LogicEngine.code(bits))accent().copy(alpha=.07f)else androidx.compose.ui.graphics.Color.Transparent).padding(vertical=7.dp)){Text(LogicEngine.binary(code,s.variables),fontFamily=Mono,fontSize=13.sp,modifier=Modifier.weight(1f));Text(value.toString(),fontFamily=Mono,fontSize=13.sp)}}
                TextButton(onClick={clipboard.setText(AnnotatedString("F = ${s.left}\nParsed: ${r.normalized}\nSOP: ${r.sop}\nPOS: ${r.pos}\n${r.sigmaM}\n${r.piM}\n${(0 until s.variables).map{('A'+it).toString()}.joinToString("\t")}\tF\n"+r.cells.mapIndexed{c,v->LogicEngine.binary(c,s.variables).toCharArray().joinToString("\t")+"\t$v"}.joinToString("\n")));notify("Expression report copied.")},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Copy expression + complete table",fontSize=12.sp)}
            }
        }
        StudyCard("Compare another expression"){
            OutlinedTextField(value=s.right,onValueChange={vm.setExpression(s.copy(right=it))},label={Text("Expression G · optional")},textStyle=LocalTextStyle.current.copy(fontFamily=Mono),modifier=Modifier.fillMaxWidth(),minLines=2,maxLines=4)
            comparison?.fold(onSuccess={c->if(report.isSuccess){Text(if(c.equivalent)"✓ Equivalent in EVERY row"else"Not equivalent · ${c.mismatches.size} different rows",color=if(c.equivalent)accent()else MaterialTheme.colorScheme.error,fontWeight=FontWeight.Bold,fontSize=15.sp);if(!c.equivalent)StudyNote("Counterexamples: "+c.mismatches.joinToString{LogicEngine.binary(it,s.variables)})}},onFailure={StudyNote(it.message?:"Check comparison expression.",true)})
            StudyNote("Comparisons include every selected input combination, not just the current switches. A–F and constants 0/1 are supported; no scripting is executed.")
        }
    }
}

@Composable internal fun RepresentationScreen(vm:RadixViewModel,notify:(String)->Unit){
    val s=vm.representation;val clipboard=LocalClipboardManager.current;val result=remember(s){runCatching{RepresentationEngine.report(s.input,s.width,s.signed,s.mode)}}
    StudyPage("Same bits.","Different meaning.","THE NUMBER REPRESENTATION LAB","Choose a width. Read the value. Understand the sign."){
        StudyCard("Encode or decode"){
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(7.dp)){RepresentationEngine.widths.forEach{w->OutlinedButton(onClick={vm.setRepresentation(s.copy(width=w))},modifier=Modifier.heightIn(min=actionHeight())){Text("$w bits",fontSize=11.sp,color=if(w==s.width)accent()else muted())}}}
            FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf("decimal" to "Decimal", "bits" to "Binary word", "gray" to "Gray code").forEach{(key,name)->OutlinedButton(onClick={vm.setRepresentation(s.copy(mode=key,input=when(key){"decimal"->"-13";"bits"->"11110011";else->"0110"},width=maxOf(8,s.width)))},modifier=Modifier.heightIn(min=actionHeight())){Text(name,fontSize=11.sp,color=if(key==s.mode)accent()else muted())}}}
            Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Two’s-complement interpretation",fontSize=13.sp);StudyNote("Off = unsigned decimal input/range")};Switch(checked=s.signed,onCheckedChange={vm.setRepresentation(s.copy(signed=it))},modifier=Modifier.semantics{contentDescription="Use signed two’s-complement interpretation"})}
            OutlinedTextField(value=s.input,onValueChange={vm.setRepresentation(s.copy(input=it))},label={Text(if(s.mode=="decimal")"Whole decimal integer"else if(s.mode=="gray")"Gray-code word"else"Binary word")},modifier=Modifier.fillMaxWidth(),textStyle=LocalTextStyle.current.copy(fontFamily=Mono),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Ascii),minLines=1,maxLines=3)
            result.exceptionOrNull()?.let{StudyNote(it.message?:"Check input.",true)}
        }
        result.getOrNull()?.let{r->
            StudyCard("One word, two interpretations"){
                StudyValue("${s.width}-BIT BINARY WORD",r.word.chunked(4).joinToString(" "));StudyValue("UNSIGNED",r.unsigned);StudyValue("TWO’S COMPLEMENT",r.signed);StudyValue("HEX · SAME WORD",r.hex)
                StudyNote("Selected decimal range: ${r.range}. No overflowing value is wrapped or silently truncated.")
            }
            StudyCard("One’s complement · invert every bit"){
                StudyValue("INVERTED WORD",r.ones.chunked(4).joinToString(" "));StudyValue("ONE’S-COMPLEMENT VALUE",r.onesSigned)
                StudyNote("One’s complement negates by flipping bits only — no +1. So zero has two faces: all-0s (+0) and all-1s (−0).")
            }
            StudyCard("Codes are not the same as binary"){
                StudyValue("GRAY CODE OF THIS WORD",r.gray.chunked(4).joinToString(" "))
                StudyValue("BCD OF THE SELECTED DECIMAL VALUE",r.bcd?:"Not defined for negative values")
                StudyNote("Gray changes one bit between consecutive non-negative counts. BCD encodes each decimal digit separately; unused BCD nibbles 1010–1111 are not decimal digits.")
            }
            StudyCard("Show the working"){r.steps.forEachIndexed{i,text->StudyNote("${i+1}. $text")}
                Button(onClick={clipboard.setText(AnnotatedString("${r.width}-bit representations\nInput: ${s.input} (${s.mode})\nWord: ${r.word}\nUnsigned: ${r.unsigned}\nTwo’s complement: ${r.signed}\nOne’s complement: ${r.ones} = ${r.onesSigned}\nHex: ${r.hex}\nGray: ${r.gray}\nBCD: ${r.bcd?:"not defined"}\nRange: ${r.range}\n"+r.steps.joinToString("\n")));notify("Representation working copied.")},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Copy complete working",fontSize=12.sp)}
            }
        }
        StudyNote("This lab is fixed-width. The original Convert tab uses signed magnitude for negative values; it does not automatically mean two’s complement.")
    }
}

@Composable internal fun PracticeScreen(vm:RadixViewModel){
    val haptic=LocalHapticFeedback.current
    var filter by remember{mutableStateOf("All")};var chooser by remember{mutableStateOf(false)};var reset by remember{mutableStateOf(false)}
    val tierNames=listOf("All levels","Easy","Intermediate","Advanced")
    val options=PracticeCatalog.questions.filter{(vm.practiceTier==0||it.tier==vm.practiceTier-1)&&(filter=="All"||it.category==filter)};val q=PracticeCatalog.questions[vm.practiceIndex]
    var chosen by remember(q.id){mutableStateOf<Int?>(null)};var checked by remember(q.id){mutableStateOf(false)};var hint by remember(q.id){mutableStateOf(false)};var revealed by remember(q.id){mutableStateOf(false)}
    StudyPage("Try the idea.","Keep the lesson.","YOUR LOGIC PRACTICE HUB","Hints before answers. Understanding before scores."){
        StudyCard("${vm.practiceProgress.size} of ${PracticeCatalog.questions.size} mastered · Level ${vm.gameLevel}"){
            StudyNote("${vm.gameXp} XP as ${vm.gameProfiles.getOrNull(vm.gameActive)?.name?:"Player"} · Easy 10, Intermediate 20, Advanced 30 per card + streak bonus.")
            vm.lastAward?.let{award->Row(verticalAlignment=Alignment.CenterVertically){Text(award,color=accent(),fontSize=16.sp,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f));TextButton(onClick=vm::clearAward,modifier=Modifier.heightIn(min=actionHeight())){Text("Nice!",fontSize=11.sp)}}}
            StudyNote("A correct check counts as mastered. Revealing an answer does not. Streaks celebrate practice days — they never punish a missed day.")
            FlowRow(horizontalArrangement=Arrangement.spacedBy(7.dp)){tierNames.forEachIndexed{tier,name->TextButton(onClick={vm.setPracticeTier(tier)},modifier=Modifier.heightIn(min=actionHeight())){Text(name,fontSize=11.sp,fontWeight=if(tier==vm.practiceTier)FontWeight.Bold else FontWeight.Normal,color=if(tier==vm.practiceTier)accent()else muted())}}}
            StudyNote("${options.size} cards at this level · ${options.count{it.id in vm.practiceProgress}} mastered here")
            FlowRow(horizontalArrangement=Arrangement.spacedBy(7.dp)){(listOf("All")+PracticeCatalog.questions.map{it.category}.distinct()).forEach{cat->TextButton(onClick={filter=cat;val next=PracticeCatalog.questions.indexOfFirst{(vm.practiceTier==0||it.tier==vm.practiceTier-1)&&(cat=="All"||it.category==cat)};if(next>=0)vm.selectQuestion(next)},modifier=Modifier.heightIn(min=actionHeight())){Text(cat,fontSize=11.sp,color=if(cat==filter)accent()else muted())}}}
            Box{OutlinedButton(onClick={chooser=true},enabled=options.isNotEmpty(),modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Choose study card · ${vm.practiceIndex+1}/${PracticeCatalog.questions.size}",fontSize=12.sp)};DropdownMenu(expanded=chooser,onDismissRequest={chooser=false}){options.forEach{question->DropdownMenuItem(text={Text((if(question.id in vm.practiceProgress)"✓ "else"")+question.title,fontSize=13.sp)},onClick={vm.selectQuestion(PracticeCatalog.questions.indexOf(question));chooser=false})}}}
        }
        StudyCard(q.title){Text((q.category+" · "+listOf("EASY","INTERMEDIATE","ADVANCED")[q.tier]).uppercase(),fontSize=10.sp,color=accent(),letterSpacing=1.sp);Text(q.prompt,fontSize=17.sp,lineHeight=25.sp)
            q.choices.forEachIndexed{i,answer->OutlinedButton(onClick={chosen=i;checked=false},enabled=!revealed,shape=RoundedCornerShape(13.dp),border=BorderStroke(if(chosen==i)2.dp else 1.dp,if(chosen==i)accent()else outline()),modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text(answer,fontSize=13.sp,modifier=Modifier.fillMaxWidth())}}
            Button(onClick={checked=true;if(chosen==q.answer&&!revealed){vm.completeQuestion(q.id);haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)}},enabled=chosen!=null&&!revealed,modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Check my answer",fontSize=13.sp)}
            if(checked)Text(if(chosen==q.answer)"✓ Correct. Here’s why."else"Not yet. Try the hint and check again.",color=if(chosen==q.answer)accent()else MaterialTheme.colorScheme.error,fontSize=14.sp,fontWeight=FontWeight.Bold)
            if(checked&&chosen==q.answer||revealed)StudyNote(q.explanation)
            Row(horizontalArrangement=Arrangement.spacedBy(9.dp)){TextButton(onClick={hint=!hint},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text(if(hint)"Hide hint"else"Show a hint",fontSize=12.sp)};TextButton(onClick={revealed=true},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("Reveal & explain",fontSize=12.sp)}}
            if(hint)StudyNote(q.hint);if(revealed)Text("Answer: ${q.choices[q.answer]}",color=accent(),fontSize=14.sp)
            if(options.isEmpty())StudyNote("No cards combine this level with this topic — loosen a filter to continue.")
            else Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){OutlinedButton(onClick={val i=options.indexOf(q);vm.selectQuestion(PracticeCatalog.questions.indexOf(options[(i-1+options.size)%options.size]))},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("← Previous",fontSize=11.sp)};Button(onClick={val i=options.indexOf(q);vm.selectQuestion(PracticeCatalog.questions.indexOf(options[(i+1)%options.size]))},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("Next idea →",fontSize=11.sp)}}
        }
        TextButton(onClick={reset=true},modifier=Modifier.heightIn(min=actionHeight())){Text("Reset practice progress",fontSize=12.sp)}
    }
    if(reset)AlertDialog(onDismissRequest={reset=false},title={Text("Reset mastered cards?")},text={Text("Only practice progress is cleared. Circuits, conversions and K-maps stay unchanged.")},confirmButton={TextButton(onClick={vm.resetProgress();reset=false}){Text("Reset progress")}},dismissButton={TextButton(onClick={reset=false}){Text("Keep progress")}})
}

@Composable internal fun SessionsScreen(vm:RadixViewModel,notify:(String)->Unit){
    var deletion by remember{mutableStateOf<CircuitSession?>(null)}
    StudyPage("Save the state.","Return to the idea.","YOUR CIRCUIT CHECKPOINTS","Named, offline and ready when you are."){
        StudyNote("Save a checkpoint from Logic. A saved session restores its model, inputs, clock and memory; its timing trace starts fresh. Up to 24 sessions stay on this device.")
        Spacer(Modifier.height(16.dp))
        if(vm.sessions.isEmpty())StudyCard("Your first checkpoint awaits") {StudyNote("Open Logic, explore a circuit, then tap Save session.");Button(onClick={vm.tab=4},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Open Logic")}}
        vm.sessions.forEach{session->StudyCard(session.name){StudyNote("${session.model} · ${session.kind} · inputs ${session.inputs.joinToString(""){if(it)"1"else"0"}}${if(session.kind=="sequential")" · Q=${LogicEngine.binary(session.bits,session.width)} · CLK=${if(session.clock)1 else 0}"else""}");Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){Button(onClick={vm.restoreSession(session);notify("Session restored; fresh timing trace.")},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("Restore",fontSize=12.sp)};OutlinedButton(onClick={deletion=session},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("Delete",fontSize=12.sp)}}}}
    }
    deletion?.let{s->AlertDialog(onDismissRequest={deletion=null},title={Text("Delete ${s.name}?")},text={Text("This removes the saved checkpoint, not the current circuit. Export a backup first if you want a copy.")},confirmButton={TextButton(onClick={vm.deleteSession(s.id);deletion=null}){Text("Delete")}},dismissButton={TextButton(onClick={deletion=null}){Text("Keep")}})}
}

@Composable internal fun PreferencesScreen(vm:RadixViewModel,notify:(String)->Unit){
    val context=LocalContext.current;val scope=rememberCoroutineScope();val clipboard=LocalClipboardManager.current
    var draft by remember{mutableStateOf("")};var checked by remember{mutableStateOf<StudyBackup?>(null)};var error by remember{mutableStateOf<String?>(null)};var confirm by remember{mutableStateOf(false)};var exporting by remember{mutableStateOf("")}
    val create=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")){uri->if(uri!=null)scope.launch{val result=runCatching{withContext(Dispatchers.IO){context.contentResolver.openOutputStream(uri)?.use{it.write(exporting.toByteArray(Charsets.UTF_8))}?:kotlin.error("Cannot open output file")}};notify(if(result.isSuccess)"Offline backup file saved."else"Could not save backup: ${result.exceptionOrNull()?.message}")}}
    val open=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri->if(uri!=null)scope.launch{val result=runCatching{withContext(Dispatchers.IO){context.contentResolver.openInputStream(uri)?.use{stream->val out=java.io.ByteArrayOutputStream();val buffer=ByteArray(4096);while(true){val count=stream.read(buffer);if(count<0)break;require(out.size()+count<=262144){"Backup exceeds 256 KiB."};out.write(buffer,0,count)};out.toString("UTF-8")}?:kotlin.error("Cannot open backup")}};result.fold(onSuccess={draft=it;checked=null;error=null;notify("Backup loaded. Validate before restoring.")},onFailure={error=it.message})}}
    StudyPage("Make it yours.","Keep it safe.","PREFERENCES & OFFLINE BACKUP","No cloud. No account. Your study data stays yours."){
        StudyCard("Comfort controls"){
            Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Haptic feedback",fontSize=15.sp);StudyNote("Allow short native tap feedback")};Switch(checked=vm.haptics,onCheckedChange=vm::setHaptics,modifier=Modifier.semantics{contentDescription="Allow haptic feedback"})}
            Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Larger learning controls",fontSize=15.sp);StudyNote("More room for circuit inputs and study actions; system font scaling is respected")};Switch(checked=vm.largeControls,onCheckedChange=vm::setLargeControls,modifier=Modifier.semantics{contentDescription="Use larger learning controls"})}
        }
        StudyCard("Look & language"){
            ThemeSection(vm)
            HorizontalDivider(color=outline(),modifier=Modifier.padding(vertical=6.dp))
            LanguageSection(vm)
        }
        StudyCard("Reminders & inbox"){
            ReminderSection(vm,notify)
            HorizontalDivider(color=outline(),modifier=Modifier.padding(vertical=6.dp))
            InboxSection(vm)
        }
        StudyCard("Updates & version"){
            UpdateSection(vm,notify)
        }
        StudyCard("Sync via your cloud drive"){
            StudyNote("Radix runs no cloud of its own — instead, save the portable backup file straight into Google Drive, Dropbox or any folder the system picker offers. Reopen that same file on another device (or after reinstalling) to continue exactly where you stopped. Same validation, same privacy: the file is yours, unencrypted JSON, and Radix never uploads anything by itself.")
            Button(onClick={exporting=vm.backupText();create.launch("Radix-study-backup.json")},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Save backup into Drive…",fontSize=12.sp)}
            OutlinedButton(onClick={open.launch(arrayOf("application/json","text/plain"))},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Open backup from Drive…",fontSize=12.sp)}
        }
        StudyCard("Create a portable backup"){
            StudyNote("Includes saved conversions, circuit sessions, mastered practice cards, the current K-map/lab drafts and comfort preferences. Theme, language, reminder time and inbox stay on this device. Circuit traces and signing keys/passwords are never included. JSON is NOT encrypted; store it privately.")
            Button(onClick={exporting=vm.backupText();create.launch("Radix-study-backup.json")},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Save backup file",fontSize=12.sp)}
            OutlinedButton(onClick={draft=vm.backupText();checked=null;error=null;clipboard.setText(AnnotatedString(draft));notify("Backup text copied; paste it somewhere private.")},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Copy backup text",fontSize=12.sp)}
        }
        StudyCard("Validate, then restore"){
            OutlinedButton(onClick={open.launch(arrayOf("application/json","text/plain"))},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Choose backup file",fontSize=12.sp)}
            OutlinedTextField(value=draft,onValueChange={draft=it.take(262144);checked=null;error=null},label={Text("Or paste backup JSON")},modifier=Modifier.fillMaxWidth(),minLines=4,maxLines=7,textStyle=LocalTextStyle.current.copy(fontFamily=Mono,fontSize=11.sp))
            Button(onClick={runCatching{StudyData.decode(draft)}.fold(onSuccess={checked=it;error=null},onFailure={checked=null;error=it.message})},enabled=draft.isNotBlank(),modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Validate backup",fontSize=12.sp)}
            error?.let{StudyNote(it,true)}
            checked?.let{b->StudyNote("✓ Valid: ${b.saved.size} conversions · ${b.sessions.size} sessions · ${b.progress.size} mastered cards · ${b.kmap.variables}-variable ${b.kmap.form} K-map.");OutlinedButton(onClick={confirm=true},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Review replacement & restore",fontSize=12.sp)}}
        }
    }
    if(confirm)AlertDialog(onDismissRequest={confirm=false},title={Text("Replace local study data?")},text={Text("The validated backup REPLACES your saved conversions, sessions, progress, K-map, lab drafts and preferences. Export a backup of your current data first. The active circuit is not changed.")},confirmButton={TextButton(onClick={checked?.let{vm.restoreBackup(it)};confirm=false;checked=null;notify("Validated study data restored.")}){Text("Replace & restore")}},dismissButton={TextButton(onClick={confirm=false}){Text("Cancel")}})
}

@Composable internal fun SaveSessionDialog(vm:RadixViewModel,notify:(String)->Unit,dismiss:()->Unit){
    var name by remember{mutableStateOf(when(vm.logicSection){0->vm.gateId;1->LogicCatalog.circuits.first{it.id==vm.circuitId}.title;else->LogicCatalog.sequential.first{it.id==vm.sequentialId}.title})}
    var error by remember{mutableStateOf<String?>(null)}
    AlertDialog(onDismissRequest=dismiss,title={Text("Save a circuit checkpoint")},text={Column{OutlinedTextField(value=name,onValueChange={name=it.filter{c->c.code>=32}.take(40);error=null},label={Text("Session name · up to 40 characters")},modifier=Modifier.fillMaxWidth());StudyNote("Current inputs, clock and stored memory are kept. The trace starts fresh on restore.");error?.let{StudyNote(it,true)};if(vm.sessions.size>=24)StudyNote("Library is full (24). Back up/delete an older session first.",true)}},confirmButton={TextButton(enabled=name.isNotBlank()&&vm.sessions.size<24,onClick={runCatching{vm.saveCircuitSession(name)}.fold(onSuccess={notify("Circuit checkpoint saved.");dismiss()},onFailure={error=it.message})}){Text("Save session")}},dismissButton={TextButton(onClick=dismiss){Text("Cancel")}})
}
