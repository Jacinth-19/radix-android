#!/usr/bin/env node
'use strict';
const fs=require('node:fs'),path=require('node:path');
const {K,working}=require('../web/student-engines.js');
const S=require('../web/study-engine.js'),practice=require('../web/practice-catalog.js');
const conversions=[
 ['13',10,2,'Integer remainders are read bottom to top.'],
 ['45',10,2,'Do not treat 10 in another base as decimal ten.'],
 ['255',10,16,'Remainders 10–35 use A–Z.'],
 ['125',10,8,'Repeated division uses the target base.'],
 ['1024',10,2,'A power of two gives one 1 followed by zeroes.'],
 ['110101',2,10,'The rightmost integer position is power zero.'],
 ['2F',16,10,'F has value 15, not 16.'],
 ['157',8,10,'The source base determines positional weights.'],
 ['10101.101',2,16,'Pad the whole part left, the fraction right.'],
 ['2F.A',16,2,'Each hexadecimal digit corresponds to four bits.'],
 ['17.4',8,2,'Each octal digit corresponds to three bits.'],
 ['10.625',10,2,'Fractional digits stay in top-to-bottom order.'],
 ['0.125',10,8,'A finite decimal fraction may terminate in another base.'],
 ['.5',10,16,'A leading zero before the point is optional.'],
 ['0.1',10,2,'A finite decimal fraction may repeat in binary.'],
 ['0.(3)',10,2,'Parentheses denote a mathematically exact cycle.'],
 ['0.1(6)',10,16,'Handle the non-repeating prefix before the geometric tail.'],
 ['-42',10,2,"The minus sign is signed magnitude, not two's complement."],
 ['-10.625',10,16,'Convert the magnitude; restore the sign last.'],
 ['0xFF',16,8,'Matching 0x/0o/0b prefixes are supported.'],
 ['00010',2,10,'Leading zeroes do not change the value.'],
 ['Z',36,10,'The maximum supported digit Z has value 35.'],
 ['35',10,36,'Base 36 is a positional system, not a decimal suffix.'],
 ['1021',3,10,'The same method applies to less familiar bases.'],
 ['A.6',12,10,'Alphabet digits and fractional place powers work together.'],
 ['10.1',7,10,'Recurring decimal output is exact when the cycle is detected.'],
 ['100000000000000000000',10,16,'Large integers never pass through floating-point arithmetic.']
];
let out="# Radix 7.0 — learner workbook\n\nOpen **Convert → Show the working**, or **K-maps → 24 examples**. All exercises run offline. The **Logic** tab contains 8 gates, 23 combinational circuits (MUX/DEMUX, encoders, decoders, comparators, parity and BCD) and 11 sequential models; very wide tables are generated in pages. Rotate Android to landscape for the full circuit lab and widest models. Tap **?** for eleven illustrated cards and a twelve-step tour. The separate **DIGITAL-LOGIC-REFERENCE.html** supplies every logic model, diagram and bounded truth-table sample.\n\n## Conversion answers\n\nThese are exact unless marked approximate. Parentheses mean a repeating cycle. A minus sign is signed magnitude; Convert uses ordinary signed magnitude. The new **Representations** lab separately encodes fixed-width two’s complement.\n\n| # | Source | Target | Answer | Common pitfall |\n|---|---|---|---|---|\n";
const reports=conversions.map(([input,from,to,lesson],i)=>{const r=working(input,from,to);out+=`| ${i+1} | \`${input}\` base ${from} | Base ${to} | \`${r.result.display}\` | ${lesson} |\n`;return r;});
out+='\n## K-map answers\n\nIndices are ordinary binary minterm numbers with A as the MSB. Gray-code labels reorder the display; they do not renumber the truth table. Both forms below represent the same specified truth table. Different equally minimum answers can exist.\n\n| # | Challenge | Variables | Ones | Xs | Minimum SOP | Minimum POS |\n|---|---|---|---|---|---|---|\n';
K.exercises.forEach((e,i)=>{out+=`| ${i+1} | ${e.title} | ${e.variables} | ${e.ones.join(', ')||'None'} | ${e.dc.join(', ')||'None'} | \`${K.solve(e.variables,e.cells(),'SOP').expression}\` | \`${K.solve(e.variables,e.cells(),'POS').expression}\` |\n`;});
out+='\n### What to remember\n\n- Row/column order is **00, 01, 11, 10**, not 00, 01, 10, 11.\n- Choose rectangles of **1, 2, 4, 8, 16, 32 or 64** cells; never diagonal-only pairs or groups of 3/6.\n- Opposite edges touch. Four corners may be one wrapped group.\n- Overlap is allowed. Xs are optional; an all-X group covers no required output.\n- SOP groups **1s**: fixed 0 contributes a complemented literal; fixed 1 contributes an uncomplemented literal.\n- POS groups **0s**: fixed 0 is uncomplemented; fixed 1 is complemented. Variables that change disappear.\n- Cover every required output. Minimize terms/clauses first, then literals.\n- Switching form does **not** complement the underlying truth table.\n- `+` means OR, adjacency means AND, and an apostrophe means NOT.\n\n## Fully worked conversions\n\n';
reports.forEach((r,i)=>{out+=`### ${i+1}. ${r.input} (base ${r.from}) → ${r.result.display} (base ${r.to})\n\n\`\`\`text\n${r.text()}\n\`\`\`\n\n`;});
const approximate=working('0.('+'0123456789'.repeat(4)+')',10,3);
out+='## Honest precision limit\n\nRadix stops at 24 fractional places if zero or a repeating remainder has not yet been found. That is **truncation**, not rounding. This known case must be labeled approximate:\n\n```text\n'+approximate.text()+'\n```\n\n## Inputs that should fail, not produce an answer\n\n- `2` in base 2, `G` in base 16, two points such as `1.2.3`, and empty source input.\n- K-map indices outside 0–(2^n−1) (the six-variable maximum is 0–63), negative indices, ranges such as `1-3`, or a required cell also listed as X.\n- A group containing the opposite output, a diagonal-only pair, a group of three/six, or only Xs.\n\nSee `design/VERIFICATION.md` for automated coverage and the distinction between browser QA and Android runtime testing.\n';
out+='\n## Boolean expression lab\n\nMore → Expression Lab accepts any A–Z variable labels, with 2–6 input lines and no more distinct variables than selected inputs. Variables follow first-appearance order; unused input lines are filled from the remaining letters. Apostrophe/prime or `!` means NOT, `+` means OR, `.` or adjacency means AND; XOR, `^` or ⊕ and XNOR or ⊙ are accepted. The keypad buttons are optional shortcuts. Precedence is NOT, AND/NAND, XOR/XNOR, then OR/NOR; binary operations at each level associate left-to-right. Parenthesize mixed NAND/NOR. The truth table always has 2ⁿ rows (8 for 3 inputs; 16 for 4). The notation `F(A.B.C)=Σm(...)` or `ΠM(...)` is supported in K-maps. These examples are generated from the same verified engine:\n\n| Expression | Variables | Minimum SOP | Minimum POS |\n|---|---|---|---|\n';
for(const [source,n] of [["A'B+AB'",2],["!(AB)",2],["A(B+C)",3],["A XOR D",4],["A XOR E",5],["A XOR F",6],["1+AB",2]]){const r=S.Expression.report(source,n);out+=`| \`${source}\` | ${n} | \`${r.sop}\` | \`${r.pos}\` |\n`;}
out+='\n**Equivalence:** `AB` versus `A+B` differs at input rows **01** and **10**. `!(AB)` versus `A′+B′` is equivalent in every row. A SOP/POS K-map handoff keeps the underlying truth table unchanged.\n\n## Fixed-width representations\n\nMore → Representations. Overflow is rejected, never silently wrapped. A short word is **zero-padded**, not sign-extended. These generated answers use exact BigInt arithmetic:\n\n| Input | Mode | Width | Interpretation | Binary word | Unsigned | Signed | Gray | BCD |\n|---|---|---|---|---|---|---|---|\n';
for(const [input,mode,width,signed] of [['-13','decimal',8,true],['12','decimal',8,false],['1111','bits',4,true],['1111','bits',8,true],['0110','gray',4,false],['255','decimal',8,false]]){const r=S.Representation.report(input,width,signed,mode);out+=`| \`${input}\` | ${mode} | ${width} | ${signed?'Two’s complement':'Unsigned'} | \`${r.word}\` | ${r.unsigned} | ${r.signed} | \`${r.gray}\` | ${r.bcd===null?'Not defined for negative selected value':'\`'+r.bcd+'\`'} |\n`;}
out+='\nTwo’s-complement width w has range −2^(w−1) through 2^(w−1)−1; unsigned range is 0 through 2^w−1. BCD gives each **decimal digit** a separate 4-bit nibble. Gray is a code, not positional binary.\n\n## '+practice.length+' practice cards — answer key\n\nTry More → Practice before reading these answers. Hints/checks/reveals are available. Only a correct check creates local mastery; a reveal does not.\n\n';
practice.forEach((q,i)=>{out+=`### ${i+1}. ${q.category} · ${q.title}\n\n${q.prompt}\n\n**Answer:** ${q.choices[q.answer]}\n\n**Why:** ${q.explanation}\n\n`;});
out+='## Keep a learning checkpoint\n\nLogic → Save session preserves model, inputs, clock and nullable memory. Restore from More → Sessions; timing begins with one fresh sample. Use Undo/Redo for circuit/map edits; pulse and simultaneous latch release are atomic. A full library holds 24 sessions and refuses a 25th.\n\nMore → Preferences & Backup exports portable **plain, unencrypted** JSON. Validate an import, review the domain counts and explicitly confirm **replacement**, not merge. Export current data first. The active circuit stays unchanged; histories clear. Backups exclude traces, history and signing credentials.\n\nThe ? helper now has **11 illustrated lessons** and a replayable **12-step tour**. Browser sandbox previews disclose session-only storage and offer selectable backup/copy text if clipboard/downloads are blocked. See `VERIFICATION.md` for testing limits; no installed native UI or upgrade-runtime QA is claimed.\n';
const destination=path.join(__dirname,'../design/STUDENT-WORKBOOK.md');fs.writeFileSync(destination,out);console.log(`Built ${conversions.length} worked conversions and ${K.exercises.length} K-map answers, expression/representation examples and ${practice.length} practice solutions: ${destination}`);
