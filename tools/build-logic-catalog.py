#!/usr/bin/env python3
"""Single-source learning catalog -> pure Kotlin and offline JS (no runtime JSON library)."""
from pathlib import Path
import json
root=Path(__file__).resolve().parent.parent
G=[
 ('AND','All inputs must be 1.','Y = A · B','An AND gate only outputs 1 when every input is 1.'),
 ('OR','At least one input is 1.','Y = A + B','OR is inclusive: both inputs at 1 still produce 1.'),
 ('NOT','Reverse one input.','Y = A′','The small output bubble means inversion. NOT has exactly one input.'),
 ('NAND','Invert the AND result.','Y = (A · B)′','NAND is universal: combinations of NAND gates can implement any Boolean function.'),
 ('NOR','Invert the OR result.','Y = (A + B)′','NOR is universal. Its output is 1 only when all inputs are 0.'),
 ('XOR','An odd number of inputs is 1.','Y = A ⊕ B','For two inputs XOR means different. With 3 or 4 inputs, it means ODD parity—not exactly one 1.'),
 ('XNOR','An even number of inputs is 1.','Y = A ⊙ B','For two inputs XNOR means equal. With 3 or 4 inputs, it means EVEN parity—not that all inputs are equal.'),
 ('BUFFER','Keep the input unchanged.','Y = A','A buffer has one input. It preserves the logic value; electrical drive strength is not modeled.')]
