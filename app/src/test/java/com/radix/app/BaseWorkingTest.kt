package com.radix.app

import java.math.BigInteger
import org.junit.Assert.*
import org.junit.Test

class BaseWorkingTest {
    private fun verify(input: String, from: Int, to: Int): BaseWorking.Report {
        val report=BaseWorking.explain(input,from,to)
        var numerator=BigInteger.ZERO;var denominator=BigInteger.ONE
        val values=report.places.map {it.value}+listOfNotNull(report.repeatContribution)
        values.forEach { p -> val sum=BaseConverter.Rational.of(numerator*p.denominator+p.numerator*denominator,denominator*p.denominator);numerator=sum.numerator;denominator=sum.denominator }
        assertEquals(BaseConverter.Rational.of(report.value.numerator.abs(),report.value.denominator),BaseConverter.Rational.of(numerator,denominator))
        report.divisions.forEach { step ->
            assertEquals(step.dividend,step.quotient*BigInteger.valueOf(to.toLong())+BigInteger.valueOf(step.remainder.toLong()))
            assertTrue(step.remainder in 0 until to)
            assertEquals("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"[step.remainder],step.digit)
        }
        report.multiplications.forEach { step ->
            assertEquals(step.before*BigInteger.valueOf(to.toLong()),BigInteger.valueOf(step.digitValue.toLong())*step.denominator+step.after)
            assertTrue(step.after>=BigInteger.ZERO&&step.after<step.denominator)
        }
        val digits=report.fractionalDigits
        val fractional=report.cycleStart?.let {digits.take(it)+"("+digits.drop(it)+")"}?:digits
        val reconstructed=(if(report.negative) "-" else "")+report.reversedRemainders+(if(fractional.isNotEmpty()) ".$fractional" else "")
        assertEquals(report.result.raw,reconstructed)
        assertEquals(BaseConverter.format(report.value,to),report.result)
        return report
    }
    @Test fun decimalIntegerReadBottomToTop() {
        val r=verify("13",10,2)
        assertEquals(listOf(1,0,1,1),r.divisions.map {it.remainder})
        assertEquals("1101",r.reversedRemainders)
    }
    @Test fun fractionalDigitsReadTopToBottom() {
        val r=verify("0.625",10,2)
        assertEquals("101",r.fractionalDigits);assertEquals("0.101",r.result.raw)
        assertEquals(BigInteger.ZERO,r.remaining)
    }
    @Test fun hexadecimalDigitValues() {
        val r=verify("0xFF",16,10)
        assertEquals(listOf(15,15),r.places.map {it.digitValue});assertEquals("255",r.result.raw)
        assertEquals("Z",verify("35",10,36).result.raw)
    }
    @Test fun repeatedRemainderIsExactNotApproximate() {
        val r=verify("0.(3)",10,2)
        assertEquals(0,r.cycleStart);assertTrue(r.result.exact);assertEquals("0.(01)",r.result.raw)
        verify("0.1",10,2);verify("0.1(6)",10,16)
    }
    @Test fun sourceRecurringContributionUsesGeometricSeries() {
        val r=verify("0.1(6)",10,2)
        assertEquals(BaseConverter.Rational.of(BigInteger.ONE,BigInteger.valueOf(15)),r.repeatContribution)
        assertEquals(BaseConverter.Rational.of(BigInteger.ONE,BigInteger.valueOf(6)),r.value)
    }
    @Test fun binaryIntegerPadsLeftAndFractionPadsRight() {
        val r=verify("10101.101",2,16)
        assertEquals("15.A",r.result.raw)
        assertTrue(r.shortcut.contains("Integer 0001 → 1"))
        assertTrue(r.shortcut.contains("Fraction 1010 → A"))
    }
    @Test fun octalGroupsUseThreeBitsNotFour() {
        val r=verify("10101.1",2,8)
        assertEquals("25.4",r.result.raw)
        assertTrue(r.shortcut.contains("Fraction 100 → 4"))
        assertTrue(verify("17.4",8,2).shortcut.contains("Integer 7 → 111"))
    }
    @Test fun negativeSignsAreRestoredNotTwosComplement() {
        val r=verify("-10.625",10,2)
        assertEquals("-1010.101",r.result.raw);assertTrue(r.text().contains("NOT two's complement"))
        assertFalse(verify("-0.0",10,2).negative)
    }
    @Test fun zeroAndLeadingZeroes() {
        assertEquals(1,verify("0",10,2).divisions.size)
        assertEquals("1010",verify("00010",10,2).result.raw)
        assertEquals("0.8",verify(".5",10,16).result.raw)
    }
    @Test fun signedPrefixesAndSpaces() {
        verify(" -0B1111_1111 ",2,10);verify("+0o17",8,16);verify("a.f",16,2)
    }
    @Test fun exactPrecisionBoundary() {
        val r=verify("0.000000000000000000000001",2,10)
        assertTrue(r.result.exact)
        assertTrue(verify("0.000000000000000000000001",2,2).result.exact)
    }
    @Test fun truncationIsExplainedWithoutRounding() {
        val input="0.("+"0123456789".repeat(4)+")"
        val r=verify(input,10,3)
        assertFalse(r.result.exact);assertEquals(24,r.multiplications.size)
        assertNull(r.cycleStart);assertTrue(r.text().contains("truncated/approximate"))
    }
    @Test fun beginnerConversionsAcrossEveryBase() {
        val samples=listOf("42","255","-42","10.625","0.125","0.(3)","0.1(6)")
        samples.forEach { input -> (2..36).forEach {base->verify(input,10,base)} }
    }
    @Test fun everyBaseSupportsAlphabetAndPlacePowers() {
        (2..36).forEach {base->verify("10.1",base,10)}
    }
    @Test fun invalidInputCannotGeneratePlausibleWorking() {
        for((input,from,to) in listOf(Triple("2",2,10),Triple("",10,2),Triple("G",16,2),Triple("1.2.3",10,2),Triple("10",10,1))) {
            assertThrows(IllegalArgumentException::class.java) {BaseWorking.explain(input,from,to)}
        }
    }
}
