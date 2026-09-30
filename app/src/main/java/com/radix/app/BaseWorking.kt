package com.radix.app

import java.math.BigInteger
import java.util.Locale

/** Exact school-style working. It shares the converter's arithmetic and never uses Double. */
object BaseWorking {
    private const val DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val ZERO = BigInteger.ZERO
    private val ONE = BigInteger.ONE
    data class Place(val digit: Char, val digitValue: Int, val exponent: Int, val numerator: BigInteger, val denominator: BigInteger) {
        val value: BaseConverter.Rational get() = BaseConverter.Rational.of(numerator, denominator)
        fun line(base: Int): String = "$digit ($digitValue) × $base^$exponent = " + if (denominator == ONE) numerator.toString() else "$numerator/$denominator"
    }
    data class Division(val dividend: BigInteger, val base: Int, val quotient: BigInteger, val remainder: Int, val digit: Char) {
        fun line(): String = "$dividend ÷ $base = $quotient, remainder $remainder ($digit)"
    }
    data class Multiplication(val before: BigInteger, val denominator: BigInteger, val base: Int, val digitValue: Int, val digit: Char, val after: BigInteger) {
        fun line(): String = "$before/$denominator × $base = $digitValue + $after/$denominator → $digit"
    }
    data class Report(
        val input: String, val from: Int, val to: Int, val value: BaseConverter.Rational,
        val places: List<Place>, val repeatContribution: BaseConverter.Rational?, val repeatFormula: String?,
        val integer: BigInteger, val divisions: List<Division>, val multiplications: List<Multiplication>,
        val cycleStart: Int?, val remaining: BigInteger, val result: BaseConverter.Formatted, val shortcut: List<String>
    ) {
        val negative: Boolean get() = value.numerator < ZERO
        val reversedRemainders: String get() = divisions.asReversed().joinToString("") { it.digit.toString() }
        val fractionalDigits: String get() = multiplications.joinToString("") { it.digit.toString() }
        val ratio: String get() = if (value.denominator == ONE) value.numerator.toString() else "${value.numerator}/${value.denominator}"
        fun text(): String = buildString {
            appendLine("$input (base $from) → ${result.display} (base $to)")
            appendLine("1. Expand the source place values (work on the magnitude).")
            places.forEach { appendLine(it.line(from)) }
            repeatFormula?.let { appendLine("Repeating tail: $it") }
            appendLine("Exact value: $ratio; decimal: ${BaseConverter.format(value, 10).display}")
            appendLine("2. Divide the integer part by $to. Read remainders BOTTOM TO TOP.")
            divisions.forEach { appendLine(it.line()) }
            appendLine("Integer digits: $reversedRemainders")
            if (multiplications.isNotEmpty()) {
                appendLine("3. Multiply the fractional remainder by $to. Read digits TOP TO BOTTOM.")
                multiplications.forEachIndexed { index, step -> appendLine("${index + 1}. ${step.line()}") }
                if (cycleStart != null) appendLine("The remainder repeats: the cycle starts at fractional step ${cycleStart + 1}. Put those digits in parentheses.")
                else if (remaining != ZERO) appendLine("Stopped at 24 fractional places. The ellipsis means truncated/approximate, not exact.")
                else appendLine("The remainder is zero: the fraction terminates exactly.")
            }
            if (negative) appendLine("Restore the minus sign. This is signed-magnitude notation, NOT two's complement.")
            shortcut.forEach { appendLine(it) }
            appendLine("Answer: ${result.display} (base $to)${if (!result.exact) " — approximate" else " — exact"}")
        }
    }

