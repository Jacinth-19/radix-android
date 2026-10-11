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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private data class Law(val name: String, val statement: String, val left: String, val right: String, val vars: Int)
private data class LawGroup(val title: String, val tip: String, val laws: List<Law>)

private val lawGroups = listOf(
    LawGroup("Identity & null", "Exam favorite: kill 0s and 1s before you draw anything.", listOf(
        Law("Identity OR", "A + 0 = A", "A+0", "A", 2),
        Law("Identity AND", "A · 1 = A", "A1", "A", 2),
        Law("Null OR", "A + 1 = 1", "A+1", "1", 2),
        Law("Null AND", "A · 0 = 0", "A0", "0", 2))),
    LawGroup("Idempotent, complement & negation", "A term ORed/ANDed with itself changes nothing; a variable plus its complement covers everything.", listOf(
        Law("Idempotent OR", "A + A = A", "A+A", "A", 2),
        Law("Idempotent AND", "A · A = A", "AA", "A", 2),
        Law("Complement OR", "A + A′ = 1", "A+A'", "1", 2),
        Law("Complement AND", "A · A′ = 0", "AA'", "0", 2),
        Law("Double negation", "(A′)′ = A", "(A')'", "A", 2))),
    LawGroup("Commutative & associative", "Reorder and regroup freely — then spot cancellations.", listOf(
        Law("Commutative OR", "A + B = B + A", "A+B", "B+A", 2),
        Law("Commutative AND", "A · B = B · A", "AB", "BA", 2),
        Law("Associative OR", "(A + B) + C = A + (B + C)", "(A+B)+C", "A+(B+C)", 3),
        Law("Associative AND", "(A · B) · C = A · (B · C)", "(AB)C", "A(BC)", 3))),
    LawGroup("Distributive", "Factor like ordinary algebra — then watch terms vanish.", listOf(
        Law("AND over OR", "A · (B + C) = A·B + A·C", "A(B+C)", "AB+AC", 3),
        Law("OR over AND", "A + B·C = (A + B) · (A + C)", "A+BC", "(A+B)(A+C)", 3))),
    LawGroup("Absorption", "The disappearing term: the longer clause is redundant.", listOf(
        Law("Absorption OR", "A + A·B = A", "A+AB", "A", 2),
        Law("Absorption AND", "A · (A + B) = A", "A(A+B)", "A", 2))),
    LawGroup("De Morgan", "Break the bar, flip the sign: OR becomes AND and back.", listOf(
        Law("De Morgan OR", "(A + B)′ = A′ · B′", "(A+B)'", "A'B'", 2),
        Law("De Morgan AND", "(A · B)′ = A′ + B′", "(AB)'", "A'+B'", 2))),
    LawGroup("XOR & XNOR", "⊕ detects difference; ⊙ detects equality. Both appear in adders and comparators.", listOf(
        Law("XOR expansion", "A ⊕ B = A′·B + A·B′", "A XOR B", "A'B+AB'", 2),
        Law("XNOR as inverted XOR", "A ⊙ B = (A ⊕ B)′", "A XNOR B", "(A XOR B)'", 2)))
)

@Composable internal fun LawsScreen(vm: RadixViewModel, notify: (String) -> Unit) {
    val clipboard = LocalClipboardManager.current
    val language = LocalStrings.current.languageCode
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
        Hero("Laws, not luck.", "Prove every line.", "THE BOOLEAN LAWS LAB", "Each law is verified live against every input row.")
        Text("NOTHING IS TAKEN ON FAITH HERE. Tap Verify and the app checks both sides on all 4 rows (2 variables) or all 8 rows (3 variables).", color = muted(), fontSize = 10.sp, letterSpacing = 1.sp, lineHeight = 17.sp, modifier = Modifier.padding(bottom = 14.dp))
        lawGroups.forEach { group ->
            Surface(shape = Round, color = panel(), border = BorderStroke(1.dp, outline()), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text(group.title, fontSize = 19.sp, fontWeight = FontWeight.Bold, fontFamily = Grotesk)
                    Text(group.tip, color = muted(), fontSize = 12.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
                    group.laws.forEach { law -> LawRow(law) }
                }
            }
        }
        OutlinedButton(onClick = {
            val report = lawGroups.joinToString("\n\n") { g ->
                AppStrings.translate(g.title, language) + "\n" + g.laws.joinToString("\n") {
                    "  ${AppStrings.translate(it.name.uppercase(), language)}: ${it.statement}  ✓ ${AppStrings.translate("all ${1 shl it.vars} rows", language)}"
                }
            }
            clipboard.setText(AnnotatedString(report))
            notify("All Boolean laws copied.")
        }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(14.dp)) { Text("Copy all laws", fontSize = 12.sp) }
        Text("Notation: · is AND, + is OR, ⊕ is XOR, ⊙ is XNOR, ′ is NOT. Adjacency (AB) also means AND.", color = muted(), fontSize = 11.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable private fun LawRow(law: Law) {
    var checked by remember { mutableStateOf(false) }
    val proof = remember(law) { runCatching { ExpressionEngine.compare(law.left, law.right, law.vars) }.getOrNull() }
    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Column(Modifier.padding(14.dp)) {
            Text(law.name.uppercase(), color = muted(), fontSize = 9.sp, letterSpacing = 1.sp)
            Text(law.statement, fontFamily = Mono, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp).horizontalScroll(rememberScrollState()))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                Text(if (!checked) "Tap to verify live" else if (proof?.equivalent == true) "✓ Holds in all ${1 shl law.vars} rows" else "Mismatch — report this bug",
                    color = if (!checked) muted() else if (proof?.equivalent == true) accent() else MaterialTheme.colorScheme.error,
                    fontSize = 12.sp, modifier = Modifier.weight(1f))
                if (!checked) TextButton(onClick = { checked = true }) { Text("Verify", fontSize = 12.sp) }
            }
        }
    }
}

