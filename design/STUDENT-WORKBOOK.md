# Radix 4.0 — beginner workbook

Open **Convert → Show the working**, or **K-maps → 24 examples**. All exercises run offline. The new **Logic** tab contains gates, combinational/sequential circuits and timing traces. Tap **?** for eleven illustrated cards and a twelve-step tour. The separate **Radix-Logic-Reference.html** supplies every logic model, diagram and complete table.

## Conversion answers

These are exact unless marked approximate. Parentheses mean a repeating cycle. A minus sign is signed magnitude; Convert uses ordinary signed magnitude. The new **Representations** lab separately encodes fixed-width two’s complement.

| # | Source | Target | Answer | Common pitfall |
|---|---|---|---|---|
| 1 | `13` base 10 | Base 2 | `1101` | Integer remainders are read bottom to top. |
| 2 | `45` base 10 | Base 2 | `101101` | Do not treat 10 in another base as decimal ten. |
| 3 | `255` base 10 | Base 16 | `FF` | Remainders 10–35 use A–Z. |
| 4 | `125` base 10 | Base 8 | `175` | Repeated division uses the target base. |
| 5 | `1024` base 10 | Base 2 | `10000000000` | A power of two gives one 1 followed by zeroes. |
| 6 | `110101` base 2 | Base 10 | `53` | The rightmost integer position is power zero. |
| 7 | `2F` base 16 | Base 10 | `47` | F has value 15, not 16. |
| 8 | `157` base 8 | Base 10 | `111` | The source base determines positional weights. |
| 9 | `10101.101` base 2 | Base 16 | `15.A` | Pad the whole part left, the fraction right. |
| 10 | `2F.A` base 16 | Base 2 | `101111.101` | Each hexadecimal digit corresponds to four bits. |
| 11 | `17.4` base 8 | Base 2 | `1111.1` | Each octal digit corresponds to three bits. |
| 12 | `10.625` base 10 | Base 2 | `1010.101` | Fractional digits stay in top-to-bottom order. |
| 13 | `0.125` base 10 | Base 8 | `0.1` | A finite decimal fraction may terminate in another base. |
| 14 | `.5` base 10 | Base 16 | `0.8` | A leading zero before the point is optional. |
| 15 | `0.1` base 10 | Base 2 | `0.0(0011)` | A finite decimal fraction may repeat in binary. |
| 16 | `0.(3)` base 10 | Base 2 | `0.(01)` | Parentheses denote a mathematically exact cycle. |
| 17 | `0.1(6)` base 10 | Base 16 | `0.2(A)` | Handle the non-repeating prefix before the geometric tail. |
| 18 | `-42` base 10 | Base 2 | `-101010` | The minus sign is signed magnitude, not two's complement. |
| 19 | `-10.625` base 10 | Base 16 | `-A.A` | Convert the magnitude; restore the sign last. |
| 20 | `0xFF` base 16 | Base 8 | `377` | Matching 0x/0o/0b prefixes are supported. |
| 21 | `00010` base 2 | Base 10 | `2` | Leading zeroes do not change the value. |
| 22 | `Z` base 36 | Base 10 | `35` | The maximum supported digit Z has value 35. |
| 23 | `35` base 10 | Base 36 | `Z` | Base 36 is a positional system, not a decimal suffix. |
| 24 | `1021` base 3 | Base 10 | `34` | The same method applies to less familiar bases. |
| 25 | `A.6` base 12 | Base 10 | `10.5` | Alphabet digits and fractional place powers work together. |
| 26 | `10.1` base 7 | Base 10 | `7.(142857)` | Recurring decimal output is exact when the cycle is detected. |
| 27 | `100000000000000000000` base 10 | Base 16 | `56BC75E2D63100000` | Large integers never pass through floating-point arithmetic. |

## K-map answers

Indices are ordinary binary minterm numbers with A as the MSB. Gray-code labels reorder the display; they do not renumber the truth table. Both forms below represent the same specified truth table. Different equally minimum answers can exist.

| # | Challenge | Variables | Ones | Xs | Minimum SOP | Minimum POS |
|---|---|---|---|---|---|---|
| 1 | Overlapping pairs | 2 | 1, 2, 3 | None | `A + B` | `(A + B)` |
| 2 | One isolated 1 | 2 | 3 | None | `AB` | `(A)(B)` |
| 3 | Do not group diagonals | 2 | 1, 2 | None | `A'B + AB'` | `(A + B)(A' + B')` |
| 4 | The other diagonal | 2 | 0, 3 | None | `A'B' + AB` | `(A + B')(A' + B)` |
| 5 | Drop a changing variable | 2 | 0, 1 | None | `A'` | `(A')` |
| 6 | The constant-one map | 2 | 0, 1, 2, 3 | None | `1` | `1` |
| 7 | No required 1s | 2 | None | None | `0` | `0` |
| 8 | Use a helpful don't-care | 2 | 1 | 3 | `B` | `(B)` |
| 9 | Three-variable edge wrap | 3 | 0, 2 | None | `A'C'` | `(A')(C')` |
| 10 | A whole row | 3 | 0, 1, 2, 3 | None | `A'` | `(A')` |
| 11 | An odd-index pattern | 3 | 1, 3, 5, 7 | None | `C` | `(C)` |
| 12 | Three is not a group size | 3 | 0, 1, 3 | None | `A'B' + A'C` | `(A')(B' + C)` |
| 13 | No adjacent 1s | 3 | 1, 2, 4, 7 | None | `A'B'C + A'BC' + AB'C' + ABC` | `(A + B + C)(A + B' + C')(A' + B + C')(A' + B' + C)` |
| 14 | Grow to four with Xs | 3 | 1, 3 | 5, 7 | `C` | `(C)` |
| 15 | Four corners are adjacent | 4 | 0, 2, 8, 10 | None | `B'D'` | `(B')(D')` |
| 16 | An eight-cell group | 4 | 0, 1, 2, 3, 4, 5, 6, 7 | None | `A'` | `(A')` |
| 17 | Read Gray-code rows | 4 | 4, 5, 6, 7 | None | `A'B` | `(A')(B)` |
| 18 | Four Gray-code rows | 4 | 1, 5, 9, 13 | None | `C'D` | `(C')(D)` |
| 19 | Three overlapping groups | 4 | 0, 1, 2, 3, 4, 5, 8, 9 | None | `A'B' + A'C' + B'C'` | `(A' + B')(A' + C')(B' + C')` |
| 20 | An X-assisted octet | 4 | 1, 3, 7, 11, 15 | 5, 9, 13 | `D` | `(D)` |
| 21 | POS groups zeroes | 2 | 1, 2, 3 | None | `A + B` | `(A + B)` |
| 22 | POS complement rule | 2 | 0, 2 | None | `B'` | `(B')` |
| 23 | POS corner wrapping | 4 | 1, 3, 4, 5, 6, 7, 9, 11, 12, 13, 14, 15 | None | `B + D` | `(B + D)` |
| 24 | All don't-cares | 2 | None | 0, 1, 2, 3 | `0` | `1` |

