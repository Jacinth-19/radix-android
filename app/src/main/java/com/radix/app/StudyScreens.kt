@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.radix.app

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.stateDescription
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
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).testTag("study-page-scroll").padding(horizontal=20.dp).padding(bottom=24.dp),horizontalAlignment=Alignment.CenterHorizontally){
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
    val s=vm.expression;val clipboard=LocalClipboardManager.current;val haptic=LocalHapticFeedback.current;val strings=LocalStrings.current;val language=strings.languageCode
    var bits by remember(s.variables){mutableStateOf(List(s.variables){it==0})}
    var submitted by remember(s.left,s.right,s.variables){mutableStateOf(false)}
    var resultTab by remember(s.left,s.variables){mutableIntStateOf(0)}
    var resultForm by remember(s.left,s.variables,vm.expressionResultForm){mutableStateOf(vm.expressionResultForm)}
    var highlightedGroup by remember(s.left,s.variables){mutableIntStateOf(-1)}
    var showGroups by remember(s.left,s.variables){mutableStateOf(true)}
    var showIndices by remember(s.left,s.variables){mutableStateOf(true)}
    var savedOpen by remember{mutableStateOf(false)}
    val report=remember(s.left,s.variables){runCatching{ExpressionEngine.report(s.left,s.variables)}}
    val comparison=remember(s.left,s.right,s.variables){if(s.right.isBlank())null else runCatching{ExpressionEngine.compare(s.left,s.right,s.variables)}}
    val r=report.getOrNull()
    val sop=remember(r?.cells,s.variables,r?.names){r?.let{KMapEngine.solve(s.variables,it.cells,KMapEngine.Form.SOP,it.names.joinToString(""))}}
    val pos=remember(r?.cells,s.variables,r?.names){r?.let{KMapEngine.solve(s.variables,it.cells,KMapEngine.Form.POS,it.names.joinToString(""))}}
    val selectedSolution=if(resultForm==KMapEngine.Form.SOP)sop else pos
    val liveCode=if(bits.size==s.variables)LogicEngine.code(bits)else 0
    val liveOutput=r?.cells?.getOrNull(liveCode)
    StudyPage("Write logic.","Prove the idea.","THE BOOLEAN EXPRESSION LAB","Enter, solve, verify and follow every rule."){
        StudyCard("Your expression"){
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)){
                (2..6).forEach{n->OutlinedButton(onClick={vm.setExpression(s.copy(variables=n))},modifier=Modifier.width(70.dp).heightIn(min=actionHeight()).semantics{selected=n==s.variables}){Text("$n vars",fontSize=11.sp,color=if(n==s.variables)accent()else muted())}}
            }
            OutlinedTextField(value=s.left,onValueChange={vm.setExpression(s.copy(left=it))},label={Text("Expression F · A–Z, up to 6 variables")},textStyle=LocalTextStyle.current.copy(fontFamily=Mono),modifier=Modifier.fillMaxWidth(),minLines=2,maxLines=4)
            Text("Type any uppercase variable from A to Z, or tap a letter. Choose at least as many input lines as distinct variables.",fontSize=10.sp,color=muted(),lineHeight=16.sp)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(5.dp)){
                ('A'..'Z').forEach{letter->OutlinedButton(onClick={vm.setExpression(s.copy(left=s.left+letter))},modifier=Modifier.width(45.dp).heightIn(min=actionHeight()),contentPadding=PaddingValues(horizontal=4.dp)){Text(letter.toString(),fontFamily=Mono,fontSize=12.sp)}}
            }
            FlowRow(horizontalArrangement=Arrangement.spacedBy(7.dp),verticalArrangement=Arrangement.spacedBy(5.dp)){
                listOf("NOT !" to "!","OR +" to " + ","AND ·" to " · ","XOR ⊕" to " ⊕ ","XNOR ⊙" to " ⊙ ","′" to "′","(" to "(",")" to ")","0" to "0","1" to "1").forEach{(label,token)->
                    OutlinedButton(onClick={vm.setExpression(s.copy(left=s.left+token))},modifier=Modifier.heightIn(min=actionHeight()),contentPadding=PaddingValues(horizontal=10.dp)){Text(label,fontFamily=Mono,fontSize=11.sp)}
                }
            }
            StudyNote("Type A–Z; NOT is apostrophe/prime or !, OR is +, AND is . or adjacency, XOR is XOR/^/⊕, and XNOR is XNOR/⊙. Parentheses group expressions; the buttons are optional shortcuts.")
            StudyNote("Precedence: NOT, AND (including adjacency), XOR/XNOR, then OR. Parentheses override the order. No scripting is executed.")
            FlowRow(horizontalArrangement=Arrangement.spacedBy(7.dp),verticalArrangement=Arrangement.spacedBy(4.dp)){
                listOf("XOR" to "A'B + AB'","De Morgan" to "!(A B)","Distribute" to "A(B+C)").forEach{(name,text)->TextButton(onClick={vm.setExpression(s.copy(left=text,variables=if(name=="Distribute")maxOf(3,s.variables)else s.variables))},modifier=Modifier.heightIn(min=actionHeight())){Text(name,fontSize=12.sp)}}
                TextButton(onClick={vm.setExpression(s.copy(left=""))},modifier=Modifier.heightIn(min=actionHeight())){Text("Clear",fontSize=12.sp)}
                TextButton(onClick={savedOpen=true},modifier=Modifier.heightIn(min=actionHeight())){Text("Saved solutions · ${vm.savedExpressions.size}",fontSize=12.sp)}
            }
            report.exceptionOrNull()?.let{StudyNote(it.message?:"Check the expression.",true)}
            OutlinedTextField(value=s.right,onValueChange={vm.setExpression(s.copy(right=it))},label={Text("Optional expression G · compare for equivalence")},textStyle=LocalTextStyle.current.copy(fontFamily=Mono),modifier=Modifier.fillMaxWidth(),minLines=1,maxLines=3)
            Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){
                Button(onClick={submitted=true;resultTab=0;haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove);notify(if(report.isSuccess)"Expression solved across all ${1 shl s.variables} input rows."else"Fix the expression before solving.")},enabled=s.left.isNotBlank(),modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("Solve",fontSize=12.sp)}
                OutlinedButton(onClick={submitted=true;resultTab=0;haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove);notify(if(s.right.isBlank())"Enter expression G to verify equivalence."else"Equivalence checked across all ${1 shl s.variables} input rows.")},enabled=s.left.isNotBlank()&&s.right.isNotBlank(),modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("Verify",fontSize=12.sp)}
            }
        }
        if(submitted&&r!=null){
            StudyCard("Live input values"){
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    bits.forEachIndexed{i,v->Surface(
                        modifier=Modifier.width(58.dp).heightIn(min=if(vm.largeControls)78.dp else 64.dp)
                            .toggleable(value=v,role=Role.Switch){checked->
                                bits=bits.mapIndexed{j,b->if(i==j)checked else b}
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            .testTag("expression-input-${r.names[i]}")
                            .semantics{
                                contentDescription=strings.text("Expression input ${r.names[i]} ${if(v)1 else 0}. Tap to toggle.")
                                stateDescription=strings.text(if(v)"On"else"Off")
                            },
                        color=MaterialTheme.colorScheme.secondaryContainer,
                        contentColor=MaterialTheme.colorScheme.onSecondaryContainer,
                        shape=RoundedCornerShape(13.dp)
                    ){
                        Column(horizontalAlignment=Alignment.CenterHorizontally){Text(r.names[i].toString(),fontSize=10.sp);Text(if(v)"1"else"0",fontFamily=Mono,fontSize=25.sp)}
                    }}
                }
                StudyValue("F · CURRENT OUTPUT",liveOutput?.toString()?:"—")
                StudyNote("Parsed as ${r.normalized}. The live switches drive both the output and the gate-level diagram.")
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){
                    StudyValue("MINIMUM SOP",r.sop);StudyValue("MINIMUM POS",r.pos)
                }
                StudyValue("MINTERMS · Σm",r.sigmaM);StudyValue("MAXTERMS · ΠM",r.piM)
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){
                    OutlinedButton(onClick={vm.logicToKMap(s.variables,r.cells,KMapEngine.Form.SOP,r.names.joinToString(""))},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("Open SOP map",fontSize=11.sp)}
                    OutlinedButton(onClick={vm.logicToKMap(s.variables,r.cells,KMapEngine.Form.POS,r.names.joinToString(""))},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("Open POS map",fontSize=11.sp)}
                }
            }
            if(s.right.isNotBlank())StudyCard("Equivalence verification"){
                comparison?.fold(onSuccess={c->
                    Text(if(c.equivalent)"✓ Equivalent in every row"else"Not equivalent · ${c.mismatches.size} counterexample row${if(c.mismatches.size==1)""else"s"}",color=if(c.equivalent)accent()else MaterialTheme.colorScheme.error,fontWeight=FontWeight.Bold,fontSize=15.sp)
                    if(!c.equivalent)StudyNote("Counterexamples (input order ${r.names.joinToString(" ")}): "+c.mismatches.joinToString{code->r.names.indices.joinToString(""){i->if(code and (1 shl (s.variables-1-i))!=0)"1"else"0"}})
                },onFailure={StudyNote(it.message?:"Check expression G.",true)})
                StudyNote("The verifier compares all ${1 shl s.variables} input combinations, not only the switches shown above.")
            }
            StudyCard("Solution workspace"){
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(5.dp)){
                    listOf("Steps","Truth table","K-map").forEachIndexed{index,label->
                        val tabModifier=Modifier.weight(1f).heightIn(min=actionHeight())
                            .then(if(index==2)Modifier.testTag("expression-kmap-tab")else Modifier)
                            .semantics{selected=index==resultTab}
                        if(index==resultTab)Button(onClick={resultTab=index},modifier=tabModifier,contentPadding=PaddingValues(horizontal=3.dp)){Text(label,fontSize=10.sp,maxLines=1)}
                        else OutlinedButton(onClick={resultTab=index},modifier=tabModifier,contentPadding=PaddingValues(horizontal=3.dp)){Text(label,fontSize=10.sp,maxLines=1)}
                    }
                }
                when(resultTab){
                    0->ExpressionSteps(r,s.left,checkNotNull(sop),checkNotNull(pos))
                    1->ExpressionTruthTable(r,liveCode)
                    else->ExpressionKMapTab(r,s.left,resultForm,selectedSolution,highlightedGroup,showGroups,showIndices,liveCode,
                        onForm={resultForm=it;vm.setExpressionResultForm(it);highlightedGroup=-1},onHighlight={highlightedGroup=it},onGroups={showGroups=it},onIndices={showIndices=it})
                }
                val copyText=when(resultTab){
                    0->expressionStepsText(s.left,r,checkNotNull(sop),checkNotNull(pos))
                    1->expressionTableText(s.left,r)
                    else->expressionMapText(s.left,r,resultForm,checkNotNull(selectedSolution))
                }
                val copyLabel=when(resultTab){0->"Copy derivation";1->"Copy TSV truth table";else->"Copy K-map working"}
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    OutlinedButton(onClick={clipboard.setText(AnnotatedString(AppStrings.translateBlock(copyText,language)));haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove);notify("${copyLabel.removePrefix("Copy ")} copied.")},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text(copyLabel,fontSize=11.sp)}
                    Button(onClick={val added=runCatching{vm.saveExpressionSolution(s,resultForm)}.getOrElse{notify(it.message?:"Could not save this solution.");false};if(added)notify("Boolean solution saved on this device and included in backups.")else notify("That solution is already saved; it was moved to the top.")},enabled=vm.savedExpressions.size<50||vm.savedExpressions.any{it.variables==s.variables&&it.expression==s.left.trim()&&it.form==resultForm.name},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("Save solution",fontSize=11.sp)}
                }
            }
        }else if(submitted){
            StudyCard("Solution paused") {StudyNote(report.exceptionOrNull()?.message?:"Correct the expression, then solve again.",true)}
        }else StudyCard("Ready when you are") {StudyNote("Enter an expression and tap Solve. Add expression G and tap Verify to compare every row. The three result tabs appear after a valid solve.")}
    }
    if(savedOpen)SavedExpressionSheet(vm,notify){savedOpen=false}
}

