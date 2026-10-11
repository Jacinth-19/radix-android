@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.radix.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

internal val Lime = Color(0xFFD2FA69)
internal val Ink = Color(0xFF111410)
internal val Grotesk = FontFamily(Font(R.font.space_grotesk, FontWeight.Normal), Font(R.font.space_grotesk, FontWeight.Bold))
internal val Mono = FontFamily(Font(R.font.jetbrains_mono, FontWeight.Normal), Font(R.font.jetbrains_mono, FontWeight.Bold))
internal val Round = RoundedCornerShape(24.dp)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { RadixApp(viewModel()) }
    }
}

@Composable internal fun muted(): Color = if (MaterialTheme.colorScheme.background == Ink) Color(0xFF969D8F) else Color(0xFF666D5D)
@Composable internal fun panel(): Color = MaterialTheme.colorScheme.surface
@Composable internal fun outline(): Color = MaterialTheme.colorScheme.outlineVariant
@Composable internal fun accent(): Color = if (MaterialTheme.colorScheme.background == Ink) Lime else Color(0xFF4F6B10)

@Composable
private fun RadixApp(vm: RadixViewModel) {
    val scheme = if (vm.dark) darkColorScheme(
        primary = Lime, onPrimary = Ink, background = Ink, onBackground = Color(0xFFF4F6EF),
        surface = Color(0xFF1C2019), onSurface = Color(0xFFF4F6EF),
        surfaceVariant = Color(0xFF242920), outlineVariant = Color(0xFF343B2D),
        secondary = Color(0xFFAACBFA), error = Color(0xFFFFADB0)
    ) else lightColorScheme(
        primary = Color(0xFF52720F), onPrimary = Color.White, background = Color(0xFFF5F6EF),
        onBackground = Ink, surface = Color.White, onSurface = Ink,
        surfaceVariant = Color(0xFFEDF0E5), outlineVariant = Color(0xFFDDE2D3),
        secondary = Color(0xFF355D99), error = Color(0xFFB3261E)
    )
    val typography = Typography().let { t ->
        t.copy(
            bodyLarge = t.bodyLarge.copy(fontFamily = Grotesk), bodyMedium = t.bodyMedium.copy(fontFamily = Grotesk),
            bodySmall = t.bodySmall.copy(fontFamily = Grotesk), titleLarge = t.titleLarge.copy(fontFamily = Grotesk),
            titleMedium = t.titleMedium.copy(fontFamily = Grotesk), titleSmall = t.titleSmall.copy(fontFamily = Grotesk),
            labelLarge = t.labelLarge.copy(fontFamily = Grotesk), labelMedium = t.labelMedium.copy(fontFamily = Grotesk),
            labelSmall = t.labelSmall.copy(fontFamily = Grotesk)
        )
    }
    MaterialTheme(colorScheme = scheme, typography = typography) {
        val context = LocalContext.current
        SideEffect {
            (context as? Activity)?.let {
                WindowCompat.getInsetsController(it.window, it.window.decorView).apply {
                    isAppearanceLightStatusBars = !vm.dark
                    isAppearanceLightNavigationBars = !vm.dark
                }
            }
        }
        val snack = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val systemLanguage=AppStrings.systemLanguage(LocalConfiguration.current.locales)
        val notify: (String) -> Unit = { message -> scope.launch { snack.currentSnackbarData?.dismiss(); snack.showSnackbar(AppStrings.translate(message,systemLanguage)) } }
        var sourcePicker by remember { mutableStateOf(false) }
        var outputPicker by remember { mutableStateOf(false) }
        var workTarget by remember { mutableStateOf<Int?>(null) }
        var helpOpen by remember { mutableStateOf(false) }
        var tourIndex by remember { mutableIntStateOf(-1) }
        var previousTab by remember { mutableIntStateOf(0) }
        var previousSection by remember { mutableIntStateOf(0) }
        val keyboard=LocalSoftwareKeyboardController.current
        val anchors=remember{mutableStateMapOf<String,androidx.compose.ui.geometry.Rect>()}
        var splash by remember{mutableStateOf(true)}
        var whatsNew by remember{mutableStateOf(false)}
        LaunchedEffect(systemLanguage,vm.notifEnabled){if(vm.notifEnabled)StudyReminder.prepareChannel(context)}
        val systemDark=isSystemInDarkTheme()
        LaunchedEffect(systemDark,vm.themeMode){vm.applySystemTheme(systemDark)}
        LaunchedEffect(Unit){vm.markOpened();delay(1100);splash=false}
        val showOnboarding=!splash&&!vm.onboardingDone&&!vm.isUpgrader
        LaunchedEffect(splash,vm.onboardingDone,vm.lastSeenCode){whatsNew=!splash&&!showOnboarding&&vm.lastSeenCode<APP_VERSION_CODE}
        fun closeTour(){tourIndex=-1;vm.tab=previousTab;vm.logicSection=previousSection;vm.persistLogic()}
        LaunchedEffect(tourIndex){if(tourIndex>=0){keyboard?.hide();val step=guideSteps[tourIndex];vm.tab=step.tab;step.section?.let{vm.logicSection=it}}}
        BackHandler(enabled=vm.tab!=0&&tourIndex<0){vm.tab=if(vm.tab in listOf(1,2,6,7,8,9,10,11,12,13,14,15,16,17))5 else 0}
        val physicalHaptics=LocalHapticFeedback.current
        val hapticPolicy=remember(physicalHaptics,vm.haptics){object:HapticFeedback{override fun performHapticFeedback(hapticFeedbackType:HapticFeedbackType){if(vm.haptics)physicalHaptics.performHapticFeedback(hapticFeedbackType)}}}
        CompositionLocalProvider(LocalHapticFeedback provides hapticPolicy,LocalLargeStudyControls provides vm.largeControls,LocalGuideAnchors provides anchors,LocalGuideTarget provides if(tourIndex>=0)guideSteps[tourIndex].anchor else null,LocalStrings provides AppStrings.forLang(systemLanguage)){
            Box(Modifier.fillMaxSize()){
                Scaffold(
                    containerColor = scheme.background,
                    snackbarHost = { SnackbarHost(snack) },
                    bottomBar = { if(!splash&&!showOnboarding)BottomBar(vm) },
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { padding ->
                    Column(Modifier.fillMaxSize().padding(padding)) {
                        when {
                            splash -> SplashScreen()
                            showOnboarding -> OnboardingScreen(vm)
                            else -> {
                                BrandHeader(vm){keyboard?.hide();helpOpen=true}
                                if(vm.tab in listOf(1,2,6,7,8,9,10,11,12,13,14,15,16,17))TextButton(onClick={vm.tab=5},modifier=Modifier.padding(horizontal=16.dp).heightIn(min=48.dp)){Text("← More",fontSize=12.sp)}
                                when (vm.tab) {
                                    0 -> ConverterScreen(vm, notify, { sourcePicker = true }, { outputPicker = true }, { workTarget = it })
                                    1 -> SavedScreen(vm, notify)
                                    2 -> BitLabScreen(vm)
                                    3 -> KMapScreen(vm, notify)
                                    4 -> LogicScreen(vm, notify)
                                    6 -> ExpressionScreen(vm,notify)
                                    7 -> RepresentationScreen(vm,notify)
                                    8 -> PracticeScreen(vm)
                                    9 -> SessionsScreen(vm,notify)
                                    10 -> PreferencesScreen(vm,notify)
                                    11 -> LawsScreen(vm,notify)
                                    12 -> ArithmeticScreen(vm,notify)
                                    13 -> BuilderScreen(vm,notify)
                                    14 -> FloatScreen(vm,notify)
                                    15 -> LeaderboardScreen(vm,notify)
                                    16 -> LearningPathScreen(vm)
                                    17 -> ConversionHistoryScreen(vm,notify)
                                    else -> MoreScreen(vm){helpOpen=true}
                                }
                            }
                        }
                    }
                }
                if(tourIndex>=0)GuideOverlay(tourIndex,anchors,{if(tourIndex==guideSteps.lastIndex)closeTour()else tourIndex++},{if(tourIndex>0)tourIndex--},::closeTour)
            }
        }
        if(whatsNew)WhatsNewSheet({whatsNew=false;vm.markVersionSeen()},{vm.tab=10})
        workTarget?.let { target -> WorkingSheet(vm.number, vm.sourceBase, target, notify) { workTarget = null } }
        if (sourcePicker) BasePicker(vm.sourceBase, false, { sourcePicker = false }) {vm.selectSource(it); sourcePicker = false}
        if (outputPicker) BasePicker(vm.customBase ?: 12, true, { outputPicker = false }) {vm.setCustom(it); outputPicker = false}
        if(helpOpen)IllustratedHelper({helpOpen=false},{helpOpen=false;previousTab=vm.tab;previousSection=vm.logicSection;vm.markTourSeen();tourIndex=0})
    }
}

@Composable
private fun BrandHeader(vm: RadixViewModel, help:()->Unit) {
    val strings=LocalStrings.current
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Lime), contentAlignment = Alignment.Center) {
            Text("rₓ", color = Ink, fontFamily = Grotesk, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(10.dp))
        Text("radix", fontFamily = Grotesk, fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = (-1.4).sp)
        Spacer(Modifier.weight(1f))
        IconButton(onClick=help,modifier=Modifier.size(48.dp).border(if(vm.tourSeen)1.dp else 2.dp,if(vm.tourSeen)outline()else accent(),CircleShape)){
            Text("?",fontFamily=Mono,fontSize=22.sp,color=accent(),modifier=Modifier.semantics{contentDescription=strings.text("Open visual helper and app tour")})
        }
        Spacer(Modifier.width(6.dp))
        IconButton(onClick={vm.tab=10},modifier=Modifier.size(48.dp).border(if(vm.tab==10)2.dp else 1.dp,if(vm.tab==10)accent()else outline(),CircleShape)){
            Text("⚙",fontSize=20.sp,color=accent(),modifier=Modifier.semantics{contentDescription=strings.text("Open settings")})
        }
        Spacer(Modifier.width(6.dp))
        IconButton(onClick = vm::toggleTheme, modifier = Modifier.size(48.dp).border(1.dp, outline(), CircleShape)) {
            Icon(if (vm.dark) ImageVector.vectorResource(R.drawable.ic_sun) else ImageVector.vectorResource(R.drawable.ic_moon),
                contentDescription = if (vm.dark) "Switch to light theme" else "Switch to dark theme", modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
internal fun Hero(first: String, second: String, eyebrow: String, subtitle: String) {
    val strings=LocalStrings.current
    Column(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 20.dp)) {
        Text(eyebrow, color = muted(), fontSize = 10.sp, letterSpacing = 1.6.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(9.dp))
        Text(buildAnnotatedString {
            append(strings.text(first) + "\n")
            withStyle(SpanStyle(color = accent())) { append(strings.text(second)) }
        }, fontFamily = Grotesk, fontWeight = FontWeight.Bold, fontSize = 37.sp, lineHeight = 39.sp, letterSpacing = (-1.5).sp)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, color = muted(), fontSize = 13.sp)
    }
}

@Composable
private fun ConverterScreen(vm: RadixViewModel, notify: (String) -> Unit, chooseSource: () -> Unit, chooseOutput: () -> Unit, showWorking: (Int) -> Unit) {
    val parsed = remember(vm.number, vm.sourceBase) { BaseConverter.parse(vm.number, vm.sourceBase) }
    LaunchedEffect(vm.number,vm.sourceBase){if(parsed is BaseConverter.Parsed.Valid){delay(800);vm.recordConversionHistory(vm.number,vm.sourceBase)}}
    var workingMode by remember { mutableStateOf(false) }
    val compact = workingMode && with(LocalDensity.current){LocalWindowInfo.current.containerSize.width.toDp()} < 840.dp
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 20.dp)) {
        if (!compact) Hero("One number.", "Every base.", "A NEW PERSPECTIVE ON NUMBERS", "A little input. A whole lot of possibilities.")
        else Spacer(Modifier.height(12.dp))
        BoxWithConstraints {
            if (maxWidth >= 760.dp) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column(Modifier.weight(0.42f)) { SourceCard(vm, parsed, notify, chooseSource, compact) { if (it) workingMode = true }; Spacer(Modifier.height(16.dp)); LabTeaser(vm) }
                    Column(Modifier.weight(0.58f)) { Results(vm, parsed, notify, chooseOutput, showWorking) }
                }
            } else {
                Column {
                    SourceCard(vm, parsed, notify, chooseSource, compact) { if (it) workingMode = true }
                    Spacer(Modifier.height(22.dp))
                    Results(vm, parsed, notify, chooseOutput, showWorking)
                    Spacer(Modifier.height(16.dp))
                    LabTeaser(vm)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Icon(ImageVector.vectorResource(R.drawable.ic_lock), null, tint = muted(), modifier = Modifier.size(13.dp))
            Text("  No ads. No accounts. Just numbers.", color = muted(), fontSize = 11.sp)
        }
    }
}