### What to remember

- Row/column order is **00, 01, 11, 10**, not 00, 01, 10, 11.
- Choose rectangles of **1, 2, 4, 8 or 16** cells; never diagonal-only pairs or groups of 3/6.
- Opposite edges touch. Four corners may be one wrapped group.
- Overlap is allowed. Xs are optional; an all-X group covers no required output.
- SOP groups **1s**: fixed 0 contributes a complemented literal; fixed 1 contributes an uncomplemented literal.
- POS groups **0s**: fixed 0 is uncomplemented; fixed 1 is complemented. Variables that change disappear.
- Cover every required output. Minimize terms/clauses first, then literals.
- Switching form does **not** complement the underlying truth table.
- `+` means OR, adjacency means AND, and an apostrophe means NOT.

## Fully worked conversions

### 1. 13 (base 10) → 1101 (base 2)

```text
13 (base 10) → 1101 (base 2)
1. Expand the source place values (work on the magnitude).
1 (1) × 10^1 = 10
3 (3) × 10^0 = 3
Exact value: 13; decimal: 13
2. Divide the integer part by 2. Read remainders BOTTOM TO TOP.
13 ÷ 2 = 6, remainder 1 (1)
6 ÷ 2 = 3, remainder 0 (0)
3 ÷ 2 = 1, remainder 1 (1)
1 ÷ 2 = 0, remainder 1 (1)
Integer digits: 1101
Answer: 1101 (base 2) — exact
```

### 2. 45 (base 10) → 101101 (base 2)

```text
45 (base 10) → 101101 (base 2)
1. Expand the source place values (work on the magnitude).
4 (4) × 10^1 = 40
5 (5) × 10^0 = 5
Exact value: 45; decimal: 45
2. Divide the integer part by 2. Read remainders BOTTOM TO TOP.
45 ÷ 2 = 22, remainder 1 (1)
22 ÷ 2 = 11, remainder 0 (0)
11 ÷ 2 = 5, remainder 1 (1)
5 ÷ 2 = 2, remainder 1 (1)
2 ÷ 2 = 1, remainder 0 (0)
1 ÷ 2 = 0, remainder 1 (1)
Integer digits: 101101
Answer: 101101 (base 2) — exact
```

### 3. 255 (base 10) → FF (base 16)

```text
255 (base 10) → FF (base 16)
1. Expand the source place values (work on the magnitude).
2 (2) × 10^2 = 200
5 (5) × 10^1 = 50
5 (5) × 10^0 = 5
Exact value: 255; decimal: 255
2. Divide the integer part by 16. Read remainders BOTTOM TO TOP.
255 ÷ 16 = 15, remainder 15 (F)
15 ÷ 16 = 0, remainder 15 (F)
Integer digits: FF
Answer: FF (base 16) — exact
```

### 4. 125 (base 10) → 175 (base 8)

```text
125 (base 10) → 175 (base 8)
1. Expand the source place values (work on the magnitude).
1 (1) × 10^2 = 100
2 (2) × 10^1 = 20
5 (5) × 10^0 = 5
Exact value: 125; decimal: 125
2. Divide the integer part by 8. Read remainders BOTTOM TO TOP.
125 ÷ 8 = 15, remainder 5 (5)
15 ÷ 8 = 1, remainder 7 (7)
1 ÷ 8 = 0, remainder 1 (1)
Integer digits: 175
Answer: 175 (base 8) — exact
```

### 5. 1024 (base 10) → 10000000000 (base 2)

```text
1024 (base 10) → 10000000000 (base 2)
1. Expand the source place values (work on the magnitude).
1 (1) × 10^3 = 1000
0 (0) × 10^2 = 0
2 (2) × 10^1 = 20
4 (4) × 10^0 = 4
Exact value: 1024; decimal: 1024
2. Divide the integer part by 2. Read remainders BOTTOM TO TOP.
1024 ÷ 2 = 512, remainder 0 (0)
512 ÷ 2 = 256, remainder 0 (0)
256 ÷ 2 = 128, remainder 0 (0)
128 ÷ 2 = 64, remainder 0 (0)
64 ÷ 2 = 32, remainder 0 (0)
32 ÷ 2 = 16, remainder 0 (0)
16 ÷ 2 = 8, remainder 0 (0)
8 ÷ 2 = 4, remainder 0 (0)
4 ÷ 2 = 2, remainder 0 (0)
2 ÷ 2 = 1, remainder 0 (0)
1 ÷ 2 = 0, remainder 1 (1)
Integer digits: 10000000000
Answer: 10000000000 (base 2) — exact
```