@Composable private fun ExpressionSteps(r:ExpressionEngine.Report,source:String,sop:KMapEngine.Solution,pos:KMapEngine.Solution){
    StudyCard("01 · Parse and apply operator rules"){
        StudyNote("Read parentheses first, then NOT, AND (including adjacency), XOR/XNOR, and OR. The parser rejects unknown symbols and never executes code.")
        StudyValue("INPUT F",source)
        StudyValue("PARSED EXPRESSION",r.normalized)
    }
    StudyCard("02 · Evaluate every input combination"){
        StudyNote("Evaluate all ${1 shl r.names.size} rows in binary order. Rows with output 1 become minterms; rows with output 0 become maxterms.")
        StudyValue("Σm · ON SET",r.sigmaM);StudyValue("ΠM · OFF SET",r.piM)
    }
    StudyCard("03 · Minimize the ones for SOP"){
        StudyNote("SOP groups required 1s. A literal that changes within a group is removed; a fixed 0 is complemented and a fixed 1 is not.")
        if(sop.groups.isEmpty())StudyNote("No 1-cells: the minimum SOP is the constant 0.")
        sop.groups.forEachIndexed{i,g->
            StudyNote("G${i+1}: cells ${g.cube.indices(1 shl r.names.size).joinToString(", ")} → ${g.term} ${if(g.wraps)"(opposite edges wrap)"else""}${if(g.essential)" · essential"else""}")
            g.explanation.forEach{StudyNote("  • $it")}
        }
        StudyValue("MINIMUM SOP",r.sop)
    }
    StudyCard("04 · Minimize the zeros for POS"){
        StudyNote("POS groups required 0s. A fixed 0 stays uncomplemented; a fixed 1 is complemented in its sum clause. Opposite map edges are adjacent.")
        if(pos.groups.isEmpty())StudyNote("No 0-cells: the minimum POS is the constant 1.")
        pos.groups.forEachIndexed{i,g->
            StudyNote("G${i+1}: cells ${g.cube.indices(1 shl r.names.size).joinToString(", ")} → (${g.term}) ${if(g.wraps)"(opposite edges wrap)"else""}${if(g.essential)" · essential"else""}")
            g.explanation.forEach{StudyNote("  • $it")}
        }
        StudyValue("MINIMUM POS",r.pos)
    }
    StudyCard("05 · Check both answers against the table"){
        StudyNote("The exact minimizer covers every required cell, uses no forbidden cell, then minimizes term count and literal count. XNOR and NAND/NOR are already evaluated by the parser.")
        StudyValue("Σm",r.sigmaM);StudyValue("ΠM",r.piM)
        StudyNote("Original: $source")
    }
}