@Composable
private fun SourceCard(vm: RadixViewModel, parsed: BaseConverter.Parsed, notify: (String) -> Unit, chooseSource: () -> Unit, compact: Boolean, focusChanged: (Boolean) -> Unit) {
    val strings=LocalStrings.current
    val clipboard = LocalClipboardManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val haptic = LocalHapticFeedback.current
    Surface(shape = Round, color = panel(), border = BorderStroke(1.dp, accent().copy(alpha = 0.35f))) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("YOUR NUMBER", color = muted(), fontSize = 10.sp, letterSpacing = 1.4.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text("BASE ${vm.sourceBase}", color = accent(), fontFamily = Mono, fontSize = 10.sp)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.guideAnchor("source-base"),horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(2, 8, 10, 16).forEach { base ->
                    val selected = vm.sourceBase == base
                    Surface(onClick = { vm.selectSource(base); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp), shape = RoundedCornerShape(12.dp),
                        color = if (selected) Lime else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selected) Ink else muted()) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(BaseConverter.shortName(base), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Surface(onClick = chooseSource, shape = RoundedCornerShape(12.dp), modifier = Modifier.size(48.dp),
                    color = if (vm.sourceBase !in listOf(2, 8, 10, 16)) Lime else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (vm.sourceBase !in listOf(2, 8, 10, 16)) Ink else muted()) {
                    Box(contentAlignment = Alignment.Center) {
                        if (vm.sourceBase !in listOf(2, 8, 10, 16)) Text(vm.sourceBase.toString(), fontFamily = Mono, fontSize = 13.sp)
                        else Icon(ImageVector.vectorResource(R.drawable.ic_more), "Choose any source base from 2 to 36", modifier = Modifier.size(22.dp))
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            BasicTextField(
                value = vm.number, onValueChange = vm::updateNumber,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).guideAnchor("number").onFocusChanged { focusChanged(it.isFocused) }.semantics { contentDescription = strings.text("Number to convert in base ${vm.sourceBase}") },
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 38.sp),
                cursorBrush = SolidColor(accent()), singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters, autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboard?.hide() }),
                decorationBox = { inner ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f)) {
                            if (vm.number.isEmpty()) Text("Enter a number", color = muted(), fontFamily = Mono, fontSize = 22.sp)
                            inner()
                        }
                        IconButton(onClick = { vm.updateNumber("") }, enabled = vm.number.isNotEmpty(), modifier = Modifier.size(48.dp)) {
                            Icon(ImageVector.vectorResource(R.drawable.ic_close), "Clear number", tint = muted(), modifier = Modifier.size(19.dp))
                        }
                    }
                }
            )
            if (compact) {
                val value = (parsed as? BaseConverter.Parsed.Valid)?.value
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(2, 16, 8, 10).forEach { base ->
                        val output = remember(value, base) { value?.let { BaseConverter.format(it, base) } }
                        val color = when (base) {
                            2 -> MaterialTheme.colorScheme.secondary
                            16 -> if (vm.dark) Color(0xFFF2ADC9) else Color(0xFF9D3866)
                            10 -> accent()
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                        Surface(
                            onClick = { output?.let {
                                clipboard.setText(AnnotatedString(it.raw))
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                notify(if (it.exact) "${BaseConverter.name(base)} copied." else "Approximate value copied · 24 fractional places.")
                            } }, enabled = output != null,
                            color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.width(132.dp).heightIn(min = 68.dp).semantics { contentDescription = strings.text("Copy ${BaseConverter.name(base)} quick result") }
                        ) {
                            Column(Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${BaseConverter.shortName(base)} $base", fontFamily = Mono, color = muted(), fontSize = 9.sp, modifier = Modifier.weight(1f))
                                    Icon(ImageVector.vectorResource(R.drawable.ic_copy), null, tint = muted(), modifier = Modifier.size(12.dp))
                                }
                                Box(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 6.dp)) {
                                    Text(output?.let { BaseConverter.grouped(it, base) } ?: "—", fontFamily = Mono, color = color, fontSize = 19.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }
            HorizontalDivider(color = outline(), modifier = Modifier.padding(top = 9.dp, bottom = 8.dp))
            val message = when (parsed) {
                is BaseConverter.Parsed.Invalid -> parsed.message
                BaseConverter.Parsed.Empty -> "Type a number to see it in every base."
                else -> if (compact) "Tap to copy · swipe for more bases · … means approximate" else "${BaseConverter.digitHint(vm.sourceBase)} · fractions welcome"
            }
            Text(message, color = if (parsed is BaseConverter.Parsed.Invalid) MaterialTheme.colorScheme.error else muted(), fontSize = 11.sp, lineHeight = 16.sp)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = {
                    val text = clipboard.getText()?.text
                    if (text.isNullOrBlank()) notify("Your clipboard is empty.") else vm.updateNumber(text)
                }, contentPadding = PaddingValues(horizontal = 6.dp), modifier = Modifier.heightIn(min = 48.dp)) {
                    Icon(ImageVector.vectorResource(R.drawable.ic_paste), null, modifier = Modifier.size(15.dp))
                    Text("  Paste", fontSize = 12.sp)
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = vm::surprise,contentPadding=PaddingValues(horizontal=5.dp),modifier=Modifier.heightIn(min=48.dp)) {
                    Icon(ImageVector.vectorResource(R.drawable.ic_dice), null, tint = muted(), modifier = Modifier.size(17.dp))
                    Text("  ${LocalStrings.current.convertRandomize}",fontSize=10.sp,maxLines=1)
                }
                TextButton(onClick = {
                    if (vm.save()) { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); notify("Saved. A good number to keep.") }
                }, enabled = parsed is BaseConverter.Parsed.Valid, contentPadding = PaddingValues(horizontal = 8.dp), modifier = Modifier.heightIn(min = 48.dp)) {
                    Icon(ImageVector.vectorResource(R.drawable.ic_bookmark), null, modifier = Modifier.size(17.dp))
                    Text("  Save", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun Results(vm: RadixViewModel, parsed: BaseConverter.Parsed, notify: (String) -> Unit, chooseOutput: () -> Unit, showWorking: (Int) -> Unit) {
    val value = (parsed as? BaseConverter.Parsed.Valid)?.value
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
            Box(Modifier.size(6.dp).background(if (value != null) accent() else muted(), CircleShape))
            Text("  LIVE CONVERSIONS", color = muted(), fontSize = 10.sp, letterSpacing = 1.3.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text(if (value != null) "ALL IN SYNC" else "WAITING FOR INPUT", color = muted(), fontSize = 9.sp)
        }
        listOf(listOf(2, 8), listOf(10, 16)).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 10.dp)) {
                pair.forEach { base -> ResultCard(value, base, notify, Modifier.weight(1f)) }
            }
        }
        OutlinedButton(onClick = { showWorking(2) }, enabled = value != null, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(bottom = 6.dp).guideAnchor("working"), shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, accent().copy(alpha = 0.45f))) {
            Icon(ImageVector.vectorResource(R.drawable.ic_sparkle), null, modifier = Modifier.size(17.dp))
            Text("  Show the working", fontSize = 13.sp)
            Spacer(Modifier.weight(1f))
            Icon(ImageVector.vectorResource(R.drawable.ic_arrow), null, modifier = Modifier.size(16.dp))
        }
        vm.customBase?.let { base ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = chooseOutput, modifier = Modifier.weight(1f)) { Text("Custom base $base · change", fontSize = 12.sp) }
                IconButton(onClick = { vm.setCustom(null) }) { Icon(ImageVector.vectorResource(R.drawable.ic_close), "Remove custom output base", modifier = Modifier.size(18.dp)) }
            }
            ResultCard(value, base, notify, Modifier.fillMaxWidth())
        } ?: OutlinedButton(onClick = chooseOutput, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, outline())) {
            Icon(ImageVector.vectorResource(R.drawable.ic_add), null, modifier = Modifier.size(17.dp))
            Text("  Add a custom base", fontSize = 12.sp)
            Spacer(Modifier.weight(1f))
            Text("2–36", color = muted(), fontFamily = Mono, fontSize = 11.sp)
        }
        if (value != null && !value.isInteger) {
            Text("( ) = repeating digits · … = truncated at 24 places", color = muted(), fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun ResultCard(value: BaseConverter.Rational?, base: Int, notify: (String) -> Unit, modifier: Modifier = Modifier) {
    val strings=LocalStrings.current
    val clipboard = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    val output = remember(value, base) { value?.let { BaseConverter.format(it, base) } }
    val dark = MaterialTheme.colorScheme.background == Ink
    val numberColor = when (base) {
        2 -> if (dark) Color(0xFFAACBFA) else Color(0xFF355D99)
        16 -> if (dark) Color(0xFFF2ADC9) else Color(0xFF9D3866)
        10 -> accent()
        else -> MaterialTheme.colorScheme.onSurface
    }
    Surface(modifier = modifier, shape = RoundedCornerShape(20.dp), color = panel(), border = BorderStroke(1.dp, outline())) {
        Column(Modifier.padding(start = 15.dp, end = 10.dp, top = 9.dp, bottom = 15.dp).heightIn(min = 116.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(if (base == 16) "Hexadecimal" else BaseConverter.name(base), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("BASE $base", fontFamily = Mono, color = muted(), fontSize = 9.sp, modifier = Modifier.padding(top = 2.dp))
                }
                IconButton(onClick = {
                    output?.let {
                        clipboard.setText(AnnotatedString(it.raw))
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        notify(if (it.exact) "${BaseConverter.name(base)} copied." else "Approximate value copied · 24 fractional places.")
                    }
                }, enabled = output != null, modifier = Modifier.size(48.dp)) {
                    Icon(ImageVector.vectorResource(R.drawable.ic_copy), "Copy ${BaseConverter.name(base)} result", tint = muted(), modifier = Modifier.size(17.dp))
                }
            }
            val text = output?.let { BaseConverter.grouped(it, base) } ?: "—"
            Box(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 7.dp)) {
                Text(text, fontFamily = Mono, color = numberColor, fontWeight = FontWeight.Bold,
                    fontSize = if (text.length > 12) 21.sp else if (text.length > 7) 23.sp else 30.sp,
                    maxLines = 1, modifier = Modifier.semantics { contentDescription = strings.text("${BaseConverter.name(base)}: ${output?.display ?: "no result"}") })
            }
            Text(when { output == null -> "Waiting for a number"; !output.exact -> "≈ 24 places · approximate"; output.repeating -> "Repeating · exact"; else -> "Exact conversion" },
                color = if (output?.exact == false) numberColor else muted(), fontSize = 10.sp, modifier = Modifier.padding(top = 10.dp))
        }
    }
}

@Composable
private fun LabTeaser(vm: RadixViewModel) {
    Surface(onClick = vm::openLab, color = panel(), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, outline())) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).background(Lime.copy(alpha = 0.12f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(ImageVector.vectorResource(R.drawable.ic_sparkle), null, tint = accent(), modifier = Modifier.size(21.dp))
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text("A tiny bit of magic", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Flip a bit. See what happens.", color = muted(), fontSize = 11.sp)
            }
            Icon(ImageVector.vectorResource(R.drawable.ic_arrow), "Open the bit lab", tint = muted(), modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
private fun BottomBar(vm: RadixViewModel) {
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxWidth()) {
        Column {
            HorizontalDivider(color = outline())
            Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf(0 to LocalStrings.current.tabConvert, 4 to LocalStrings.current.tabLogic, 3 to LocalStrings.current.tabKmaps, 5 to LocalStrings.current.tabMore).forEach { (index, label) ->
                    val active = (if(vm.tab in listOf(1,2,6,7,8,9,10,11,12,13,14,15,16,17))5 else vm.tab) == index
                    val icon = when (index) { 0 -> ImageVector.vectorResource(R.drawable.ic_swap); 4 -> ImageVector.vectorResource(R.drawable.ic_sparkle); 3 -> ImageVector.vectorResource(R.drawable.ic_grid); else -> ImageVector.vectorResource(R.drawable.ic_more) }
                    Surface(onClick = { vm.tab = index }, modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                        color = if (active) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                        shape = RoundedCornerShape(14.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(icon, null, tint = if (active) accent() else muted(), modifier = Modifier.size(18.dp))
                            Text(label, color = if (active) MaterialTheme.colorScheme.onSurface else muted(), fontWeight = if (active) FontWeight.Bold else FontWeight.Normal, fontSize = 10.sp, modifier = Modifier.padding(top = 3.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BasePicker(selected: Int, output: Boolean, dismiss: () -> Unit, choose: (Int) -> Unit) {
    ModalBottomSheet(onDismissRequest = dismiss, containerColor = panel()) {
        Column(Modifier.padding(horizontal = 20.dp).verticalScroll(rememberScrollState()).padding(bottom = 30.dp)) {
            Text(if (output) "A different perspective." else "Choose your source base.", fontFamily = Grotesk, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(if (output) "Add any base from 2 to 36 to your results." else "Changing the base reinterprets your current input.", color = muted(), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp, bottom = 18.dp))
            (2..36).toList().chunked(5).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                    row.forEach { base ->
                        Surface(onClick = { choose(base) }, modifier = Modifier.weight(1f).heightIn(min = 54.dp),
                            shape = RoundedCornerShape(12.dp), color = if (base == selected) Lime else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (base == selected) Ink else MaterialTheme.colorScheme.onSurface) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(base.toString(), fontFamily = Mono, fontSize = 16.sp)
                                if (base in listOf(2, 8, 10, 16)) Text(BaseConverter.shortName(base), fontSize = 8.sp)
                            }
                        }
                    }
                    repeat(5 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun SavedScreen(vm: RadixViewModel, notify: (String) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 20.dp)) {
        Hero("Good numbers.", "Worth keeping.", "YOUR PERSONAL NUMBER COLLECTION", "Saved on your device. Ready when you are.")
        if (vm.saved.isEmpty()) {
            Surface(shape = Round, color = panel(), border = BorderStroke(1.dp, outline())) {
                Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(ImageVector.vectorResource(R.drawable.ic_bookmark), null, tint = accent(), modifier = Modifier.size(48.dp))
                    Text("Nothing saved. Yet.", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp))
                    Text("Tap Save on a conversion to keep it here.\nYour collection stays completely offline.", color = muted(), fontSize = 13.sp, modifier = Modifier.padding(vertical = 12.dp))
                    Button(onClick = { vm.tab = 0 }, shape = RoundedCornerShape(14.dp), modifier = Modifier.heightIn(min = 48.dp)) { Text("Make a conversion") }
                }
            }
        } else {
            Text("${vm.saved.size} SAVED · UP TO 50", color = muted(), fontSize = 10.sp, letterSpacing = 1.sp)
            Spacer(Modifier.height(14.dp))
            vm.saved.forEach { item ->
                Surface(shape = RoundedCornerShape(20.dp), color = panel(), border = BorderStroke(1.dp, outline()), modifier = Modifier.padding(bottom = 12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("${BaseConverter.name(item.base)} · base ${item.base}", color = accent(), fontSize = 12.sp)
                                Box(Modifier.horizontalScroll(rememberScrollState())) { Text(item.input, fontFamily = Mono, fontSize = 26.sp, maxLines = 1, modifier = Modifier.padding(top = 8.dp)) }
                            }
                            IconButton(onClick = { vm.delete(item.id); notify("Removed from saved numbers.") }) { Icon(ImageVector.vectorResource(R.drawable.ic_delete), "Delete saved conversion ${item.input}", tint = muted(), modifier = Modifier.size(20.dp)) }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(item.id)), color = muted(), fontSize = 10.sp, modifier = Modifier.weight(1f))
                            TextButton(onClick = { vm.restore(item) }) { Text("Open conversion  ", fontSize = 12.sp); Icon(ImageVector.vectorResource(R.drawable.ic_arrow), null, modifier = Modifier.size(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BitLabScreen(vm: RadixViewModel) {
    val strings=LocalStrings.current
    val haptic = LocalHapticFeedback.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
        Hero("A little bit.", "A lot of magic.", "THE 8-BIT PLAYGROUND", "Tap a bit. Watch every number change.")
        Surface(shape = Round, color = panel(), border = BorderStroke(1.dp, outline())) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("BUILD YOUR BYTE", color = muted(), fontSize = 10.sp, letterSpacing = 1.3.sp, modifier = Modifier.weight(1f))
                    Text("0–255", color = accent(), fontFamily = Mono, fontSize = 11.sp)
                }
                Spacer(Modifier.height(18.dp))
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val bitRows = (7 downTo 0).toList().chunked(if (maxWidth < 400.dp) 4 else 8)
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        bitRows.forEach { bitRow ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                bitRow.forEach { bit ->
                                    val on = (vm.bitValue and (1 shl bit)) != 0
                                    val color by animateColorAsState(if (on) Lime else MaterialTheme.colorScheme.surfaceVariant, label = "bit color")
                                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Surface(
                                            onClick = { vm.toggleBit(bit); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) },
                                            color = color, contentColor = if (on) Ink else muted(), shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).semantics {
                                                contentDescription = strings.text("Toggle bit $bit, weight ${1 shl bit}")
                                                stateDescription = strings.text(if (on) "On" else "Off")
                                            }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(if (on) "1" else "0", fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                                            }
                                        }
                                        Text((1 shl bit).toString(), color = muted(), fontFamily = Mono, fontSize = 9.sp, modifier = Modifier.padding(top = 8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { vm.setBits(0) }) { Text("All off", fontSize = 11.sp) }
                    TextButton(onClick = { vm.setBits(255) }) { Text("All on", fontSize = 11.sp) }
                    TextButton(onClick = { vm.setBits((0..255).random()) }) { Icon(ImageVector.vectorResource(R.drawable.ic_dice), null, modifier = Modifier.size(16.dp)); Text("  Surprise me", fontSize = 11.sp) }
                }
                HorizontalDivider(color = outline())
                Row(Modifier.fillMaxWidth().padding(vertical = 18.dp), verticalAlignment = Alignment.Bottom) {
                    Text(vm.bitValue.toString(), color = accent(), fontFamily = Mono, fontSize = 60.sp, fontWeight = FontWeight.Bold)
                    Text("  decimal", color = muted(), fontSize = 13.sp, modifier = Modifier.padding(bottom = 12.dp))
                    Spacer(Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${vm.bitValue.toString(16).uppercase()} HEX", color = muted(), fontFamily = Mono, fontSize = 14.sp)
                        Text("${vm.bitValue.toString(8)} OCT", color = muted(), fontFamily = Mono, fontSize = 14.sp, modifier = Modifier.padding(top = 6.dp, bottom = 12.dp))
                    }
                }
                val terms = (7 downTo 0).filter { vm.bitValue and (1 shl it) != 0 }.map { (1 shl it).toString() }
                Text((if (terms.isEmpty()) "0" else terms.joinToString(" + ")) + " = ${vm.bitValue}", color = muted(), fontFamily = Mono, fontSize = 11.sp, lineHeight = 18.sp)
                Button(onClick = vm::useBits, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().padding(top = 18.dp).heightIn(min = 48.dp)) {
                    Text("Take it to the converter  "); Icon(ImageVector.vectorResource(R.drawable.ic_arrow), null, modifier = Modifier.size(18.dp))
                }
            }
        }
        Text("THE IDEA BEHIND THE DIGITS", color = muted(), fontSize = 10.sp, letterSpacing = 1.3.sp, modifier = Modifier.padding(top = 26.dp, bottom = 14.dp))
        Lesson("01", "Binary is just yes or no.", "Each bit is a switch: 0 is off and 1 is on. From right to left, the positions are worth 1, 2, 4, 8, 16, 32, 64 and 128. Add the positions that are switched on.")
        Lesson("FF", "Hex makes binary bite-size.", "One hexadecimal digit represents four bits. 1111 in binary is F in hex, so 1111 1111 becomes FF. The digits are 0–9 followed by A–F.")
        Lesson("10", "The base changes the place.", "In any base, each position is worth a power of that base. 10 in binary means 2 in decimal. 10 in hexadecimal means 16. Same digits, different perspective.")
    }
}

@Composable
private fun Lesson(symbol: String, title: String, body: String) {
    var expanded by remember { mutableStateOf(false) }
    Surface(onClick = { expanded = !expanded }, shape = RoundedCornerShape(18.dp), color = panel(), border = BorderStroke(1.dp, outline()), modifier = Modifier.padding(bottom = 10.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(symbol, fontFamily = Mono, color = accent(), fontSize = 15.sp, modifier = Modifier.width(40.dp))
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Icon(if (expanded) ImageVector.vectorResource(R.drawable.ic_up) else ImageVector.vectorResource(R.drawable.ic_down), if (expanded) "Collapse lesson" else "Expand lesson", tint = muted(), modifier = Modifier.size(20.dp))
            }
            if (expanded) Text(body, color = muted(), fontSize = 13.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 14.dp))
        }
    }
}