gates=[dict(id=a,title=a,description=b,formula=c,lesson=d) for a,b,c,d in G]
def node(id,gate,*refs):return dict(id=id,gate=gate,refs=list(refs))
def circ(id,title,inputs,outputs,nodes,formula,lesson):return dict(id=id,title=title,inputs=inputs.split(),outputs=outputs,nodes=nodes,formula=formula,lesson=lesson)
C=[]
C.append(circ('half-adder','Half adder','A B',{'Sum':'s','Carry':'c'},[node('s','XOR','A','B'),node('c','AND','A','B')],['Sum = A ⊕ B','Carry = A · B'],['Adds two one-bit values. It has no carry-in.','Read Carry:Sum as a two-bit number; 1 + 1 = 10₂, not a single output of 2.']))
C.append(circ('full-adder','Full adder','A B Cin',{'Sum':'s','Cout':'c'},[node('x','XOR','A','B'),node('s','XOR','x','Cin'),node('a','AND','A','B'),node('b','AND','x','Cin'),node('c','OR','a','b')],['Sum = A ⊕ B ⊕ Cin','Cout = AB + Cin(A ⊕ B)'],['Includes a carry-in, so three one-bit values are added.','Cout:Sum represents A + B + Cin. Connect Cout to the next stage’s Cin for a ripple adder.']))
C.append(circ('half-subtractor','Half subtractor','A B',{'Diff':'d','Borrow':'b'},[node('n','NOT','A'),node('d','XOR','A','B'),node('b','AND','n','B')],['Diff = A ⊕ B','Borrow = A′B'],['Subtracts B from A. Operand order matters.','When A=0 and B=1, Diff=1 with Borrow=1. This is binary subtraction, not an ordinary negative output bit.']))
C.append(circ('full-subtractor','Full subtractor','A B Bin',{'Diff':'d','Bout':'b'},[node('x','XOR','A','B'),node('d','XOR','x','Bin'),node('na','NOT','A'),node('nx','NOT','x'),node('p','AND','na','B'),node('q','AND','nx','Bin'),node('b','OR','p','q')],['Diff = A ⊕ B ⊕ Bin','Bout = A′B + (A ⊕ B)′Bin'],['Computes A − B − Bin.','Bout indicates that the next higher stage must supply a borrow; Diff = (A − B − Bin) mod 2.']))
C.append(circ('mux-2','2:1 multiplexer','D0 D1 S',{'Y':'y'},[node('ns','NOT','S'),node('a','AND','D0','ns'),node('b','AND','D1','S'),node('y','OR','a','b')],['Y = S′D0 + SD1'],['A multiplexer selects one data input, not one output.','S=0 selects D0; S=1 selects D1. Changing an unselected input does not affect Y.']))
C.append(circ('mux-4','4:1 multiplexer','D0 D1 D2 D3 S1 S0',{'Y':'y'},[node('n1','NOT','S1'),node('n0','NOT','S0'),node('p0','AND','D0','n1','n0'),node('p1','AND','D1','n1','S0'),node('p2','AND','D2','S1','n0'),node('p3','AND','D3','S1','S0'),node('y','OR','p0','p1','p2','p3')],['Y = D[2S1 + S0]'],['S1 is the most significant select bit. 00/01/10/11 select D0/D1/D2/D3.','There are six independent inputs, so the full truth table has 64 rows.']))
C.append(circ('demux-2','1:2 demultiplexer','D S',{'Y0':'a','Y1':'b'},[node('ns','NOT','S'),node('a','AND','D','ns'),node('b','AND','D','S')],['Y0 = DS′','Y1 = DS'],['Routes one data input to one selected output.','The unselected output is 0. It is not a multiplexer with reversed labels.']))
C.append(circ('decoder-2','2:4 decoder + enable','A B EN',{'Y0':'p0','Y1':'p1','Y2':'p2','Y3':'p3'},[node('na','NOT','A'),node('nb','NOT','B'),node('p0','AND','EN','na','nb'),node('p1','AND','EN','na','B'),node('p2','AND','EN','A','nb'),node('p3','AND','EN','A','B')],['EN=0: all outputs 0','EN=1: Y[2A + B] = 1'],['Active-high one-hot outputs. A is the MSB.','When disabled no output is asserted; this differs from a decoder without an enable input.']))
C.append(circ('encoder-4','4:2 priority encoder','D3 D2 D1 D0',{'Y1':'y1','Y0':'y0','Valid':'v'},[node('n2','NOT','D2'),node('p','AND','D1','n2'),node('y1','OR','D3','D2'),node('y0','OR','D3','p'),node('v','OR','D3','D2','D1','D0')],['D3 > D2 > D1 > D0','Y1Y0 = highest asserted input index'],['Unlike a basic one-hot encoder, multiple active inputs are allowed: the highest index wins.','Valid distinguishes D0=1 (code 00) from no active input (code 00, Valid=0).']))
C.append(circ('compare-1','1-bit magnitude comparator','A B',{'LT':'l','EQ':'e','GT':'g'},[node('na','NOT','A'),node('nb','NOT','B'),node('l','AND','na','B'),node('e','XNOR','A','B'),node('g','AND','A','nb')],['LT = A′B','EQ = A ⊙ B','GT = AB′'],['Compares unsigned one-bit values.','Exactly one of LT/EQ/GT is 1 for every valid input.']))
C.append(circ('compare-2','2-bit magnitude comparator','A1 A0 B1 B0',{'LT':'l','EQ':'e','GT':'g'},[node('e1','XNOR','A1','B1'),node('e0','XNOR','A0','B0'),node('na1','NOT','A1'),node('nb1','NOT','B1'),node('na0','NOT','A0'),node('nb0','NOT','B0'),node('lp','AND','na1','B1'),node('lq','AND','e1','na0','B0'),node('gp','AND','A1','nb1'),node('gq','AND','e1','A0','nb0'),node('l','OR','lp','lq'),node('g','OR','gp','gq'),node('e','AND','e1','e0')],['Compare A1 vs B1 first','If equal, compare A0 vs B0'],['Unsigned values A=2A1+A0 and B=2B1+B0.','The least-significant comparison only matters when the most-significant bits match.']))
nodes=[];prev='Cin';outputs={}
for i in range(4):
 x=f'x{i}';a=f'a{i}';b=f'b{i}';c=f'c{i}';s=f's{i}'
 nodes += [node(x,'XOR',f'A{i}',f'B{i}'),node(s,'XOR',x,prev),node(a,'AND',f'A{i}',f'B{i}'),node(b,'AND',x,prev),node(c,'OR',a,b)]
 prev=c