@Composable private fun ExpressionTruthTable(r:ExpressionEngine.Report,current:Int){
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(14.dp)){
        r.names.forEach{Text(it.toString(),fontFamily=Mono,fontSize=11.sp,fontWeight=FontWeight.Bold,color=muted())}
        Text("F",fontFamily=Mono,fontSize=11.sp,fontWeight=FontWeight.Bold,color=muted())
    }
    r.cells.forEachIndexed{code,value->
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).background(if(code==current)accent().copy(alpha=.10f)else Color.Transparent).padding(vertical=7.dp),horizontalArrangement=Arrangement.spacedBy(14.dp)){
            r.names.indices.forEach{i->Text(if(code and (1 shl (r.names.size-1-i))!=0)"1"else"0",fontFamily=Mono,fontSize=13.sp,modifier=Modifier.width(12.dp))}
            Text(value.toString(),fontFamily=Mono,fontSize=13.sp,color=if(value==1)accent()else muted())
        }
    }
    StudyNote("Highlighted row follows the live input switches. All ${r.cells.size} rows remain available for copying.")
}

@Composable private fun ExpressionKMapTab(r:ExpressionEngine.Report,source:String,form:KMapEngine.Form,solution:KMapEngine.Solution?,highlight:Int,showGroups:Boolean,showIndices:Boolean,current:Int,onForm:(KMapEngine.Form)->Unit,onHighlight:(Int)->Unit,onGroups:(Boolean)->Unit,onIndices:(Boolean)->Unit){
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
        KMapFormButton("SOP · group ones",form==KMapEngine.Form.SOP,{onForm(KMapEngine.Form.SOP)},Modifier.weight(1f))
        KMapFormButton("POS · group zeros",form==KMapEngine.Form.POS,{onForm(KMapEngine.Form.POS)},Modifier.weight(1f))
    }
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
        KMapFormButton(if(showGroups)"Groups ✓"else"Show groups",showGroups,{onGroups(!showGroups)},Modifier.weight(1f))
        KMapFormButton(if(showIndices)"Indices ✓"else"Show indices",showIndices,{onIndices(!showIndices)},Modifier.weight(1f))
    }
    StudyValue(if(form==KMapEngine.Form.SOP)"MINTERMS · Σm"else"MAXTERMS · ΠM",if(form==KMapEngine.Form.SOP)r.sigmaM else r.piM)
    StudyValue(if(form==KMapEngine.Form.SOP)"SOP MINIMUM"else"POS MINIMUM",if(form==KMapEngine.Form.SOP)r.sop else r.pos)
    if(solution==null){StudyNote("The map is unavailable until the expression is valid.",true);return}
    ExpressionMapGrid(r.names.joinToString(""),r.cells,solution,form,highlight,showGroups,showIndices,current)
            if(solution.groups.isEmpty())StudyNote("No required ${if(form==KMapEngine.Form.SOP)"1"else"0"}-cells; the constant answer needs no groups.")
    else{
        Text("SELECTED PRIME ${if(form==KMapEngine.Form.SOP)"IMPLICANTS"else"IMPLICATES"} · ${solution.groups.size}",fontSize=10.sp,color=muted(),letterSpacing=1.sp,modifier=Modifier.padding(top=10.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)){
            OutlinedButton(onClick={onHighlight(-1)},modifier=Modifier.heightIn(min=actionHeight()).semantics{selected=highlight<0}){Text("All groups",fontSize=11.sp)}
            solution.groups.forEachIndexed{i,g->OutlinedButton(onClick={onHighlight(if(highlight==i)-1 else i)},border=BorderStroke(if(highlight==i)2.dp else 1.dp,expressionGroupColors[i%expressionGroupColors.size]),modifier=Modifier.heightIn(min=actionHeight()).semantics{selected=highlight==i}){Text("G${i+1} · ${if(form==KMapEngine.Form.POS)"(${g.term})"else g.term}",fontSize=10.sp,fontFamily=Mono)}}
        }
        solution.groups.forEachIndexed{i,g->
            StudyCard("G${i+1} · ${if(form==KMapEngine.Form.POS)"(${g.term})"else g.term}"){
                StudyNote("Cells ${g.cube.indices(1 shl r.names.size).joinToString(", ")} · ${java.lang.Long.bitCount(g.cube.members)} cells${if(g.wraps)" · wraps an edge"else""}${if(g.essential)" · essential"else""}")
                g.explanation.forEach{StudyNote(it)}
                StudyNote(if(g.essential)"At least one required cell has no other maximal covering group."else"Chosen to finish a minimum cover. Overlap is allowed.")
            }
        }
    }
    ExpressionCircuitDiagram(r,solution,form,current)
    StudyNote("K-map variables are shown in the expression's first-appearance order; the leftmost listed input is the most significant bit.")
}