### 6. 110101 (base 2) → 53 (base 10)

```text
110101 (base 2) → 53 (base 10)
1. Expand the source place values (work on the magnitude).
1 (1) × 2^5 = 32
1 (1) × 2^4 = 16
0 (0) × 2^3 = 0
1 (1) × 2^2 = 4
0 (0) × 2^1 = 0
1 (1) × 2^0 = 1
Exact value: 53; decimal: 53
2. Divide the integer part by 10. Read remainders BOTTOM TO TOP.
53 ÷ 10 = 5, remainder 3 (3)
5 ÷ 10 = 0, remainder 5 (5)
Integer digits: 53
Answer: 53 (base 10) — exact
```

### 7. 2F (base 16) → 47 (base 10)

```text
2F (base 16) → 47 (base 10)
1. Expand the source place values (work on the magnitude).
2 (2) × 16^1 = 32
F (15) × 16^0 = 15
Exact value: 47; decimal: 47
2. Divide the integer part by 10. Read remainders BOTTOM TO TOP.
47 ÷ 10 = 4, remainder 7 (7)
4 ÷ 10 = 0, remainder 4 (4)
Integer digits: 47
Answer: 47 (base 10) — exact
```

### 8. 157 (base 8) → 111 (base 10)

```text
157 (base 8) → 111 (base 10)
1. Expand the source place values (work on the magnitude).
1 (1) × 8^2 = 64
5 (5) × 8^1 = 40
7 (7) × 8^0 = 7
Exact value: 111; decimal: 111
2. Divide the integer part by 10. Read remainders BOTTOM TO TOP.
111 ÷ 10 = 11, remainder 1 (1)
11 ÷ 10 = 1, remainder 1 (1)
1 ÷ 10 = 0, remainder 1 (1)
Integer digits: 111
Answer: 111 (base 10) — exact
```

### 9. 10101.101 (base 2) → 15.A (base 16)

```text
10101.101 (base 2) → 15.A (base 16)
1. Expand the source place values (work on the magnitude).
1 (1) × 2^4 = 16
0 (0) × 2^3 = 0
1 (1) × 2^2 = 4
0 (0) × 2^1 = 0
1 (1) × 2^0 = 1
1 (1) × 2^-1 = 1/2
0 (0) × 2^-2 = 0/4
1 (1) × 2^-3 = 1/8
Exact value: 173/8; decimal: 21.625
2. Divide the integer part by 16. Read remainders BOTTOM TO TOP.
21 ÷ 16 = 1, remainder 5 (5)
1 ÷ 16 = 0, remainder 1 (1)
Integer digits: 15
3. Multiply the fractional remainder by 16. Read digits TOP TO BOTTOM.
1. 5/8 × 16 = 10 + 0/8 → A
The remainder is zero: the fraction terminates exactly.
Binary shortcut: group 4 bits at a time around the point. Pad the INTEGER on the LEFT; pad the FRACTION on the RIGHT.
Integer 0001 → 1
Integer 0101 → 5
Fraction 1010 → A
Answer: 15.A (base 16) — exact
```

### 10. 2F.A (base 16) → 101111.101 (base 2)

```text
2F.A (base 16) → 101111.101 (base 2)
1. Expand the source place values (work on the magnitude).
2 (2) × 16^1 = 32
F (15) × 16^0 = 15
A (10) × 16^-1 = 10/16
Exact value: 381/8; decimal: 47.625
2. Divide the integer part by 2. Read remainders BOTTOM TO TOP.
47 ÷ 2 = 23, remainder 1 (1)
23 ÷ 2 = 11, remainder 1 (1)
11 ÷ 2 = 5, remainder 1 (1)
5 ÷ 2 = 2, remainder 1 (1)
2 ÷ 2 = 1, remainder 0 (0)
1 ÷ 2 = 0, remainder 1 (1)
Integer digits: 101111
3. Multiply the fractional remainder by 2. Read digits TOP TO BOTTOM.
1. 5/8 × 2 = 1 + 2/8 → 1
2. 2/8 × 2 = 0 + 4/8 → 0
3. 4/8 × 2 = 1 + 0/8 → 1
The remainder is zero: the fraction terminates exactly.
Binary shortcut: each base-16 digit represents exactly 4 bits. Keep the point in place.
Integer 2 → 0010
Integer F → 1111
Fraction A → 1010
Answer: 101111.101 (base 2) — exact
```

### 11. 17.4 (base 8) → 1111.1 (base 2)

```text
17.4 (base 8) → 1111.1 (base 2)
1. Expand the source place values (work on the magnitude).
1 (1) × 8^1 = 8
7 (7) × 8^0 = 7
4 (4) × 8^-1 = 4/8
Exact value: 31/2; decimal: 15.5
2. Divide the integer part by 2. Read remainders BOTTOM TO TOP.
15 ÷ 2 = 7, remainder 1 (1)
7 ÷ 2 = 3, remainder 1 (1)
3 ÷ 2 = 1, remainder 1 (1)
1 ÷ 2 = 0, remainder 1 (1)
Integer digits: 1111
3. Multiply the fractional remainder by 2. Read digits TOP TO BOTTOM.
1. 1/2 × 2 = 1 + 0/2 → 1
The remainder is zero: the fraction terminates exactly.
Binary shortcut: each base-8 digit represents exactly 3 bits. Keep the point in place.
Integer 1 → 001
Integer 7 → 111
Fraction 4 → 100
Answer: 1111.1 (base 2) — exact
```

### 12. 10.625 (base 10) → 1010.101 (base 2)