outputs={f'S{i}':f's{i}' for i in range(3,-1,-1)};outputs['Cout']='c3'
C.append(circ('ripple-4','4-bit ripple-carry adder','A3 A2 A1 A0 B3 B2 B1 B0 Cin',outputs,nodes,['Cout:S3S2S1S0 = A + B + Cin'],['Four full-adder stages carry from bit 0 toward bit 3.','A3/B3 are the MSBs. S3…S0 is the low four-bit sum; Cout is the fifth bit.','This ideal model has no propagation-delay simulation. The 512-row truth table is paginated.']))

def lit(name,one):return name if one else 'n'+name
def reduce_or(nodes,refs,prefix):
 while len(refs)>1:
  next_refs=[]
  for start in range(0,len(refs),4):
   group=refs[start:start+4]
   if len(group)==1:next_refs.append(group[0])
   else:
    ident=f'{prefix}_{len(nodes)}';nodes.append(node(ident,'OR',*group));next_refs.append(ident)
  refs=next_refs
 return refs[0]
def add_mux(width):
 bits=width.bit_length()-1;selects=[f'S{i}' for i in range(bits-1,-1,-1)];inputs=[f'D{i}' for i in range(width)]+selects;nodes=[]
 for name in selects:nodes.append(node('n'+name,'NOT',name))
 pair_bits=bits-2 if bits>3 else 0
 if pair_bits:
  hi_count=1<<pair_bits;low_count=1<<pair_bits
  for prefix_names,prefix,offset in [(selects[:pair_bits],'hi',0),(selects[pair_bits:],'lo',pair_bits)]:
   for code in range(1<<len(prefix_names)):
    refs=[lit(name,(code>>(len(prefix_names)-j-1))&1==1) for j,name in enumerate(prefix_names)]
    nodes.append(node(f'{prefix}{code}','AND',*refs))
 terms=[]
 for i in range(width):
  if bits<=3:
   refs=[f'D{i}']+[lit(name,(i>>(bits-j-1))&1==1) for j,name in enumerate(selects)]
  else:
   low_bits=bits-pair_bits;hi_code=i>>low_bits;lo_code=i&((1<<low_bits)-1)
   refs=[f'D{i}',f'hi{hi_code}',f'lo{lo_code}']
  ident=f'm{i}';nodes.append(node(ident,'AND',*refs));terms.append(ident)
 root=reduce_or(nodes,terms,'mux-or')
 C.append(circ(f'mux-{width}',f'{width}:1 multiplexer',' '.join(inputs),{'Y':root},nodes,
  [f'Y = D[base-2 index selected by {", ".join(selects)}]'],
  [f'{bits} select bits choose exactly one of {width} data inputs.','Select inputs are ordered most-significant first; 0… selects D0…D'+str(width-1)+'.','A full table has 2^'+str(len(inputs))+' rows and is paginated on demand.']))
for width in (8,16):add_mux(width)

