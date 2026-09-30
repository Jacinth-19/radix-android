/* Radix: exact, bounded, dependency-free base conversion. */
(() => {
  'use strict';
  const DIGITS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const MAX_DIGITS = 256;
  const FRACTION_PLACES = 24;
  const gcd = (a, b) => { a = a < 0n ? -a : a; while (b) [a, b] = [b, a % b]; return a; };
  const rational = (n, d = 1n) => { if (d <= 0n) throw new Error('Denominator must be positive'); const g = gcd(n, d); return { n: n / g, d: d / g }; };
  const integer = (s, base) => { let n = 0n; for (const ch of s) n = n * BigInt(base) + BigInt(DIGITS.indexOf(ch)); return n; };
  function parse(raw, base) {
    const fail = message => ({ kind: 'invalid', message });
    if (!Number.isInteger(base) || base < 2 || base > 36) return fail('Choose a base between 2 and 36.');
    if (raw.length > 600) return fail(`Keep your input to ${MAX_DIGITS} digits or fewer.`);
    let text = raw.trim().replace(/[\s_]/g, '').toUpperCase();
    if (!text) return { kind: 'empty' };
    const negative = text[0] === '-';
    if (/^[+-]/.test(text)) text = text.slice(1);
    if (!text) return fail('Add a number after the sign.');
    const prefix = ({ 2: '0B', 8: '0O', 16: '0X' })[base];
    if (prefix && text.startsWith(prefix)) text = text.slice(2);
    if (!text) return fail('Add digits after the base prefix.');
    const match = /^([0-9A-Z]*)(?:\.([0-9A-Z]*)(?:\(([0-9A-Z]+)\))?)?$/.exec(text);
    if (!match) return fail('Use digits, one point, and (parentheses) for repeating digits.');
    const whole = match[1], fraction = match[2] || '', repeat = match[3] || '';
    const digits = whole + fraction + repeat;
    if (!digits) return fail('Enter at least one digit.');
    if (digits.length > MAX_DIGITS) return fail(`Keep your input to ${MAX_DIGITS} digits or fewer.`);
    const invalid = [...digits].find(ch => DIGITS.indexOf(ch) >= base);
    if (invalid) return fail(`${invalid} isn't a base-${base} digit. Use ${base <= 10 ? `0–${DIGITS[base - 1]}` : `0–9 and A–${DIGITS[base - 1]}`}.`);
    const scale = BigInt(base) ** BigInt(fraction.length);
    let n, d;
    if (repeat) {
      const cycle = BigInt(base) ** BigInt(repeat.length) - 1n;
      d = scale * cycle;
      n = integer(whole, base) * d + integer(fraction, base) * cycle + integer(repeat, base);
    } else {
      d = scale;
      n = integer(whole, base) * d + integer(fraction, base);
    }
    return { kind: 'valid', value: rational(negative ? -n : n, d) };
  }
  function format(value, base, places = FRACTION_PLACES) {
    if (!Number.isInteger(base) || base < 2 || base > 36) throw new RangeError('Base must be 2–36');
    if (!Number.isInteger(places) || places < 1 || places > 256) throw new RangeError('Places must be 1–256');
    const abs = value.n < 0n ? -value.n : value.n;
    const sign = value.n < 0n ? '-' : '';
    const whole = (abs / value.d).toString(base).toUpperCase();
    let remainder = abs % value.d;
    const out = (raw, exact, repeating = false) => ({ raw, exact, repeating, display: raw + (exact ? '' : '…') });
    if (!remainder) return out(sign + whole, true);
    let digits = '', repeatIndex;
    const seen = new Map();
    while (remainder && digits.length < places) {
      if (seen.has(remainder)) { repeatIndex = seen.get(remainder); break; }
      seen.set(remainder, digits.length);
      const expanded = remainder * BigInt(base);
      digits += DIGITS[Number(expanded / value.d)];
      remainder = expanded % value.d;
    }
    if (remainder && repeatIndex === undefined && seen.has(remainder)) repeatIndex = seen.get(remainder);
    if (repeatIndex !== undefined) return out(`${sign}${whole}.${digits.slice(0, repeatIndex)}(${digits.slice(repeatIndex)})`, true, true);
    return out(`${sign}${whole}.${digits}`, remainder === 0n);
  }
  function grouped(output, base) {
    if (base !== 2 && base !== 16) return output.display;
    const [whole, ...tail] = output.raw.split('.');
    const negative = whole.startsWith('-');
    const unsigned = negative ? whole.slice(1) : whole;
    const chunks = unsigned.replace(/\B(?=(?:.{4})+$)/g, ' ');
    return (negative ? '-' : '') + chunks + (tail.length ? '.' + tail.join('.') : '') + (output.exact ? '' : '…');
  }
  const name = base => ({ 2: 'Binary', 8: 'Octal', 10: 'Decimal', 16: 'Hexadecimal' })[base] || `Base ${base}`;
  const shortName = base => ({ 2: 'BIN', 8: 'OCT', 10: 'DEC', 16: 'HEX' })[base] || `B${base}`;
  const digitHint = base => base <= 10 ? `Digits 0–${DIGITS[base - 1]}` : `Digits 0–9, A–${DIGITS[base - 1]}`;
  const engine = { parse, format, grouped, rational, name, shortName, digitHint, MAX_DIGITS, FRACTION_PLACES };
  if (typeof module !== 'undefined' && module.exports) module.exports = engine;
  if (typeof window !== 'undefined') window.RadixEngine = engine;
})();