```text
10.625 (base 10) → 1010.101 (base 2)
1. Expand the source place values (work on the magnitude).
1 (1) × 10^1 = 10
0 (0) × 10^0 = 0
6 (6) × 10^-1 = 6/10
2 (2) × 10^-2 = 2/100
5 (5) × 10^-3 = 5/1000
Exact value: 85/8; decimal: 10.625
2. Divide the integer part by 2. Read remainders BOTTOM TO TOP.
10 ÷ 2 = 5, remainder 0 (0)
5 ÷ 2 = 2, remainder 1 (1)
2 ÷ 2 = 1, remainder 0 (0)
1 ÷ 2 = 0, remainder 1 (1)
Integer digits: 1010
3. Multiply the fractional remainder by 2. Read digits TOP TO BOTTOM.
1. 5/8 × 2 = 1 + 2/8 → 1
2. 2/8 × 2 = 0 + 4/8 → 0
3. 4/8 × 2 = 1 + 0/8 → 1
The remainder is zero: the fraction terminates exactly.
Answer: 1010.101 (base 2) — exact
```

### 13. 0.125 (base 10) → 0.1 (base 8)

```text
0.125 (base 10) → 0.1 (base 8)
1. Expand the source place values (work on the magnitude).
0 (0) × 10^0 = 0
1 (1) × 10^-1 = 1/10
2 (2) × 10^-2 = 2/100
5 (5) × 10^-3 = 5/1000
Exact value: 1/8; decimal: 0.125
2. Divide the integer part by 8. Read remainders BOTTOM TO TOP.
0 ÷ 8 = 0, remainder 0 (0)
Integer digits: 0
3. Multiply the fractional remainder by 8. Read digits TOP TO BOTTOM.
1. 1/8 × 8 = 1 + 0/8 → 1
The remainder is zero: the fraction terminates exactly.
Answer: 0.1 (base 8) — exact
```

### 14. .5 (base 10) → 0.8 (base 16)

```text
.5 (base 10) → 0.8 (base 16)
1. Expand the source place values (work on the magnitude).
0 (0) × 10^0 = 0
5 (5) × 10^-1 = 5/10
Exact value: 1/2; decimal: 0.5
2. Divide the integer part by 16. Read remainders BOTTOM TO TOP.
0 ÷ 16 = 0, remainder 0 (0)
Integer digits: 0
3. Multiply the fractional remainder by 16. Read digits TOP TO BOTTOM.
1. 1/2 × 16 = 8 + 0/2 → 8
The remainder is zero: the fraction terminates exactly.
Answer: 0.8 (base 16) — exact
```

### 15. 0.1 (base 10) → 0.0(0011) (base 2)

```text
0.1 (base 10) → 0.0(0011) (base 2)
1. Expand the source place values (work on the magnitude).
0 (0) × 10^0 = 0
1 (1) × 10^-1 = 1/10
Exact value: 1/10; decimal: 0.1
2. Divide the integer part by 2. Read remainders BOTTOM TO TOP.
0 ÷ 2 = 0, remainder 0 (0)
Integer digits: 0
3. Multiply the fractional remainder by 2. Read digits TOP TO BOTTOM.
1. 1/10 × 2 = 0 + 2/10 → 0
2. 2/10 × 2 = 0 + 4/10 → 0
3. 4/10 × 2 = 0 + 8/10 → 0
4. 8/10 × 2 = 1 + 6/10 → 1
5. 6/10 × 2 = 1 + 2/10 → 1
The remainder repeats: the cycle starts at fractional step 2. Put those digits in parentheses.
Answer: 0.0(0011) (base 2) — exact
```

### 16. 0.(3) (base 10) → 0.(01) (base 2)

```text
0.(3) (base 10) → 0.(01) (base 2)
1. Expand the source place values (work on the magnitude).
0 (0) × 10^0 = 0
Repeating tail: 3 (base 10) / [10^0 × (10^1 − 1)] = 1/3
Exact value: 1/3; decimal: 0.(3)
2. Divide the integer part by 2. Read remainders BOTTOM TO TOP.
0 ÷ 2 = 0, remainder 0 (0)
Integer digits: 0
3. Multiply the fractional remainder by 2. Read digits TOP TO BOTTOM.
1. 1/3 × 2 = 0 + 2/3 → 0
2. 2/3 × 2 = 1 + 1/3 → 1
The remainder repeats: the cycle starts at fractional step 1. Put those digits in parentheses.
Answer: 0.(01) (base 2) — exact
```

### 17. 0.1(6) (base 10) → 0.2(A) (base 16)

```text
0.1(6) (base 10) → 0.2(A) (base 16)
1. Expand the source place values (work on the magnitude).
0 (0) × 10^0 = 0
1 (1) × 10^-1 = 1/10
Repeating tail: 6 (base 10) / [10^1 × (10^1 − 1)] = 1/15
Exact value: 1/6; decimal: 0.1(6)
2. Divide the integer part by 16. Read remainders BOTTOM TO TOP.
0 ÷ 16 = 0, remainder 0 (0)
Integer digits: 0
3. Multiply the fractional remainder by 16. Read digits TOP TO BOTTOM.
1. 1/6 × 16 = 2 + 4/6 → 2
2. 4/6 × 16 = 10 + 4/6 → A
The remainder repeats: the cycle starts at fractional step 2. Put those digits in parentheses.
Answer: 0.2(A) (base 16) — exact
```

### 18. -42 (base 10) → -101010 (base 2)

