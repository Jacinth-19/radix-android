package com.radix.app

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.fetchSemanticsNode
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNode
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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

    @Test
    fun compactLayoutsTouchAndAccessibilitySemanticsWorkAcrossRoutesAndRotation() {
        openHome()
        composeRule.onNode(hasText("Convert") and hasClickAction()).assert(
            SemanticsMatcher.expectValue(SemanticsProperties.Selected, true)
        )

        // The 8-bit switches must remain comfortably tappable at 320 dp, and expose
        // both their identity and changing on/off state to accessibility services.
        composeRule.onNode(hasText("More") and hasClickAction()).performClick()
        composeRule.onNodeWithText("The 8-bit playground").performScrollTo().performClick()
        composeRule.onNodeWithText("All off").performScrollTo().performClick()
        val bit7 = composeRule.onNodeWithContentDescription("Toggle bit 7, weight 128")
        bit7.performScrollTo()
        bit7.assert(hasStateDescription("Off"))
        assertMinimumTarget(bit7, minWidthDp = 48f, minHeightDp = 56f)
        bit7.performClick()
        composeRule.onNodeWithContentDescription("Toggle bit 7, weight 128").assert(hasStateDescription("On"))

        setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
        val rotatedBit7 = composeRule.onNodeWithContentDescription("Toggle bit 7, weight 128")
        rotatedBit7.performScrollTo()
        assertMinimumTarget(rotatedBit7, minWidthDp = 48f, minHeightDp = 56f)
        rotatedBit7.performClick()
        composeRule.onNodeWithContentDescription("Toggle bit 7, weight 128").assert(hasStateDescription("Off"))
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT)

        // K-map cells report a useful label and selected state, not only their visible digit.
        composeRule.onNode(hasText("K-maps") and hasClickAction()).performClick()
        composeRule.onNodeWithText("Reset to 0").performScrollTo().performClick()
        val zeroCell = composeRule.onNodeWithContentDescription(
            "Minterm m0, A=0, B=0, output 0. Tap to cycle"
        )
        zeroCell.performScrollTo()
        zeroCell.assert(hasStateDescription("Not selected"))
        assertMinimumTarget(zeroCell, minWidthDp = 48f, minHeightDp = 76f)
        zeroCell.performClick()
        composeRule.onNodeWithContentDescription(
            "Minterm m0, A=0, B=0, output 1. Tap to cycle"
        ).assertExists()
        composeRule.onNodeWithText("Check a group").performScrollTo().performClick()
        val groupCell = composeRule.onNodeWithContentDescription(
            "Minterm m0, A=0, B=0, output 1. Select group cell"
        )
        groupCell.performScrollTo().performClick()
        groupCell.assert(hasStateDescription("Selected"))

        // The circuit builder exposes labelled pins and large external-input targets;
        // switch semantics and input state both update after real touch actions.
        composeRule.onNode(hasText("More") and hasClickAction()).performClick()
        composeRule.onNodeWithText("Circuit builder").performScrollTo().performClick()
        composeRule.onNodeWithText("Half adder (probe = Sum)").performScrollTo().performClick()
        composeRule.onNodeWithText("Canvas · 2/12 gates").performScrollTo()
        composeRule.onNodeWithContentDescription("XOR input 1, wired").assertExists()

        val wiringMode = composeRule.onNodeWithContentDescription("Toggle wiring mode")
        wiringMode.assertIsOff()
        wiringMode.performClick()
        wiringMode.assertIsOn()
        wiringMode.performClick()
        wiringMode.assertIsOff()

        val inputA = composeRule.onNodeWithContentDescription("External input A, 0")
        inputA.performScrollTo()
        assertMinimumTarget(inputA, minWidthDp = 48f, minHeightDp = 48f)
        inputA.performClick()
        composeRule.onNodeWithContentDescription("External input A, 1").assertExists()
    }

    private fun openHome() {
        composeRule.waitUntil(timeoutMillis = 15_000) {
            hasTextNode("Skip") || hasTextNode("A NEW PERSPECTIVE ON NUMBERS") || hasTextNode("Convert")
        }
        if (hasTextNode("Skip")) {
            composeRule.onNodeWithText("Skip").performClick()
            composeRule.waitForIdle()
        }
        if (hasTextNode("Keep exploring")) {
            composeRule.onNodeWithText("Keep exploring").performClick()
        }
        composeRule.waitUntil(timeoutMillis = 15_000) { hasTextNode("Convert") }
    }

    private fun hasTextNode(text: String): Boolean =
        composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()

    private fun setOrientation(requested: Int, expected: Int) {
        composeRule.activity.requestedOrientation = requested
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.activity.resources.configuration.orientation == expected
        }
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