private val expressionGroupColors=listOf(Color(0xFF8AB9F4),Color(0xFFEDA5C5),Color(0xFFB3D852),Color(0xFFBFA6F2),Color(0xFFE7BA76),Color(0xFF75C7B9),Color(0xFFB7C9F0),Color(0xFFE49D85))

@Composable private fun KMapFormButton(label:String,selected:Boolean,onClick:()->Unit,modifier:Modifier=Modifier){
    val controlModifier=modifier.heightIn(min=actionHeight()).semantics{this.selected=selected}
    if(selected)Button(onClick=onClick,modifier=controlModifier,contentPadding=PaddingValues(horizontal=5.dp)){Text(label,fontSize=10.sp,maxLines=1)}
    else OutlinedButton(onClick=onClick,modifier=controlModifier,contentPadding=PaddingValues(horizontal=5.dp)){Text(label,fontSize=10.sp,maxLines=1)}
}

@Composable private fun ExpressionMapGrid(names:String,cells:List<Int>,solution:KMapEngine.Solution,form:KMapEngine.Form,highlight:Int,showGroups:Boolean,showIndices:Boolean,current:Int){
    val n=names.length;val rowBits=KMapEngine.rowBits(n);val rows=KMapEngine.gray(rowBits);val cols=KMapEngine.gray(KMapEngine.columnBits(n))
    BoxWithConstraints(Modifier.fillMaxWidth()){
        val width=if(n>=5)maxOf(maxWidth,548.dp)else maxWidth
        Column(Modifier.horizontalScroll(rememberScrollState())){
            Column(Modifier.width(width)){
                Row(horizontalArrangement=Arrangement.spacedBy(5.dp),verticalAlignment=Alignment.CenterVertically){
                    Text("${names.take(rowBits)}/${names.drop(rowBits)}",fontFamily=Mono,fontSize=9.sp,color=muted(),modifier=Modifier.width(42.dp))
                    cols.forEach{Text(KMapEngine.binary(it,KMapEngine.columnBits(n)),fontFamily=Mono,fontSize=11.sp,color=muted(),modifier=Modifier.weight(1f))}
                }
                rows.forEachIndexed{row,label->Row(horizontalArrangement=Arrangement.spacedBy(5.dp),verticalAlignment=Alignment.CenterVertically,modifier=Modifier.padding(bottom=5.dp)){
                    Text(KMapEngine.binary(label,rowBits),fontFamily=Mono,fontSize=11.sp,color=muted(),modifier=Modifier.width(42.dp))
                    cols.forEachIndexed{column,_->
                        val index=KMapEngine.cellAt(n,row,column);val value=cells[index]
                        val groups=if(showGroups)solution.groups.indices.filter{solution.groups[it].cube.contains(index)&&(highlight<0||highlight==it)}else emptyList()
                        val border=when{groups.isNotEmpty()->BorderStroke(2.dp,expressionGroupColors[groups.first()%expressionGroupColors.size]);index==current->BorderStroke(2.dp,accent());else->BorderStroke(1.dp,outline())}
                        Surface(shape=RoundedCornerShape(10.dp),color=when(value){1->accent().copy(alpha=.13f);2->MaterialTheme.colorScheme.secondaryContainer.copy(alpha=.55f);else->MaterialTheme.colorScheme.surfaceVariant},border=border,modifier=Modifier.weight(1f).heightIn(min=66.dp)){
                            Column(Modifier.padding(vertical=5.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
                                if(showIndices)Text("${if(form==KMapEngine.Form.SOP)"m"else"M"}$index",fontFamily=Mono,fontSize=9.sp,color=muted())
                                Text(if(value==2)"X"else value.toString(),fontFamily=Mono,fontSize=21.sp,fontWeight=FontWeight.Bold,color=if(value==1)accent()else MaterialTheme.colorScheme.onSurface)
                                Text(if(groups.isNotEmpty())groups.joinToString(" "){"G${it+1}"}else KMapEngine.binary(index,n),fontFamily=Mono,fontSize=8.sp,color=muted(),maxLines=1)
                            }
                        }
                    }
                }}
            }
        }
    }
}

@Composable private fun ExpressionCircuitDiagram(r:ExpressionEngine.Report,solution:KMapEngine.Solution,form:KMapEngine.Form,current:Int){
    val output=solution.evaluate(current)
    StudyCard("Gate-level circuit · live"){
        StudyNote("Inputs: ${r.names.indices.joinToString(" · "){i->"${r.names[i]}=${if(current and (1 shl (r.names.size-1-i))!=0)1 else 0}"}}. Group gates update as the switches change.")
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(7.dp)){
            solution.groups.forEachIndexed{i,g->
                val covered=g.cube.contains(current);val gateOut=if(form==KMapEngine.Form.SOP)covered else !covered
                Surface(shape=RoundedCornerShape(12.dp),color=MaterialTheme.colorScheme.surfaceVariant,border=BorderStroke(1.dp,expressionGroupColors[i%expressionGroupColors.size])){
                    Column(Modifier.widthIn(min=112.dp).padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally){
                        Text("${if(form==KMapEngine.Form.SOP)"AND"else"OR"} · G${i+1}",fontWeight=FontWeight.Bold,fontSize=11.sp)
                        Text(if(form==KMapEngine.Form.POS)"(${g.term})"else g.term,fontFamily=Mono,fontSize=12.sp,color=accent())
                        Text("out = ${if(gateOut)1 else 0}",fontFamily=Mono,fontSize=11.sp)
                    }
                }
                Text("→",color=muted(),fontSize=16.sp)
            }
            Surface(shape=RoundedCornerShape(12.dp),color=if(output)accent().copy(alpha=.18f)else MaterialTheme.colorScheme.surfaceVariant,border=BorderStroke(2.dp,accent())){
                Column(Modifier.widthIn(min=98.dp).padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(if(form==KMapEngine.Form.SOP)"OR → F"else"AND → F",fontWeight=FontWeight.Bold,fontSize=11.sp);Text(if(output)"1"else"0",fontFamily=Mono,fontSize=19.sp,color=accent())}
            }
        }
        StudyNote(if(form==KMapEngine.Form.SOP)"Each product term is an AND gate; their outputs feed the final OR."else"Each sum clause is an OR gate; their outputs feed the final AND.")
    }
}