```text
-42 (base 10) → -101010 (base 2)
1. Expand the source place values (work on the magnitude).
4 (4) × 10^1 = 40
2 (2) × 10^0 = 2
Exact value: -42; decimal: -42
2. Divide the integer part by 2. Read remainders BOTTOM TO TOP.
42 ÷ 2 = 21, remainder 0 (0)
21 ÷ 2 = 10, remainder 1 (1)
10 ÷ 2 = 5, remainder 0 (0)
5 ÷ 2 = 2, remainder 1 (1)
2 ÷ 2 = 1, remainder 0 (0)
1 ÷ 2 = 0, remainder 1 (1)
Integer digits: 101010
Restore the minus sign. This is signed-magnitude notation, NOT two's complement.
Answer: -101010 (base 2) — exact
```

### 19. -10.625 (base 10) → -A.A (base 16)

```text
-10.625 (base 10) → -A.A (base 16)
1. Expand the source place values (work on the magnitude).
1 (1) × 10^1 = 10
0 (0) × 10^0 = 0
6 (6) × 10^-1 = 6/10
2 (2) × 10^-2 = 2/100
5 (5) × 10^-3 = 5/1000
Exact value: -85/8; decimal: -10.625
2. Divide the integer part by 16. Read remainders BOTTOM TO TOP.
10 ÷ 16 = 0, remainder 10 (A)
Integer digits: A
3. Multiply the fractional remainder by 16. Read digits TOP TO BOTTOM.
1. 5/8 × 16 = 10 + 0/8 → A
The remainder is zero: the fraction terminates exactly.
Restore the minus sign. This is signed-magnitude notation, NOT two's complement.
Answer: -A.A (base 16) — exact
```

### 20. 0xFF (base 16) → 377 (base 8)

```text
0xFF (base 16) → 377 (base 8)
1. Expand the source place values (work on the magnitude).
F (15) × 16^1 = 240
F (15) × 16^0 = 15
Exact value: 255; decimal: 255
2. Divide the integer part by 8. Read remainders BOTTOM TO TOP.
255 ÷ 8 = 31, remainder 7 (7)
31 ÷ 8 = 3, remainder 7 (7)
3 ÷ 8 = 0, remainder 3 (3)
Integer digits: 377
Answer: 377 (base 8) — exact
```

### 21. 00010 (base 2) → 2 (base 10)

```text
00010 (base 2) → 2 (base 10)
1. Expand the source place values (work on the magnitude).
0 (0) × 2^4 = 0
0 (0) × 2^3 = 0
0 (0) × 2^2 = 0
1 (1) × 2^1 = 2
0 (0) × 2^0 = 0
Exact value: 2; decimal: 2
2. Divide the integer part by 10. Read remainders BOTTOM TO TOP.
2 ÷ 10 = 0, remainder 2 (2)
Integer digits: 2
Answer: 2 (base 10) — exact
```

### 22. Z (base 36) → 35 (base 10)

```text
Z (base 36) → 35 (base 10)
1. Expand the source place values (work on the magnitude).
Z (35) × 36^0 = 35
Exact value: 35; decimal: 35
2. Divide the integer part by 10. Read remainders BOTTOM TO TOP.
35 ÷ 10 = 3, remainder 5 (5)
3 ÷ 10 = 0, remainder 3 (3)
Integer digits: 35
Answer: 35 (base 10) — exact
```

### 23. 35 (base 10) → Z (base 36)

```text
35 (base 10) → Z (base 36)
1. Expand the source place values (work on the magnitude).
3 (3) × 10^1 = 30
5 (5) × 10^0 = 5
Exact value: 35; decimal: 35
2. Divide the integer part by 36. Read remainders BOTTOM TO TOP.
35 ÷ 36 = 0, remainder 35 (Z)
Integer digits: Z
Answer: Z (base 36) — exact
```

### 24. 1021 (base 3) → 34 (base 10)

```text
1021 (base 3) → 34 (base 10)
1. Expand the source place values (work on the magnitude).
1 (1) × 3^3 = 27
0 (0) × 3^2 = 0
2 (2) × 3^1 = 6
1 (1) × 3^0 = 1
Exact value: 34; decimal: 34
2. Divide the integer part by 10. Read remainders BOTTOM TO TOP.
34 ÷ 10 = 3, remainder 4 (4)
3 ÷ 10 = 0, remainder 3 (3)
Integer digits: 34
Answer: 34 (base 10) — exact
```

### 25. A.6 (base 12) → 10.5 (base 10)

```text
A.6 (base 12) → 10.5 (base 10)
1. Expand the source place values (work on the magnitude).
A (10) × 12^0 = 10
6 (6) × 12^-1 = 6/12
Exact value: 21/2; decimal: 10.5
2. Divide the integer part by 10. Read remainders BOTTOM TO TOP.
10 ÷ 10 = 1, remainder 0 (0)
1 ÷ 10 = 0, remainder 1 (1)
Integer digits: 10
3. Multiply the fractional remainder by 10. Read digits TOP TO BOTTOM.
1. 1/2 × 10 = 5 + 0/2 → 5
The remainder is zero: the fraction terminates exactly.
Answer: 10.5 (base 10) — exact
```

### 26. 10.1 (base 7) → 7.(142857) (base 10)

```text
10.1 (base 7) → 7.(142857) (base 10)
1. Expand the source place values (work on the magnitude).
1 (1) × 7^1 = 7
0 (0) × 7^0 = 0
1 (1) × 7^-1 = 1/7
Exact value: 50/7; decimal: 7.(142857)
2. Divide the integer part by 10. Read remainders BOTTOM TO TOP.
7 ÷ 10 = 0, remainder 7 (7)
Integer digits: 7
3. Multiply the fractional remainder by 10. Read digits TOP TO BOTTOM.
1. 1/7 × 10 = 1 + 3/7 → 1
2. 3/7 × 10 = 4 + 2/7 → 4
3. 2/7 × 10 = 2 + 6/7 → 2
4. 6/7 × 10 = 8 + 4/7 → 8
5. 4/7 × 10 = 5 + 5/7 → 5
6. 5/7 × 10 = 7 + 1/7 → 7
The remainder repeats: the cycle starts at fractional step 1. Put those digits in parentheses.
Answer: 7.(142857) (base 10) — exact
```

