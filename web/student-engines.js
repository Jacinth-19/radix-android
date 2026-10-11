/* Exact student engines: rational working + exhaustive-cube Karnaugh minimization. */
(() => {
 'use strict';
 const E = typeof module !== 'undefined' && module.exports ? require('./engine.js') : window.RadixEngine;
 const DIGITS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
 const bitCount = n => { let count=0;if(typeof n==='bigint'){while(n){n&=n-1n;count++;}}else{while(n){n&=n-1;count++;}}return count; };
 const gray = bits => { if(!Number.isInteger(bits)||bits<1||bits>3) throw new RangeError('Use 1–3 axis bits'); return Array.from({length:1<<bits},(_,i)=>i^(i>>1)); };
 const rowBits = n => { if(!Number.isInteger(n)||n<2||n>6) throw new RangeError('Use 2–6 variables'); return Math.floor(n/2); };
 const columnBits = n => n-rowBits(n);
 const cellAt = (n,r,c) => { const rows=gray(rowBits(n)),columns=gray(columnBits(n)); if(r<0||r>=rows.length||c<0||c>=columns.length) throw new RangeError('Cell outside map'); return (rows[r]<<columnBits(n))|columns[c]; };
 const binary = (i,n) => i.toString(2).padStart(n,'0');
 const names = n => 'ABCDEF'.slice(0,n);
 const hasCell = (members,index) => (members&(1n<<BigInt(index)))!==0n;
 const indices = (cube,n) => Array.from({length:1<<n},(_,i)=>i).filter(i=>hasCell(cube.members,i));
 const cubes = {};
 for(let n=2;n<=6;n++) {
  cubes[n]=[];
  for(let mask=0;mask<(1<<n);mask++) for(let bits=0;bits<(1<<n);bits++) {
   if((bits&mask)!==bits) continue;
   let members=0n; for(let i=0;i<(1<<n);i++) if((i&mask)===bits) members|=1n<<BigInt(i);
   cubes[n].push({mask,bits,members,literals:bitCount(mask)});
  }
  cubes[n].sort((a,b)=>a.literals-b.literals||a.mask-b.mask||a.bits-b.bits);
 }
 const term = (cube,n,form) => {
  const literals=[];
  for(let i=0;i<n;i++) { const bit=1<<(n-1-i); if(!(cube.mask&bit)) continue; const one=!!(cube.bits&bit); literals.push('ABCDEF'[i]+(((form==='SOP'&&!one)||(form==='POS'&&one))?"'":'')); }
  return form==='SOP' ? literals.join('')||'1' : literals.join(' + ')||'0';
 };
 const wraps = (cube,n) => {
  const rows=gray(rowBits(n)),cols=gray(columnBits(n)),rs=new Set(),cs=new Set();
  rows.forEach((_,r)=>cols.forEach((_,c)=>{if(hasCell(cube.members,cellAt(n,r,c))) {rs.add(r);cs.add(c);} }));
  return (rows.length>2&&rs.size<rows.length&&rs.has(0)&&rs.has(rows.length-1))||(cols.length>2&&cs.size<cols.length&&cs.has(0)&&cs.has(cols.length-1));
 };
 function fromLists(n,text,dc,form='SOP') {
  try {
   rowBits(n); if(!['SOP','POS'].includes(form)) throw Error('Choose SOP or POS.');
   const parse=s=>{ if(s.length>256) throw Error('Keep each list to 256 characters or fewer.'); const tokens=s.trim().split(/[,\s]+/).filter(Boolean); return new Set(tokens.map(t=>{ if(!/^\d+$/.test(t)) throw Error('Use comma-separated cell indices, not ranges or expressions.'); const i=Number(t); if(!Number.isInteger(i)||i<0||i>=(1<<n)) throw Error(`Use cell indices 0–${(1<<n)-1}.`); return i; })); };
   const required=parse(text),ignored=parse(dc),overlap=[...required].filter(i=>ignored.has(i)).sort((a,b)=>a-b);
   if(overlap.length) throw Error(`A required cell cannot also be a don't-care: ${overlap.join(', ')}.`);
   const target=form==='SOP'?1:0;
   return {kind:'valid',cells:Array.from({length:1<<n},(_,i)=>ignored.has(i)?2:required.has(i)?target:1-target)};
  } catch(error) { return {kind:'invalid',message:error.message}; }
 }
 function fromNotation(source) {
  if(typeof source!=='string'||source.length>512)throw Error('Keep function notation within 512 characters.');
  const normalized=source.trim().toUpperCase(),match=/^F\s*\(\s*([A-F](?:\s*[,·.]\s*[A-F]){1,5})\s*\)\s*=\s*([Σ∑Π])\s*([MM])\s*\(\s*([^()]*)\s*\)$/.exec(normalized);
  if(!match)throw Error('Use F(A.B.C)=Σm(1,3,5) or F(A,B,C)=ΠM(0,2). Variables A–F; indices are comma-separated.');
  const variables=match[1].split(/[,·.\s]+/).join('');if(variables.length<2||variables.length>6||new Set(variables).size!==variables.length)throw Error('List 2–6 different variables in significance order.');
  const form=match[2]==='Π'?'POS':'SOP',parsed=fromLists(variables.length,match[4],'',form);if(parsed.kind==='invalid')throw Error(parsed.message);
  return {variables:variables.length,names:variables,form,cells:parsed.cells};
 }
 function solve(n,cells,form='SOP') {
  rowBits(n); if(!['SOP','POS'].includes(form)||cells.length!==(1<<n)||cells.some(v=>!Number.isInteger(v)||v<0||v>2)) throw Error('Check dimensions and values.');
  const target=form==='SOP'?1:0; let required=0n,forbidden=0n;
  cells.forEach((v,i)=>{const bit=1n<<BigInt(i);if(v===target) required|=bit; if(v===1-target) forbidden|=bit;});
  const finish=(expression,groups,primeCount,note)=>({variables:n,form,expression,groups,primeCount,requiredMask:required,literalCount:groups.reduce((a,g)=>a+g.cube.literals,0),note,
   evaluate:i=>{if(!Number.isInteger(i)||i<0||i>=(1<<n)) throw RangeError('Index outside map'); const found=groups.some(g=>hasCell(g.cube.members,i)); return form==='SOP'?found:!found;}});
  if(required===0n) return finish(form==='SOP'?'0':'1',[],0,cells.every(v=>v===2)?`Every cell is unspecified. ${form==='SOP'?'0':'1'} is a valid simplest choice; the other constant is valid too.`:`There are no required ${target}s. The constant ${form==='SOP'?'0':'1'} satisfies every specified cell.`);
  const eligible=cubes[n].filter(c=>(c.members&forbidden)===0n&&(c.members&required)!==0n);
  const primes=eligible.filter(c=>!eligible.some(o=>o.members!==c.members&&(o.members&c.members)===c.members));
  const coverage=primes.map(c=>c.members&required);
  const essential=primes.map((_,p)=>p).filter(p=>cells.some((_,i)=>{const bit=1n<<BigInt(i);return (required&bit)!==0n&&(coverage[p]&bit)!==0n&&coverage.filter(c=>(c&bit)!==0n).length===1;}));
  let start=0n; essential.forEach(p=>start|=coverage[p]);
  let best=null,bestLiterals=Infinity; const visited=new Map();
  function search(covered,chosen,literals) {
   if(covered===required) {
    const sorted=[...chosen].sort((a,b)=>a-b);
    if(!best||sorted.length<best.length||(sorted.length===best.length&&(literals<bestLiterals||(literals===bestLiterals&&sorted.join(',')<best.join(','))))) {best=sorted;bestLiterals=literals;}
    return;
   }
   if(best&&chosen.length>=best.length) return;
   const old=visited.get(covered); if(old&&(old[0]<chosen.length||(old[0]===chosen.length&&old[1]<=literals))) return;
   visited.set(covered,[chosen.length,literals]); const remaining=required&~covered;
   let options=[],fewest=Infinity;
   for(let i=0;i<cells.length;i++) if((remaining&(1n<<BigInt(i)))!==0n) { const bit=1n<<BigInt(i),p=coverage.map((_,j)=>j).filter(j=>(coverage[j]&bit)!==0n); if(p.length<fewest) {fewest=p.length;options=p;} }
   options.sort((a,b)=>bitCount(coverage[b]&remaining)-bitCount(coverage[a]&remaining)||primes[a].literals-primes[b].literals||a-b).forEach(p=>search(covered|coverage[p],[...chosen,p],literals+primes[p].literals));
  }
  search(start,essential,essential.reduce((a,p)=>a+primes[p].literals,0));
  if(!best) throw Error('Cover could not be found');
  const groups=best.map(p=>{const cube=primes[p];return {cube,term:term(cube,n,form),essential:essential.includes(p),wraps:wraps(cube,n),explanation:Array.from({length:n},(_,i)=>{
   const bit=1<<(n-1-i),letter='ABCDEF'[i]; if(!(cube.mask&bit)) return `${letter} changes inside the group → remove it.`;
   const fixed=(cube.bits&bit)?1:0,literal=letter+(((form==='SOP'&&fixed===0)||(form==='POS'&&fixed===1))?"'":''); return `${letter} stays ${fixed} → ${literal}.`;
  })};}).sort((a,b)=>a.term<b.term?-1:a.term>b.term?1:0);
  const expression=form==='SOP'?groups.map(g=>g.term).join(' + '):groups.some(g=>g.term==='0')?'0':groups.map(g=>`(${g.term})`).join('');
  return finish(expression,groups,primes.length,`Exact minimum: fewest ${form==='SOP'?'product terms':'sum clauses'}, then fewest literals. Other equally minimal covers may exist.`);
 }
 function checkGroup(n,cells,list,form='SOP') {
  if(!Number.isInteger(n)||n<2||n>6||cells.length!==(1<<n)||cells.some(v=>!Number.isInteger(v)||v<0||v>2)||!['SOP','POS'].includes(form)) return {valid:false,message:'Check the map dimensions, form and values.'};
  const selected=[...new Set(list)],target=form==='SOP'?1:0;
  if(!selected.length) return {valid:false,message:'Select at least one cell.'};
  if(selected.some(i=>!Number.isInteger(i)||i<0||i>=cells.length)) return {valid:false,message:'A selected index is outside this map.'};
  if(selected.length&(selected.length-1)) return {valid:false,message:'Groups must have a power-of-two size: 1, 2, 4, 8, 16, 32 or 64.'};
  if(selected.some(i=>cells[i]===1-target)) return {valid:false,message:`${form} groups cannot contain a ${1-target}.`};
  if(!selected.some(i=>cells[i]===target)) return {valid:false,message:`A useful group must cover a required ${target}, not only don't-cares.`};
  const members=selected.reduce((a,i)=>a|(1n<<BigInt(i)),0n),cube=cubes[n].find(c=>c.members===members);
  if(!cube) return {valid:false,message:'Use adjacent Gray-code rectangles. Diagonal-only groups are not valid; opposite edges can touch.'};
  const canGrow=cubes[n].some(c=>c.members!==members&&(c.members&members)===members&&indices(c,n).every(i=>cells[i]!==1-target));
  return {valid:true,cube,message:`Valid ${selected.length}-cell group → ${form==='POS'?'('+term(cube,n,form)+')':term(cube,n,form)}. ${wraps(cube,n)?'Opposite edges are adjacent. ':''}${canGrow?'It can grow into a larger valid group.':'It is already a maximal group.'}`};
 }
 const ex=(id,title,n,ones,dc=[],form='SOP',hint='',lesson='')=>({id,title,variables:n,ones,dc,form,hint,lesson,cells:()=>Array.from({length:1<<n},(_,i)=>dc.includes(i)?2:ones.includes(i)?1:0)});
 const range=(a,b)=>Array.from({length:b-a+1},(_,i)=>a+i);
 const exercises=[
  ex('or','Overlapping pairs',2,[1,2,3],[],'SOP','Pairs may share a cell. Cover every 1.','Overlap is allowed; m3 belongs to both useful pairs.'),
  ex('and','One isolated 1',2,[3],[],'SOP','A group of one is legal when it cannot expand.','Both variables stay 1, giving AB.'),
  ex('xor','Do not group diagonals',2,[1,2],[],'SOP','The two 1s differ in two bits.','Diagonal cells do not touch. Keep two one-cell terms.'),
  ex('xnor','The other diagonal',2,[0,3],[],'SOP','Corners in a 2×2 map are not all mutually adjacent.','A two-cell diagonal is not a Gray-code rectangle.'),
  ex('pair','Drop a changing variable',2,[0,1],[],'SOP','A stays 0; B changes.','The changing B disappears; A=0 contributes A\'.'),
  ex('all-one','The constant-one map',2,[0,1,2,3],[],'SOP','Use the whole map as one group.','All variables change, leaving F=1.'),
  ex('all-zero','No required 1s',2,[],[],'SOP','A SOP needs no products when there are no 1s.','The simplest function is F=0.'),
  ex('dc-pair','Use a helpful don\'t-care',2,[1],[3],'SOP','An X can enlarge the group, but need not be used.','The pair m1,m3 simplifies to B.'),
  ex('wrap-three','Three-variable edge wrap',3,[0,2],[],'SOP','Columns 00 and 10 are neighbors across the edge.','The outer pair gives A\'C\'.'),
  ex('row-three','A whole row',3,[0,1,2,3],[],'SOP','B and C both vary within this row.','Only A=0 remains: A\'.'),
  ex('column-three','An odd-index pattern',3,[1,3,5,7],[],'SOP','Read binary indices; C is the least significant bit.','C stays 1, so the answer is C.'),
  ex('three-ones','Three is not a group size',3,[0,1,3],[],'SOP','Cover three 1s using overlapping power-of-two groups.','Use two pairs; never draw a group of three.'),
  ex('parity','No adjacent 1s',3,[1,2,4,7],[],'SOP','Every possible pair would include a 0.','Four isolated cells need four product terms.'),
  ex('dc-four','Grow to four with Xs',3,[1,3],[5,7],'SOP','The two Xs can complete the C=1 group.','C alone covers the required 1s without covering any required 0.'),
  ex('corners','Four corners are adjacent',4,[0,2,8,10],[],'SOP','Wrap horizontally and vertically at the same time.','The four corners give B\'D\', not four separate terms.'),
  ex('octet','An eight-cell group',4,range(0,7),[],'SOP','Three variables change; A stays 0.','The largest group is eight cells, giving A\'.'),
  ex('row-four','Read Gray-code rows',4,[4,5,6,7],[],'SOP','Row AB=01 contains these four minterms.','CD vary; AB=01 leaves A\'B.'),
  ex('column-four','Four Gray-code rows',4,[1,5,9,13],[],'SOP','Column CD=01 spans all rows.','A and B vanish, leaving C\'D.'),
  ex('overlap-four','Three overlapping groups',4,[0,1,2,3,4,5,8,9],[],'SOP','A cell may be used in several maximal groups.','Overlapping four-cell groups cover all 1s with three terms.'),
  ex('dc-octet','An X-assisted octet',4,[1,3,7,11,15],[5,9,13],'SOP','Don\'t-cares complete all odd-index cells.','Use the eight-cell D=1 group.'),
  ex('pos-or','POS groups zeroes',2,[1,2,3],[],'POS','Group the lone 0, not the three 1s.','A=0 and B=0 produce the sum clause (A+B).'),
  ex('pos-complement','POS complement rule',2,[0,2],[],'POS','B stays 1 in the zero group.','For POS, fixed 1 becomes complemented: (B\').'),
  ex('pos-corners','POS corner wrapping',4,range(0,15).filter(i=>![0,2,8,10].includes(i)),[],'POS','The zeroes are the four corners; B and D stay 0.','Fixed 0 is uncomplemented in POS: (B+D).'),
  ex('all-x','All don\'t-cares',2,[],range(0,3),'SOP','No input combination imposes a required output.','Either constant is valid; the SOP solver chooses 0 and POS chooses 1.')
 ];
 function working(raw,from,to) {
  const p=E.parse(raw,from); if(p.kind!=='valid') throw Error(p.message||'Enter a number first.');
  if(!Number.isInteger(to)||to<2||to>36) throw Error('Choose a target base from 2 to 36.');
  const value=p.value; let text=raw.trim().replace(/[\s_]/g,'').toUpperCase().replace(/^[+-]/,'');
  const prefix=({2:'0B',8:'0O',16:'0X'})[from]; if(prefix&&text.startsWith(prefix)) text=text.slice(2);
  const match=/^([0-9A-Z]*)(?:\.([0-9A-Z]*)(?:\(([0-9A-Z]+)\))?)?$/.exec(text),whole=match[1]||'0',fraction=match[2]||'',repeat=match[3]||'';
  const b=BigInt(from),t=BigInt(to),number=s=>[...s].reduce((a,c)=>a*b+BigInt(DIGITS.indexOf(c)),0n);
  const places=[];
  [...whole].forEach((digit,i)=>{const digitValue=DIGITS.indexOf(digit),exponent=whole.length-1-i; places.push({digit,digitValue,exponent,n:BigInt(digitValue)*b**BigInt(exponent),d:1n});});
  [...fraction].forEach((digit,i)=>places.push({digit,digitValue:DIGITS.indexOf(digit),exponent:-(i+1),n:BigInt(DIGITS.indexOf(digit)),d:b**BigInt(i+1)}));
  const repeatContribution=repeat?E.rational(number(repeat),b**BigInt(fraction.length)*(b**BigInt(repeat.length)-1n)):null;
  const repeatFormula=repeatContribution?`${repeat} (base ${from}) / [${from}^${fraction.length} × (${from}^${repeat.length} − 1)] = ${repeatContribution.n}/${repeatContribution.d}`:null;
  const abs=value.n<0n?-value.n:value.n,integer=abs/value.d,divisions=[]; let current=integer;
  do {const q=current/t,r=Number(current%t);divisions.push({dividend:current,base:to,quotient:q,remainder:r,digit:DIGITS[r]});current=q;} while(current);
  const multiplications=[],seen=new Map(); let remainder=abs%value.d;
  while(remainder&&multiplications.length<24&&!seen.has(remainder)) {seen.set(remainder,multiplications.length);const expanded=remainder*t,q=Number(expanded/value.d),after=expanded%value.d; multiplications.push({before:remainder,denominator:value.d,base:to,digitValue:q,digit:DIGITS[q],after});remainder=after;}
  const cycleStart=remainder&&seen.has(remainder)?seen.get(remainder):null,shortcut=[];
  if(!repeat&&from===2&&[8,16].includes(to)) {
   const width=to===8?3:4,padded=whole.padStart(Math.ceil(whole.length/width)*width,'0');
   shortcut.push(`Binary shortcut: group ${width} bits at a time around the point. Pad the INTEGER on the LEFT; pad the FRACTION on the RIGHT.`);
   for(let i=0;i<padded.length;i+=width) {const g=padded.slice(i,i+width);shortcut.push(`Integer ${g} → ${DIGITS[parseInt(g,2)]}`);}
   const f=fraction.padEnd(Math.ceil(fraction.length/width)*width,'0');for(let i=0;i<f.length;i+=width) {const g=f.slice(i,i+width);shortcut.push(`Fraction ${g} → ${DIGITS[parseInt(g,2)]}`);}
  } else if(!repeat&&[8,16].includes(from)&&to===2) {
   const width=from===8?3:4;shortcut.push(`Binary shortcut: each base-${from} digit represents exactly ${width} bits. Keep the point in place.`);
   [...whole].forEach(c=>shortcut.push(`Integer ${c} → ${DIGITS.indexOf(c).toString(2).padStart(width,'0')}`)); [...fraction].forEach(c=>shortcut.push(`Fraction ${c} → ${DIGITS.indexOf(c).toString(2).padStart(width,'0')}`));
  }
  const report={input:raw,from,to,value,places,repeatContribution,repeatFormula,integer,divisions,multiplications,cycleStart,remaining:remainder,result:E.format(value,to),shortcut,
   negative:value.n<0n,ratio:value.d===1n?String(value.n):`${value.n}/${value.d}`,reversedRemainders:[...divisions].reverse().map(s=>s.digit).join(''),fractionalDigits:multiplications.map(s=>s.digit).join('')};
  report.text=()=>[
   `${raw} (base ${from}) → ${report.result.display} (base ${to})`,
   '1. Expand the source place values (work on the magnitude).',
   ...places.map(s=>`${s.digit} (${s.digitValue}) × ${from}^${s.exponent} = ${s.d===1n?s.n:s.n+'/'+s.d}`),
   ...(repeatFormula?['Repeating tail: '+repeatFormula]:[]),`Exact value: ${report.ratio}; decimal: ${E.format(value,10).display}`,
   `2. Divide the integer part by ${to}. Read remainders BOTTOM TO TOP.`,
   ...divisions.map(s=>`${s.dividend} ÷ ${to} = ${s.quotient}, remainder ${s.remainder} (${s.digit})`),`Integer digits: ${report.reversedRemainders}`,
   ...(multiplications.length?[`3. Multiply the fractional remainder by ${to}. Read digits TOP TO BOTTOM.`,...multiplications.map((s,i)=>`${i+1}. ${s.before}/${s.denominator} × ${to} = ${s.digitValue} + ${s.after}/${s.denominator} → ${s.digit}`),cycleStart!==null?`The remainder repeats: the cycle starts at fractional step ${cycleStart+1}. Put those digits in parentheses.`:remainder?'Stopped at 24 fractional places. The ellipsis means truncated/approximate, not exact.':'The remainder is zero: the fraction terminates exactly.']:[]),
   ...(report.negative?["Restore the minus sign. This is signed-magnitude notation, NOT two's complement."]:[]),...shortcut,
   `Answer: ${report.result.display} (base ${to}) — ${report.result.exact?'exact':'approximate'}`
  ].join('\n'); return report;
 }
 const K={solve,fromLists,fromNotation,checkGroup,gray,rowBits,columnBits,cellAt,binary,names,term,indices,hasCell,exercises,bitCount};
 if(typeof module!=='undefined'&&module.exports) module.exports={K,working};
 if(typeof window!=='undefined') {window.RadixKMap=K;window.RadixWorking=working;}
})();
