#!/usr/bin/env node
'use strict';
const assert=require('node:assert/strict'),fs=require('node:fs'),path=require('node:path');
const L=require('../web/logic-engine.js'),D=require('../web/logic-diagrams.js');
let checks=0,gateRows=0,circuitRows=0,stateRows=0,edgeTransitions=0;
const eq=(a,b,n)=>{checks++;assert.deepEqual(a,b,n);},ok=(a,n)=>{checks++;assert.ok(a,n);};
for(const g of L.catalog.gates)for(const n of ['NOT','BUFFER'].includes(g.id)?[1]:[2,3,4])for(let c=0;c<(1<<n);c++){
 const bits=L.bits(c,n),ones=c.toString(2).split('1').length-1;
 const expected=({AND:ones===n,OR:ones>0,NOT:ones===0,BUFFER:ones===1,NAND:ones<n,NOR:ones===0,XOR:ones%2===1,XNOR:ones%2===0})[g.id];
 eq(L.gate(g.id,bits),expected,`${g.id}/${n}/${c}`);eq(L.code(bits),c);gateRows++;
 const svg=D.gate(g.id,bits,expected);ok(svg.includes('role="img"'));ok(svg.includes('<title>'));ok(!svg.includes('NaN'));
}
function expectedCircuit(c,bits){const v=Object.fromEntries(c.inputs.map((n,i)=>[n,Number(bits[i])])),a=v.A||0,b=v.B||0;let out;
 switch(c.id){case 'half-adder':out=[(a+b)%2,Math.floor((a+b)/2)];break;case 'full-adder':{const x=a+b+v.Cin;out=[x%2,Math.floor(x/2)];break;}case 'half-subtractor':case 'full-subtractor':{const x=a-b-(v.Bin||0);out=[x&1,Number(x<0)];break;}case 'mux-2':out=[v['D'+v.S]];break;case 'mux-4':out=[v['D'+(2*v.S1+v.S0)]];break;case 'demux-2':out=Array.from({length:2},(_,i)=>i===v.S?v.D:0);break;case 'decoder-2':out=Array.from({length:4},(_,i)=>Number(!!v.EN&&i===2*a+b));break;case 'encoder-4':{let index=-1;for(let i=3;i>=0;i--)if(v['D'+i]){index=i;break;}out=[Math.floor(Math.max(0,index)/2),Math.max(0,index)%2,Number(index>=0)];break;}case 'compare-1':out=[Number(a<b),Number(a===b),Number(a>b)];break;case 'compare-2':{const a=2*v.A1+v.A0,b=2*v.B1+v.B0;out=[Number(a<b),Number(a===b),Number(a>b)];break;}case 'ripple-4':{let a=0,b=0;for(let i=0;i<4;i++){a+=v['A'+i]*2**i;b+=v['B'+i]*2**i;}const sum=a+b+v.Cin;out=[(sum>>3)&1,(sum>>2)&1,(sum>>1)&1,sum&1,Math.floor(sum/16)];break;}default:throw Error(c.id);}return out;
}
for(const c of L.catalog.circuits){const layout=D.layout(c);ok(Number.isFinite(layout.w)&&Number.isFinite(layout.h));for(const n of c.nodes){const p=layout.positions[n.id];ok(p.x>=0&&p.x+70<=layout.w&&p.y>=26&&p.y+45<=layout.h);for(const ref of n.refs)ok(layout.positions[ref].x<p.x,'Topological geometry');}
 for(let code=0;code<(1<<c.inputs.length);code++){const inputs=L.bits(code,c.inputs.length),r=L.evaluate(c,inputs);eq(Object.values(r.outputs).map(Number),expectedCircuit(c,inputs),c.id+'/'+code);circuitRows++;}
 const r=L.evaluate(c,Array(c.inputs.length).fill(false));for(const expanded of [true,false]){const svg=D.circuit(c,r,expanded);ok(!svg.includes('NaN'));ok(!svg.includes('undefined'));ok(svg.includes(c.title));}
}
function expectedSeq(s,q,i){const a=Number(i[0]),b=Number(i[1]||false),sr=(set,reset)=>set&&reset?null:set?1:reset?0:q;switch(s.id){case 'SR_NOR':case 'SR_FF':return sr(a,b);case 'SR_NAND':return sr(!a,!b);case 'D_LATCH':return b?a:q;case 'D_FF':return a;case 'JK_FF':return !a&&!b?q:!a?0:!b?1:q===null?null:1-q;case 'T_FF':return !a?q:q===null?null:1-q;case 'COUNTER_3':return b?0:!a?q:q===null?null:(q+1)%8;case 'SHIFT_4':return i[2]?0:!b?q:q===null?null:(2*q+a)%16;case 'RING_4':return b?1:!a?q:q===null?null:(2*q)%16+Math.floor(q/8);case 'JOHNSON_4':return b?0:!a?q:q===null?null:(2*q)%16+1-Math.floor(q/8);default:throw Error(s.id);}}
for(const s of L.catalog.sequential){const rows=L.nextRows(s);stateRows+=rows.length;for(const r of rows){eq(r.after,expectedSeq(s,r.before,r.inputs));for(const oldClock of [false,true])for(const clock of [false,true]){const result=L.update(s,{bits:r.before,clock:oldClock},r.inputs,clock);eq(result.bits,!s.clocked||(!oldClock&&clock)?expectedSeq(s,r.before,r.inputs):r.before,`${s.id} edge`);eq(result.clock,s.clocked&&clock);edgeTransitions++;}}
 for(let c=0;c<(1<<s.inputs.length);c++){const i=L.bits(c,s.inputs.length);eq(L.update(s,{bits:null,clock:false},i,true).bits,expectedSeq(s,null,i),'Unknown-state recovery');}
 const i=L.defaultInputs(s),st=L.initial(s),outs=L.outputs(s,st,i);for(const expanded of [false,true]){const svg=D.sequential(s,st,i,outs,expanded);ok(!svg.includes('NaN'));ok(!svg.includes('undefined'));}
 const trace=D.timing(s,[{tick:0,clock:false,inputs:i,outputs:outs},{tick:1,clock:true,inputs:i,outputs:L.outputs(s,{bits:null,clock:true},i)}]);ok(trace.includes('Discrete timing trace'));ok(trace.includes('>X<'));
}
const sr=L.catalog.sequential.find(x=>x.id==='SR_NOR'),nand=L.catalog.sequential.find(x=>x.id==='SR_NAND');
let q=L.update(sr,{bits:1,clock:false},[true,true],false);eq(q.bits,null);eq(L.outputs(sr,q,[true,true]),[0,0]);q=L.update(sr,q,[false,false],false);eq(q.bits,null);eq(L.outputs(sr,q,[false,false]),[null,null]);eq(L.update(sr,q,[true,false],false).bits,1);
q=L.update(nand,{bits:0,clock:false},[false,false],false);eq(L.outputs(nand,q,[false,false]),[1,1]);
for(const [id,expected]of [['RING_4',[1,2,4,8,1]],['JOHNSON_4',[0,1,3,7,15,14,12,8,0]]]){const s=L.catalog.sequential.find(x=>x.id===id);let q=L.initial(s),actual=[q.bits];for(let i=1;i<expected.length;i++){q=L.update(s,{...q,clock:false},[true,false],true);actual.push(q.bits);}eq(actual,expected);}
const shift=L.catalog.sequential.find(x=>x.id==='SHIFT_4');q=L.initial(shift);for(const d of [true,false,true,true])q=L.update(shift,{...q,clock:false},[d,true,false],true);eq(q.bits,11);
assert.throws(()=>L.gate('AND',[true]));assert.throws(()=>L.gate('NOT',[true,false]));assert.throws(()=>L.gate('OR',[0,1]));assert.throws(()=>L.bits(512,9));assert.throws(()=>L.evaluate(L.catalog.circuits[0],[true]));assert.throws(()=>L.update(shift,{bits:16,clock:false},[false,true,false],true));
eq(gateRows,172);eq(circuitRows,656);eq(stateRows,336);eq(edgeTransitions,1344);
const report={status:'pass',assertions:checks,gateKinds:8,gateTruthRows:gateRows,combinationalCircuits:12,combinationalTruthRows:circuitRows,sequentialModels:11,nextStateRows:stateRows,clockCombinationTransitions:edgeTransitions,note:'Independent arithmetic/state oracle and diagram topology checks; not Android runtime tests.'};fs.writeFileSync(path.join(__dirname,'../design/logic-engine-results.json'),JSON.stringify(report,null,2)+'\n');console.log(JSON.stringify(report,null,2));
