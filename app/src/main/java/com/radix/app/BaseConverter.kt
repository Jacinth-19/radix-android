package com.radix.app

import java.math.BigInteger
import java.util.Locale

/** No floating-point arithmetic, Android dependencies, or network calls. */
object BaseConverter {
    const val MAX_DIGITS = 256
    const val FRACTION_PLACES = 24
    private val ZERO = BigInteger.ZERO
    private val ONE = BigInteger.ONE
    private const val DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val pattern = Regex("^([0-9A-Za-z]*)(?:\\.([0-9A-Za-z]*)(?:\\(([0-9A-Za-z]+)\\))?)?$")

    data class Rational(val numerator: BigInteger, val denominator: BigInteger) {
        init { require(denominator > ZERO) }
        val isInteger: Boolean get() = denominator == ONE
        companion object {
            fun of(numerator: BigInteger, denominator: BigInteger = ONE): Rational {
                require(denominator > ZERO)
                val gcd = numerator.abs().gcd(denominator)
                return Rational(numerator / gcd, denominator / gcd)
            }
        }
    }

    sealed interface Parsed {
        data class Valid(val value: Rational) : Parsed
        data class Invalid(val message: String) : Parsed
        data object Empty : Parsed
    }

    data class Formatted(val raw: String, val exact: Boolean, val repeating: Boolean = false) {
        val display: String get() = raw + if (exact) "" else "…"
    }

    fun parse(raw: String, base: Int): Parsed {
        if (base !in 2..36) return Parsed.Invalid("Choose a base between 2 and 36.")
        if (raw.length > 600) return Parsed.Invalid("Keep your input to $MAX_DIGITS digits or fewer.")
        var text = raw.trim().replace(Regex("[\\s_]"), "").uppercase(Locale.ROOT)
        if (text.isEmpty()) return Parsed.Empty
        val negative = text.startsWith('-')
        if (text.startsWith('-') || text.startsWith('+')) text = text.drop(1)
        if (text.isEmpty()) return Parsed.Invalid("Add a number after the sign.")
        val prefix = when (base) { 2 -> "0B"; 8 -> "0O"; 16 -> "0X"; else -> null }
        if (prefix != null && text.startsWith(prefix)) text = text.drop(2)
        if (text.isEmpty()) return Parsed.Invalid("Add digits after the base prefix.")
        val match = pattern.matchEntire(text)
            ?: return Parsed.Invalid("Use digits, one point, and (parentheses) for repeating digits.")
        val whole = match.groupValues[1]
        val fraction = match.groupValues[2]
        val repeat = match.groupValues[3]
        val digits = whole + fraction + repeat
        if (digits.isEmpty()) return Parsed.Invalid("Enter at least one digit.")
        if (digits.length > MAX_DIGITS) return Parsed.Invalid("Keep your input to $MAX_DIGITS digits or fewer.")
        val invalid = digits.firstOrNull { DIGITS.indexOf(it) !in 0 until base }
        if (invalid != null) {
            val allowed = if (base <= 10) "0–${DIGITS[base - 1]}" else "0–9 and A–${DIGITS[base - 1]}"
            return Parsed.Invalid("$invalid isn't a base-$base digit. Use $allowed.")
        }
        val radix = BigInteger.valueOf(base.toLong())
        fun number(s: String) = if (s.isEmpty()) ZERO else BigInteger(s, base)
        val scale = radix.pow(fraction.length)
        val denominator: BigInteger
        val numerator: BigInteger
        if (repeat.isNotEmpty()) {
            val cycle = radix.pow(repeat.length) - ONE
            denominator = scale * cycle
            numerator = number(whole) * denominator + number(fraction) * cycle + number(repeat)
        } else {
            denominator = scale
            numerator = number(whole) * denominator + number(fraction)
        }
        return Parsed.Valid(Rational.of(if (negative) -numerator else numerator, denominator))
    }

    fun format(value: Rational, base: Int, places: Int = FRACTION_PLACES): Formatted {
        require(base in 2..36)
        require(places in 1..256)
        val sign = if (value.numerator < ZERO) "-" else ""
        val parts = value.numerator.abs().divideAndRemainder(value.denominator)
        val whole = parts[0].toString(base).uppercase(Locale.ROOT)
        var remainder = parts[1]
        if (remainder == ZERO) return Formatted(sign + whole, true)
        val radix = BigInteger.valueOf(base.toLong())
        val seen = mutableMapOf<BigInteger, Int>()
        val digits = StringBuilder()
        var repeatIndex: Int? = null
        while (remainder != ZERO && digits.length < places) {
            val known = seen[remainder]
            if (known != null) { repeatIndex = known; break }
            seen[remainder] = digits.length
            val step = (remainder * radix).divideAndRemainder(value.denominator)
            digits.append(DIGITS[step[0].toInt()])
            remainder = step[1]
        }
        if (remainder != ZERO && repeatIndex == null) repeatIndex = seen[remainder]
        if (repeatIndex != null) {
            val i = repeatIndex
            return Formatted("$sign$whole.${digits.substring(0, i)}(${digits.substring(i)})", true, true)
        }
        return Formatted("$sign$whole.$digits", remainder == ZERO)
    }

    /** Cosmetic grouping only. Always copy Formatted.raw, not this value. */
    fun grouped(output: Formatted, base: Int): String {
        val text = output.display
        if (base != 2 && base != 16) return text
        val split = text.indexOf('.')
        val integer = if (split >= 0) text.substring(0, split) else text.removeSuffix("…")
        val negative = integer.startsWith('-')
        val unsigned = integer.removePrefix("-")
        if (unsigned.length <= 4) return text
        val groups = unsigned.reversed().chunked(4).joinToString(" ").reversed()
        return (if (negative) "-" else "") + groups + if (split >= 0) text.substring(split) else if (!output.exact) "…" else ""
    }

    fun name(base: Int): String = when (base) {
        2 -> "Binary"; 8 -> "Octal"; 10 -> "Decimal"; 16 -> "Hexadecimal"; else -> "Base $base"
    }
    fun shortName(base: Int): String = when (base) {
        2 -> "BIN"; 8 -> "OCT"; 10 -> "DEC"; 16 -> "HEX"; else -> "B$base"
    }
    fun digitHint(base: Int): String = if (base <= 10) "Digits 0–${DIGITS[base - 1]}" else "Digits 0–9, A–${DIGITS[base - 1]}"
}
