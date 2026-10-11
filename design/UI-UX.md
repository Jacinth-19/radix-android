# Radix 7.0 — UI/UX, accessibility and learner support

## First reward, without friction

- Launch directly into useful conversions for **255**, with instant typing updates and no Convert button.
- Keep four mobile destinations: **Convert · Logic · K-maps · More**. More is the toolbox, not a cramped eight-item bottom bar.
- New study routes offer immediately meaningful defaults: XOR equivalence, signed 8-bit −13 and a beginner gate question.
- Use a lime accent for actions/known live states, pink for errors, monospaced values and calm explanatory text. Dark/light themes and locally embedded fonts/icons remain.
- Encourage understanding through toggling, tracing wires, choosing an answer and discovering why. No forced account/onboarding, punitive streaks or fabricated mastery from revealing answers.

## Information and mobile layout

- Expression/representation/practice/backup use two columns on wide screens and stack at phone widths. Long numbers/expressions wrap inside their card.
- Circuits open compact block views; detailed netlists and wide truth tables pan **inside** the card. The page itself must not widen.
- Core study actions are at least 48 px/dp high. Larger-control preference raises learning actions toward 60 and live input controls to 78. Native Material controls preserve system font scaling.
- Undo/redo and Save session sit directly below Logic’s model picker. K-map history sits above its workspace. Disabled history controls explain availability visually through their disabled state.
- Expression/K-map truth tables cover up to six variables. Circuit tables paginate at 16 rows; the 16:1 MUX (1,048,576 rows) is generated on demand instead of allocated all at once. Page-copy actions clearly copy only the visible page.
- Native outer pages have finite constraints before vertical scrolling; avoid infinite nested scroll heights.

## Honest feedback and progress

- Expression syntax errors and representation overflow hide stale results. Equivalence checks cover **every** input row, with explicit counterexamples if needed.
- Negative conversion is signed magnitude; fixed-width two’s complement is clearly separated in Representations.
- Gray is a code, not positional binary; BCD maps each decimal digit independently. Short words explicitly zero-pad.
- Practice: 108 leveled cards; hints are optional, wrong checks say “Not yet,” correct checks show the explanation and save a stable mastery ID. Reveal explains without marking mastery. Reset confirms.
- Haptics are optional, persisted and honor device/browser support. Native feedback is centrally gated; companion feedback covers learning controls and successful study/converter copies. Do not require vibration for understanding.
- Native/circuit traces are action samples, not real time. Unknown SR memory is **X**, never an invented zero.

## Sessions, histories and backups

- Save named checkpoints from Logic; use readable names and escape them as text in HTML. A full 24-session library rejects a new save rather than silently removing an older checkpoint.
- Restore model/inputs/clock/memory exactly; start a fresh one-sample sequential trace. Restoring is a reversible circuit action. Deletion is confirmed and does not change the live circuit.
- Histories are local/in-memory, 32 actions. Pulse/release are atomic; new edits clear redo. Library changes, transient manual grouping and paging are outside the promised circuit/map edit history.
- Backups expose format/version, domain counts and a validation summary before **Replace & restore**. Cancel/invalid data never mutate domains.
- Replacement is explicit, not merge. Warn that JSON is plain/unencrypted and that current data should be exported first. Active circuit remains unchanged; histories clear.
- Native file access uses the system document picker; companion supports file/paste/copy/download. Opaque-origin previews expose selectable text if file downloads/clipboard are denied and disclose session-only storage.

## Accessibility, localization and help

- Text labels accompany icons, inputs/output roles, bit states and clocks. Toggle controls expose pressed/checked states; truth tables have headers. Dynamic feedback uses status/live regions where practical.
- Keyboard focus and Escape dismissal are preserved in the companion’s dialogs/tour. Do not rely on color or motion alone.
- Browser reduced-motion media preference suppresses decorative animation/transitions.
- Core chrome, onboarding and reminder content follow the device's first supported system locale (English, French, Spanish or Portuguese; otherwise English); there is no in-app language toggle. Localization is partial: many study, circuit and technical teaching screens remain English-only. Respect Material accessibility semantics, larger controls, system font scaling and optional haptics.
- Reminder settings explain the bundled offline CC0 chime; preview and Android channel settings provide ways to check or silence it. Android notification permission and channel controls remain authoritative.
- For five-/six-variable K-maps and very wide combinational models, show a short landscape/side-scroll tip rather than letting the page overflow. The 16:1 MUX truth table is generated in 16-row pages.
- **12-step observational spotlight tour** highlights actual controls, can be replayed and restores the previous route/logic section. It does not toggle a user’s inputs, memory or new study drafts.

## Verified companion viewports

320×640 · 390×520 · 390×844 · 844×390 landscape · 768×900 · 1440×1000. Companion suites should check all study routes, large controls, motion preference, storage/clipboard denial, histories, checkpoints, six-variable K-maps/expressions, paginated 16:1 MUX rows and backup replacement.

Evidence files from earlier versions are historical and must not be used as proof for source 7.0. Current-run evidence and untested native/device areas are summarized in `DEVELOPMENT-STATUS.md`. Browser screenshots/tests are **companion** evidence, not installed Android UI screenshots. Native compile/lint/audit and device UI/font-scale/TalkBack/gesture/inset/upgrade QA remain separate; this work intentionally does not compile.