def add_demux(width):
 bits=width.bit_length()-1;selects=[f'S{i}' for i in range(bits-1,-1,-1)];nodes=[node('n'+name,'NOT',name) for name in selects];inputs=['D']+selects
 pair_bits=bits-2 if bits>3 else 0
 if pair_bits:
  low_bits=bits-pair_bits
  for prefix_names,prefix in [(selects[:pair_bits],'hi'),(selects[pair_bits:],'lo')]:
   for code in range(1<<len(prefix_names)):
    refs=[lit(name,(code>>(len(prefix_names)-j-1))&1==1) for j,name in enumerate(prefix_names)]
    nodes.append(node(f'{prefix}{code}','AND',*refs))
 outputs={};formulas=[]
 for i in range(width):
  if bits<=3:refs=['D']+[lit(name,(i>>(bits-j-1))&1==1) for j,name in enumerate(selects)]
  else:
   low_bits=bits-pair_bits;refs=['D',f'hi{i>>low_bits}',f'lo{i&((1<<low_bits)-1)}']
  ident=f'y{i}';nodes.append(node(ident,'AND',*refs));outputs[f'Y{i}']=ident
  if width<=4:formulas.append(f'Y{i} = D · minterm(S) for select {i:0{bits}b}')
 C.append(circ(f'demux-{width}',f'1:{width} demultiplexer',' '.join(inputs),outputs,nodes,
  formulas or [f'Y[i] = D when select = i; all other outputs are 0.'],
  ['Routes one data input to the output selected by the binary select word.','Select inputs are most-significant first. With D=0 every output is 0.','A full table has 2^'+str(len(inputs))+' rows and is paginated on demand.']))
for width in (4,8,16):add_demux(width)

def add_decoder(width):
 bits=width.bit_length()-1;inputs=[f'A{i}' for i in range(bits-1,-1,-1)];nodes=[]
 for name in inputs:nodes.append(node('n'+name,'NOT',name))
 outputs={}
 for i in range(width):
  refs=[lit(name,(i>>(bits-j-1))&1==1) for j,name in enumerate(inputs)]
  ident=f'y{i}';nodes.append(node(ident,'AND',*refs));outputs[f'Y{i}']=ident
 C.append(circ(f'decoder-{bits}-{width}',f'{bits}:{width} decoder',' '.join(inputs),outputs,nodes,
  [f'Y[i] = 1 exactly when input A = i (0…{width-1}).'],
  ['Active-high one-hot outputs; exactly one output is 1 for every input word.','Input A'+str(bits-1)+' is the most significant bit.']))
for width in (8,16):add_decoder(width)

# Four-bit unsigned magnitude comparator, evaluated from the most-significant bit downward.
cmp_nodes=[]
for i in range(3,-1,-1):cmp_nodes.extend([node(f'e{i}','XNOR',f'A{i}',f'B{i}'),node(f'na{i}','NOT',f'A{i}'),node(f'nb{i}','NOT',f'B{i}')])
cmp_nodes += [node('eq32','AND','e3','e2'),node('eq321','AND','eq32','e1')]
for relation,na,bb in [('l','na','B'),('g','A','nb')]:
 terms=[]
 terms.append(f'{relation}t3')
 cmp_nodes.append(node(f'{relation}t3','AND',f'{na}3' if na=='na' else 'A3',f'{bb}3'))
 cmp_nodes.append(node(f'{relation}t2','AND','e3',f'{na}2' if na=='na' else 'A2',f'{bb}2'))
 cmp_nodes.append(node(f'{relation}t1','AND','eq32',f'{na}1' if na=='na' else 'A1',f'{bb}1'))
 cmp_nodes.append(node(f'{relation}t0','AND','eq321',f'{na}0' if na=='na' else 'A0',f'{bb}0'))
 terms += [f'{relation}t2',f'{relation}t1',f'{relation}t0']
 cmp_nodes.append(node(relation,'OR',*terms))
cmp_nodes.append(node('eq','AND','e3','e2','e1','e0'))
C.append(circ('compare-4','4-bit magnitude comparator','A3 A2 A1 A0 B3 B2 B1 B0',{'LT':'l','EQ':'eq','GT':'g'},cmp_nodes,
 ['LT is true at the first (most-significant) bit where A=0 and B=1.','EQ = (A3 ⊙ B3)(A2 ⊙ B2)(A1 ⊙ B1)(A0 ⊙ B0).','Exactly one of LT, EQ or GT is 1.'],
 ['Unsigned words A=A3A2A1A0 and B=B3B2B1B0 are compared lexicographically from the MSB.','A lower bit matters only when every more-significant bit is equal.']))

