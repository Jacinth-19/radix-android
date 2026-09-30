package com.radix.app

import java.math.BigInteger
import java.util.Random
import org.junit.Assert.*
import org.junit.Test

class BaseConverterTest {
    private fun parse(text: String, base: Int) = (BaseConverter.parse(text, base) as BaseConverter.Parsed.Valid).value
    private fun convert(text: String, from: Int, to: Int) = BaseConverter.format(parse(text, from), to)

    @Test fun familiarIntegerConversions() {
        assertEquals("11111111", convert("255", 10, 2).raw)
        assertEquals("377", convert("255", 10, 8).raw)
        assertEquals("FF", convert("255", 10, 16).raw)
        assertEquals("255", convert("ff", 16, 10).raw)
        assertEquals("35", convert("z", 36, 10).raw)
    }
    @Test fun handlesSignsAndZero() {
        assertEquals("-101010", convert("-42", 10, 2).raw)
        assertEquals("42", convert("+2a", 16, 10).raw)
        assertEquals("0", convert("-0.000", 10, 16).raw)
    }
    @Test fun stripsMatchingPrefixesAndGrouping() {
        assertEquals("255", convert("0xFF", 16, 10).raw)
        assertEquals("255", convert("0B1111_1111", 2, 10).raw)
        assertEquals("255", convert("0o377", 8, 10).raw)
        assertEquals("65535", convert(" FF FF ", 16, 10).raw)
    }
    @Test fun handlesTerminatingFractionsExactly() {
        assertEquals("1010.101", convert("10.625", 10, 2).raw)
        assertEquals("10.625", convert("1010.101", 2, 10).raw)
        assertEquals("0.8", convert(".5", 10, 16).raw)
        assertEquals("-0.8", convert("-.5", 10, 16).raw)
        assertEquals("12", convert("12.", 10, 10).raw)
    }
    @Test fun detectsRepeatingFractions() {
        val third = BaseConverter.Rational.of(BigInteger.ONE, BigInteger.valueOf(3))
        val decimal = BaseConverter.format(third, 10)
        assertEquals("0.(3)", decimal.raw)
        assertTrue(decimal.exact)
        assertTrue(decimal.repeating)
        assertEquals("0.(01)", BaseConverter.format(third, 2).raw)
        assertEquals(third, parse("0.(3)", 10))
    }
    @Test fun normalizesMixedRepeatingInputs() {
        assertEquals("0.1(6)", convert("0.1(6)", 10, 10).raw)
        assertEquals("1", convert("0.(9)", 10, 10).raw)
        assertEquals("-1.1(6)", convert("-1.1(6)", 10, 10).raw)
        assertEquals("0.0(0011)", convert("0.1", 10, 2).raw)
    }
    @Test fun flagsLongFractionsWithoutFloatRounding() {
        val value = BaseConverter.Rational.of(BigInteger.ONE, BigInteger.valueOf(97))
        val output = BaseConverter.format(value, 10)
        assertFalse(output.exact)
        assertEquals(24, output.raw.substringAfter('.').length)
        assertTrue(output.display.endsWith("…"))
        assertFalse(output.raw.contains("…"))
    }
    @Test fun detectsRepeatAtPrecisionBoundary() {
        val third = BaseConverter.Rational.of(BigInteger.ONE, BigInteger.valueOf(3))
        assertEquals("0.(3)", BaseConverter.format(third, 10, places = 1).raw)
        assertTrue(BaseConverter.format(third, 10, places = 1).exact)
    }
    @Test fun permitsExactFractionAtPrecisionBoundary() {
        val v = BaseConverter.Rational.of(BigInteger.ONE, BigInteger.valueOf(2).pow(24))
        val result = BaseConverter.format(v, 2)
        assertTrue(result.exact)
        assertEquals(24, result.raw.substringAfter('.').length)
    }
    @Test fun rejectsInvalidDigitsAndSyntax() {
        for ((text, base) in listOf("2" to 2, "G" to 16, "10..5" to 10, "1,5" to 10, "1(3)" to 10, "1.()" to 10, "0xFF" to 2, "--1" to 10, "NaN" to 10, "∞" to 10)) {
            assertTrue("$text in base $base", BaseConverter.parse(text, base) is BaseConverter.Parsed.Invalid)
        }
    }
    @Test fun handlesEmptyAndIncompleteInputs() {
        assertEquals(BaseConverter.Parsed.Empty, BaseConverter.parse("  ", 10))
        for (text in listOf("-", "+", ".", "0x")) assertTrue(BaseConverter.parse(text, 16) is BaseConverter.Parsed.Invalid)
    }
    @Test fun enforcesResourceLimits() {
        assertTrue(BaseConverter.parse("1".repeat(256), 2) is BaseConverter.Parsed.Valid)
        assertTrue(BaseConverter.parse("1".repeat(257), 2) is BaseConverter.Parsed.Invalid)
        assertTrue(BaseConverter.parse("1".repeat(10000), 2) is BaseConverter.Parsed.Invalid)
    }
    @Test fun validatesBaseRange() {
        assertTrue(BaseConverter.parse("10", 1) is BaseConverter.Parsed.Invalid)
        assertTrue(BaseConverter.parse("10", 37) is BaseConverter.Parsed.Invalid)
    }
    @Test fun arbitraryPrecisionRoundTripEveryBase() {
        val random = Random(73)
        repeat(50) {
            val n = BigInteger(256, random).let { if (it.testBit(0)) -it else it }
            val value = BaseConverter.Rational.of(n)
            for (base in 2..36) {
                val output = BaseConverter.format(value, base)
                assertEquals("base $base", value, parse(output.raw, base))
            }
        }
    }
    @Test fun rationalRoundTripsAcrossBases() {
        val values = listOf("0.125", "-42.625", "1.1(6)", "0.(3)")
        for (input in values) {
            val value = parse(input, 10)
            for (base in 2..36) {
                val result = BaseConverter.format(value, base)
                if (result.exact) assertEquals(value, parse(result.raw, base))
            }
        }
    }
    @Test fun groupingDoesNotAlterCopyValue() {
        val o = convert("65535", 10, 2)
        assertEquals("1111 1111 1111 1111", BaseConverter.grouped(o, 2))
        assertEquals("1111111111111111", o.raw)
        assertEquals("-1 FFFF", BaseConverter.grouped(convert("-131071", 10, 16), 16))
    }
}