### 27. 100000000000000000000 (base 10) → 56BC75E2D63100000 (base 16)

```text
100000000000000000000 (base 10) → 56BC75E2D63100000 (base 16)
1. Expand the source place values (work on the magnitude).
1 (1) × 10^20 = 100000000000000000000
0 (0) × 10^19 = 0
0 (0) × 10^18 = 0
0 (0) × 10^17 = 0
0 (0) × 10^16 = 0
0 (0) × 10^15 = 0
0 (0) × 10^14 = 0
0 (0) × 10^13 = 0
0 (0) × 10^12 = 0
0 (0) × 10^11 = 0
0 (0) × 10^10 = 0
0 (0) × 10^9 = 0
0 (0) × 10^8 = 0
0 (0) × 10^7 = 0
0 (0) × 10^6 = 0
0 (0) × 10^5 = 0
0 (0) × 10^4 = 0
0 (0) × 10^3 = 0
0 (0) × 10^2 = 0
0 (0) × 10^1 = 0
0 (0) × 10^0 = 0
Exact value: 100000000000000000000; decimal: 100000000000000000000
2. Divide the integer part by 16. Read remainders BOTTOM TO TOP.
100000000000000000000 ÷ 16 = 6250000000000000000, remainder 0 (0)
6250000000000000000 ÷ 16 = 390625000000000000, remainder 0 (0)
390625000000000000 ÷ 16 = 24414062500000000, remainder 0 (0)
24414062500000000 ÷ 16 = 1525878906250000, remainder 0 (0)
1525878906250000 ÷ 16 = 95367431640625, remainder 0 (0)
95367431640625 ÷ 16 = 5960464477539, remainder 1 (1)
5960464477539 ÷ 16 = 372529029846, remainder 3 (3)
372529029846 ÷ 16 = 23283064365, remainder 6 (6)
23283064365 ÷ 16 = 1455191522, remainder 13 (D)
1455191522 ÷ 16 = 90949470, remainder 2 (2)
90949470 ÷ 16 = 5684341, remainder 14 (E)
5684341 ÷ 16 = 355271, remainder 5 (5)
355271 ÷ 16 = 22204, remainder 7 (7)
22204 ÷ 16 = 1387, remainder 12 (C)
1387 ÷ 16 = 86, remainder 11 (B)
86 ÷ 16 = 5, remainder 6 (6)
5 ÷ 16 = 0, remainder 5 (5)
Integer digits: 56BC75E2D63100000
Answer: 56BC75E2D63100000 (base 16) — exact
```

## Honest precision limit

Radix stops at 24 fractional places if zero or a repeating remainder has not yet been found. That is **truncation**, not rounding. This known case must be labeled approximate:

```text
0.(0123456789012345678901234567890123456789) (base 10) → 0.000022222222222222221211… (base 3)
1. Expand the source place values (work on the magnitude).
0 (0) × 10^0 = 0
Repeating tail: 0123456789012345678901234567890123456789 (base 10) / [10^0 × (10^40 − 1)] = 13717421/1111111111
Exact value: 13717421/1111111111; decimal: 0.(0123456789)
2. Divide the integer part by 3. Read remainders BOTTOM TO TOP.
0 ÷ 3 = 0, remainder 0 (0)
Integer digits: 0
3. Multiply the fractional remainder by 3. Read digits TOP TO BOTTOM.
1. 13717421/1111111111 × 3 = 0 + 41152263/1111111111 → 0
2. 41152263/1111111111 × 3 = 0 + 123456789/1111111111 → 0
3. 123456789/1111111111 × 3 = 0 + 370370367/1111111111 → 0
4. 370370367/1111111111 × 3 = 0 + 1111111101/1111111111 → 0
5. 1111111101/1111111111 × 3 = 2 + 1111111081/1111111111 → 2
6. 1111111081/1111111111 × 3 = 2 + 1111111021/1111111111 → 2
7. 1111111021/1111111111 × 3 = 2 + 1111110841/1111111111 → 2
8. 1111110841/1111111111 × 3 = 2 + 1111110301/1111111111 → 2
9. 1111110301/1111111111 × 3 = 2 + 1111108681/1111111111 → 2
10. 1111108681/1111111111 × 3 = 2 + 1111103821/1111111111 → 2
11. 1111103821/1111111111 × 3 = 2 + 1111089241/1111111111 → 2
12. 1111089241/1111111111 × 3 = 2 + 1111045501/1111111111 → 2
13. 1111045501/1111111111 × 3 = 2 + 1110914281/1111111111 → 2
14. 1110914281/1111111111 × 3 = 2 + 1110520621/1111111111 → 2
15. 1110520621/1111111111 × 3 = 2 + 1109339641/1111111111 → 2
16. 1109339641/1111111111 × 3 = 2 + 1105796701/1111111111 → 2
17. 1105796701/1111111111 × 3 = 2 + 1095167881/1111111111 → 2
18. 1095167881/1111111111 × 3 = 2 + 1063281421/1111111111 → 2
19. 1063281421/1111111111 × 3 = 2 + 967622041/1111111111 → 2
20. 967622041/1111111111 × 3 = 2 + 680643901/1111111111 → 2
21. 680643901/1111111111 × 3 = 1 + 930820592/1111111111 → 1
22. 930820592/1111111111 × 3 = 2 + 570239554/1111111111 → 2
23. 570239554/1111111111 × 3 = 1 + 599607551/1111111111 → 1
24. 599607551/1111111111 × 3 = 1 + 687711542/1111111111 → 1
Stopped at 24 fractional places. The ellipsis means truncated/approximate, not exact.
Answer: 0.000022222222222222221211… (base 3) — approximate
```