@Composable internal fun SavedExpressionSheet(vm:RadixViewModel,notify:(String)->Unit,dismiss:()->Unit){
    ModalBottomSheet(onDismissRequest=dismiss,containerColor=panel(),sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true)){
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=28.dp)){
            Text("Saved Boolean solutions",fontFamily=Grotesk,fontSize=25.sp,fontWeight=FontWeight.Bold)
            StudyNote("Saved on this device, restored with portable study backups, and capped at 50 items.")
            if(vm.savedExpressions.isEmpty())StudyNote("Nothing saved yet. Solve an expression or K-map, then tap Save solution.")
            vm.savedExpressions.forEach{item->
                Surface(shape=RoundedCornerShape(14.dp),color=MaterialTheme.colorScheme.surfaceVariant,modifier=Modifier.fillMaxWidth().padding(top=8.dp)){
                    Column(Modifier.padding(12.dp)){
                        Text("${item.variables} variables · ${item.form}",fontSize=10.sp,color=muted())
                        Text(item.expression,fontFamily=Mono,fontSize=14.sp,modifier=Modifier.padding(top=4.dp))
                        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                            Button(onClick={vm.openSavedExpression(item);dismiss();notify("Saved expression opened.")},modifier=Modifier.weight(1f).heightIn(min=48.dp)){Text("Open",fontSize=12.sp)}
                            OutlinedButton(onClick={vm.deleteSavedExpression(item.id);notify("Saved solution removed.")},modifier=Modifier.weight(1f).heightIn(min=48.dp)){Text("Delete",fontSize=12.sp)}
                        }
                    }
                }
            }
        }
    }
}

private fun expressionStepsText(source:String,r:ExpressionEngine.Report,sop:KMapEngine.Solution,pos:KMapEngine.Solution)=buildString{
    appendLine("F(${r.names.joinToString(",")}) = $source");appendLine("Parsed: ${r.normalized}")
    appendLine("1. Precedence: parentheses → NOT → AND/adjacency → XOR/XNOR → OR.")
    appendLine("2. Evaluate ${1 shl r.names.size} rows: ${r.sigmaM}; ${r.piM}.")
    appendLine("3. SOP: group required ones; changing variables disappear, fixed 0s complement, fixed 1s do not.")
    sop.groups.forEachIndexed{i,g->appendLine("G${i+1}: cells ${g.cube.indices(1 shl r.names.size).joinToString(",")} → ${g.term}${if(g.wraps)" (edge wrap)"else""}");g.explanation.forEach{appendLine("  $it")}}
    appendLine("Minimum SOP: ${r.sop}")
    appendLine("4. POS: group required zeros; fixed 0s stay, fixed 1s complement.")
    pos.groups.forEachIndexed{i,g->appendLine("G${i+1}: cells ${g.cube.indices(1 shl r.names.size).joinToString(",")} → (${g.term})${if(g.wraps)" (edge wrap)"else""}");g.explanation.forEach{appendLine("  $it")}}
    appendLine("Minimum POS: ${r.pos}");appendLine("Exact cover: fewest terms/clauses, then fewest literals.")
}
private fun expressionTableText(source:String,r:ExpressionEngine.Report)=buildString{
    appendLine("F(${r.names.joinToString(",")}) = $source");appendLine("Parsed: ${r.normalized}");appendLine("SOP: ${r.sop}");appendLine("POS: ${r.pos}");appendLine(r.sigmaM);appendLine(r.piM)
    append(r.names.joinToString("\t"));append("\tF\n")
    r.cells.forEachIndexed{code,value->append(r.names.indices.joinToString("\t"){i->if(code and (1 shl (r.names.size-1-i))!=0)"1"else"0"});append("\t$value\n")}
}
private fun expressionMapText(source:String,r:ExpressionEngine.Report,form:KMapEngine.Form,solution:KMapEngine.Solution)=buildString{
    appendLine("F(${r.names.joinToString(",")}) = $source");appendLine("${form.name}: ${solution.expression}");appendLine("${if(form==KMapEngine.Form.SOP)r.sigmaM else r.piM}");appendLine("Selected prime ${if(form==KMapEngine.Form.SOP)"implicants"else"implicates"} (${solution.groups.size})");appendLine("Gray-code rows; leftmost variable is MSB.")
    solution.groups.forEachIndexed{i,g->appendLine("G${i+1}: cells ${g.cube.indices(1 shl r.names.size).joinToString(",")} → ${if(form==KMapEngine.Form.POS)"(${g.term})"else g.term}${if(g.wraps)" (wraps edge)"else""}");g.explanation.forEach{appendLine(it)}}
    appendLine("Gate-level: ${if(form==KMapEngine.Form.SOP)"AND groups feed OR"else"OR clauses feed AND"}.")
    appendLine("Truth table: ${r.names.joinToString(" ")} / F");r.cells.forEachIndexed{code,value->appendLine("${r.names.indices.joinToString(""){i->if(code and (1 shl (r.names.size-1-i))!=0)"1"else"0"}} / $value")}
}

