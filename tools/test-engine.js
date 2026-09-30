const assert = require('node:assert/strict');
const E = require('../web/engine.js');
let checks = 0;
const equal = (actual, expected) => { assert.deepEqual(actual,expected); checks++; };
const convert = (s,a,b) => { const p=E.parse(s,a); assert.equal(p.kind,'valid'); return E.format(p.value,b); };
equal(convert('255',10,2).raw,'11111111');
equal(convert('255',10,8).raw,'377');
equal(convert('ff',16,10).raw,'255');
equal(convert('-42',10,2).raw,'-101010');
equal(convert('z',36,10).raw,'35');
equal(convert('0xFF',16,10).raw,'255');
equal(convert('0B1111_1111',2,10).raw,'255');
equal(convert('10.625',10,2).raw,'1010.101');
equal(convert('.5',10,16).raw,'0.8');
equal(convert('-.5',10,16).raw,'-0.8');
equal(convert('0.1',10,2).raw,'0.0(0011)');
equal(convert('0.(9)',10,10).raw,'1');
equal(convert('0.1(6)',10,10).raw,'0.1(6)');
equal(E.format(E.rational(1n,3n),2).raw,'0.(01)');
equal(E.format(E.rational(1n,3n),10,1).raw,'0.(3)');
equal(E.format(E.rational(1n,97n),10).exact,false);
equal(E.format(E.rational(1n,2n**24n),2).exact,true);
equal(E.parse('1'.repeat(256),2).kind,'valid');
equal(E.parse('1'.repeat(257),2).kind,'invalid');
equal(E.parse('   ',10).kind,'empty');
for (const [s,b] of [['2',2],['G',16],['10..5',10],['1,5',10],['1(3)',10],['1.()',10],['0xFF',2],['--1',10],['NaN',10],['∞',10]]) equal(E.parse(s,b).kind,'invalid');
for (let i=1;i<=80;i++) {
 const n = BigInt(i)*((1n << BigInt(180+i%60)) + 1234567890123456789n)*(i%2 ? -1n : 1n);
 const v = E.rational(n);
 for (let base=2;base<=36;base++) equal(E.parse(E.format(v,base).raw,base).value,v);
}
for (const s of ['0.125','-42.625','1.1(6)','0.(3)']) {
 const v=E.parse(s,10).value;
 for (let base=2;base<=36;base++) { const out=E.format(v,base); if(out.exact) equal(E.parse(out.raw,base).value,v); }
}
equal(E.grouped(convert('65535',10,2),2),'1111 1111 1111 1111');
equal(E.grouped(convert('-131071',10,16),16),'-1 FFFF');
console.log(`PASS: ${checks} exact conversion, validation, precision and round-trip checks.`);