## Inputs that should fail, not produce an answer

- `2` in base 2, `G` in base 16, two points such as `1.2.3`, and empty source input.
- K-map indices outside 0–3 / 0–7 / 0–15, negative indices, ranges such as `1-3`, or a required cell also listed as X.
- A group containing the opposite output, a diagonal-only pair, a group of three/six, or only Xs.

See `design/VERIFICATION.md` for automated coverage and the distinction between browser QA and Android runtime testing.

## Boolean expression lab

More → Expression Lab supports 2–4 variables. NOT/prime precedes AND/NAND, then XOR/XNOR, then OR/NOR; binary operations at each level associate left-to-right. Parenthesize mixed NAND/NOR. These examples are generated from the same verified engine:

| Expression | Variables | Minimum SOP | Minimum POS |
|---|---|---|---|
| `A'B+AB'` | 2 | `A'B + AB'` | `(A + B)(A' + B')` |
| `!(AB)` | 2 | `A' + B'` | `(A' + B')` |
| `A(B+C)` | 3 | `AB + AC` | `(A)(B + C)` |
| `A XOR D` | 4 | `A'D + AD'` | `(A + D)(A' + D')` |
| `1+AB` | 2 | `1` | `1` |

**Equivalence:** `AB` versus `A+B` differs at input rows **01** and **10**. `!(AB)` versus `A′+B′` is equivalent in every row. A SOP/POS K-map handoff keeps the underlying truth table unchanged.

## Fixed-width representations

More → Representations. Overflow is rejected, never silently wrapped. A short word is **zero-padded**, not sign-extended. These generated answers use exact BigInt arithmetic:

| Input | Mode | Width | Interpretation | Binary word | Unsigned | Signed | Gray | BCD |
|---|---|---|---|---|---|---|---|
| `-13` | decimal | 8 | Two’s complement | `11110011` | 243 | -13 | `10001010` | Not defined for negative selected value |
| `12` | decimal | 8 | Unsigned | `00001100` | 12 | 12 | `00001010` | `0001 0010` |
| `1111` | bits | 4 | Two’s complement | `1111` | 15 | -1 | `1000` | Not defined for negative selected value |
| `1111` | bits | 8 | Two’s complement | `00001111` | 15 | 15 | `00001000` | `0001 0101` |
| `0110` | gray | 4 | Unsigned | `0100` | 4 | 4 | `0110` | `0100` |
| `255` | decimal | 8 | Unsigned | `11111111` | 255 | -1 | `10000000` | `0010 0101 0101` |

Two’s-complement width w has range −2^(w−1) through 2^(w−1)−1; unsigned range is 0 through 2^w−1. BCD gives each **decimal digit** a separate 4-bit nibble. Gray is a code, not positional binary.

## 36 practice cards — answer key

Try More → Practice before reading these answers. Hints/checks/reveals are available. Only a correct check creates local mastery; a reveal does not.

### 1. Gates · Every input matters

AND: A=1, B=0. What is Y?

**Answer:** 0

**Why:** One input is 0, so A·B is 0.

### 2. Gates · At least one

OR: A=0, B=0. What is Y?

**Answer:** 0

**Why:** Neither input is 1, so Y=0.

### 3. Gates · Flip the value

NOT: A=1. What is Y?

**Answer:** 0

**Why:** NOT reverses 1 to 0.

### 4. Gates · AND, then invert

NAND: A=1, B=1. What is Y?

**Answer:** 0

**Why:** AND produces 1; NAND inverts it to 0.

### 5. Gates · OR, then invert

NOR: A=0, B=0. What is Y?

**Answer:** 1

**Why:** OR produces 0; NOR inverts it to 1.

### 6. Gates · Odd means one

Three-input XOR: A=B=C=1. What is Y?

**Answer:** 1

**Why:** Three 1s is odd parity, so Y=1. Multi-input XOR is not an exactly-one detector.

### 7. Gates · Even means one

Four-input XNOR: all four inputs are 1. What is Y?

**Answer:** 1

**Why:** Four 1s is even parity, so XNOR produces 1.

### 8. Gates · Pass it through

BUFFER: A=0. What is Y?

**Answer:** 0

**Why:** The output follows its one input: Y=0.

### 9. Circuits · Two ones make two

Half adder: A=1, B=1. Give Sum, Carry.

**Answer:** 0, 1

**Why:** 1+1=2=10₂. Sum is the low bit 0; Carry is the high bit 1.

### 10. Circuits · Remember carry-in

Full adder: A=B=Cin=1. Give Sum, Cout.

**Answer:** 1, 1

**Why:** 1+1+1=3=11₂: Sum=1 and Cout=1.

### 11. Circuits · Borrow one

Half subtractor: A=0, B=1. Give Difference, Borrow.

**Answer:** 1, 1

**Why:** After borrowing 2, 2−1=1; Difference=1 and Borrow=1.

### 12. Circuits · Borrow-in counts too

Full subtractor: A=0, B=1, Bin=1. Give Difference, Bout.

**Answer:** 0, 1

**Why:** 0−1−1=−2. At one bit, Difference=0 and Borrow=1.

### 13. Circuits · Read the selector

4:1 MUX: D0=0, D1=0, D2=1, D3=0; S1S0=10. Y?

**Answer:** 1

**Why:** The MUX routes D2, which is 1. It does not OR all data inputs.

### 14. Circuits · The highest input wins

Priority encoder: D3=D2=D1=D0=1. Give Y1Y0 and Valid.

**Answer:** 11 / 1

**Why:** D3 wins, so its index is 11₂; at least one input is high, so Valid=1.