@Composable internal fun LearningPathScreen(vm:RadixViewModel){
    val steps=listOf(
        Triple("Number systems & base conversion",listOf("Number bases","Arithmetic"),0),
        Triple("Signed numbers & representations",listOf("Representations"),1),
        Triple("Logic gates & Boolean laws",listOf("Gates","Laws"),2),
        Triple("Expressions, operators & truth tables",listOf("Expressions"),3),
        Triple("Karnaugh maps & minimization",listOf("K-maps"),4),
        Triple("Combinational circuits",listOf("Circuits","Builder"),5),
        Triple("Sequential logic & memory",listOf("Memory"),6)
    )
    val done=vm.practiceProgress.size;val total=PracticeCatalog.questions.size
    fun openStep(index:Int){when(index){
        0->vm.tab=0
        1->vm.tab=7
        2->{vm.logicSection=0;vm.persistLogic();vm.tab=4}
        3->vm.tab=6
        4->vm.tab=3
        5->{vm.logicSection=1;vm.persistLogic();vm.tab=4}
        else->{vm.logicSection=2;vm.persistLogic();vm.tab=4}
    }}
    val (xpIntoLevel,xpForLevel)=GameEngine.progressToNext(vm.gameXp)
    val gameLines=GameEngine.scoreLines(vm.gameProfiles.getOrNull(vm.gameActive)?.name?:"Player",vm.gameXp,done,total,vm.streakDays)
    StudyPage("From digits to memory.","One idea at a time.","YOUR GUIDED LEARNING PATH","A flexible route from number systems through sequential logic."){
        StudyCard("Your overall progress"){
            Text("$done of $total practice cards mastered",fontSize=15.sp,fontWeight=FontWeight.Bold)
            LinearProgressIndicator(progress={if(total==0)0f else done.toFloat()/total},modifier=Modifier.fillMaxWidth().height(9.dp).clip(RoundedCornerShape(6.dp)))
            Text("Level ${vm.gameLevel} · ${GameEngine.levelName(vm.gameLevel)} · ${vm.gameXp} XP",fontSize=13.sp,fontWeight=FontWeight.Bold,color=accent())
            LinearProgressIndicator(progress={if(xpForLevel==0)0f else xpIntoLevel.toFloat()/xpForLevel},modifier=Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(5.dp)))
            StudyNote(gameLines[1]);StudyNote(gameLines[3])
            StudyNote("No steps are locked. Follow the sequence or jump directly to any tool.")
            OutlinedButton(onClick={openStep(steps.indexOfFirst { (_,categories,_) -> PracticeCatalog.questions.filter{it.category in categories}.any{it.id !in vm.practiceProgress} }.coerceAtLeast(0))},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Continue the path",fontSize=12.sp)}
        }
        steps.forEachIndexed{index,(title,categories,_)->
            val questions=PracticeCatalog.questions.filter{it.category in categories}
            val mastered=questions.count{it.id in vm.practiceProgress}
            val complete=questions.isNotEmpty()&&mastered==questions.size
            StudyCard("${if(complete)"✓"else"${index+1}."}  $title"){
                StudyNote("${if(categories.size==1)categories.single()else categories.joinToString(" · ")} · $mastered/${questions.size} practice checks mastered")
                LinearProgressIndicator(progress={if(questions.isEmpty())0f else mastered.toFloat()/questions.size},modifier=Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(5.dp)))
                Button(onClick={openStep(index)},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Open topic",fontSize=12.sp)}
            }
        }
    }
}

@Composable internal fun ConversionHistoryScreen(vm:RadixViewModel,notify:(String)->Unit){
    var clearConfirm by remember{mutableStateOf(false)}
    StudyPage("Every conversion.","One tap away.","YOUR CONVERSION HISTORY","Recent valid entries are kept offline on this device."){
        StudyCard("Recent activity · ${vm.conversionHistory.size}/100"){
            StudyNote("History records a value after you pause typing. Repeated adjacent entries are combined; this is separate from your hand-picked saved numbers.")
            if(vm.conversionHistory.isNotEmpty())TextButton(onClick={clearConfirm=true},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Clear conversion history",fontSize=12.sp)}
        }
        if(vm.conversionHistory.isEmpty())StudyCard("No conversions yet"){
            StudyNote("Type a valid value in Convert and pause briefly. It will appear here with its source base.")
            Button(onClick={vm.tab=0},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Open Convert")}
        }
        vm.conversionHistory.forEach{item->
            val parsed=remember(item.id){BaseConverter.parse(item.input,item.base)}
            val value=(parsed as? BaseConverter.Parsed.Valid)?.value
            StudyCard("${BaseConverter.name(item.base)} · base ${item.base}"){
                Text(item.input,fontFamily=Mono,fontSize=22.sp,color=accent(),modifier=Modifier.horizontalScroll(rememberScrollState()))
                Text(java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM,java.text.DateFormat.SHORT).format(java.util.Date(item.id)),fontSize=10.sp,color=muted())
                if(value!=null)Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(12.dp)){
                    listOf(2,8,10,16).forEach{base->Text("${BaseConverter.shortName(base)} ${BaseConverter.grouped(BaseConverter.format(value,base),base)}",fontFamily=Mono,fontSize=10.sp,color=muted())}
                }
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    Button(onClick={vm.restoreHistoryEntry(item);notify("Conversion restored from history.")},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("Open",fontSize=12.sp)}
                    OutlinedButton(onClick={vm.deleteHistoryEntry(item.id);notify("History entry removed.")},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("Delete",fontSize=12.sp)}
                }
            }
        }
    }
    if(clearConfirm)AlertDialog(onDismissRequest={clearConfirm=false},title={Text("Clear conversion history?")},text={Text("This removes recent conversion entries only. Your separately saved numbers and all other study data stay unchanged.")},confirmButton={TextButton(onClick={vm.clearConversionHistory();clearConfirm=false;notify("Conversion history cleared.")}){Text("Clear history")}},dismissButton={TextButton(onClick={clearConfirm=false}){Text("Keep history")}})
}

