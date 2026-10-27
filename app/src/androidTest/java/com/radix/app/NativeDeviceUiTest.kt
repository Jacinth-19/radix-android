package com.radix.app

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compact-device coverage for touch targets, rotation, and TalkBack-facing Compose semantics.
 * This verifies the accessibility tree and state updates; it does not synthesize or listen to
 * TalkBack speech.
 */
@RunWith(AndroidJUnit4::class)
class NativeDeviceUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val strings: Strings
        get() = AppStrings.forLang(
            AppStrings.systemLanguage(composeRule.activity.resources.configuration.locales)
        )

    private fun localized(source: String): String = strings.text(source)

    @Test
    fun compactLayoutsTouchAndAccessibilitySemanticsWorkAcrossRoutesAndRotation() {
        openHome()
        composeRule.onNode(hasText(strings.tabConvert) and hasClickAction()).assert(
            SemanticsMatcher.expectValue(SemanticsProperties.Selected, true)
        )

        // The 8-bit switches must remain comfortably tappable at 320 dp, and expose
        // both their identity and changing on/off state to accessibility services.
        composeRule.onNode(hasText(strings.tabMore) and hasClickAction()).performClick()
        composeRule.onNodeWithText(localized("The 8-bit playground")).performScrollTo().performClick()
        composeRule.onNodeWithText("All off").performScrollTo().performClick()
        val bit7Description = localized("Toggle bit 7, weight 128")
        val bit7 = composeRule.onNodeWithContentDescription(bit7Description)
        bit7.performScrollTo()
        bit7.assert(hasStateDescription(localized("Off")))
        assertMinimumTarget(bit7, minWidthDp = 48f, minHeightDp = 56f)
        bit7.performClick()
        composeRule.onNodeWithContentDescription(bit7Description)
            .assert(hasStateDescription(localized("On")))

        setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
        val rotatedBit7 = composeRule.onNodeWithContentDescription(bit7Description)
        rotatedBit7.performScrollTo()
        assertMinimumTarget(rotatedBit7, minWidthDp = 48f, minHeightDp = 56f)
        rotatedBit7.performClick()
        composeRule.onNodeWithContentDescription(bit7Description)
            .assert(hasStateDescription(localized("Off")))
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT)

        // K-map cells report a useful label and selected state, not only their visible digit.
        composeRule.onNode(hasText(strings.tabKmaps) and hasClickAction()).performClick()
        composeRule.onNodeWithText("Reset to 0").performScrollTo().performClick()
        val zeroCellDescription = localized("Minterm m0, A=0, B=0, output 0. Tap to cycle")
        val zeroCell = composeRule.onNodeWithContentDescription(zeroCellDescription)
        zeroCell.performScrollTo()
        zeroCell.assert(hasStateDescription(localized("Not selected")))
        assertMinimumTarget(zeroCell, minWidthDp = 48f, minHeightDp = 76f)
        zeroCell.performClick()
        composeRule.onNodeWithContentDescription(
            localized("Minterm m0, A=0, B=0, output 1. Tap to cycle")
        ).assertExists()
        composeRule.onNodeWithText("Check a group").performScrollTo().performClick()
        val groupCell = composeRule.onNodeWithContentDescription(
            localized("Minterm m0, A=0, B=0, output 1. Select group cell")
        )
        groupCell.performScrollTo().performClick()
        groupCell.assert(hasStateDescription(localized("Selected")))

        // The circuit builder exposes labelled pins and large external-input targets;
        // switch semantics and input state both update after real touch actions.
        composeRule.onNode(hasText(strings.tabMore) and hasClickAction()).performClick()
        composeRule.onNodeWithText(localized("Circuit builder")).performScrollTo().performClick()
        composeRule.onNodeWithText("Half adder (probe = Sum)").performScrollTo().performClick()
        composeRule.onNodeWithText("Canvas · 2/12 gates").performScrollTo()
        composeRule.onNodeWithContentDescription(localized("XOR input 1, wired")).assertExists()

        val wiringMode = composeRule.onNodeWithContentDescription(localized("Toggle wiring mode"))
        wiringMode.performScrollTo()
        wiringMode.assertIsOff()
        wiringMode.performTouchInput { click() }
        wiringMode.assertIsOn()
        wiringMode.performTouchInput { click() }
        wiringMode.assertIsOff()

        val inputADescription = localized("External input A, 0. Tap to toggle.")
        val inputA = composeRule.onNodeWithContentDescription(inputADescription)
        inputA.performScrollTo()
        assertMinimumTarget(inputA, minWidthDp = 48f, minHeightDp = 48f)
        inputA.performClick()
        composeRule.onNodeWithContentDescription(
            localized("External input A, 1. Tap to toggle.")
        ).assertExists()
    }

    @Test
    fun moreGroupsToolsAndMakesHistoryLearningPracticeAndSettingsDiscoverable() {
        openHome()
        openMore()
        listOf(
            "Learn & track progress",
            "Number tools",
            "Logic & circuit labs",
            "Progress & rewards",
            "Quick tools",
            "Learning path & progress",
            "Conversion history",
            "Practice & self-tests",
            "Base arithmetic lab",
            "Full circuit lab",
            "BCD 4-in / 7-out decoder",
            "Expression-to-circuit lab",
            "Circuit builder"
        ).forEach { title ->
            composeRule.onNodeWithText(localized(title)).performScrollTo().assertExists()
        }

        composeRule.onNodeWithText(localized("Conversion history")).performScrollTo().performClick()
        composeRule.onNodeWithText(localized("Recent activity"), substring = true).performScrollTo().assertExists()

        openMore()
        composeRule.onNodeWithText(localized("Learning path & progress")).performScrollTo().performClick()
        composeRule.onNodeWithText(localized("Your overall progress")).performScrollTo().assertExists()
        composeRule.onNodeWithText(localized("Continue the path")).performScrollTo().assertExists()

        openMore()
        composeRule.onNodeWithText(localized("Practice & self-tests")).performScrollTo().performClick()
        composeRule.onNodeWithText(localized("Check my answer")).performScrollTo().assertExists()
        composeRule.onNodeWithText(localized("Subjective · short-answer checkpoint")).performScrollTo().assertExists()

        openMore()
        composeRule.onNodeWithText(localized("Settings, reminders & backup")).performScrollTo().performClick()
        composeRule.onNodeWithText(localized("Appearance")).performScrollTo().assertExists()
        composeRule.onNodeWithText(localized("Theme")).assertExists()
        assertTrue(
            "The old language note must not appear under Appearance",
            composeRule.onAllNodesWithText("Theme, automatic system language, haptics, notifications and portable study data")
                .fetchSemanticsNodes().isEmpty()
        )
    }

    @Test
    fun bcdDecoderShowsLiveSevenSegmentPreviewAndSurvivesLandscapeRotation() {
        openHome()
        openMore()
        composeRule.onNodeWithText(localized("Full circuit lab")).performScrollTo().performClick()
        composeRule.onNodeWithText(localized("Combinational")).assertExists()

        openMore()
        composeRule.onNodeWithText(localized("BCD 4-in / 7-out decoder")).performScrollTo().performClick()
        composeRule.onNodeWithText(localized("BCD input · 8421")).performScrollTo().assertExists()
        composeRule.onNodeWithText(localized("a–g: a1 b1 c1 d1 e1 f1 g0")).assertExists()
        val displayLabel=localized("Seven-segment display")
        composeRule.onNodeWithContentDescription(
            "$displayLabel: 0", substring = true
        ).assertExists()

        composeRule.onNodeWithContentDescription(localized("Input D 0. Tap to toggle."))
            .performScrollTo().performClick()
        composeRule.onNodeWithText(localized("a–g: a0 b1 c1 d0 e0 f0 g0")).assertExists()
        composeRule.onNodeWithContentDescription(
            "$displayLabel: 1", substring = true
        ).assertExists()

        setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
        composeRule.onNodeWithText(localized("Rotate the device to landscape for more room in wide gate and circuit diagrams; portrait remains swipeable."))
            .assertExists()
        composeRule.onNodeWithText(localized("Seven-segment display")).performScrollTo().assertExists()
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT)
    }

    @Test
    fun expressionToCircuitEntryKeepsLiveInputsAndGateDiagramInSync() {
        openHome()
        openMore()
        composeRule.onNodeWithText(localized("Expression-to-circuit lab")).performScrollTo().performClick()
        composeRule.onNodeWithText(localized("Solve")).performScrollTo().performClick()

        composeRule.onNodeWithContentDescription(localized("Expression input A 1. Tap to toggle."))
            .performScrollTo().performClick()
        composeRule.onNodeWithContentDescription(localized("Expression input A 0. Tap to toggle."))
            .assertExists()

        composeRule.onNodeWithText(localized("K-map")).performScrollTo().performClick()
        composeRule.onNodeWithText(localized("Gate-level circuit · live")).performScrollTo().assertExists()
        composeRule.onNodeWithText(
            localized("Inputs: A=0 · B=0. Group gates update as the switches change."),
            substring = true
        ).performScrollTo().assertExists()
    }

    @Test
    fun arithmeticLabShowsAdditionSubtractionMultiplicationAndDivisionInBinaryAndDenary() {
        openHome()
        openMore()
        composeRule.onNodeWithText(localized("Base arithmetic lab")).performScrollTo().performClick()
        composeRule.onNodeWithText(localized("Equivalent answers")).performScrollTo().assertExists()

        // The lab opens in binary with 1011 + 0111 = 10010 (18 denary).
        composeRule.onNodeWithText("18").assertExists()
        composeRule.onNodeWithText("10010").assertExists()
        chooseOperator("−")
        composeRule.onNodeWithText("4").assertExists()
        composeRule.onNodeWithText("100").assertExists()
        chooseOperator("×")
        composeRule.onNodeWithText("77").assertExists()
        composeRule.onNodeWithText("1001101").assertExists()
        chooseOperator("÷")
        composeRule.onNodeWithText("10: 4  ·  2: 100").assertExists()

        composeRule.onNode(hasText("DEC") and hasClickAction()).performScrollTo().performClick()
        val firstNumber=composeRule.onNodeWithText(localized("First number"))
        firstNumber.performScrollTo()
        firstNumber.performTextClearance()
        firstNumber.performTextInput("24")
        val secondNumber=composeRule.onNodeWithText(localized("Second number"))
        secondNumber.performScrollTo()
        secondNumber.performTextClearance()
        secondNumber.performTextInput("5")
        chooseOperator("+")
        composeRule.onNodeWithText("29").assertExists()
        composeRule.onNodeWithText("11101").assertExists()
        chooseOperator("−")
        composeRule.onNodeWithText("19").assertExists()
        composeRule.onNodeWithText("10011").assertExists()
        chooseOperator("×")
        composeRule.onNodeWithText("120").assertExists()
        composeRule.onNodeWithText("1111000").assertExists()
        chooseOperator("÷")
        composeRule.onNodeWithText("10: 4  ·  2: 100").assertExists()
    }

    private fun openMore() {
        composeRule.onNode(hasText(strings.tabMore) and hasClickAction()).performClick()
    }

    private fun chooseOperator(symbol:String) {
        composeRule.onNode(hasText(symbol) and hasClickAction()).performScrollTo().performClick()
    }

    private fun openHome() {
        // Compose's test clock owns LaunchedEffect delays; advance past the app's splash delay.
        composeRule.mainClock.advanceTimeBy(1_500)
        composeRule.waitForIdle()
        waitForUi("startup, onboarding, or home") {
            hasTextNode(strings.skip) ||
                hasTextNode(strings.tabConvert) ||
                hasTextNode(strings.keepExploring)
        }
        if (hasTextNode(strings.skip)) {
            composeRule.onNodeWithText(strings.skip).performClick()
        }
        // Completing onboarding can briefly expose the What's New sheet before the home
        // tab is actionable. Wait for either state rather than racing its LaunchedEffect.
        waitForUi("onboarding completion or What's New") {
            hasTextNode(strings.keepExploring) || hasTextNode(strings.tabConvert)
        }
        if (hasTextNode(strings.keepExploring)) {
            composeRule.onNodeWithText(strings.keepExploring).performClick()
        }
        waitForUi("home tab") { hasTextNode(strings.tabConvert) }
    }

    private fun waitForUi(state: String, condition: () -> Boolean) {
        val wait = runCatching { composeRule.waitUntil(timeoutMillis = 20_000, condition = condition) }
        if (wait.isFailure) {
            throw AssertionError(
                "Timed out waiting for $state; locale=${strings.languageCode}; " +
                    "deviceLocales=${composeRule.activity.resources.configuration.locales.toLanguageTags()}; " +
                    "visible=${semanticsTextSnapshot()}; failure=${wait.exceptionOrNull()?.message}"
            )
        }
    }

    private fun semanticsTextSnapshot(): String = runCatching {
        composeRule.onAllNodes(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.Text),
            useUnmergedTree = true
        ).fetchSemanticsNodes().flatMap { node ->
            node.config.getOrElse(SemanticsProperties.Text) { emptyList() }
        }.map { it.text }.distinct().take(50).joinToString(" | ")
    }.getOrElse { "<semantics unavailable: ${it.message}>" }

    private fun hasTextNode(text: String): Boolean =
        composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()

    private fun setOrientation(requested: Int, expected: Int) {
        composeRule.activity.requestedOrientation = requested
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.activity.resources.configuration.orientation == expected
        }
        composeRule.waitForIdle()
        // MainActivity's activity-scoped splash state is recreated on orientation changes.
        composeRule.mainClock.advanceTimeBy(1_500)
        composeRule.waitForIdle()
    }

    private fun assertMinimumTarget(
        node: SemanticsNodeInteraction,
        minWidthDp: Float,
        minHeightDp: Float
    ) {
        val density = composeRule.activity.resources.displayMetrics.density
        val bounds = node.fetchSemanticsNode().boundsInRoot
        assertTrue(
            "Expected target width >= ${minWidthDp}dp; was ${bounds.width / density}dp",
            bounds.width + 1f >= minWidthDp * density
        )
        assertTrue(
            "Expected target height >= ${minHeightDp}dp; was ${bounds.height / density}dp",
            bounds.height + 1f >= minHeightDp * density
        )
    }
}