C.append(circ('even-parity-gen-3','3-bit even-parity generator','A B C',{'P':'p'},[node('p','XOR','A','B','C')],
 ['P = A ⊕ B ⊕ C'],['Append P to the three data bits.','P is 1 when the data contain an odd number of 1s, making the total number of 1s even.']))
C.append(circ('even-parity-check-4','4-bit even-parity checker','A B C P',{'Even':'ok','Error':'err'},[node('ok','XNOR','A','B','C','P'),node('err','XOR','A','B','C','P')],
 ['Even = (A ⊕ B ⊕ C ⊕ P)′','Error = A ⊕ B ⊕ C ⊕ P'],['Even=1 means the received four-bit word has even parity.','Error=1 means an odd number of 1s was received; this detects, but cannot correct, an odd-bit error.']))

# Common-cathode, active-high 7-segment outputs; invalid BCD words blank all segments.
segment_digits={
 'a':[0,2,3,5,6,7,8,9], 'b':[0,1,2,3,4,7,8,9],
 'c':[0,1,3,4,5,6,7,8,9], 'd':[0,2,3,5,6,8,9],
 'e':[0,2,6,8], 'f':[0,4,5,6,8,9], 'g':[2,3,4,5,6,8,9]
}
bcd_nodes=[node('n'+name,'NOT',name) for name in 'ABCD'];bcd_outputs={}
for segment,digits in segment_digits.items():
 terms=[]
 for digit in digits:
  refs=[lit(name,(digit>>(3-j))&1==1) for j,name in enumerate('ABCD')]
  ident=f'{segment}{digit}';bcd_nodes.append(node(ident,'AND',*refs));terms.append(ident)
 bcd_outputs[segment]=reduce_or(bcd_nodes,terms,f'{segment}or')
C.append(circ('bcd-7seg','BCD-to-seven-segment decoder','A B C D',bcd_outputs,bcd_nodes,
 ['A B C D are 8-4-2-1 BCD inputs.','a…g are active-high segments for a common-cathode display.'],
 ['Digits 0–9 display their decimal glyph; invalid BCD inputs 10–15 turn every segment off.','The model describes logic only; it does not model current limiting, display wiring or physical polarity.']))
