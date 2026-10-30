@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.radix.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

internal const val APP_VERSION_CODE = 8
internal const val APP_VERSION_NAME = "7.0.1"

@Composable internal fun SplashScreen() {
    val s = LocalStrings.current
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(84.dp).clip(RoundedCornerShape(24.dp)).background(Lime), contentAlignment = Alignment.Center) {
                Text("rₓ", color = Ink, fontFamily = Grotesk, fontSize = 52.sp, fontWeight = FontWeight.Bold)
            }
            Text("radix", fontFamily = Grotesk, fontWeight = FontWeight.Bold, fontSize = 44.sp, letterSpacing = (-2).sp, modifier = Modifier.padding(top = 14.dp))
            Text(s.tagline, color = muted(), fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
            CircularProgressIndicator(color = accent(), strokeWidth = 3.dp, modifier = Modifier.padding(top = 26.dp).size(28.dp))
        }
    }
}

private data class TourPage(val title: String, val body: String)
@Composable internal fun OnboardingScreen(vm: RadixViewModel) {
    val s = LocalStrings.current
    val scope = rememberCoroutineScope()
    val pages = listOf(
        TourPage(s.tourConvertTitle, s.tourConvertBody),
        TourPage(s.tourLogicTitle, s.tourLogicBody),
        TourPage(s.tourKmapTitle, s.tourKmapBody),
        TourPage(s.tourPracticeTitle, s.tourPracticeBody),
        TourPage(s.tourRemindTitle, s.tourRemindBody)
    )
    val pager = rememberPagerState(pageCount = { pages.size })
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp).padding(top = 20.dp, bottom = 24.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("radix", fontFamily = Grotesk, fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.weight(1f))
            TextButton(onClick = vm::completeOnboarding) { Text(s.skip, fontSize = 13.sp) }
        }
        HorizontalPager(pager, Modifier.weight(1f).fillMaxWidth()) { page ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Center) {
                Text("0${page + 1} / 05", fontFamily = Mono, color = accent(), fontSize = 12.sp)
                Text(pages[page].title, fontFamily = Grotesk, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 37.sp, modifier = Modifier.padding(top = 10.dp))
                Text(pages[page].body, color = muted(), fontSize = 14.sp, lineHeight = 22.sp, modifier = Modifier.padding(top = 12.dp))
                Spacer(Modifier.height(18.dp))
                when (page) {
                    0 -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("FF" to "HEX", "377" to "OCT", "11111111" to "BIN").forEach { (v, b) ->
                            Surface(shape = RoundedCornerShape(16.dp), color = panel(), border = BorderStroke(1.dp, outline()), modifier = Modifier.weight(1f)) {
                                Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(v, fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = accent())
                                    Text(b, fontFamily = Mono, fontSize = 9.sp, color = muted(), modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                    1 -> Surface(shape = RoundedCornerShape(16.dp), color = panel(), border = BorderStroke(1.dp, outline())) {
                        Column(Modifier.padding(16.dp)) {
                            listOf("0 · 0 → 0", "0 · 1 → 0", "1 · 0 → 0", "1 · 1 → 1").forEach {
                                Text(it, fontFamily = Mono, fontSize = 15.sp, lineHeight = 26.sp)
                            }
                            Text("A AND B — tap inputs to feel it", color = muted(), fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                    2 -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("0", "1", "1", "0").forEachIndexed { i, v ->
                            Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant,
                                border = if (i in listOf(1, 2)) BorderStroke(2.dp, accent()) else BorderStroke(1.dp, outline()),
                                modifier = Modifier.weight(1f).heightIn(min = 72.dp)) {
                                Box(contentAlignment = Alignment.Center) { Text(v, fontFamily = Mono, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = if (v == "1") accent() else MaterialTheme.colorScheme.onSurface) }
                            }
                        }
                    }
                    3 -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Easy — first wins" to 1f, "Intermediate — exam drills" to 0.66f, "Advanced — prove it" to 0.33f).forEach { (label, _) ->
                            Surface(shape = RoundedCornerShape(14.dp), color = panel(), border = BorderStroke(1.dp, outline()), modifier = Modifier.fillMaxWidth()) {
                                Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(14.dp))
                            }
                        }
                    }
                    else -> Surface(shape = RoundedCornerShape(16.dp), color = panel(), border = BorderStroke(1.dp, outline())) {
                        Column(Modifier.padding(16.dp)) {
                            Text("19:00", fontFamily = Mono, fontSize = 34.sp, fontWeight = FontWeight.Bold, color = accent())
                            Text(s.reminderIntro, color = muted(), fontSize = 12.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.Center) {
            repeat(pages.size) { i ->
                Box(Modifier.padding(horizontal = 4.dp).size(if (i == pager.currentPage) 22.dp else 8.dp, 8.dp).clip(CircleShape).background(if (i == pager.currentPage) accent() else outline()))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (pager.currentPage > 0) OutlinedButton(onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage - 1) } }, modifier = Modifier.weight(1f).heightIn(min = 54.dp), shape = RoundedCornerShape(14.dp)) { Text(s.back, fontSize = 13.sp) }
            Button(onClick = { if (pager.currentPage == pages.size - 1) vm.completeOnboarding() else scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } }, modifier = Modifier.weight(1f).heightIn(min = 54.dp), shape = RoundedCornerShape(14.dp)) {
                Text(if (pager.currentPage == pages.size - 1) s.startLearning else s.next, fontSize = 13.sp)
            }
        }
    }
}