@Composable internal fun RepresentationScreen(vm:RadixViewModel,notify:(String)->Unit){
    val s=vm.representation;val clipboard=LocalClipboardManager.current;val strings=LocalStrings.current;val language=strings.languageCode;val result=remember(s){runCatching{RepresentationEngine.report(s.input,s.width,s.signed,s.mode)}}
    StudyPage("Same bits.","Different meaning.","THE NUMBER REPRESENTATION LAB","Choose a width. Read the value. Understand the sign."){
        StudyCard("Encode or decode"){
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(7.dp)){RepresentationEngine.widths.forEach{w->OutlinedButton(onClick={vm.setRepresentation(s.copy(width=w))},modifier=Modifier.heightIn(min=actionHeight()).semantics{selected=w==s.width}){Text("$w bits",fontSize=11.sp,color=if(w==s.width)accent()else muted())}}}
            FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf("decimal" to "Decimal", "bits" to "Binary word", "gray" to "Gray code").forEach{(key,name)->OutlinedButton(onClick={vm.setRepresentation(s.copy(mode=key,input=when(key){"decimal"->"-13";"bits"->"11110011";else->"0110"},width=maxOf(8,s.width)))},modifier=Modifier.heightIn(min=actionHeight()).semantics{selected=key==s.mode}){Text(name,fontSize=11.sp,color=if(key==s.mode)accent()else muted())}}}
            Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Two’s-complement interpretation",fontSize=13.sp);StudyNote("Off = unsigned decimal input/range")};Switch(checked=s.signed,onCheckedChange={vm.setRepresentation(s.copy(signed=it))},modifier=Modifier.semantics{contentDescription=strings.text("Use signed two’s-complement interpretation")})}
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
                Button(onClick={clipboard.setText(AnnotatedString(AppStrings.translateBlock("${r.width}-bit representations\nInput: ${s.input} (${s.mode})\nWord: ${r.word}\nUnsigned: ${r.unsigned}\nTwo’s complement: ${r.signed}\nOne’s complement: ${r.ones} = ${r.onesSigned}\nHex: ${r.hex}\nGray: ${r.gray}\nBCD: ${r.bcd?:"not defined"}\nRange: ${r.range}\n"+r.steps.joinToString("\n"),language)));notify("Representation working copied.")},modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Copy complete working",fontSize=12.sp)}
            }
        }
        StudyNote("This lab is fixed-width. The original Convert tab uses signed magnitude for negative values; it does not automatically mean two’s complement.")
    }
}

private data class ShortAnswerPrompt(val title:String,val question:String,val model:String,val explanation:String)
private val shortAnswerPrompts=listOf(
    ShortAnswerPrompt("Beginner · number systems","Convert decimal 13 to an 8-bit unsigned binary word. Include the place-value sum.","00001101₂ = 8 + 4 + 1 = 13.","The set bit positions are 8, 4 and 1. Pad the four-bit pattern 1101 with four leading zeroes."),
    ShortAnswerPrompt("Intermediate · Boolean minimization","For F(Z,B,C)=Σm(1,2,5,6), write a minimum SOP and name the variable that disappears.","B′C + BC′; Z disappears.","The two groups pair each required cell with the cell that differs only in Z. Z changes inside each group, so its literal is removed."),
    ShortAnswerPrompt("Advanced · sequential logic","A 4-bit magnitude comparator receives A=1010 and B=1001. State A>B, A=B and A<B, then explain the first decisive bit.","A>B=1, A=B=0, A<B=0.","Compare from the most significant bit. A3=B3 and A2=B2; at bit 1, A1=1 and B1=0, so A>B. Lower bits cannot change that result.")
)