### 15. Circuits · Keep the fifth bit

4-bit adder: 1111 + 0001, Cin=0. Give S3S2S1S0 and Cout.

**Answer:** 0000 / 1

**Why:** The result is 10000₂. The low four sum bits are 0000 and Cout=1.

### 16. Circuits · Start at the MSB

2-bit comparator: A=10, B=01. Which relation is true?

**Answer:** A > B

**Why:** A=2 and B=1, so only A>B is high.

### 17. Memory · High is not an edge

D flip-flop: Q=1, CLK stays high, D changes to 0. Q next?

**Answer:** 1

**Why:** No new rising edge occurs. Q holds 1 even though D is now 0.

### 18. Memory · A latch follows a level

D latch: Q=0, EN=1, D becomes 1. Q next?

**Answer:** 1

**Why:** EN=1 lets Q follow D immediately. No clock edge is required.

### 19. Memory · Toggle, do not set

JK flip-flop: Q=1, J=K=1 at a rising edge. Q next?

**Answer:** 0

**Why:** JK=11 toggles Q from 1 to 0. It is valid in this ideal edge model.

### 20. Memory · No second capture

T flip-flop: Q=1, T=1, CLK stays high. Q next?

**Answer:** 1

**Why:** Q remains 1. T toggles only at each new rising edge.

### 21. Memory · Forbidden does not mean complementary

NOR SR latch: S=R=1. What happens?

**Answer:** Q=0, Q′=0; memory X

**Why:** Both physical outputs are 0; stored memory is indeterminate. Simultaneous release to hold keeps X until valid recovery.

### 22. Memory · Active-low changes the rule

NAND SR latch: nS=nR=0. What happens?

**Answer:** Q=1, Q′=1; memory X

**Why:** Both physical outputs are 1 and stored memory is indeterminate. The active-low hold combination is 11.

### 23. Memory · Wrap at the width

3-bit counter: Q=111, EN=1, RESET=0. After one rising edge?

**Answer:** 000

**Why:** The next count is (7+1) mod 8 = 0, so Q=000.

### 24. Memory · Insert at Q0

Shift register: Q3Q2Q1Q0=0110, D=1, EN=1, RESET=0. Next edge?

**Answer:** 1101

**Why:** Q3←Q2, Q2←Q1, Q1←Q0, Q0←D produces 1101.

### 25. Memory · Clocked forbidden state

SR flip-flop: Q=1, S=R=1 at a rising edge. Q next?

**Answer:** X

**Why:** This ideal SR flip-flop stores unknown X after capturing 11. A valid set/reset edge recovers a known state.

### 26. Memory · Circulate the one

Ring counter: Q=1000, EN=1, RESET=0. Next rising edge?

**Answer:** 0001

**Why:** Rotate 1000 left: the high bit re-enters at Q0, giving 0001.

### 27. Memory · Invert the feedback

Johnson counter: Q=1111, EN=1, RESET=0. Next rising edge?

**Answer:** 1110

**Why:** Shift left and insert NOT old Q3 = 0: the next state is 1110.

### 28. Memory · Reset wins over enable

3-bit counter: Q=101, EN=0, RESET=1. After a rising edge?

**Answer:** 000

**Why:** At the edge, RESET=1 clears Q to 000 even though EN=0. Before the edge it would still hold 101.

### 29. Representations · Not signed magnitude

Encode −13 in 8-bit two’s complement.

**Answer:** 11110011

**Why:** +13=00001101; invert→11110010; add 1→11110011. Signed magnitude 10001101 is a different representation.

### 30. Representations · The upper endpoint

Largest unsigned 8-bit integer?

**Answer:** 255

**Why:** The range is 0 through 2^8−1 = 255. Signed two’s complement instead ends at 127.

### 31. Representations · Neighboring codes

Gray code for the 4-bit binary word 0100?

**Answer:** 0110

**Why:** 0100 XOR 0010 = 0110. Gray code is not positional binary.

### 32. Representations · One nibble per digit

BCD encoding of decimal 12?

**Answer:** 0001 0010

**Why:** BCD is 0001 0010. Ordinary binary for decimal 12 is 1100; these are different encodings.

### 33. Expressions · Invert the whole product

Which expression equals NOT (A AND B)?

**Answer:** A′ OR B′

**Why:** NOT (A·B)=A′+B′. Inverting only the variables but keeping AND is incorrect.

### 34. Expressions · Multiplication first

In A + BC, which operation happens first?

**Answer:** B AND C

**Why:** A + BC means A OR (B AND C), not (A OR B) AND C.

### 35. Expressions · Different inputs

Which equals A XOR B?

**Answer:** A′B + AB′

**Why:** A′B + AB′ covers 01 and 10 only. AB + A′B′ is XNOR instead.

### 36. Expressions · One dominates OR

Simplify 1 OR (A AND B).

**Answer:** 1

**Why:** Every truth-table row is 1. Both SOP and POS reduce to constant 1.

## Keep a learning checkpoint

Logic → Save session preserves model, inputs, clock and nullable memory. Restore from More → Sessions; timing begins with one fresh sample. Use Undo/Redo for circuit/map edits; pulse and simultaneous latch release are atomic. A full library holds 24 sessions and refuses a 25th.

More → Preferences & Backup exports portable **plain, unencrypted** JSON. Validate an import, review the domain counts and explicitly confirm **replacement**, not merge. Export current data first. The active circuit stays unchanged; histories clear. Backups exclude traces, history and signing credentials.

The ? helper now has **11 illustrated lessons** and a replayable **12-step tour**. Browser sandbox previews disclose session-only storage and offer selectable backup/copy text if clipboard/downloads are blocked. See `VERIFICATION.md` for testing limits; no installed native UI or upgrade-runtime QA is claimed.