@Composable internal fun WhatsNewSheet(onDismiss: () -> Unit, onOpenSettings: () -> Unit) {
    val s = LocalStrings.current
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = panel(), sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 30.dp)) {
            Text(s.whatsNewTitle, fontFamily = Grotesk, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(s.whatsNewIntro, color = muted(), fontSize = 13.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
            s.whatsNewItems.forEach { item ->
                Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text(item, fontSize = 13.sp, lineHeight = 20.sp, modifier = Modifier.padding(14.dp))
                }
            }
            Text(s.whatsNewKeep, color = accent(), fontSize = 12.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 8.dp))
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).heightIn(min = 52.dp), shape = RoundedCornerShape(14.dp)) { Text(s.keepExploring, fontSize = 12.sp) }
                Button(onClick = { onDismiss(); onOpenSettings() }, modifier = Modifier.weight(1f).heightIn(min = 52.dp), shape = RoundedCornerShape(14.dp)) { Text(s.setReminder, fontSize = 12.sp) }
            }
        }
    }
}

@Composable internal fun ThemeSection(vm: RadixViewModel) {
    val s = LocalStrings.current
    Text(s.themeTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(0 to s.themeDark, 1 to s.themeLight, 2 to s.themeSystem).forEach { (mode, label) ->
            OutlinedButton(onClick = { vm.setThemeMode(mode, false) }, modifier = Modifier.weight(1f).heightIn(min = 52.dp).semantics { selected = vm.themeMode == mode },
                border = BorderStroke(if (vm.themeMode == mode) 2.dp else 1.dp, if (vm.themeMode == mode) accent() else outline()),
                shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 4.dp)) { Text(label, fontSize = 11.sp, maxLines = 1) }
        }
    }
    // Resolve "system" against the live system theme once composed.
    val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
    LaunchedEffect(systemDark) { if (vm.themeMode == 2) vm.setThemeMode(2, systemDark) }
}