    fun explain(raw: String, from: Int, to: Int): Report {
        require(to in 2..36) { "Choose a target base from 2 to 36." }
        val parsed = BaseConverter.parse(raw, from)
        require(parsed is BaseConverter.Parsed.Valid) { if (parsed is BaseConverter.Parsed.Invalid) parsed.message else "Enter a number first." }
        val value = parsed.value
        var text = raw.trim().replace(Regex("[\\s_]"), "").uppercase(Locale.ROOT).removePrefix("-").removePrefix("+")
        val prefix = when (from) { 2 -> "0B"; 8 -> "0O"; 16 -> "0X"; else -> null }
        if (prefix != null && text.startsWith(prefix)) text = text.drop(2)
        val pieces = Regex("^([0-9A-Z]*)(?:\\.([0-9A-Z]*)(?:\\(([0-9A-Z]+)\\))?)?$").matchEntire(text)!!
        val whole = pieces.groupValues[1].ifEmpty { "0" }
        val fraction = pieces.groupValues[2]
        val repeat = pieces.groupValues[3]
        val source = BigInteger.valueOf(from.toLong())
        val places = buildList {
            whole.forEachIndexed { i, ch ->
                val digit = DIGITS.indexOf(ch); val exponent = whole.length - 1 - i
                add(Place(ch, digit, exponent, BigInteger.valueOf(digit.toLong()) * source.pow(exponent), ONE))
            }
            fraction.forEachIndexed { i, ch ->
                val digit = DIGITS.indexOf(ch); val exponent = -(i + 1)
                add(Place(ch, digit, exponent, BigInteger.valueOf(digit.toLong()), source.pow(-exponent)))
            }
        }
        val tail = if (repeat.isNotEmpty()) BaseConverter.Rational.of(BigInteger(repeat, from), source.pow(fraction.length) * (source.pow(repeat.length) - ONE)) else null
        val tailFormula = tail?.let { "$repeat (base $from) / [$from^${fraction.length} × ($from^${repeat.length} − 1)] = ${it.numerator}/${it.denominator}" }
        val split = value.numerator.abs().divideAndRemainder(value.denominator)
        var current = split[0]
        val target = BigInteger.valueOf(to.toLong())
        val divisions = mutableListOf<Division>()
        do {
            val step = current.divideAndRemainder(target)
            val remainder = step[1].toInt()
            divisions.add(Division(current, to, step[0], remainder, DIGITS[remainder]))
            current = step[0]
        } while (current != ZERO)
        var remainder = split[1]
        val seen = mutableMapOf<BigInteger, Int>()
        val multiplication = mutableListOf<Multiplication>()
        while (remainder != ZERO && multiplication.size < BaseConverter.FRACTION_PLACES && remainder !in seen) {
            seen[remainder] = multiplication.size
            val step = (remainder * target).divideAndRemainder(value.denominator)
            val digit = step[0].toInt()
            multiplication.add(Multiplication(remainder, value.denominator, to, digit, DIGITS[digit], step[1]))
            remainder = step[1]
        }
        val cycle = if (remainder != ZERO) seen[remainder] else null
        val shortcuts = mutableListOf<String>()
        if (repeat.isEmpty() && from == 2 && to in listOf(8,16)) {
            val width = if (to == 8) 3 else 4
            val paddedInteger = whole.padStart(((whole.length + width - 1) / width) * width, '0')
            shortcuts.add("Binary shortcut: group $width bits at a time around the point. Pad the INTEGER on the LEFT; pad the FRACTION on the RIGHT.")
            paddedInteger.chunked(width).forEach { shortcuts.add("Integer $it → ${DIGITS[it.toInt(2)]}") }
            if (fraction.isNotEmpty()) fraction.padEnd(((fraction.length + width - 1) / width) * width, '0').chunked(width).forEach { shortcuts.add("Fraction $it → ${DIGITS[it.toInt(2)]}") }
        } else if (repeat.isEmpty() && from in listOf(8,16) && to == 2) {
            val width = if (from == 8) 3 else 4
            shortcuts.add("Binary shortcut: each base-$from digit represents exactly $width bits. Keep the point in place.")
            whole.forEach { shortcuts.add("Integer $it → ${DIGITS.indexOf(it).toString(2).padStart(width, '0')}") }
            fraction.forEach { shortcuts.add("Fraction $it → ${DIGITS.indexOf(it).toString(2).padStart(width, '0')}") }
        }
        return Report(raw, from, to, value, places, tail, tailFormula, split[0], divisions, multiplication, cycle, remainder,
            BaseConverter.format(value, to), shortcuts)
    }
}
