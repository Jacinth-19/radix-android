# Radix 7.0 — source tree

**One number. Every base. A digital-logic studio that keeps your learning.**

This repository contains an offline-first Kotlin/Jetpack Compose Android app and a self-contained HTML companion. Android source targets API 26+, compiles against API 36, and intentionally has no `INTERNET` permission. The current source release metadata is **7.0.0 / versionCode 7**; it has not been compiled or packaged in this work session. The checked-in signed APK and `design/VERIFICATION.md` describe the prior **6.0.0 / versionCode 6** artifact and are historical evidence, not verification of current source.

## Current learning tools

| Area | What it does |
|---|---|
| Number converter | Exact signed integers, fractions and repeating expansions in bases 2–36; conversion working, a random-value action, up to 50 saved values, recent local conversion history, and an 8-bit playground. |
| Boolean expression lab | Parse and compare bounded expressions using any A–Z labels (2–6 distinct variables, in first-appearance order); show all 2ⁿ rows (8 for 3 inputs, 16 for 4); accept apostrophe/!/prime NOT, + OR, . or adjacency AND, XOR/^/⊕, XNOR/⊙ and parentheses; provide SOP/POS minima, Σm/ΠM, counterexamples and rule-by-rule steps, copyable learning views, a live gate-level diagram with editable inputs, K-map handoff and up to 50 saved solutions. |
| Karnaugh maps | 2–6 variables with custom A–Z labels, SOP/POS, minterm/maxterm and don't-care lists, textbook notation, exact minimum grouping, term-labelled selected-prime-implicant/implicate buttons and highlighting, optional cell-index/outlines, manual group checks, copy-all working and save-minimized-formula to the shared Boolean-solution library, undo/redo, truth tables and 24 guided examples. The active map is included in portable backups; formulas save to the shared library, not to a separately named-map collection. |
| Digital logic | 8 gates, 23 combinational circuits and 11 sequential models; includes MUX/DEMUX, priority encoders, decoders, magnitude comparators, parity and BCD-to-seven-segment models. Live inputs, diagrams, next-state/logic tables, clock controls, memory, history and K-map bridges where the input count permits. Rotate Android to landscape for the full circuit lab and widest models. The 16:1 MUX table contains 1,048,576 rows and is generated 16 rows per page. |
| Builder and representations | Interactive gate/circuit builder with teaching delay/glitch simulation; fixed-width unsigned/two's-complement, Gray and BCD labs; base arithmetic and Boolean laws. |
| Practice and progress | 108 leveled objective cards with optional hints, checks and explanations, plus three written short-answer self-checks that are compared with model answers rather than automatically graded. Includes local mastery, a guided topic path, streaks, XP, up to 8 profiles, score sharing and a daily-challenge widget. A reveal alone never counts as mastery. |
| Checkpoints and privacy | Up to 24 named circuit sessions; bounded undo/redo; saved Boolean solutions, conversion history, local reminders and inbox; validated portable JSON backup/restore with explicit replacement. JSON is unencrypted. |
| Settings and help | A direct top-bar Settings shortcut and first card in More. Android core chrome, onboarding and reminders automatically follow English, French, Spanish or Portuguese from the device locale (no in-app language toggle); the browser companion is English-only. Themes, larger controls, optional haptics, reminder scheduling/tips, eleven illustrated lessons and a replayable twelve-step tour are included. Localization remains partial: many study, circuit and technical-lesson screens are still English-only. |

The separate offline logic reference is generated at `design/DIGITAL-LOGIC-REFERENCE.html`; the learner workbook is `design/STUDENT-WORKBOOK.md`. Source and bundled third-party asset notices, including the optional CC0 notification chime, are in `design/ASSET-LICENSES.md`.

## Notification sound and offline behavior

The study reminder uses a short bundled confirmation sound from Kenney's Interface Sounds collection, licensed CC0 1.0; it is packaged in the app, not streamed. Source 7.0 includes an in-app notification preview and a direct link to Android's channel controls. Android's channel controls remain authoritative: a user can silence or replace the sound. When upgrading, the app carries forward an existing channel's muted/low-importance state or a custom/silent sound before creating the new sound-capable channel. The selected 0.295-second clip was not auditioned or device-tested during this source update. The app keeps its no-Internet-permission posture.

## Source validation and current work status

Current scope, non-compiling test results and known limits are tracked in [`design/DEVELOPMENT-STATUS.md`](design/DEVELOPMENT-STATUS.md). This session is intentionally **not compiling** the Android app, at the user's direction. The historical native APK audit is version-6-specific and must not be presented as a version-7 build/audit.

The non-compiling validation commands are:

```sh
node tools/test-engine.js
node tools/test-student-engines.js
node tools/test-logic-engine.js
node tools/test-study-engines.js
python tools/test-notification-assets.py
python tools/build-preview.py
python tools/test-ui.py
python tools/test-student-ui.py
python tools/test-logic-ui.py
python tools/test-study-ui.py
```

For reproducible browser UI tests, create an isolated environment and install the pinned Playwright version:

```sh
python3 -m venv .venv
.venv/bin/python -m pip install -r tools/requirements-browser.txt
.venv/bin/python -m playwright install chromium
python3 -m http.server 3000 --bind 127.0.0.1 --directory web  # separate terminal
.venv/bin/python tools/test-ui.py
.venv/bin/python tools/test-student-ui.py
.venv/bin/python tools/test-logic-ui.py
.venv/bin/python tools/test-study-ui.py
```

These suites do not prove native Android runtime behavior. Native JVM tests, Android assembly/R8/lint, signed APK audit, TalkBack/font-scaling, device behavior and upgrade/data-retention checks remain separate evidence requirements. **Do not compile until the user authorizes it.**

## Repository map

- `app/src/main/java/com/radix/app/` — Compose UI, ViewModel, exact math/logic/study engines, reminders and bounded backup codec.
- `app/src/test/java/com/radix/app/` — native JVM tests (not run as part of the non-compiling browser/engine suites).
- `web/` — modular offline companion sources; `web/index.html` is regenerated by `tools/build-preview.py`.
- `tools/` — workbook/logic-reference/preview generators, independent engines and browser regression suites.
- `design/` — current learning references and status plus historical release/audit evidence. Versioned APK evidence is labeled by release.
- `licenses/` and `NOTICE` — existing bundled font/icon notices; the notification sound notice is in `design/ASSET-LICENSES.md`.