def seq(id,title,inputs,width,clocked,rule,lesson):return dict(id=id,title=title,inputs=inputs.split(),width=width,clocked=clocked,rule=rule,lesson=lesson)
S=[seq('SR_NOR','SR latch · NOR','S R',1,False,'10 set · 01 reset · 00 hold · 11 forbidden',['Active-high, level-sensitive, cross-coupled NOR gates.','During S=R=1, both physical outputs are 0. Releasing to 00 leaves memory indeterminate until a valid set/reset.']),seq('SR_NAND','SR latch · NAND','nS nR',1,False,'01 set · 10 reset · 11 hold · 00 forbidden',['Inputs nS/nR are active-low. A leading n means inversion.','During nS=nR=0 both physical outputs are 1; after release the stored state is indeterminate.']),seq('D_LATCH','D latch · enable','D EN',1,False,'EN=1: Q follows D · EN=0: hold',['A transparent latch is NOT an edge-triggered flip-flop.','While EN is high, changing D changes Q immediately. Lower EN to hold the last value.']),seq('D_FF','D flip-flop','D',1,True,'On ↑CLK: Q(next) = D',['A rising-edge flip-flop samples only a 0→1 clock edge.','Changing D while CLK remains high does not update Q. Falling edges do not capture D.']),seq('SR_FF','SR flip-flop','S R',1,True,'On ↑CLK: set / reset / hold; 11 forbidden',['The SR inputs are sampled on rising edges, not continuously.','S=R=1 at a rising edge is forbidden; use a valid set/reset to recover.']),seq('JK_FF','JK flip-flop','J K',1,True,'00 hold · 01 reset · 10 set · 11 toggle',['JK allows the 11 case: it toggles the stored bit.','Q(next) = JQ′ + K′Q on a rising edge. This ideal edge model does not simulate race-around.']),seq('T_FF','T flip-flop','T',1,True,'On ↑CLK: T=0 hold · T=1 toggle',['Q(next) = T ⊕ Q on a rising edge.','Holding CLK high does not repeatedly toggle Q.']),seq('COUNTER_3','3-bit up counter','EN RESET',3,True,'On ↑CLK: RESET wins; EN counts mod 8',['Synchronous active-high RESET takes effect only at a rising edge.','EN=0 holds the count. After 111 the count wraps to 000.']),seq('SHIFT_4','4-bit shift register','D EN RESET',4,True,'On ↑CLK: RESET wins; EN shifts D into Q0',['Shift direction is toward Q3: Q3←Q2←Q1←Q0←D.','The earliest serial bit moves toward the MSB. RESET is synchronous, not asynchronous.']),seq('RING_4','4-bit ring counter','EN RESET',4,True,'On ↑CLK: rotate left · RESET loads 0001',['Starting from 0001: 0001 → 0010 → 0100 → 1000 → 0001.','This demonstration starts and resets one-hot. A real all-zero ring cannot start without initialization.']),seq('JOHNSON_4','4-bit Johnson counter','EN RESET',4,True,'On ↑CLK: shift inverted Q3 into Q0',['Starting from 0000: 0000 → 0001 → 0011 → 0111 → 1111 → 1110 → 1100 → 1000 → 0000.','The four-bit twisted-ring counter has eight states in its initialized cycle. RESET is synchronous.'])]
data=dict(gates=gates,circuits=C,sequential=S)
(root/'design/logic-catalog.json').write_text(json.dumps(data,indent=2)+'\n')
(root/'web/logic-catalog.js').write_text('/* Generated by tools/build-logic-catalog.py. */\n(()=>{const catalog='+json.dumps(data,separators=(',',':'))+';if(typeof module!=="undefined"&&module.exports)module.exports=catalog;if(typeof window!=="undefined")window.RadixLogicCatalog=catalog;})();\n')
def q(s):return json.dumps(s,ensure_ascii=False)
def ls(v):return 'listOf('+', '.join(q(x) for x in v)+')'
out='// Generated by tools/build-logic-catalog.py. Edit its catalog, not this file.\npackage com.radix.app\n\ninternal object LogicCatalog {\n val gates = listOf(\n'
out+=',\n'.join('  LogicEngine.GateInfo(LogicEngine.Gate.'+g['id']+', '+', '.join(q(g[k])for k in ['description','formula','lesson'])+')' for g in gates)+'\n )\n val circuits = listOf(\n'
for j,c in enumerate(C):
 out+='  LogicEngine.Circuit('+q(c['id'])+', '+q(c['title'])+', '+ls(c['inputs'])+', linkedMapOf('+', '.join(q(k)+' to '+q(v)for k,v in c['outputs'].items())+'), listOf(\n'
 out+=',\n'.join('   LogicEngine.Node('+q(n['id'])+', LogicEngine.Gate.'+n['gate']+', '+ls(n['refs'])+')'for n in c['nodes'])+'\n  ), '+ls(c['formula'])+', '+ls(c['lesson'])+')'+(',' if j<len(C)-1 else '')+'\n'
out+=' )\n val sequential = listOf(\n'+',\n'.join('  LogicEngine.Sequential('+q(s['id'])+', '+q(s['title'])+', '+ls(s['inputs'])+', '+str(s['width'])+', '+str(s['clocked']).lower()+', '+q(s['rule'])+', '+ls(s['lesson'])+')'for s in S)+'\n )\n}\n'
(root/'app/src/main/java/com/radix/app/LogicCatalog.kt').write_text(out)
print(f'Generated {len(G)} gates, {len(C)} combinational circuits and {len(S)} sequential circuits for Kotlin and JS.')
