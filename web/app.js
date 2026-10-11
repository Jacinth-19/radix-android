(() => {
 'use strict';
 const E = window.RadixEngine;
 const paths = {
  sun:'M16 12a4 4 0 1 1-8 0 4 4 0 0 1 8 0M12 2v2m0 16v2M2 12h2m16 0h2M4.93 4.93l1.41 1.41m11.32 11.32 1.41 1.41M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41',
  moon:'M20.9 13A9 9 0 0 1 11 3.1 9 9 0 1 0 20.9 13Z',
  swap:'M4 7h16m-3-3 3 3-3 3M20 17H4m3-3-3 3 3 3',
  bookmark:'M6 3h12v18l-6-4-6 4Z',
  grid:'M3 3h7v7H3zm11 0h7v7h-7zM3 14h7v7H3zm11 0h7v7h-7z',
  arrow:'M5 12h14m-7-7 7 7-7 7',
  lock:'M7 11V7a5 5 0 0 1 10 0v4M5 11h14v10H5zm7 4v2',
  settings:'M12 8a4 4 0 1 0 0 8 4 4 0 0 0 0-8ZM12 2v2m0 16v2M4.93 4.93l1.41 1.41m11.32 11.32 1.41 1.41M2 12h2m16 0h2M4.93 19.07l1.41-1.41m11.32-11.32 1.41-1.41',
  more:'M5 12h.01M12 12h.01M19 12h.01',
  close:'m6 6 12 12M18 6 6 18',
  paste:'M9 4H6a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2h-3M9 2h6v4H9Z',
  dice:'M5 3h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2ZM7 7h.01M12 12h.01M17 17h.01',
  copy:'M9 9h12v12H9ZM15 3H3v12',
  add:'M12 5v14M5 12h14',
  sparkle:'m12 3 2.5 6.5L21 12l-6.5 2.5L12 21l-2.5-6.5L3 12l6.5-2.5ZM20 2v4m-2-2h4',
  delete:'M3 6h18M8 6V3h8v3M5 6l1 15h12l1-15M10 10v7m4-7v7',
  down:'m6 9 6 6 6-6',
  check:'m5 12 4 4L19 6',
  precision:'M12 2v4m0 12v4M2 12h4m12 0h4M19 12a7 7 0 1 1-14 0 7 7 0 0 1 14 0Z',
 };
 const icon = name => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="${paths[name] || paths.arrow}"/></svg>`;
 const esc = s => String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
 const defaults = { number:'255', base:10, custom:null, theme:'dark', saved:[], bits:42 };
 let stored = {};
 try { stored = JSON.parse(localStorage.getItem('radix-v1') || '{}') || {}; } catch (_) {}
 const state = { ...defaults, ...stored, tab:'convert', workingMode:false };
 state.number = typeof state.number === 'string' ? state.number.slice(0,600) : '255';
 state.base = Number.isInteger(state.base) && state.base >= 2 && state.base <= 36 ? state.base : 10;
 state.custom = Number.isInteger(state.custom) && state.custom >= 2 && state.custom <= 36 ? state.custom : null;
 state.theme = state.theme === 'light' ? 'light' : 'dark';
 state.bits = Number.isInteger(state.bits) ? Math.max(0,Math.min(255,state.bits)) : 42;
 state.saved = Array.isArray(state.saved) ? state.saved.filter(item => item && typeof item.input === 'string' && Number.isFinite(item.id) && E.parse(item.input,item.base).kind === 'valid').slice(0,50) : [];
 const content = document.getElementById('content');
 const dialog = document.getElementById('base-dialog');
 const student = window.RadixStudentUI({dialog,icon,esc,toast,manualCopy});
 const logic = window.RadixLogicUI({icon,esc,toast,manualCopy,onSaveSession:()=>study.saveCurrentSession(),onFeedback:()=>study.feedback(),onKmap:(n,cells)=>{student.loadTruth(n,cells);navigate('kmap');toast('Truth table opened in K-maps.');}});
 const study = window.RadixStudyUI({dialog,icon,esc,toast,manualCopy,navigate,student,logic,getConverter:()=>({saved:state.saved,converter:{number:state.number,base:state.base,custom:state.custom,bits:state.bits,dark:state.theme==='dark'}}),applyConverter:data=>{state.saved=data.saved;const c=data.converter;state.number=c.number;state.base=c.base;state.custom=c.custom;state.bits=c.bits;state.theme=c.dark?'dark':'light';persist();theme();}});
 student.setSaveSolution(study.saveKmapSolution);
 const visualHelp = window.RadixVisualHelp({dialog,navigate,logic,icon,esc,getTab:()=>state.tab});
 let pickerType = 'source', outputs = new Map(), toastTimer;
 function persist() {
  try { const {tab,workingMode,...data} = state; localStorage.setItem('radix-v1',JSON.stringify(data)); return true; } catch (_) { return false; }
 }
 function toast(message) {
  const el = document.getElementById('toast');
  el.innerHTML = icon('check') + `<span>${esc(message)}</span>`;
  el.classList.add('show'); clearTimeout(toastTimer);
  toastTimer = setTimeout(() => el.classList.remove('show'),3200);
 }
 function theme() {
  document.documentElement.dataset.theme = state.theme;
  const el = document.getElementById('theme-toggle');
  el.innerHTML = icon(state.theme === 'dark' ? 'sun' : 'moon');
  el.setAttribute('aria-label',`Switch to ${state.theme === 'dark' ? 'light' : 'dark'} theme`);
  document.querySelector('meta[name="theme-color"]').content = state.theme === 'dark' ? '#111410' : '#f4f5ed';
 }
 function nav() {
  const activeTab = ['saved','lab','expression','representation','practice','sessions','preferences'].includes(state.tab) ? 'more' : state.tab;
  const html = [['convert','swap','Convert'],['logic','sparkle','Logic'],['kmap','grid','K-maps'],['more','more','More']].map(([tab,ic,label]) =>
   `<button class="nav-btn ${activeTab === tab ? 'active' : ''}" data-action="nav" data-tab="${tab}" ${activeTab === tab ? 'aria-current="page"' : ''}>${icon(ic)}<span>${label}</span>${tab === 'saved' ? `<span class="saved-count" ${state.saved.length ? 'style="display:inline-block"' : ''}>${state.saved.length}</span>` : ''}</button>`).join('');
  document.querySelectorAll('.topnav,.mobile-nav').forEach(el => el.innerHTML = html);
 }
 function hero(first,second,eyebrow,copy,side = true) {
  return `<section class="hero"><div><p class="eyebrow">${eyebrow}</p><h1 id="main-heading">${first}<br><span class="accent">${second}</span></h1><p class="hero-copy">${copy}</p></div>${side ? `<div class="hero-side" aria-hidden="true"><div class="orbit"><span>36</span></div><div class="hero-side-copy"><strong>Up to base 36.</strong><br>A whole new perspective.<br>All in the blink of an eye.</div></div>` : ''}</section>`;
 }
 function renderConvert() {
  student.deactivate();
  document.body.classList.toggle('is-editing',state.workingMode);
  content.innerHTML = hero('One number.','Every base.','A NEW PERSPECTIVE ON NUMBERS','A little input. A whole lot of possibilities.') + `
  <div class="workspace">
   <div class="source-stack">
    <section class="source-card" aria-label="Number input">
     <div class="label-row"><label class="section-label" for="number-input">YOUR NUMBER</label><span class="base-label">BASE ${state.base}</span></div>
     <div class="base-chips" role="group" aria-label="Source base">
      ${[2,8,10,16].map(base => `<button class="base-chip ${state.base === base ? 'selected' : ''}" data-action="source-base" data-base="${base}" aria-pressed="${state.base === base}" title="Read your input as ${E.name(base)}"><span>${E.shortName(base)}</span><small>${base}</small></button>`).join('')}
      <button class="base-chip more ${![2,8,10,16].includes(state.base) ? 'selected' : ''}" data-action="picker" data-kind="source" aria-label="Choose any source base from 2 to 36">${![2,8,10,16].includes(state.base) ? `<span class="mono">${state.base}</span>` : icon('more')}</button>
     </div>
     <div class="input-row"><input class="number-input" id="number-input" type="text" inputmode="text" value="${esc(state.number)}" maxlength="600" autocomplete="off" autocapitalize="characters" spellcheck="false" aria-describedby="input-help" placeholder="Enter a number"><button class="icon-btn" data-action="clear" aria-label="Clear number">${icon('close')}</button></div>
     <div class="editing-results" id="editing-results" aria-label="Quick conversions while editing"></div>
     <div class="input-help" id="input-help"></div>
     <div class="source-footer"><button class="quiet-btn" data-action="paste">${icon('paste')}Paste</button><div class="footer-right"><button class="quiet-btn" data-action="surprise" title="Try a different example" aria-label="Randomize value">${icon('dice')}<span>Randomize</span></button><button class="quiet-btn primary" data-action="save" id="save-number">${icon('bookmark')}Save</button></div></div>
    </section>
    <button class="teaser" data-action="open-lab"><span class="mini-bits" id="mini-bits" aria-hidden="true"></span><span class="teaser-bottom"><span><strong>A tiny bit of magic</strong><p>Flip a bit. See what happens.</p></span>${icon('arrow')}</span></button>
    <p class="trust-note">${icon('lock')}No ads. No accounts. Just numbers.</p>
   </div>
   <section class="results-wrap" aria-label="Live conversion results">
    <div class="results-heading"><span class="section-label">YOUR NUMBER, REIMAGINED</span><span class="live-label" id="live-label"><span class="dot"></span>LIVE</span></div>
    <div class="results-grid" id="result-cards"></div>
    <button class="custom-btn working-btn" data-action="working" id="show-working"><span>${icon('sparkle')}Show the working</span>${icon('arrow')}</button>
    <div id="custom-result"></div>
    <p class="precision-note" id="precision-note"></p>
   </section>
  </div>`;
  document.getElementById('number-input').addEventListener('focus', () => { state.workingMode = true; document.body.classList.add('is-editing'); });
  document.getElementById('number-input').addEventListener('input', event => {
   state.number = event.target.value; persist(); updateConversions();
  });
  updateConversions();
 }
 function resultCard(base, output, custom = false) {
  const text = output ? E.grouped(output,base) : '—';
  const cls = ({2:'binary',10:'decimal',16:'hex'})[base] || '';
  const longClass = text.length > 13 ? 'very-long' : text.length > 7 ? 'long' : '';
  const meta = !output ? 'Waiting for a number' : !output.exact ? '<span class="approx">≈ 24 places · approximate</span>' : output.repeating ? 'Repeating · exact' : output.raw.includes('.') ? 'Exact conversion' : `${output.raw.replace('-','').length} ${output.raw.replace('-','').length === 1 ? 'digit' : 'digits'} · exact`;
  return `<article class="result-card ${cls} ${custom ? 'custom' : ''}" aria-label="${E.name(base)} result"><div class="card-header"><div><div class="card-name">${E.name(base)}</div><div class="card-base">BASE ${base}</div></div><button class="icon-btn copy-btn" data-action="copy" data-base="${base}" aria-label="Copy ${E.name(base)} result" ${output ? '' : 'disabled'}>${icon('copy')}</button></div><div class="result-number ${longClass}" id="result-${custom ? 'custom-' : ''}${base}" tabindex="0" aria-label="${esc(E.name(base) + ': ' + (output ? output.display : 'no result'))}">${esc(text)}</div><div class="result-meta">${meta}${output && state.base === base ? '<span class="same-input">SOURCE BASE</span>' : ''}</div></article>`;
 }
 function updateConversions() {
  const parsed = E.parse(state.number,state.base);
  const help = document.getElementById('input-help');
  help.classList.toggle('error',parsed.kind === 'invalid');
  help.textContent = parsed.kind === 'invalid' ? parsed.message : parsed.kind === 'empty' ? 'Type a number to see it in every base.' : `${E.digitHint(state.base)} · negative & fractional values welcome`;
  const field = document.getElementById('number-input');
  field.setAttribute('aria-invalid',String(parsed.kind === 'invalid'));
  field.style.fontSize = state.number.length > 14 ? '29px' : state.number.length > 7 ? '38px' : '';
  document.getElementById('save-number').disabled = parsed.kind !== 'valid';
  const live = document.getElementById('live-label');
  live.classList.toggle('waiting',parsed.kind !== 'valid');
  live.innerHTML = '<span class="dot"></span>' + (parsed.kind === 'valid' ? 'LIVE' : 'READY');
  outputs = new Map();
  const bases = [...new Set([2,8,10,16,...(state.custom ? [state.custom] : [])])];
  for (const base of bases) outputs.set(base,parsed.kind === 'valid' ? E.format(parsed.value,base) : null);
  document.getElementById('editing-results').innerHTML = [2,16,8,10].map(base => {
   const o = outputs.get(base), cls = ({2:'binary',16:'hex',10:'decimal'})[base] || '';
   return `<button class="quick-result ${cls}" data-action="quick-copy" data-base="${base}" aria-label="Copy ${E.name(base)} quick result" ${o ? '' : 'disabled'}><span class="quick-top">${E.shortName(base)} ${base}${icon('copy')}</span><span class="quick-value">${esc(o ? E.grouped(o,base) : '—')}</span></button>`;
  }).join('');
  document.getElementById('result-cards').innerHTML = [2,8,10,16].map(base => resultCard(base,outputs.get(base))).join('');
  document.getElementById('show-working').disabled = parsed.kind !== 'valid';
  document.getElementById('custom-result').innerHTML = state.custom ? `<div class="custom-controls"><button class="quiet-btn primary" data-action="picker" data-kind="output">Custom base ${state.custom} · change${icon('down')}</button><button class="quiet-btn" data-action="remove-custom" aria-label="Remove custom output base">${icon('close')}</button></div>${resultCard(state.custom,outputs.get(state.custom),true)}` : `<button class="custom-btn" data-action="picker" data-kind="output"><span>${icon('add')}Add a custom base</span><span class="mono">2–36</span></button>`;
  document.getElementById('precision-note').innerHTML = icon('precision') + (parsed.kind === 'valid' && parsed.value.d !== 1n ? '<span>( ) = repeating digits &nbsp;·&nbsp; … = truncated at 24 places</span>' : '<span>Full precision for integers. Zero floating-point guesswork.</span>');
  let mini = state.bits;
  if (parsed.kind === 'valid' && parsed.value.d === 1n && parsed.value.n >= 0n && parsed.value.n <= 255n) mini = Number(parsed.value.n);
  document.getElementById('mini-bits').innerHTML = mini.toString(2).padStart(8,'0').split('').map(bit => `<span class="mini-bit ${bit === '1' ? 'on' : ''}">${bit}</span>`).join('');
 }
 function renderSaved() {
  content.innerHTML = `<button class="quiet-btn library-back" data-action="nav" data-tab="more">← More</button>` + hero('Good numbers.','Worth keeping.','YOUR PERSONAL NUMBER COLLECTION','Saved on your device. Ready when you are.') + (state.saved.length ? `<div class="section-label">${state.saved.length} SAVED · UP TO 50</div><div class="saved-list">${state.saved.map(item => `<article class="saved-card"><div class="saved-top"><span class="saved-base">${E.name(item.base)} · base ${item.base}</span><button class="icon-btn" data-action="delete" data-id="${item.id}" aria-label="Delete saved conversion ${esc(item.input)}">${icon('delete')}</button></div><div class="saved-value">${esc(item.input)}</div><div class="saved-bottom"><span>${esc(new Date(item.id).toLocaleDateString(undefined,{year:'numeric',month:'short',day:'numeric'}))}</span><button class="quiet-btn primary" data-action="restore" data-id="${item.id}">Open conversion${icon('arrow')}</button></div></article>`).join('')}</div>` : `<section class="empty-state"><div class="empty-icon">${icon('bookmark')}</div><h2>Nothing saved. Yet.</h2><p>Tap Save on a conversion to keep it here.<br>Your collection stays completely offline.</p><button class="solid-btn" data-action="nav" data-tab="convert">Make a conversion${icon('arrow')}</button></section>`);
 }
 function renderLab() {
  content.innerHTML = `<button class="quiet-btn library-back" data-action="nav" data-tab="more">← More</button>` + hero('A little bit.','A lot of magic.','THE 8-BIT PLAYGROUND','Tap a bit. Watch every number change.') + `<div class="bit-workspace"><section class="bit-panel" aria-label="Interactive 8-bit lab"><div class="label-row"><span class="section-label">BUILD YOUR BYTE</span><span class="base-label">0–255</span></div><div class="bits" id="bit-buttons"></div><div class="bit-actions"><button class="quiet-btn" data-action="bits-off">All off</button><button class="quiet-btn" data-action="bits-on">All on</button><button class="quiet-btn" data-action="bits-random">${icon('dice')}Surprise me</button></div><div class="bit-answer"><span class="big-number" id="bit-decimal"></span><span class="answer-label">decimal</span><div class="bit-other"><span id="bit-hex"></span><span id="bit-octal"></span></div></div><p class="bit-formula" id="bit-formula"></p><button class="solid-btn" data-action="use-bits">Take it to the converter${icon('arrow')}</button></section><aside class="lessons"><span class="section-label">THE IDEA BEHIND THE DIGITS</span>${lesson('01','Binary is just yes or no.','Each bit is a switch: 0 is off and 1 is on. From right to left, the positions are worth 1, 2, 4, 8, 16, 32, 64 and 128. Add the positions that are switched on.',true)}${lesson('FF','Hex makes binary bite-size.','One hexadecimal digit represents four bits. 1111 in binary is F in hex, so 1111 1111 becomes FF. The digits are 0–9 followed by A–F.')}${lesson('10','The base changes the place.','In any base, each position is worth a power of that base. 10 in binary means 2 in decimal. 10 in hexadecimal means 16. Same digits, different perspective.')}<p class="lesson-tip">One byte. Eight switches. 256 possibilities.<br>The smallest ideas can open up a whole new world.</p></aside></div>`;
  updateBits();
 }
 function lesson(symbol,title,body,open = false) { return `<details class="lesson" ${open ? 'open' : ''}><summary><span class="mono">${symbol}</span><span class="lesson-title">${title}</span>${icon('down')}</summary><p>${body}</p></details>`; }
 function updateBits() {
  document.getElementById('bit-buttons').innerHTML = Array.from({length:8},(_,i) => 7-i).map(bit => {
   const on = !!(state.bits & (1 << bit));
   return `<div class="bit-col"><button class="bit-btn ${on ? 'on' : ''}" id="bit-${bit}" data-action="bit" data-bit="${bit}" aria-label="Toggle bit ${bit}, weight ${1 << bit}" aria-pressed="${on}">${on ? 1 : 0}</button><span class="bit-weight">${1 << bit}</span></div>`;
  }).join('');
  document.getElementById('bit-decimal').textContent = state.bits;
  document.getElementById('bit-hex').textContent = state.bits.toString(16).toUpperCase() + ' HEX';
  document.getElementById('bit-octal').textContent = state.bits.toString(8) + ' OCT';
  const weights = Array.from({length:8},(_,i) => 7-i).filter(bit => state.bits & (1 << bit)).map(bit => 1 << bit);
  document.getElementById('bit-formula').textContent = (weights.length ? weights.join(' + ') : '0') + ' = ' + state.bits;
 }
 function renderMore() {
  const cards=[['preferences','settings','Settings & offline backup','Comfort controls and portable study data.'],['expression','grid','Boolean expression lab','A–Z syntax, truth tables, SOP/POS and saved formulas.'],['representation','precision','Number representations','Two’s complement, widths, Gray and BCD.'],['practice','sparkle','Logic practice & progress',`${window.RadixPractice.length} objective cards · ${study.progressCount()} mastered across levels`],['sessions','bookmark','Circuit checkpoints',`${study.sessionCount()} named inputs/clock/memory sessions`],['saved','bookmark','Saved conversions',`${state.saved.length} saved · local to your device`],['lab','dice','The 8-bit playground','Build a byte, one switch at a time.']];
  content.innerHTML = hero('A little extra.','A lot to explore.','YOUR LEARNING TOOLBOX','Everything offline. Everything within a tap.',false) + `<div class="more-grid">${cards.map(([tab,image,title,body])=>`<button class="more-card" data-action="nav" data-tab="${tab}">${icon(image)}<span><strong>${title}</strong><small>${body}</small></span>${icon('arrow')}</button>`).join('')}<button class="more-card" data-action="help" id="more-help">${icon('sparkle')}<span><strong>Visual helper & app tour</strong><small>Illustrated lessons and replayable highlights.</small></span>${icon('arrow')}</button></div>`;
 }
 function render() { nav(); student.deactivate(); logic.deactivate(); study.deactivate(); if (state.tab === 'saved') renderSaved(); else if (state.tab === 'lab') renderLab(); else if (state.tab === 'kmap') student.renderKmap(content); else if (state.tab === 'logic') logic.renderLogic(content); else if (state.tab === 'more') renderMore(); else if (['expression','representation','practice','sessions','preferences'].includes(state.tab)) study.render(state.tab,content); else renderConvert(); }
 function navigate(tab) { if (!['convert','saved','lab','kmap','logic','more','expression','representation','practice','sessions','preferences'].includes(tab)) return; if (tab !== state.tab) state.workingMode = false; document.body.classList.remove('is-editing'); state.tab = tab; render(); window.scrollTo({top:0,behavior:'instant'}); }
 function showPicker(kind) {
  pickerType = kind === 'output' ? 'output' : 'source';
  const selected = pickerType === 'source' ? state.base : (state.custom || 12);
  document.getElementById('dialog-body').innerHTML = `<div class="dialog-head"><h2 id="dialog-title">${pickerType === 'source' ? 'Choose your source base.' : 'A different perspective.'}</h2><button class="icon-btn" data-action="close-dialog" aria-label="Close base chooser">${icon('close')}</button></div><p id="dialog-description">${pickerType === 'source' ? 'Changing the base reinterprets your current input.' : 'Add any base from 2 to 36 to your results.'}</p><div class="base-grid">${Array.from({length:35},(_,i) => i+2).map(base => `<button class="grid-base ${selected === base ? 'selected' : ''}" data-action="choose-base" data-base="${base}" aria-label="Base ${base}${[2,8,10,16].includes(base) ? ', ' + E.name(base) : ''}" aria-pressed="${selected === base}"><span class="mono">${base}</span>${[2,8,10,16].includes(base) ? `<small>${E.shortName(base)}</small>` : ''}</button>`).join('')}</div>`;
  dialog.classList.remove('working-dialog'); dialog.scrollTop = 0; dialog.showModal();
 }
 function manualCopy(raw) {
  const multiline = raw.includes('\n');
  document.getElementById('dialog-body').innerHTML = `<div class="dialog-head"><h2 id="dialog-title">${multiline ? 'Copy your working.' : 'Copy your number.'}</h2><button class="icon-btn" data-action="close-dialog" aria-label="Close copy dialog">${icon('close')}</button></div><p id="dialog-description">Clipboard access is blocked in this preview. Select this text and copy it manually.</p>${multiline ? `<textarea id="manual-copy" class="list-input mono" rows="12" readonly aria-label="Full working to copy">${esc(raw)}</textarea>` : `<input id="manual-copy" class="number-input" style="font-size:20px" value="${esc(raw)}" readonly aria-label="Ungrouped result to copy">`}`;
  dialog.classList.toggle('working-dialog',multiline); dialog.scrollTop = 0;
  if (!dialog.open) dialog.showModal(); const input = document.getElementById('manual-copy'); input.focus(); input.select();
 }
 async function copy(base) {
  const output = outputs.get(base); if (!output) return;
  let copied = false;
  try { await navigator.clipboard.writeText(output.raw); copied = true; } catch (_) {
   const el = document.createElement('textarea'); el.value = output.raw;
   Object.assign(el.style,{position:'fixed',left:'0',top:'0',width:'1px',height:'1px',opacity:'0'});
   document.body.appendChild(el); el.select();
   try { copied = document.execCommand('copy'); } catch (_) {} el.remove();
  }
  if (copied) { toast(output.exact ? `${E.name(base)} copied. Nice.` : 'Approximate value copied · 24 fractional places.'); study.feedback(); }
  else manualCopy(output.raw);
 }
 document.addEventListener('click',async event => {
  const button = event.target.closest('button[data-action]'); if (!button || button.disabled) return;
  const action = button.dataset.action;
  switch (action) {
   case 'nav': navigate(button.dataset.tab); break;
   case 'help': visualHelp.open(); break;
   case 'working': if (E.parse(state.number,state.base).kind === 'valid') student.showWorking(state.number,state.base,2); break;
   case 'open-lab': {
    const p = E.parse(state.number,state.base);
    if (p.kind === 'valid' && p.value.d === 1n && p.value.n >= 0n && p.value.n <= 255n) state.bits = Number(p.value.n);
    persist(); navigate('lab'); break;
   }
   case 'theme': state.theme = state.theme === 'dark' ? 'light' : 'dark'; theme(); persist(); break;
   case 'source-base': state.base = Number(button.dataset.base); persist(); renderConvert(); break;
   case 'picker': showPicker(button.dataset.kind); break;
   case 'close-dialog': dialog.close(); break;
   case 'choose-base': {
    const base = Number(button.dataset.base); if (!Number.isInteger(base) || base < 2 || base > 36) return;
    if (pickerType === 'source') state.base = base; else state.custom = base;
    persist(); dialog.close(); renderConvert(); break;
   }
   case 'remove-custom': state.custom = null; persist(); updateConversions(); break;
   case 'clear': state.number = ''; persist(); document.getElementById('number-input').value = ''; updateConversions(); document.getElementById('number-input').focus(); break;
   case 'paste': {
    try {
     const text = await navigator.clipboard.readText();
     if (!text) { toast('Your clipboard is empty.'); break; }
     state.number = text.slice(0,600); persist(); document.getElementById('number-input').value = state.number; updateConversions();
    } catch (_) { document.getElementById('number-input').focus(); toast('Use your keyboard or long-press to paste here.'); }
    break;
   }
   case 'surprise': {
    const choices = [42,255,1024,1337,65535].map(n => n.toString(state.base).toUpperCase()).filter(n => n !== state.number);
    state.number = choices[Math.floor(Math.random()*choices.length)]; persist(); document.getElementById('number-input').value = state.number; updateConversions(); break;
   }
   case 'save': {
    if (E.parse(state.number,state.base).kind !== 'valid') break;
    const input = state.number.trim(), previous = state.saved.find(item => item.input === input && item.base === state.base);
    const item = {id:previous ? previous.id : Date.now(),input,base:state.base};
    state.saved = [item,...state.saved.filter(n => n.id !== item.id)].slice(0,50);
    const saved = persist(); nav(); toast(saved ? 'Saved. A good number to keep.' : 'Saved for this preview session.'); break;
   }
   case 'delete': state.saved = state.saved.filter(item => item.id !== Number(button.dataset.id)); persist(); render(); toast('Removed from saved numbers.'); break;
   case 'restore': {
    const item = state.saved.find(n => n.id === Number(button.dataset.id));
    if (item) { state.number = item.input; state.base = item.base; persist(); navigate('convert'); } break;
   }
   case 'quick-copy':
   case 'copy': await copy(Number(button.dataset.base)); break;
   case 'bit': {
    const bit = Number(button.dataset.bit); state.bits ^= (1 << bit); updateBits(); persist(); document.getElementById(`bit-${bit}`).focus({preventScroll:true}); break;
   }
   case 'bits-off': state.bits = 0; updateBits(); persist(); break;
   case 'bits-on': state.bits = 255; updateBits(); persist(); break;
   case 'bits-random': state.bits = Math.floor(Math.random()*256); updateBits(); persist(); break;
   case 'use-bits': state.number = String(state.bits); state.base = 10; persist(); navigate('convert'); break;
  }
 });
 dialog.addEventListener('click',event => { if (event.target === dialog) { const r = dialog.getBoundingClientRect(); if (event.clientX < r.left || event.clientX > r.right || event.clientY < r.top || event.clientY > r.bottom) dialog.close(); } });
 document.addEventListener('keydown',event => { if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'k') { event.preventDefault(); if (state.tab !== 'convert') navigate('convert'); document.getElementById('number-input').focus(); document.getElementById('number-input').select(); } });
 theme(); render();
})();
