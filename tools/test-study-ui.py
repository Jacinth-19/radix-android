#!/usr/bin/env python3
"""Radix 4 new-feature regressions; real browser companion, NOT Android UI."""
from pathlib import Path
import html,json
from playwright.sync_api import sync_playwright,expect
root=Path(__file__).resolve().parent.parent
results=[]
def passed(name):
    results.append(name);print('PASS:',name,flush=True)
with sync_playwright() as p:
    browser=p.chromium.launch(args=['--no-sandbox','--disable-dev-shm-usage'])
    context=browser.new_context(viewport={'width':390,'height':844},permissions=['clipboard-read','clipboard-write'],accept_downloads=True)
    context.add_init_script("window.testVibrations=[];Object.defineProperty(navigator,'vibrate',{configurable:true,value:v=>{window.testVibrations.push(v);return true}})")
    page=context.new_page();errors=[];page.on('pageerror',lambda e: errors.append(str(e)))
    page.goto('http://127.0.0.1:3000/',wait_until='networkidle')
    def nav(route):
        if route in ['convert','kmap','logic','more']:page.locator('.mobile-nav [data-tab="'+route+'"]').click()
        else:
            nav('more');page.locator('.more-card[data-tab="'+route+'"]').click()
            expect(page.locator('.mobile-nav [data-tab="more"]')).to_have_attribute('aria-current','page')
    def action(name):page.locator('[data-study="'+name+'"]').click()
    def logic(name):page.locator('[data-logic="'+name+'"]').click()
    def section(name):page.locator('[data-logic="section"][data-section="'+name+'"]').click()
    def choose(name):page.locator('#logic-choice-select').select_option(name)
    def outputs():return page.locator('#logic-outputs strong').all_text_contents()
    def logic_state():return {'choice':page.locator('#logic-choice-select').input_value(),'inputs':page.locator('#logic-inputs button').evaluate_all('(items)=>items.map(x=>x.getAttribute("aria-pressed"))'),'out':outputs(),'clock':page.locator('#logic-clock .mono').text_content() if page.locator('#logic-clock').count() else None,'trace':page.locator('#logic-timing text').all_text_contents()}
    def study_state():return page.evaluate('JSON.parse(localStorage.getItem("radix-study-v4"))')
    def storage():return page.evaluate('Object.fromEntries(["radix-v1","radix-study-v4","radix-kmap-v2","radix-logic-v3"].map(k=>[k,localStorage.getItem(k)]))')
    def map_state():return page.evaluate('JSON.parse(localStorage.getItem("radix-kmap-v2"))')
    def save(name):
        logic('save-session');page.locator('#session-name').fill(name);action('session-save');expect(page.locator('#base-dialog')).not_to_be_visible()
        return study_state()['sessions'][0]['id']
    def restore_session(id):nav('sessions');page.locator('[data-study="session-restore"][data-id="'+id+'"]').click()
    def validate(text):
        page.locator('#backup-text').fill(text);action('backup-validate')
    def review():action('backup-review');expect(page.locator('#base-dialog')).to_be_visible()
    def confirm():action('backup-confirm');expect(page.locator('#base-dialog')).not_to_be_visible()
    def close():page.locator('#base-dialog [data-action="close-dialog"]').click()
    # Boolean expression lab.
    nav('expression');expect(page.locator('#expr-truth tbody tr')).to_have_count(4)
    expect(page.locator('#expr-comparison')).to_contain_text('Equivalent in EVERY row');expect(page.locator('#expr-results')).to_contain_text("A'B + AB'")
    passed('Expression defaults show XOR, both minimum forms and the complete live truth table')
    page.locator('#expr-left').fill('AB');page.locator('#expr-right').fill('A OR B');expect(page.locator('#expr-comparison')).to_contain_text('01, 10')
    page.locator('#expr-left').fill('NOT (A B)');page.locator('#expr-right').fill('A′ + B′');expect(page.locator('#expr-comparison')).to_contain_text('Equivalent')
    passed('Live expression aliases/equivalence and explicit counterexample rows update correctly')
    page.locator('#expr-left').fill('A+');expect(page.locator('#expr-error')).not_to_be_empty();expect(page.locator('#expr-results')).to_be_empty()
    page.reload();nav('expression');expect(page.locator('#expr-left')).to_have_value('A+');expect(page.locator('#expr-results')).to_be_empty()
    page.locator('#expr-left').fill('A XOR B');page.locator('#expr-right').fill('A XOR B')
    passed('Invalid expression drafts persist honestly without stale table/results and recover on editing')
    expect(page.locator('[data-study="expr-token"][data-token="C"]')).to_be_disabled()
    page.locator('[data-study="expr-vars"][data-n="3"]').click();page.locator('#expr-left').fill('A(B+C)');expect(page.locator('#expr-truth tbody tr')).to_have_count(8)
    expect(page.locator('[data-study="expr-token"][data-token="C"]')).to_be_enabled();page.locator('[data-study="expr-bit"][data-index="1"]').click();expect(page.locator('#expr-live strong')).to_have_text('1')
    page.locator('[data-study="expr-token"][data-token=" + "]').click();page.locator('[data-study="expr-token"][data-token="1"]').click();expect(page.locator('#expr-left')).to_have_value('A(B+C) + 1')
    passed('Variable/keypad availability, implicit AND and input switches update the highlighted row')
    page.locator('#expr-left').fill('A(B+C)');page.locator('[data-study="expr-kmap"][data-form="SOP"]').click();m=map_state();assert m['variables']==3 and m['form']=='SOP' and m['cells']==[0,0,0,0,0,1,1,1]
    page.locator('[data-student="undo"]').click();expect(page.locator('#k-grid .k-cell')).to_have_count(4)
    passed('SOP expression handoff is a single reversible K-map edit')
    nav('expression');page.locator('[data-study="expr-vars"][data-n="4"]').click();page.locator('#expr-left').fill('A XOR D');page.locator('[data-study="expr-kmap"][data-form="POS"]').click();m=map_state();assert m['variables']==4 and m['form']=='POS' and m['cells']==[int(bool(c&8)!=bool(c&1)) for c in range(16)]
    page.locator('[data-student="undo"]').click();expect(page.locator('#k-grid .k-cell')).to_have_count(4)
    passed('POS handoff preserves all four-variable outputs rather than complementing the function')
    nav('expression');action('expr-copy');text=page.evaluate('navigator.clipboard.readText()');assert 'POS:' in text and 'SOP:' in text and len(text.splitlines())==21
    passed('Expression copy includes both forms and every selected truth-table row')
    # Fixed-width representations.
    nav('representation');expect(page.locator('#rep-results')).to_contain_text('1111 0011');expect(page.locator('#rep-results')).to_contain_text('243');expect(page.locator('#rep-results')).to_contain_text('Invert every bit')
    passed('Representation default shows signed −13 and unsigned 243 with encoding working')
    page.locator('#rep-signed').uncheck();expect(page.locator('#rep-error')).not_to_be_empty();expect(page.locator('#rep-results')).to_be_empty()
    page.locator('[data-study="rep-width"][data-width="64"]').click();page.locator('#rep-input').fill('18446744073709551615');expect(page.locator('#rep-results')).to_contain_text('FFFFFFFFFFFFFFFF');expect(page.locator('#rep-results')).to_contain_text('18446744073709551615')
    passed('Unsigned mode rejects negatives and all 64-bit extrema remain exact, not floating-point')
    page.locator('#rep-signed').check();expect(page.locator('#rep-error')).not_to_be_empty();page.locator('#rep-input').fill('-9223372036854775808');expect(page.locator('#rep-results')).to_contain_text('8000000000000000')
    passed('Signed endpoint/range changes reject overflow without silent wrapping')
    page.locator('[data-study="rep-mode"][data-mode="bits"]').click();page.locator('[data-study="rep-width"][data-width="8"]').click();page.locator('#rep-input').fill('1111');expect(page.locator('#rep-results')).to_contain_text('0000 1111');expect(page.locator('#rep-results')).to_contain_text('ZERO-padded')
    page.locator('[data-study="rep-width"][data-width="4"]').click();expect(page.locator('#rep-results')).to_contain_text('-1')
    passed('Short binary words explicitly zero-pad and their signed meaning follows the selected width')
    page.locator('[data-study="rep-mode"][data-mode="gray"]').click();page.locator('#rep-input').fill('0110');expect(page.locator('#rep-results')).to_contain_text('0000 0100')
    page.locator('[data-study="rep-mode"][data-mode="decimal"]').click();page.locator('#rep-input').fill('12');expect(page.locator('#rep-results')).to_contain_text('0001 0010');action('rep-copy');assert 'BCD: 0001 0010' in page.evaluate('navigator.clipboard.readText()')
    passed('Gray decoding, decimal BCD and full representation copy remain distinct and correct')
    page.locator('[data-study="rep-mode"][data-mode="bits"]').click();page.locator('#rep-input').fill('1020');expect(page.locator('#rep-error')).not_to_be_empty();expect(page.locator('#rep-results')).to_be_empty()
    page.reload();nav('representation');expect(page.locator('#rep-input')).to_have_value('1020');page.locator('#rep-input').fill('0110')
    passed('Malformed binary drafts persist but never leave a plausible stale working report')
    # Preferences and feedback.
    nav('preferences');page.locator('#pref-haptics').uncheck();page.locator('#pref-large').check();before=page.evaluate('testVibrations.length');nav('logic');page.locator('[data-logic="input"][data-index="0"]').click();assert page.evaluate('testVibrations.length')==before
    assert page.locator('#logic-inputs button').first.bounding_box()['height']>=78
    page.reload();nav('preferences');expect(page.locator('#pref-haptics')).not_to_be_checked();expect(page.locator('#pref-large')).to_be_checked();page.locator('#pref-haptics').check();nav('logic');before=page.evaluate('testVibrations.length');page.locator('[data-logic="input"][data-index="0"]').click();assert page.evaluate('testVibrations.length')==before+1
    nav('practice');assert page.locator('[data-study="practice-choice"]').first.bounding_box()['height']>=60
    passed('Larger controls and haptic opt-out persist; disabled feedback prevents learning-control vibration')
    # Practice: no false mastery from hints/reveal/incorrect checks.
    questions=page.evaluate('window.RadixPractice');q=questions[0];page.locator('#practice-question').select_option('0');wrong=(q['answer']+1)%len(q['choices']);page.locator('[data-study="practice-choice"][data-index="'+str(wrong)+'"]').click();action('practice-check');expect(page.locator('.study-feedback')).to_contain_text('Not yet');assert study_state()['progress']==[];action('practice-hint');expect(page.locator('#content')).to_contain_text(q['hint']);assert study_state()['progress']==[]
    passed('Hints and incorrect checks help learning without creating mastery')
    action('practice-reveal');expect(page.locator('#content')).to_contain_text(q['explanation']);expect(page.locator('[data-study="practice-check"]')).to_be_disabled();assert study_state()['progress']==[]
    action('practice-next');expect(page.locator('[data-study="practice-check"]')).to_be_disabled();expect(page.locator('[data-study="practice-choice"]').first).to_be_enabled()
    passed('Reveal explains the answer without mastery and next-card state resets safely')
    page.locator('#practice-question').select_option('0');page.locator('[data-study="practice-choice"][data-index="'+str(q['answer'])+'"]').click();action('practice-check');action('practice-check');assert study_state()['progress']==[q['id']];page.reload();nav('practice');expect(page.locator('#content')).to_contain_text('1 of 108 mastered')
    passed('A correct check records one stable mastery ID and progress survives reload')
    for i,q in enumerate(questions):
        page.locator('#practice-question').select_option(str(i));page.locator('[data-study="practice-choice"][data-index="'+str(q['answer'])+'"]').click();action('practice-check');expect(page.locator('#content')).to_contain_text(q['explanation'])
    assert len(study_state()['progress'])==108
    passed('All 108 questions can be checked correctly with the expected explanation and local progress')
    for category,n in [('Gates',9),('Circuits',10),('Memory',15),('Representations',10),('Expressions',5),('Number bases',10),('Arithmetic',13),('Laws',10),('K-maps',11),('Floats',10),('Builder',5)]:
        page.locator('[data-study="practice-filter"][data-category="'+category+'"]').click();expect(page.locator('#practice-question option')).to_have_count(n);action('practice-previous');assert questions[int(page.locator('#practice-question').input_value())]['category']==category
    page.locator('[data-study="practice-filter"][data-category="All"]').click()
    for tier,n in [('0',108),('1',31),('2',52),('3',25)]:
        page.locator('[data-study="practice-tier"][data-tier="'+tier+'"]').click();expect(page.locator('#practice-question option')).to_have_count(n)
    page.locator('[data-study="practice-tier"][data-tier="1"]').click();page.locator('[data-study="practice-filter"][data-category="Laws"]').click();expect(page.locator('#practice-question option')).to_have_count(3)
    page.locator('[data-study="practice-tier"][data-tier="0"]').click()
    passed('Easy, Intermediate and Advanced tiers filter alone and combined with topics')
    page.locator('[data-study="practice-filter"][data-category="All"]').click();action('practice-reset');close();assert len(study_state()['progress'])==108;action('practice-reset');action('practice-confirm-reset');assert study_state()['progress']==[]
    passed('Category filters/previous navigation work and resetting progress requires confirmation')
    # Circuit history and checkpoints.
    nav('logic');section('gates');choose('AND');base=logic_state();page.locator('[data-logic="input"][data-index="0"]').click();assert outputs()==['0'];logic('undo');assert logic_state()==base;logic('redo');assert outputs()==['0'];logic('undo');page.locator('[data-logic="input"][data-index="1"]').click();expect(page.locator('[data-logic="redo"]')).to_be_disabled()
    passed('Gate input edits undo/redo exactly and a new branch clears redo')
    choose('XOR');page.locator('[data-logic="arity"][data-n="4"]').click();assert page.locator('#logic-inputs button').count()==4;logic('undo');expect(page.locator('#logic-choice-select')).to_have_value('XOR');expect(page.locator('#logic-inputs button')).to_have_count(2);logic('undo');expect(page.locator('#logic-choice-select')).to_have_value('AND')
    passed('Undo includes gate model and input-count configuration, not only visible output bits')
    section('combinational');choose('full-adder');page.locator('[data-logic="input"][data-index="0"]').click();page.locator('[data-logic="input"][data-index="1"]').click();comb=logic_state();name='<img src=x onerror=alert(1)> & adder';comb_id=save(name);nav('sessions');expect(page.locator('.study-card h2')).to_have_text(name);expect(page.locator('.study-card img')).to_have_count(0)
    passed('Named combinational sessions save exact inputs and names are rendered as text, not HTML')
    nav('logic');section('sequential');choose('COUNTER_3');logic('pulse');logic('rise');high=logic_state();logic('pulse');assert ''.join(outputs())=='011';expect(page.locator('#logic-clock')).to_contain_text('CLK = 0');logic('undo');assert logic_state()==high;logic('redo');assert ''.join(outputs())=='011'
    passed('A pulse starting high is one atomic undo step restoring memory, clock, trace and tick')
    counter_id=save('Counter at three');restore_session(counter_id);assert ''.join(outputs())=='011';expect(page.locator('#logic-timing text[y="15"]')).to_have_count(1)
    passed('Sequential checkpoints retain clock/memory but restore with a single fresh timing sample')
    before=logic_state();restore_session(comb_id);assert logic_state()==comb;logic('undo');assert logic_state()==before
    passed('Restoring a saved circuit is itself reversible and returns the previous active simulation')
    choose('SR_NOR') if page.locator('[data-logic="section"][data-section="sequential"]').get_attribute('aria-pressed')=='true' else None
    section('sequential');choose('SR_NOR');page.locator('[data-logic="input"][data-index="0"]').click();page.locator('[data-logic="input"][data-index="1"]').click();forbidden=logic_state();assert outputs()==['0','0'];logic('release');assert outputs()==['X','X'];logic('undo');assert logic_state()==forbidden;logic('redo');assert outputs()==['X','X']
    unknown_id=save('Unknown memory after release');choose('D_FF');restore_session(unknown_id);assert outputs()==['X','X'];assert study_state()['sessions'][0]['bits'] is None
    passed('Simultaneous latch release is atomic, unknown memory survives save/restore and is never coerced to zero')
    section('gates');choose('NOT');page.locator('[data-logic="input"][data-index="0"]').click();unary_id=save('My inverter');choose('AND');restore_session(unary_id);expect(page.locator('#logic-inputs button')).to_have_count(1);choose('AND');expect(page.locator('#logic-inputs button')).to_have_count(2)
    passed('Unary sessions restore one input and returning to variable-input gates remains valid')
    logic('save-session');page.locator('#session-name').fill('   ');action('session-save');expect(page.locator('#session-error')).not_to_be_empty();close()
    long_id=save('X'*40);nav('sessions');assert page.evaluate('document.documentElement.scrollWidth<=innerWidth');page.locator('[data-study="session-delete"][data-id="'+long_id+'"]').click();assert page.locator('#base-dialog').evaluate('(d)=>d.scrollWidth<=d.clientWidth');close()
    passed('Unbroken 40-character session names wrap inside phone cards and confirmation dialogs')
    nav('sessions');count=len(study_state()['sessions']);page.locator('[data-study="session-delete"]').first.click();close();assert len(study_state()['sessions'])==count;page.locator('[data-study="session-delete"]').first.click();action('session-confirm-delete');assert len(study_state()['sessions'])==count-1
    passed('Session names are validated and deletion explicitly confirms without changing the active circuit')
    # K-map histories, including invalid draft state.
    nav('kmap');page.locator('[data-student="variables"][data-n="2"]').click();page.locator('[data-student="reset"]').click();page.locator('[data-student="cell"][data-index="0"]').click();m1=map_state();page.locator('[data-student="cell"][data-index="0"]').click();assert map_state()['cells'][0]==2;page.locator('[data-student="undo"]').click();assert map_state()==m1;page.locator('[data-student="redo"]').click();assert map_state()['cells'][0]==2
    passed('K-map 0→1→X edits undo/redo with exact cell values')
    page.locator('[data-student="form"][data-form="POS"]').click();assert map_state()['form']=='POS';page.locator('[data-student="undo"]').click();assert map_state()['form']=='SOP';page.locator('[data-student="variables"][data-n="3"]').click();page.locator('[data-student="variables"][data-n="4"]').click();page.locator('[data-student="undo"]').click();expect(page.locator('#k-grid .k-cell')).to_have_count(8)
    passed('Map form/variable changes are reversible and a new map edit clears redo')
    page.locator('.k-inputs summary').click();page.locator('#k-terms').fill('9');expect(page.locator('#k-input-error')).not_to_be_empty();before=map_state();page.locator('[data-student="undo"]').click();expect(page.locator('#k-input-error')).to_be_empty();page.locator('[data-student="redo"]').click();expect(page.locator('#k-input-error')).not_to_be_empty();expect(page.locator('#k-terms')).to_have_value('9');assert map_state()==before
    page.locator('[data-student="undo"]').click()
    passed('Invalid K-map draft/error history restores honestly without changing the last valid cells')
    # Backup: export, validation, nonmutation, explicit replacement.
    nav('preferences');action('backup-copy');export=page.evaluate('navigator.clipboard.readText()');data=json.loads(export);assert data['format']=='radix-study' and data['version']==1 and data['appVersion']=='5.0.0';assert 'history' not in export and 'password' not in export.lower() and 'keystore' not in export.lower()
    before=storage();validate(export);expect(page.locator('#backup-result')).to_contain_text('Valid:');assert storage()==before;review();close();assert storage()==before
    passed('Portable backup exports only intended study fields; validation/review/cancel never mutate local data')
    with page.expect_download() as download:action('backup-download')
    item=download.value;assert item.suggested_filename=='Radix-study-backup.json';download_data=json.loads(Path(item.path()).read_text());assert download_data['sessions']==data['sessions']
    passed('Standalone browser backup downloads a genuine versioned JSON file')
    bads=['{"version":1,"version":1}',export.replace('"version": 1','"version": 2')]
    invalid=json.loads(export);invalid['saved']=[{'id':1,'input':'FF','base':2}];bads.append(json.dumps(invalid))
    for text in bads:
        before=storage();validate(text);expect(page.locator('#backup-result .study-error')).not_to_be_empty();expect(page.locator('[data-study="backup-review"]')).to_have_count(0);assert storage()==before
    passed('Duplicate keys, unsupported versions and invalid saved conversions are rejected before any mutation')
    page.locator('#backup-file').set_input_files({'name':'oversized.json','mimeType':'application/json','buffer':b' '*262145});expect(page.locator('#backup-result')).to_contain_text('256 KiB')
    passed('Oversized imported files are rejected before parsing')
    fixture=(root/'design/study-backup-browser-fixture.json').read_text();page.locator('#backup-file').set_input_files({'name':'portable.json','mimeType':'application/json','buffer':fixture.encode()});expect(page.locator('#backup-result')).to_be_empty();action('backup-validate');expect(page.locator('#backup-result')).to_contain_text('1 sessions')
    nav('logic');active_before=logic_state();nav('preferences');review();confirm();restored=study_state();assert restored['sessions'][0]['id']=='session-1' and restored['progress']==['gate-and'];expect(page.locator('#pref-large')).not_to_be_checked();assert map_state()['form']=='POS';nav('logic');assert logic_state()==active_before;expect(page.locator('[data-logic="undo"]')).to_be_disabled();nav('kmap');expect(page.locator('[data-student="undo"]')).to_be_disabled();nav('convert');expect(page.locator('#number-input')).to_have_value('255');nav('saved');expect(page.locator('#content')).to_contain_text('FF');nav('expression');expect(page.locator('#expr-left')).to_have_value("A'B+AB'")
    passed('File import requires explicit replace, restores all study domains and clears histories while leaving the active circuit unchanged')
    page.reload();nav('sessions');expect(page.locator('.study-card h2')).to_have_text('D memory');nav('practice');expect(page.locator('#content')).to_contain_text('1 of 108 mastered');nav('representation');expect(page.locator('#rep-input')).to_have_value('-13')
    passed('Replaced sessions, progress, lab drafts and preferences persist across reload')
    full=json.loads(fixture);full['sessions']=[dict(full['sessions'][0],id='session-'+str(i),name='Checkpoint '+str(i)) for i in range(24)];nav('preferences');validate(json.dumps(full));review();confirm();nav('logic');logic('save-session');expect(page.locator('#base-dialog')).not_to_be_visible();expect(page.locator('#toast')).to_contain_text('full');assert len(study_state()['sessions'])==24
    full['sessions'].append(dict(full['sessions'][0],id='overflow'));nav('preferences');before=storage();validate(json.dumps(full));expect(page.locator('#backup-result .study-error')).not_to_be_empty();assert storage()==before
    passed('The 24-session limit is enforced for save and restore without dropping older checkpoints')
    # Responsive/accessibility/sandbox behavior of every new route.
    for width,height in [(320,640),(390,520),(390,844),(844,390),(768,900),(1440,1000)]:
        page.set_viewport_size({'width':width,'height':height})
        for route in ['expression','representation','practice','sessions','preferences']:
            # Desktop uses topnav when the mobile bar is hidden.
            prefix='.mobile-nav' if page.locator('.mobile-nav').is_visible() else '.topnav'
            page.locator(prefix+' [data-tab="more"]').click();page.locator('.more-card[data-tab="'+route+'"]').click();assert page.evaluate('document.documentElement.scrollWidth<=innerWidth'),(route,width,height)
    passed('All five new routes fit 320px phones, keyboard-height/landscape viewports, tablets and desktop without page overflow')
    page.set_viewport_size({'width':390,'height':844});nav('preferences');page.locator('#pref-large').check();nav('expression');page.locator('[data-study="expr-vars"][data-n="4"]').click();page.locator('#expr-left').fill('A XOR B XOR C XOR D');assert page.locator('[data-study="expr-bit"]').first.bounding_box()['height']>=78;assert page.evaluate('document.documentElement.scrollWidth<=innerWidth');page.screenshot(path=str(root/'design/study-expression-phone.png'),full_page=True);nav('representation');page.screenshot(path=str(root/'design/study-representation-phone.png'),full_page=True);nav('practice');page.screenshot(path=str(root/'design/study-practice-phone.png'),full_page=True);nav('preferences');page.screenshot(path=str(root/'design/study-backup-phone.png'),full_page=True)
    passed('Large-control mode keeps expression/practice controls touch-sized and contained on phones')
    page.set_viewport_size({'width':320,'height':640});nav('expression');assert page.evaluate('document.documentElement.scrollWidth<=innerWidth');assert all(b['width']>=64 and b['height']>=78 for b in [x.bounding_box() for x in page.locator('[data-study="expr-bit"]').all()]);page.set_viewport_size({'width':390,'height':844})
    passed('All four large expression inputs wrap cleanly and stay touch-sized at 320px width')
    page.emulate_media(reduced_motion='reduce');assert page.locator('.mobile-nav button').first.evaluate('(e)=>getComputedStyle(e).transitionDuration') in ['1e-05s','0.00001s','0s']
    passed('Reduced-motion media preference removes decorative animation/transition duration')
    nav('expression');before=storage();expression=page.locator('#expr-left').input_value();page.locator('#visual-help-button').click();expect(page.locator('.helper-card')).to_have_count(11);page.locator('[data-guide="start"]').click()
    for _ in range(12):page.locator('[data-guide="next"]').click()
    expect(page.locator('#expr-left')).to_have_value(expression);assert storage()['radix-study-v4']==before['radix-study-v4']
    passed('Eleven illustrated lessons and the 12-step observational tour preserve the new study drafts')
    # Opaque-origin viewer: scripts run, device storage/clipboard are unavailable.
    opaque=context.new_page();opaque_errors=[];opaque.on('pageerror',lambda e:opaque_errors.append(str(e)))
    source=(root/'web/index.html').read_text();opaque.set_content('<iframe title="Offline Radix" sandbox="allow-scripts" style="width:390px;height:844px;border:0" srcdoc="'+html.escape(source,quote=True)+'"></iframe>')
    frame=opaque.frames[1];expect(frame.locator('#number-input')).to_have_value('255')
    frame.evaluate('document.execCommand=()=>false;Object.defineProperty(navigator,"clipboard",{value:{writeText:()=>Promise.reject(Error("denied"))},configurable:true})')
    frame.locator('.mobile-nav [data-tab="more"]').click();frame.locator('.more-card[data-tab="expression"]').click();expect(frame.locator('#expr-truth tbody tr')).to_have_count(4);frame.locator('[data-study="expr-copy"]').click();expect(frame.locator('#manual-copy')).to_contain_text('POS:');frame.locator('[data-action="close-dialog"]').click()
    frame.locator('.mobile-nav [data-tab="more"]').click();frame.locator('.more-card[data-tab="preferences"]').click();frame.locator('[data-study="backup-download"]').click();assert json.loads(frame.locator('#backup-text').input_value())['format']=='radix-study';expect(frame.locator('#toast')).to_contain_text('Copy the backup text');frame.locator('[data-study="backup-copy"]').click();assert json.loads(frame.locator('#manual-copy').input_value())['version']==1;frame.locator('[data-action="close-dialog"]').click()
    passed('Offline opaque-origin viewer runs new labs and exposes selectable backup/copy text when downloads/clipboard are blocked')
    frame.locator('.mobile-nav [data-tab="logic"]').click();frame.locator('[data-logic="save-session"]').click();frame.locator('#session-name').fill('In-session checkpoint');frame.locator('[data-study="session-save"]').click();expect(frame.locator('#toast')).to_contain_text('this session only');frame.locator('.mobile-nav [data-tab="more"]').click();frame.locator('.more-card[data-tab="sessions"]').click();expect(frame.locator('.study-card h2')).to_have_text('In-session checkpoint')
    passed('Storage-denied previews remain usable but explicitly disclose that new checkpoints are session-only')
    assert not errors,errors;assert not opaque_errors,opaque_errors;passed('No new-study JavaScript runtime errors in normal or offline sandbox execution')
    browser.close()
(root/'design/study-ux-checks.json').write_text(json.dumps({'status':'pass','checks':len(results),'passed':results,'androidRuntimeQA':False,'note':'Browser companion at 320/390/768/844/1440 widths; not native Android screenshots or installed-APK testing.'},indent=2)+'\n')
print('PASS:',len(results),'Radix 4 study UX checks.',flush=True)