@Composable internal fun ReminderSection(vm: RadixViewModel, notify: (String) -> Unit) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var picker by remember { mutableStateOf(false) }
    val request = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) { vm.setNotifEnabled(true); notify(s.reminderOnToast) }
        else notify(s.reminderBlockedToast)
    }
    Text(s.reminderTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    Text(s.reminderIntro, color = muted(), fontSize = 12.sp, lineHeight = 19.sp)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(s.reminderDaily, fontSize = 14.sp); Text(s.reminderDailyNote, color = muted(), fontSize = 11.sp) }
        Switch(checked = vm.notifEnabled, onCheckedChange = { on ->
            if (!on) { vm.setNotifEnabled(false); return@Switch }
            val needs = Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            if (needs) request.launch(Manifest.permission.POST_NOTIFICATIONS) else { vm.setNotifEnabled(true); notify(s.reminderOnToast) }
        }, modifier = Modifier.semantics { contentDescription = s.reminderDaily })
    }
    OutlinedButton(onClick = { picker = true }, enabled = vm.notifEnabled, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(12.dp)) {
        Text(s.reminderTime(vm.notifHour, vm.notifMinute), fontFamily = Mono, fontSize = 14.sp)
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick={
            if(StudyReminder.notificationsAllowed(context)){StudyReminder.preview(context);notify(s.reminderPreviewToast)}else notify(s.reminderBlockedToast)
        },modifier=Modifier.weight(1f).heightIn(min=52.dp),shape=RoundedCornerShape(12.dp),contentPadding=PaddingValues(horizontal=6.dp)) {
            Text(s.reminderPreview,fontSize=10.sp,lineHeight=13.sp,maxLines=2)
        }
        OutlinedButton(onClick={
            StudyReminder.prepareChannel(context)
            val channelIntent=Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE,context.packageName).putExtra(Settings.EXTRA_CHANNEL_ID,StudyReminder.CHANNEL)
            runCatching{context.startActivity(channelIntent)}.onFailure{context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE,context.packageName))}
        },modifier=Modifier.weight(1f).heightIn(min=52.dp),shape=RoundedCornerShape(12.dp),contentPadding=PaddingValues(horizontal=6.dp)) {
            Text(s.reminderSettings,fontSize=10.sp,lineHeight=13.sp,maxLines=2)
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(s.reminderStreak, fontSize = 13.sp) }
        Switch(checked = vm.notifStreakTopic, onCheckedChange = { vm.setNotifTopics(it, vm.notifTipsTopic) }, enabled = vm.notifEnabled)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(s.reminderTips, fontSize = 13.sp) }
        Switch(checked = vm.notifTipsTopic, onCheckedChange = { vm.setNotifTopics(vm.notifStreakTopic, it) }, enabled = vm.notifEnabled)
    }
    Text(s.streakLine(vm.streakDays), color = accent(), fontFamily = Mono, fontSize = 12.sp)
    if (picker) {
        val state = rememberTimePickerState(vm.notifHour, vm.notifMinute, false)
        AlertDialog(onDismissRequest = { picker = false }, title = { Text(s.pickTime) },
            text = { TimePicker(state) },
            confirmButton = { TextButton(onClick = { vm.setNotifTime(state.hour, state.minute); picker = false; notify(s.reminderOnToast) }) { Text(s.save) } },
            dismissButton = { TextButton(onClick = { picker = false }) { Text(s.cancel) } })
    }
}

@Composable internal fun InboxSection(vm: RadixViewModel) {
    val s = LocalStrings.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(s.inboxTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        if (vm.inbox.isNotEmpty()) TextButton(onClick = vm::clearInbox) { Text(s.inboxClear, fontSize = 11.sp) }
    }
    if (vm.inbox.isEmpty()) Text(s.inboxEmpty, color = muted(), fontSize = 12.sp, lineHeight = 19.sp)
    else vm.inbox.take(10).forEach { item ->
        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) { Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold); Text(item.body, color = muted(), fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 4.dp)) }
        }
    }
}

@Composable internal fun UpdateSection(vm: RadixViewModel, notify: (String) -> Unit) {
    val s = LocalStrings.current
    val clipboard = LocalClipboardManager.current
    var notes by remember { mutableStateOf(false) }
    Text(s.updateTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    Text(s.updateCurrent(APP_VERSION_NAME, APP_VERSION_CODE), fontFamily = Mono, fontSize = 12.sp, color = accent())
    Text(s.updateHow, color = muted(), fontSize = 12.sp, lineHeight = 19.sp)
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedButton(onClick = { notes = true }, modifier = Modifier.weight(1f).heightIn(min = 52.dp), shape = RoundedCornerShape(12.dp)) { Text(s.updateNotes, fontSize = 11.sp) }
        OutlinedButton(onClick = { clipboard.setText(AnnotatedString("Radix $APP_VERSION_NAME (code $APP_VERSION_CODE)")); notify(s.updateCopied) }, modifier = Modifier.weight(1f).heightIn(min = 52.dp), shape = RoundedCornerShape(12.dp)) { Text(s.updateCopy, fontSize = 11.sp) }
    }
    if (notes) WhatsNewSheet({ notes = false }) { vm.tab = 10 }
}