@Composable internal fun PracticeScreen(vm:RadixViewModel){
    val haptic=LocalHapticFeedback.current
    var filter by remember{mutableStateOf("All")};var chooser by remember{mutableStateOf(false)};var reset by remember{mutableStateOf(false)}
    var shortTier by remember{mutableIntStateOf(0)};var shortResponse by remember(shortTier){mutableStateOf("")};var revealShort by remember(shortTier){mutableStateOf(false)}
    val tierNames=listOf("All levels","Easy","Intermediate","Advanced")
    val options=PracticeCatalog.questions.filter{(vm.practiceTier==0||it.tier==vm.practiceTier-1)&&(filter=="All"||it.category==filter)};val q=PracticeCatalog.questions[vm.practiceIndex]
    var chosen by remember(q.id){mutableStateOf<Int?>(null)};var checked by remember(q.id){mutableStateOf(false)};var hint by remember(q.id){mutableStateOf(false)};var revealed by remember(q.id){mutableStateOf(false)}
    StudyPage("Try the idea.","Keep the lesson.","YOUR LOGIC PRACTICE HUB","Hints before answers. Understanding before scores."){
        StudyCard("${vm.practiceProgress.size} of ${PracticeCatalog.questions.size} mastered · Level ${vm.gameLevel}"){
            StudyNote("${vm.gameXp} XP as ${vm.gameProfiles.getOrNull(vm.gameActive)?.name?:"Player"} · Easy 10, Intermediate 20, Advanced 30 per card + streak bonus.")
            vm.lastAward?.let{award->Row(verticalAlignment=Alignment.CenterVertically){Text(award,color=accent(),fontSize=16.sp,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f));TextButton(onClick=vm::clearAward,modifier=Modifier.heightIn(min=actionHeight())){Text("Nice!",fontSize=11.sp)}}}
            StudyNote("A correct check counts as mastered. Revealing an answer does not. Streaks celebrate practice days — they never punish a missed day.")
            FlowRow(horizontalArrangement=Arrangement.spacedBy(7.dp)){tierNames.forEachIndexed{tier,name->TextButton(onClick={vm.setPracticeTier(tier)},modifier=Modifier.heightIn(min=actionHeight()).semantics{selected=tier==vm.practiceTier}){Text(name,fontSize=11.sp,fontWeight=if(tier==vm.practiceTier)FontWeight.Bold else FontWeight.Normal,color=if(tier==vm.practiceTier)accent()else muted())}}}
            StudyNote("${options.size} cards at this level · ${options.count{it.id in vm.practiceProgress}} mastered here")
            FlowRow(horizontalArrangement=Arrangement.spacedBy(7.dp)){(listOf("All")+PracticeCatalog.questions.map{it.category}.distinct()).forEach{cat->TextButton(onClick={filter=cat;val next=PracticeCatalog.questions.indexOfFirst{(vm.practiceTier==0||it.tier==vm.practiceTier-1)&&(cat=="All"||it.category==cat)};if(next>=0)vm.selectQuestion(next)},modifier=Modifier.heightIn(min=actionHeight()).semantics{selected=cat==filter}){Text(cat,fontSize=11.sp,color=if(cat==filter)accent()else muted())}}}
            Box{OutlinedButton(onClick={chooser=true},enabled=options.isNotEmpty(),modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Choose study card · ${vm.practiceIndex+1}/${PracticeCatalog.questions.size}",fontSize=12.sp)};DropdownMenu(expanded=chooser,onDismissRequest={chooser=false}){options.forEach{question->DropdownMenuItem(text={Text((if(question.id in vm.practiceProgress)"✓ "else"")+question.title,fontSize=13.sp)},onClick={vm.selectQuestion(PracticeCatalog.questions.indexOf(question));chooser=false})}}}
        }
        StudyCard(q.title){Text((q.category+" · "+listOf("EASY","INTERMEDIATE","ADVANCED")[q.tier]).uppercase(),fontSize=10.sp,color=accent(),letterSpacing=1.sp);Text(q.prompt,fontSize=17.sp,lineHeight=25.sp)
            q.choices.forEachIndexed{i,answer->OutlinedButton(onClick={chosen=i;checked=false},enabled=!revealed,shape=RoundedCornerShape(13.dp),border=BorderStroke(if(chosen==i)2.dp else 1.dp,if(chosen==i)accent()else outline()),modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight()).semantics{selected=chosen==i}){Text(answer,fontSize=13.sp,modifier=Modifier.fillMaxWidth())}}
            Button(onClick={checked=true;if(chosen==q.answer&&!revealed){vm.completeQuestion(q.id);haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)}},enabled=chosen!=null&&!revealed,modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text("Check my answer",fontSize=13.sp)}
            if(checked)Text(if(chosen==q.answer)"✓ Correct. Here’s why."else"Not yet. Try the hint and check again.",color=if(chosen==q.answer)accent()else MaterialTheme.colorScheme.error,fontSize=14.sp,fontWeight=FontWeight.Bold)
            if(checked&&chosen==q.answer||revealed)StudyNote(q.explanation)
            Row(horizontalArrangement=Arrangement.spacedBy(9.dp)){TextButton(onClick={hint=!hint},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text(if(hint)"Hide hint"else"Show a hint",fontSize=12.sp)};TextButton(onClick={revealed=true},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("Reveal & explain",fontSize=12.sp)}}
            if(hint)StudyNote(q.hint);if(revealed)Text("Answer: ${q.choices[q.answer]}",color=accent(),fontSize=14.sp)
            if(options.isEmpty())StudyNote("No cards combine this level with this topic — loosen a filter to continue.")
            else Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){OutlinedButton(onClick={val i=options.indexOf(q);vm.selectQuestion(PracticeCatalog.questions.indexOf(options[(i-1+options.size)%options.size]))},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("← Previous",fontSize=11.sp)};Button(onClick={val i=options.indexOf(q);vm.selectQuestion(PracticeCatalog.questions.indexOf(options[(i+1)%options.size]))},modifier=Modifier.weight(1f).heightIn(min=actionHeight())){Text("Next idea →",fontSize=11.sp)}}
        }
        StudyCard("Subjective · short-answer checkpoint"){
            StudyNote("Objective mode above checks a selected answer. Here, write your reasoning first, then compare it with a model solution; written responses are intentionally self-assessed, not automatically graded.")
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                listOf("Beginner","Intermediate","Advanced").forEachIndexed{index,label->KMapFormButton(label,shortTier==index,{shortTier=index;revealShort=false},Modifier.weight(1f))}
            }
            val prompt=shortAnswerPrompts[shortTier]
            Text(prompt.title.uppercase(),fontSize=10.sp,color=accent(),letterSpacing=1.sp)
            Text(prompt.question,fontSize=14.sp,lineHeight=21.sp)
            OutlinedTextField(value=shortResponse,onValueChange={shortResponse=it.take(1000)},label={Text("Your explanation or working")},modifier=Modifier.fillMaxWidth(),minLines=3,maxLines=6)
            Button(onClick={revealShort=!revealShort},enabled=shortResponse.isNotBlank(),modifier=Modifier.fillMaxWidth().heightIn(min=actionHeight())){Text(if(revealShort)"Hide model answer"else"Compare with model answer",fontSize=12.sp)}
            if(revealShort){
                StudyValue("MODEL ANSWER",prompt.model)
                StudyNote(prompt.explanation)
                StudyNote("Check both the result and the reasoning in your own words; compare the method, not just the final line.")
            }
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
    val context=LocalContext.current;val strings=LocalStrings.current;val scope=rememberCoroutineScope();val clipboard=LocalClipboardManager.current
    var draft by remember{mutableStateOf("")};var checked by remember{mutableStateOf<StudyBackup?>(null)};var error by remember{mutableStateOf<String?>(null)};var confirm by remember{mutableStateOf(false)};var exporting by remember{mutableStateOf("")}
    val create=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")){uri->if(uri!=null)scope.launch{val result=runCatching{withContext(Dispatchers.IO){context.contentResolver.openOutputStream(uri)?.use{it.write(exporting.toByteArray(Charsets.UTF_8))}?:kotlin.error("Cannot open output file")}};notify(if(result.isSuccess)"Offline backup file saved."else"Could not save backup: ${result.exceptionOrNull()?.message}")}}
    val open=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri->if(uri!=null)scope.launch{val result=runCatching{withContext(Dispatchers.IO){context.contentResolver.openInputStream(uri)?.use{stream->val out=java.io.ByteArrayOutputStream();val buffer=ByteArray(4096);while(true){val count=stream.read(buffer);if(count<0)break;require(out.size()+count<=262144){"Backup exceeds 256 KiB."};out.write(buffer,0,count)};out.toString("UTF-8")}?:kotlin.error("Cannot open backup")}};result.fold(onSuccess={draft=it;checked=null;error=null;notify("Backup loaded. Validate before restoring.")},onFailure={error=it.message})}}
    StudyPage("Make it yours.","Keep it safe.","PREFERENCES & OFFLINE BACKUP","No cloud. No account. Your study data stays yours."){
        StudyCard("Comfort controls"){
            Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Haptic feedback",fontSize=15.sp);StudyNote("Allow short native tap feedback")};Switch(checked=vm.haptics,onCheckedChange=vm::setHaptics,modifier=Modifier.semantics{contentDescription=strings.text("Allow haptic feedback")})}
            Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Larger learning controls",fontSize=15.sp);StudyNote("More room for circuit inputs and study actions; system font scaling is respected")};Switch(checked=vm.largeControls,onCheckedChange=vm::setLargeControls,modifier=Modifier.semantics{contentDescription=strings.text("Use larger learning controls")})}
        }
        StudyCard("Appearance"){
            ThemeSection(vm)
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
            StudyNote("Includes saved conversions, circuit sessions, mastered practice cards, the current K-map/lab drafts and comfort preferences. Theme, reminder time and inbox stay on this device; the app follows your system language and does not include it in backups. Circuit traces and signing keys/passwords are never included. JSON is NOT encrypted; store it privately.")
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
