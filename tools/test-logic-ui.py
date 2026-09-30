#!/usr/bin/env python3
"""Browser companion logic/mobile/visual-guide QA. Not Android device runtime testing."""
from pathlib import Path
import json
from playwright.sync_api import sync_playwright, expect
root=Path(__file__).resolve().parent.parent
checks=[]
def passed(name):checks.append(name);print('PASS:',name,flush=True)
with sync_playwright() as p:
    b=p.chromium.launch(args=['--no-sandbox','--disable-dev-shm-usage'])
    ctx=b.new_context(viewport={'width':1440,'height':1000},permissions=['clipboard-read','clipboard-write'])
    page=ctx.new_page();errors=[];page.on('pageerror',lambda e:errors.append(str(e)))
    page.goto('http://127.0.0.1:3000/',wait_until='networkidle')
    page.locator('.topnav [data-tab="logic"]').click()
    def section(name):page.locator(f'[data-logic="section"][data-section="{name}"]').click()
    def choose(name):page.locator('#logic-choice-select').select_option(name)
    def vals():return page.locator('#logic-outputs strong').all_text_contents()
    def inputs(v):
        for i,value in enumerate(v):
            e=page.locator(f'[data-logic="input"][data-index="{i}"]')
            if e.get_attribute('aria-pressed')!=str(bool(value)).lower():e.click()
    def pulse():page.locator('[data-logic="pulse"]').click()
    def copied_rows():
        page.locator('[data-logic="copy"]').click()
        return page.evaluate('navigator.clipboard.readText()').splitlines()[2:]
    gates={'AND':['0','0','0','1'],'OR':['0','1','1','1'],'NOT':['1','0'],'NAND':['1','1','1','0'],'NOR':['1','0','0','0'],'XOR':['0','1','1','0'],'XNOR':['1','0','0','1'],'BUFFER':['0','1']}
    for name,expected in gates.items():
        choose(name)
        if name not in ['NOT','BUFFER']:page.locator('[data-logic="arity"][data-n="2"]').click()
        actual=page.locator('.truth-table tbody tr').evaluate_all('(rows)=>rows.map(r=>r.cells[r.cells.length-1].textContent)')
        assert actual==expected,(name,actual)
        expect(page.locator('#logic-diagram svg')).to_have_attribute('role','img')
        expect(page.locator('#logic-diagram title')).to_contain_text(name)
    passed('All eight gates expose independently expected truth tables and labeled symbols')
    choose('XOR');page.locator('[data-logic="arity"][data-n="3"]').click();inputs([1,1,1]);assert vals()==['1']
    expect(page.locator('.truth-table tbody tr')).to_have_count(8)
    choose('XNOR');page.locator('[data-logic="arity"][data-n="4"]').click();inputs([1,1,1,1]);assert vals()==['1']
    expect(page.locator('.truth-table tbody tr')).to_have_count(16)
    expect(page.locator('.logic-explanation')).to_contain_text('EVEN parity')
    passed('Three/four-input XOR/XNOR teach parity rather than the exactly-one/equal-all misconception')
    choose('AND');page.locator('[data-logic="arity"][data-n="2"]').click();inputs([1,0]);assert vals()==['0']
    expect(page.locator('.truth-table tr.current')).to_have_count(1)
    assert page.locator('.truth-table tr.current').inner_text().split()==['1','0','0']
    rows=copied_rows();assert len(rows)==4 and rows[-1]=='1\t1\t1'
    passed('Input switches update outputs/current row and complete truth-table copy')
    page.locator('[data-logic="kmap"]').click();expect(page.locator('#k-expression')).to_have_text('AB')
    passed('Gate truth table transfers faithfully to a two-variable K-map')
    page.locator('.topnav [data-tab="logic"]').click();section('combinational')
    circuits=page.evaluate('RadixLogic.catalog.circuits.map(c=>({id:c.id,inputs:c.inputs.length}))')
    for c in circuits:
        choose(c['id']);rows=copied_rows();assert len(rows)==2**c['inputs'],c
        expect(page.locator('#logic-diagram svg')).to_have_attribute('role','img')
        page.locator('[data-logic="diagram"]').click()
        expect(page.locator('#logic-diagram')).to_have_class('diagram-pan expanded')
        assert 'gate-level' in page.locator('#logic-diagram svg').get_attribute('aria-label')
        assert page.evaluate('document.documentElement.scrollWidth<=innerWidth')
        page.locator('[data-logic="diagram"]').click()
    passed('All 12 combinational circuits show complete tables, block views and gate-level netlists')
    choose('full-adder');inputs([1,1,1]);assert vals()==['1','1']
    choose('full-subtractor');inputs([0,1,1]);assert vals()==['0','1']
    passed('Carry and borrow outputs correctly represent three-input arithmetic')
    choose('mux-4');inputs([0,0,1,0,1,0]);assert vals()==['1']
    inputs([1,1,1,1,1,0]);assert vals()==['1']
    inputs([1,1,0,1,1,0]);assert vals()==['0']
    choose('demux-2');inputs([1,1]);assert vals()==['0','1']
    passed('MUX selects the correct data bit and DEMUX routes data to only the selected output')
    choose('decoder-2');inputs([1,0,1]);assert vals()==['0','0','1','0'];inputs([1,0,0]);assert vals()==['0','0','0','0']
    choose('encoder-4');inputs([1,1,1,1]);assert vals()==['1','1','1'];inputs([0,0,0,1]);assert vals()==['0','0','1'];inputs([0,0,0,0]);assert vals()==['0','0','0']
    passed('Enable, one-hot decoding, encoder priority and Valid distinction work correctly')
    choose('compare-2');inputs([1,0,0,1]);assert vals()==['0','0','1'];inputs([1,0,1,0]);assert vals()==['0','1','0']
    passed('Magnitude comparator honors MSB priority and produces mutually exclusive results')
    choose('ripple-4');inputs([1,1,1,1,0,0,0,1,1]);assert vals()==['0','0','0','1','1']
    assert len(copied_rows())==512
    expect(page.locator('.table-pager .mono')).to_contain_text('/32')
    page.locator('[data-logic="next"]').click();page.locator('[data-logic="previous"]').click()
    page.locator('[data-logic="diagram"]').click()
    assert page.locator('#logic-diagram').evaluate('(el)=>el.scrollWidth>el.clientWidth')
    page.screenshot(path=str(root/'design/logic-ripple-desktop.png'),full_page=True)
    passed('Ripple adder carries correctly, paginates 512 rows and locally pans its readable implementation')
    choose('full-adder');page.locator('#logic-output-select').select_option('Cout');page.locator('[data-logic="kmap"]').click()
    expect(page.locator('#k-grid .k-cell')).to_have_count(8)
    actual=page.locator('#k-grid .k-cell').evaluate_all('(els)=>els.sort((a,b)=>a.dataset.index-b.dataset.index).map(e=>Number(e.querySelector("strong").textContent))')
    assert actual==[0,0,0,1,0,1,1,1]
    passed('Multi-output circuit K-map bridge selects the requested function, not a different output')
    page.locator('.topnav [data-tab="logic"]').click();section('sequential')
    models=page.evaluate('RadixLogic.catalog.sequential.map(s=>({id:s.id,clocked:s.clocked,rows:2**(s.inputs.length+s.width)}))')
    for c in models:
        choose(c['id']);assert len(copied_rows())==c['rows'],c
        expect(page.locator('#logic-clock')).to_have_count(1 if c['clocked'] else 0)
        expect(page.locator('#logic-timing svg')).to_have_attribute('aria-label','Discrete timing trace')
        expect(page.locator('#logic-diagram svg')).to_have_attribute('role','img')
    passed('All 11 sequential models expose complete next-state tables, appropriate clocks and timing diagrams')
    choose('D_FF');assert vals()==['0','1'];page.locator('[data-logic="rise"]').click();assert vals()==['1','0']
    inputs([0]);assert vals()==['1','0'];page.locator('[data-logic="fall"]').click();assert vals()==['1','0'];page.locator('[data-logic="rise"]').click();assert vals()==['0','1']
    expect(page.locator('[data-logic="rise"]')).to_be_disabled()
    passed('D flip-flop captures only rising edges, not input changes while high or falling edges')
    choose('D_LATCH');assert vals()==['0','1'];inputs([1,1]);assert vals()==['1','0'];inputs([0,1]);assert vals()==['0','1'];inputs([0,0]);inputs([1,0]);assert vals()==['0','1']
    passed('D latch is transparent while enabled and holds after enable drops')
    choose('JK_FF');pulse();assert vals()==['1','0'];pulse();assert vals()==['0','1'];inputs([1,0]);pulse();assert vals()==['1','0'];inputs([0,0]);pulse();assert vals()==['1','0'];inputs([0,1]);pulse();assert vals()==['0','1']
    choose('T_FF');pulse();assert vals()==['1','0'];pulse();assert vals()==['0','1'];inputs([0]);pulse();assert vals()==['0','1']
    passed('JK hold/reset/set/toggle and T hold/toggle transitions are correct')
    choose('SR_NOR');inputs([1,1]);assert vals()==['0','0'];expect(page.locator('.logic-error')).to_contain_text('indeterminate');page.locator('[data-logic="release"]').click();assert vals()==['X','X'];inputs([1,0]);assert vals()==['1','0'];page.locator('[data-logic="diagram"]').click()
    expect(page.locator('#logic-diagram svg')).to_have_attribute('aria-label','SR latch · NOR cross-coupled diagram')
    choose('SR_NAND');inputs([0,0]);assert vals()==['1','1'];page.locator('[data-logic="release"]').click();assert vals()==['X','X'];inputs([0,1]);assert vals()==['1','0']
    passed('NOR/NAND forbidden latch inputs show equal driven outputs and unknown release, with recovery')
    choose('SR_FF');inputs([1,1]);assert vals()==['0','1'];pulse();assert vals()==['X','X'];inputs([0,1]);pulse();assert vals()==['0','1']
    passed('Clocked SR forbidden inputs affect stored state only on an edge')
    choose('COUNTER_3')
    for i in range(8):pulse();assert ''.join(vals())==f'{(i+1)%8:03b}'
    pulse();inputs([0,0]);pulse();assert ''.join(vals())=='001';inputs([0,1]);assert ''.join(vals())=='001';pulse();assert ''.join(vals())=='000'
    passed('Counter wraps 7→0, holds with EN=0 and resets synchronously rather than immediately')
    choose('SHIFT_4')
    for bit in [1,0,1,1]:inputs([bit,1,0]);pulse()
    assert ''.join(vals())=='1011';inputs([0,0,0]);pulse();assert ''.join(vals())=='1011';inputs([0,0,1]);assert ''.join(vals())=='1011';pulse();assert ''.join(vals())=='0000'
    passed('Serial register shifts toward Q3, inserts at Q0 and honors enable/reset priority')
    for name,cycle in [('RING_4',['0001','0010','0100','1000','0001']),('JOHNSON_4',['0000','0001','0011','0111','1111','1110','1100','1000','0000'])]:
        choose(name);assert ''.join(vals())==cycle[0]
        for out in cycle[1:]:pulse();assert ''.join(vals())==out
    passed('Ring and Johnson counters follow their initialized four/eight-state cycles')
    for _ in range(9):pulse()
    expect(page.locator('#logic-timing text[y="15"]')).to_have_count(12)
    passed('Interactive timing history stays bounded to the latest 12 samples after repeated clocks')
    expect(page.locator('#logic-timing')).to_contain_text('not elapsed time')
    assert page.locator('#logic-timing svg').evaluate('(el)=>el.querySelectorAll("text").length>10')
    page.screenshot(path=str(root/'design/logic-sequential-desktop.png'),full_page=True)
    page.reload(wait_until='networkidle');page.locator('.topnav [data-tab="logic"]').click()
    expect(page.locator('#logic-choice-select')).to_have_value('JOHNSON_4');assert ''.join(vals())=='0000'
    passed('Library choices persist across reload while simulation restarts in a documented teaching state')
    page.locator('#visual-help-button').click();expect(page.locator('.helper-card')).to_have_count(11)
    assert page.locator('.helper-card svg').count()>=3
    page.screenshot(path=str(root/'design/logic-helper-desktop.png'))
    page.locator('[data-action="close-dialog"]').click()
    passed('Illustrated helper visually explains conversions, K-maps, gates, circuits and memory')

    # Fresh touch context; no desktop selection leakage.
    mctx=b.new_context(viewport={'width':390,'height':844},device_scale_factor=2,is_mobile=True,has_touch=True)
    phone=mctx.new_page();phone.on('pageerror',lambda e:errors.append(str(e)))
    phone.goto('http://127.0.0.1:3000/',wait_until='networkidle');phone.locator('.mobile-nav [data-tab="logic"]').click()
    phone.screenshot(path=str(root/'design/logic-gates-phone-dark.png'),full_page=True)
    phone.locator('[data-logic="section"][data-section="combinational"]').click();phone.locator('#logic-choice-select').select_option('full-adder')
    phone.screenshot(path=str(root/'design/logic-combinational-phone.png'),full_page=True)
    phone.locator('[data-logic="section"][data-section="sequential"]').click();phone.locator('#logic-choice-select').select_option('JK_FF');phone.locator('[data-logic="pulse"]').click()
    phone.screenshot(path=str(root/'design/logic-sequential-phone-dark.png'),full_page=True)
    phone.locator('#theme-toggle').click();phone.screenshot(path=str(root/'design/logic-sequential-phone-light.png'),full_page=True)
    for width,height in [(320,640),(390,520),(390,844),(844,390),(768,900),(1440,1000)]:
        phone.set_viewport_size({'width':width,'height':height})
        for category in ['gates','combinational','sequential']:
            phone.locator(f'[data-logic="section"][data-section="{category}"]').click()
            assert phone.evaluate('document.documentElement.scrollWidth<=innerWidth'),(category,width)
            rect=phone.locator('.logic-bit').first.bounding_box();assert rect['width']>=44 and rect['height']>=48,rect
    passed('All three logic sections fit 320/390px phones, short viewports, tablets and desktop with touch-sized switches')
    phone.set_viewport_size({'width':320,'height':640});phone.locator('[data-logic="section"][data-section="combinational"]').click();phone.locator('#logic-choice-select').select_option('ripple-4');phone.locator('[data-logic="diagram"]').click()
    assert phone.locator('#logic-diagram').evaluate('(el)=>el.scrollWidth>el.clientWidth')
    assert phone.evaluate('document.documentElement.scrollWidth<=innerWidth')
    expect(phone.locator('.truth-table tbody tr')).to_have_count(16)
    passed('Wide circuit schematics and tables scroll inside their cards, never widening the phone page')
    phone.set_viewport_size({'width':390,'height':844});phone.locator('#visual-help-button').click();phone.locator('[data-guide="start"]').click()
    for i in range(12):
        expect(phone.locator('.tour-card .section-label')).to_contain_text(f'{i+1}/12')
        assert phone.locator('.tour-card').evaluate('(el)=>{const r=el.getBoundingClientRect();return r.left>=0&&r.right<=innerWidth&&r.bottom<=innerHeight}'),i
        assert phone.evaluate('document.documentElement.scrollWidth<=innerWidth')
        if i==5:phone.screenshot(path=str(root/'design/logic-tour-phone.png'))
        if i==8:
            phone.locator('[data-guide="back"]').click();expect(phone.locator('.tour-card .section-label')).to_contain_text('8/12');phone.locator('[data-guide="next"]').click();expect(phone.locator('.tour-card .section-label')).to_contain_text('9/12')
        phone.locator('[data-guide="next"]').click()
    expect(phone.locator('.visual-tour')).to_have_count(0)
    expect(phone.locator('#logic-choice-select')).to_have_value('ripple-4')
    passed('All 12 visual-tour steps highlight real controls, fit touch viewports and restore the original route/selection')
    phone.locator('#visual-help-button').click();phone.locator('[data-guide="start"]').click();expect(phone.locator('.tour-card')).to_be_visible();phone.keyboard.press('Escape')
    expect(phone.locator('.visual-tour')).to_have_count(0);expect(phone.locator('#visual-help-button')).to_be_focused()
    passed('Tour can be replayed, dismissed with Escape and returns keyboard focus safely')
    for width,height in [(320,640),(844,390)]:
        phone.set_viewport_size({'width':width,'height':height})
        phone.locator('#visual-help-button').click();phone.locator('[data-guide="start"]').click()
        for i in range(12):
            assert phone.locator('.tour-card').evaluate('(el)=>{const r=el.getBoundingClientRect();return r.left>=0&&r.right<=innerWidth&&r.top>=0&&r.bottom<=innerHeight}'),(width,i)
            assert phone.evaluate('document.documentElement.scrollWidth<=innerWidth')
            phone.locator('[data-guide="next"]').click()
    passed('Visual tour remains contained through all 12 steps on 320px phones and 844×390 landscape')
    phone.set_viewport_size({'width':390,'height':844})
    phone.locator('.mobile-nav [data-tab="more"]').click()
    expect(phone.locator('.more-card')).to_have_count(8)
    phone.locator('.more-card[data-tab="saved"]').click();expect(phone.locator('.library-back')).to_be_visible();phone.locator('.library-back').click();phone.locator('.more-card[data-tab="lab"]').click();expect(phone.locator('#bit-buttons')).to_be_visible()
    passed('Four-item mobile navigation keeps Saved, Bit Lab and Help reachable through More')

    embedded=ctx.new_page();embedded.on('pageerror',lambda e:errors.append(str(e)))
    embedded.set_content('<iframe id="app" sandbox="allow-scripts" style="width:100%;height:900px;border:0"></iframe>')
    embedded.eval_on_selector('#app','(el,html)=>el.srcdoc=html',(root/'web/index.html').read_text());f=embedded.frame_locator('#app')
    f.locator('.topnav [data-tab="logic"]').click();expect(f.locator('#logic-outputs strong')).to_have_text('1')
    f.locator('[data-logic="section"][data-section="sequential"]').click();f.locator('[data-logic="pulse"]').click();assert f.locator('#logic-outputs strong').all_text_contents()==['1','0']
    f.locator('#visual-help-button').click();f.locator('[data-guide="start"]').click();expect(f.locator('.tour-card')).to_be_visible();f.locator('[data-guide="skip"]').click()
    passed('Logic models, diagrams and visual tour run in the offline opaque-origin file viewer')
    assert not errors,errors;passed('No logic/guide JavaScript runtime errors')
    b.close()
report={'status':'pass','checks':len(checks),'tests':checks,'gatesInteractivelyChecked':8,'combinationalCircuitsChecked':12,'sequentialModelsChecked':11,'visualTourStepsChecked':12,'scope':'Chromium browser companion; not Android device runtime'}
(root/'design/logic-ui-results.json').write_text(json.dumps(report,indent=2)+'\n')
print(f'PASS: {len(checks)} logic/mobile/guide UX checks.',flush=True)
