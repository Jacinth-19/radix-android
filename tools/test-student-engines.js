#!/usr/bin/env node
'use strict';
// Independent expression parsing and a toroidal-rectangle/set-cover optimum oracle.
// These tests deliberately do not reuse the solver's cube generator or evaluator.
const assert=require('node:assert/strict');
const fs=require('node:fs');
const path=require('node:path');
const E=require('../web/engine.js');
const {K,working}=require('../web/student-engines.js');
let assertions=0,tables=0,solutions=0,oracleSolutions=0,workingReports=0;
const started=Date.now();
function ok(value,message) {assertions++;assert.ok(value,message);}
function eq(a,b,message) {assertions++;assert.deepEqual(a,b,message);}
function bad(fn,message) {assertions++;assert.throws(fn,undefined,message);}
const pop=n=>{let c=0;for(;n;n&=n-1)c++;return c;};
function parse(expression,n,form) {
 if(expression==='0'||expression==='1')return ()=>expression==='1';
 const clauses=form==='SOP'?expression.split('+'):Array.from(expression.matchAll(/\(([^()]*)\)/g),m=>m[1]);
 ok(clauses.length>0,'Expression must contain a clause');
 if(form==='POS')eq(clauses.map(c=>'('+c+')').join('').replace(/\s/g,''),expression.replace(/\s/g,''),'POS punctuation');
 const rules=clauses.map(clause=>{
  const literals=Array.from(clause.matchAll(/[A-F]'?/g),m=>m[0]);
  eq(literals.join(''),clause.replace(/[ +]/g,''),'No unparsed syntax');
  let mask=0,bits=0;
  for(const literal of literals) {
   const variable='ABCDEF'.indexOf(literal[0]);ok(variable>=0&&variable<n,'Variable in range');
   const bit=1<<(n-variable-1);ok(!(mask&bit),'No repeated variable');mask|=bit;
   const complement=literal.endsWith("'");if(form==='SOP'?!complement:complement)bits|=bit;
  }
  return {mask,bits};
 });
 return index=>{const covered=rules.some(r=>(index&r.mask)===r.bits);return form==='SOP'?covered:!covered;};
}
const rectangles={};
for(let n=2;n<=4;n++) {
 const rows=1<<Math.floor(n/2),cols=1<<(n-Math.floor(n/2)),unique=new Map();
 for(let height=1;height<=rows;height*=2)for(let width=1;width<=cols;width*=2)for(let r=0;r<rows;r++)for(let c=0;c<cols;c++) {
  let members=0;
  for(let dr=0;dr<height;dr++)for(let dc=0;dc<width;dc++) {
   const rr=(r+dr)%rows,cc=(c+dc)%cols;
   const i=((rr^(rr>>1))<<(n-Math.floor(n/2)))|(cc^(cc>>1));members|=1<<i;
  }
  unique.set(members,n-Math.log2(height)-Math.log2(width));
 }
 rectangles[n]=[...unique].map(([members,literals])=>({members,literals}));
}
function optimum(n,cells,form) {
 const target=form==='SOP'?1:0;let required=0,forbidden=0;
 cells.forEach((v,i)=>{if(v===target)required|=1<<i;else if(v!==2)forbidden|=1<<i;});
 if(!required)return [0,0];
 if(!forbidden)return [1,0]; // The whole torus is a valid zero-literal group.
 const valid=new Map();
 for(const r of rectangles[n])if(!(r.members&forbidden)) {
  const cover=r.members&required;if(cover)valid.set(cover,Math.min(valid.get(cover)??Infinity,r.literals));
 }
 const unit=n*cells.length+1,dp=new Map([[0,0]]);
 for(const [cover,literals]of valid)for(const [old,cost]of [...dp]) {
  const union=old|cover,next=cost+unit+literals;
  if(next<(dp.get(union)??Infinity))dp.set(union,next);
 }
 const score=dp.get(required);ok(Number.isFinite(score),'Independent cover exists');
 return [Math.floor(score/unit),score%unit];
}
function verify(n,cells,minimum=false) {
 tables++;
 for(const form of ['SOP','POS']) {
  solutions++;const q=K.solve(n,cells,form),visible=parse(q.expression,n,form),target=form==='SOP'?1:0;
  for(let i=0;i<cells.length;i++) {eq(q.evaluate(i),visible(i),'Visible expression agrees with solver');if(cells[i]!==2)eq(visible(i),!!cells[i],`Truth n=${n}, form=${form}, index=${i}`);}
  for(const g of q.groups) {
   const indices=Array.from({length:cells.length},(_,i)=>i).filter(i=>K.hasCell(g.cube.members,i));
   eq(indices.length,1<<(n-pop(g.cube.mask)),'Power-of-two rectangle');
   ok(indices.some(i=>cells[i]===target),'No all-X groups');
   ok(indices.every(i=>cells[i]===target||cells[i]===2),'No opposite outputs');
   ok(K.checkGroup(n,cells,indices,form).valid,'Selected group passes independent rectangle shape expectations');
  }
  if(minimum){oracleSolutions++;eq([q.groups.length,q.literalCount],optimum(n,cells,form),'Independent optimum: terms then literals');}
 }
}
for(const [n,count]of [[2,81],[3,6561]]) {
 for(let code=0;code<count;code++) {let rest=code;verify(n,Array.from({length:1<<n},()=>{const v=rest%3;rest=Math.floor(rest/3);return v;}),true);}
 console.log(`PASS: all ${count} ${n}-variable 0/1/X truth tables, SOP/POS and independent minimum oracle.`);
}
for(let code=0;code<65536;code++)verify(4,Array.from({length:16},(_,i)=>(code>>i)&1));
console.log('PASS: all 65,536 four-variable Boolean tables, SOP/POS, independently parsed visible expressions.');
let seed=20260929;
function random(){seed^=seed<<13;seed^=seed>>>17;seed^=seed<<5;return (seed>>>0)%3;}
for(let i=0;i<2000;i++)verify(4,Array.from({length:16},random),i<200);
console.log('PASS: 2,000 seeded four-variable 0/1/X tables; first 200 independently optimized.');
let sixSeed=20261011;function sixRandom(){sixSeed^=sixSeed<<13;sixSeed^=sixSeed>>>17;sixSeed^=sixSeed<<5;return(sixSeed>>>0)%3;}for(let i=0;i<128;i++)verify(6,Array.from({length:64},sixRandom));
console.log('PASS: 128 seeded six-variable 0/1/X maps, both forms and independently parsed expressions.');
const sigma=K.fromNotation('F(A.B.C)=Σm(1,3,5)'),pi=K.fromNotation('F(A,B,C)=ΠM(0,2)');eq([sigma.variables,sigma.names,sigma.form,sigma.cells],[3,'ABC','SOP',[0,1,0,1,0,1,0,0]]);eq([pi.variables,pi.names,pi.form,pi.cells],[3,'ABC','POS',[0,1,0,1,1,1,1,1]]);const sixNotation=K.fromNotation('F(A,B,C,D,E,F)=Σm(63)');eq([sixNotation.variables,sixNotation.cells[63]], [6,1]);bad(()=>K.fromNotation('F(A,A)=Σm(0)'));bad(()=>K.fromNotation('F(A,B)=Σm(4)'));bad(()=>K.fromNotation('F(A,B)=Σm(1-3)'));eq(K.fromLists(6,'63','', 'SOP').cells[63],1);eq(K.gray(3),[0,1,3,2,6,7,5,4]);eq(new Set(Array.from({length:64},(_,i)=>K.cellAt(6,Math.floor(i/8),i%8))).size,64);eq(K.solve(6,Array.from({length:64},(_,i)=>Number(!!(i&32))),'SOP').expression,'A');eq(K.solve(6,Array.from({length:64},(_,i)=>Number(!!(i&32))),'POS').expression,'(A)');const allSix=Array(64).fill(1),half=Array.from({length:32},(_,i)=>i);eq(K.checkGroup(6,allSix,half,'SOP').valid,true);eq(K.indices(K.solve(6,allSix,'SOP').groups[0].cube,6).length,64);
eq(K.exercises.length,24);for(const e of K.exercises)verify(e.variables,e.cells(),true);
eq(K.gray(2),[0,1,3,2]);eq(Array.from({length:4},(_,r)=>K.cellAt(4,r,0)),[0,4,12,8]);
for(const bits of [1,2]){const labels=K.gray(bits);for(let i=0;i<labels.length;i++)eq(pop(labels[i]^labels[(i+1)%labels.length]),1,'Edges differ in one bit');}
eq(K.solve(2,[0,1,1,1]).expression,'A + B');
eq(K.solve(2,[0,1,1,0]).expression,"A'B + AB'");
eq(K.solve(4,Array.from({length:16},(_,i)=>[0,2,8,10].includes(i)?1:0)).expression,"B'D'");
eq(K.solve(2,[0,1,1,1],'POS').expression,'(A + B)');
eq(K.solve(2,[1,0,1,0],'POS').expression,"(B')");
ok(K.solve(4,Array.from({length:16},(_,i)=>[0,2,8,10].includes(i)?1:0)).groups[0].wraps,'Four corners wrap');
for(const n of [2,3,4])for(const form of ['SOP','POS'])for(const fill of [0,1,2])verify(n,Array(1<<n).fill(fill),true);
const badGroups=[[4,Array(16).fill(1),[0,1,3]],[4,Array(16).fill(1),[0,1,2,3,4,5]],[2,Array(4).fill(1),[0,3]],[2,Array(4).fill(2),[0,1]],[2,[1,0,0,0],[0,1]],[2,Array(4).fill(1),[]],[2,Array(4).fill(1),[4]]];
for(const [n,cells,selection]of badGroups)ok(!K.checkGroup(n,cells,selection,'SOP').valid,'Beginner invalid group rejected');
ok(K.checkGroup(2,Array(4).fill(1),[0],'SOP').message.includes('grow'),'Non-maximal feedback');
for(const text of ['-1','4','1-3','A','1.5','99999999999999999'])eq(K.fromLists(2,text,'').kind,'invalid');
eq(K.fromLists(3,'1,2','2').kind,'invalid');eq(K.fromLists(4,'16','').kind,'invalid');eq(K.fromLists(1,'','').kind,'invalid');
eq(K.fromLists(2,'1, 1, 2,','').cells,[0,1,1,0]);eq(K.fromLists(2,'1','','POS').cells,[1,0,1,1]);
assert.throws(()=>K.solve(5,[]));assert.throws(()=>K.solve(2,[0,1,1,3]));assert.throws(()=>K.solve(2,[0,1,1,1],'XYZ'));
console.log('PASS: 24 exercises, wrapping, overlap, constants, complement rules, group mistakes and invalid lists.');

function verifyWorking(input,from,to) {
 workingReports++;const r=working(input,from,to);let sum=E.rational(0n,1n);
 for(const p of [...r.places,...(r.repeatContribution?[r.repeatContribution]:[])])sum=E.rational(sum.n*p.d+p.n*sum.d,sum.d*p.d);
 eq(sum,E.rational(r.value.n<0n?-r.value.n:r.value.n,r.value.d),'Source place values reconstruct exact magnitude');
 for(const d of r.divisions){eq(d.dividend,d.quotient*BigInt(to)+BigInt(d.remainder),'Division identity');ok(d.remainder>=0&&d.remainder<to);eq(d.digit,'0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'[d.remainder]);}
 for(const m of r.multiplications){eq(m.before*BigInt(to),BigInt(m.digitValue)*m.denominator+m.after,'Multiplication identity');ok(m.after>=0n&&m.after<m.denominator);}
 const fraction=r.cycleStart===null?r.fractionalDigits:r.fractionalDigits.slice(0,r.cycleStart)+'('+r.fractionalDigits.slice(r.cycleStart)+')';
 eq((r.negative?'-':'')+r.reversedRemainders+(fraction?'.'+fraction:''),r.result.raw,'Step order reconstructs formatter output');
 eq(r.result,E.format(r.value,to));ok(r.text().endsWith(`${r.result.exact?'exact':'approximate'}`));
 if(r.result.exact){const parsed=E.parse(r.result.raw,to);eq(parsed.kind,'valid');eq(parsed.value,r.value,'Exact worked answer reparses');}
 return r;
}
eq(verifyWorking('13',10,2).divisions.map(d=>d.remainder),[1,0,1,1]);
eq(verifyWorking('0.625',10,2).fractionalDigits,'101');
eq(verifyWorking('0xFF',16,10).places.map(p=>p.digitValue),[15,15]);
eq(verifyWorking('35',10,36).result.raw,'Z');
eq(verifyWorking('0.(3)',10,2).cycleStart,0);
eq(verifyWorking('0.1(6)',10,2).repeatContribution,E.rational(1n,15n));
let r=verifyWorking('10101.101',2,16);eq(r.result.raw,'15.A');ok(r.shortcut.includes('Integer 0001 → 1'));ok(r.shortcut.includes('Fraction 1010 → A'));
r=verifyWorking('10101.1',2,8);eq(r.result.raw,'25.4');ok(r.shortcut.includes('Fraction 100 → 4'));
ok(verifyWorking('17.4',8,2).shortcut.includes('Integer 7 → 111'));
ok(verifyWorking('-10.625',10,2).text().includes("NOT two's complement"));ok(!verifyWorking('-0.0',10,2).negative);
eq(verifyWorking('0',10,2).divisions.length,1);eq(verifyWorking('00010',10,2).result.raw,'1010');eq(verifyWorking('.5',10,16).result.raw,'0.8');
for(const [input,from,to]of [[' -0B1111_1111 ',2,10],['+0o17',8,16],['a.f',16,2],['0.000000000000000000000001',2,10]])verifyWorking(input,from,to);
r=verifyWorking('0.('+('0123456789'.repeat(4))+')',10,3);ok(!r.result.exact);eq(r.multiplications.length,24);eq(r.cycleStart,null);ok(r.text().includes('truncated/approximate'));
for(const input of ['42','255','-42','10.625','0.125','0.(3)','0.1(6)'])for(let base=2;base<=36;base++)verifyWorking(input,10,base);
for(let base=2;base<=36;base++)verifyWorking('10.1',base,10);
for(const [input,from,to]of [['2',2,10],['',10,2],['G',16,2],['1.2.3',10,2],['10',10,1]])assert.throws(()=>working(input,from,to));
console.log(`PASS: ${workingReports} exact worked conversions, source sums, division/multiplication identities, bit padding, signs, recurring fractions and approximation labels.`);
const report={status:'pass',assertions,truthTables:tables,sopPosSolutions:solutions,independentMinimumChecks:oracleSolutions,workedConversionReports:workingReports,durationSeconds:Number(((Date.now()-started)/1000).toFixed(2)),baseExhaustiveCounts:{twoVariableTernary:81,threeVariableTernary:6561,fourVariableBoolean:65536,fourVariableSeededTernary:2000,sixVariableSeededTernary:128},note:'Additional catalog and constant tables are included in total counts. Browser tests do not imply Android device runtime verification.'};
fs.writeFileSync(path.join(__dirname,'../design/student-engine-results.json'),JSON.stringify(report,null,2)+'\n');
console.log(`PASS: ${assertions.toLocaleString()} assertions; ${solutions.toLocaleString()} SOP/POS solutions (${oracleSolutions.toLocaleString()} minimum-cover oracle checks).`);