@Composable internal fun ArithmeticScreen(vm: RadixViewModel, notify: (String) -> Unit) {
    val clipboard = LocalClipboardManager.current
    val strings=LocalStrings.current
    val language=strings.languageCode
    var a by remember { mutableStateOf("1011") }
    var b by remember { mutableStateOf("0111") }
    var base by remember { mutableIntStateOf(2) }
    var op by remember { mutableStateOf('+') }
    val report = remember(a, b, base, op) { runCatching { BaseArithmetic.compute(a, b, base, op) }.getOrElse { null } }
    val error = remember(a, b, base, op) { runCatching { BaseArithmetic.compute(a, b, base, op) }.exceptionOrNull()?.message }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
        Hero("Add. Subtract.", "Multiply. Divide.", "BASE ARITHMETIC, WITH WORKING", "Exact integer math in any base from 2 to 36.")
        Surface(shape = Round, color = panel(), border = BorderStroke(1.dp, outline()), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("BASE $base · ${BaseConverter.name(base).uppercase()}", color = accent(), fontFamily = Mono, fontSize = 12.sp)
                Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(2 to "BIN", 8 to "OCT", 10 to "DEC", 16 to "HEX").forEach { (v, name) ->
                        OutlinedButton(onClick = { base = v }, modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                            border = BorderStroke(if (base == v) 2.dp else 1.dp, if (base == v) accent() else outline()),
                            shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 2.dp)) { Text(name, fontSize = 10.sp) }
                    }
                }
                Slider(value = base.toFloat(), onValueChange = { base = it.roundToInt() }, valueRange = 2f..36f, steps = 33,
                    modifier = Modifier.semantics { contentDescription = strings.text("Arithmetic base from 2 to 36") })
                OutlinedTextField(value = a, onValueChange = { a = it.take(64) }, label = { Text("First number") }, textStyle = LocalTextStyle.current.copy(fontFamily = Mono), modifier = Modifier.fillMaxWidth(), singleLine = true)
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf('+' to "+", '-' to "−", '×' to "×", '÷' to "÷").forEach { (value, label) ->
                        OutlinedButton(onClick = { op = value }, modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                            border = BorderStroke(if (op == value) 2.dp else 1.dp, if (op == value) accent() else outline()),
                            shape = RoundedCornerShape(12.dp)) { Text(label, fontFamily = Mono, fontSize = 18.sp) }
                    }
                }
                OutlinedTextField(value = b, onValueChange = { b = it.take(64) }, label = { Text("Second number") }, textStyle = LocalTextStyle.current.copy(fontFamily = Mono), modifier = Modifier.fillMaxWidth(), singleLine = true)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 10.dp)) }
                report?.let { r ->
                    Text("ANSWER · BASE $base", color = muted(), fontSize = 10.sp, letterSpacing = 1.sp, modifier = Modifier.padding(top = 16.dp))
                    Text(if (op == '÷' || op == '/') "${r.quotient} remainder ${r.remainder}" else r.result, fontFamily = Mono, color = accent(), fontSize = 28.sp, modifier = Modifier.padding(top = 6.dp).horizontalScroll(rememberScrollState()))
                    r.steps.forEachIndexed { i, step -> Text("${i + 1}. $step", fontFamily = Mono, fontSize = 11.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 6.dp)) }
                    TextButton(onClick = {
                        clipboard.setText(AnnotatedString(AppStrings.translateBlock("${r.a} ${r.op} ${r.b} (base $base) = ${if (r.quotient != null) "${r.quotient} rem ${r.remainder}" else r.result}\n" + r.steps.joinToString("\n"),language)))
                        notify("Arithmetic working copied.")
                    }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Copy full working", fontSize = 12.sp) }
                }
            }
        }
        Surface(shape = Round, color = panel(), border = BorderStroke(1.dp, outline()), modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Column(Modifier.padding(18.dp)) {
                Text("Denary · octal · hex · binary", fontSize = 19.sp, fontWeight = FontWeight.Bold, fontFamily = Grotesk)
                Text("One table for the numbers 0–15. Read a row across to convert instantly.", color = muted(), fontSize = 12.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 4.dp, bottom = 10.dp))
                Column(Modifier.horizontalScroll(rememberScrollState())) {
                    Row { listOf("DEC", "OCT", "HEX", "BIN").forEach { Text(it, fontFamily = Mono, fontSize = 10.sp, color = muted(), modifier = Modifier.width(72.dp).padding(vertical = 6.dp)) } }
                    BaseArithmetic.referenceTable().forEach { row ->
                        Row { row.forEach { Text(it, fontFamily = Mono, fontSize = 13.sp, modifier = Modifier.width(72.dp).padding(vertical = 5.dp)) } }
                    }
                }
                Text("Why octal and hex exist: 3 bits make one octal digit, 4 bits make one hex digit — so long binary strings stay readable.", color = muted(), fontSize = 11.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 10.dp))
            }
        }
    }
}
