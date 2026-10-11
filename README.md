# Radix 7.0 — source tree

**One number. Every base. A digital-logic studio that keeps your learning.**

This repository contains an offline-first Kotlin/Jetpack Compose Android app and a self-contained HTML companion. Android source targets API 26+, compiles against API 36, and intentionally has no `INTERNET` permission. The current source release metadata is **7.0.0 / versionCode 7**; it has not been compiled or packaged in this work session. The checked-in signed APK and `design/VERIFICATION.md` describe the prior **6.0.0 / versionCode 6** artifact and are historical evidence, not verification of current source.

## Current learning tools

| Area | What it does |
|---|---|
| Number converter | Exact signed integers, fractions and repeating expansions in bases 2–36; conversion working, 50 saved values, random examples and an 8-bit playground. |
| Boolean expression lab | Parse and compare expressions using the selected consecutive input prefix A–F (2–6 variables); show all 4–64 rows, SOP/POS minima, counterexamples and K-map handoff. |
| Karnaugh maps | 2–6 variables, SOP/POS, minterm/maxterm and don't-care lists, textbook notation, exact minimum grouping, manual group checks, undo/redo, truth tables and 24 guided examples. |
| Digital logic | 8 gates, 23 combinational circuits and 11 sequential models; live inputs, diagrams, next-state/logic tables, clock controls, memory, history and K-map bridges where the input count permits. The 16:1 MUX table contains 1,048,576 rows and is generated 16 rows per page. |
| Builder and representations | Interactive gate/circuit builder with teaching delay/glitch simulation; fixed-width unsigned/two's-complement, Gray and BCD labs; base arithmetic and Boolean laws. |
| Practice and progress | 108 leveled practice cards, optional hints, checks and explanations, local mastery, streaks, XP, up to 8 profiles, score sharing and a daily-challenge widget. A reveal alone never counts as mastery. |
| Checkpoints and privacy | Up to 24 named circuit sessions; bounded undo/redo; local reminders and inbox; validated portable JSON backup/restore with explicit replacement. JSON is unencrypted. |
| Settings and help | English/French/Spanish UI chrome, themes, larger controls, optional haptics, reminder scheduling, eleven illustrated lessons and a replayable twelve-step tour. Technical lessons are primarily English. |

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

For reproducible browser UI tests, install `python -m pip install -r tools/requirements-browser.txt` and `python -m playwright install chromium`. Start a local static server in one terminal with `python -m http.server 3000 --bind 127.0.0.1 --directory web`, then run the four `test-*-ui.py` scripts from another terminal. These suites do not prove native Android runtime behavior. Native JVM tests, Android assembly/R8/lint, signed APK audit, TalkBack/font-scaling, device behavior and upgrade/data-retention checks remain separate evidence requirements. **Do not compile until the user authorizes it.**

## Repository map

- `app/src/main/java/com/radix/app/` — Compose UI, ViewModel, exact math/logic/study engines, reminders and bounded backup codec.
- `app/src/test/java/com/radix/app/` — native JVM tests (not run as part of the non-compiling browser/engine suites).
- `web/` — modular offline companion sources; `web/index.html` is regenerated by `tools/build-preview.py`.
- `tools/` — workbook/logic-reference/preview generators, independent engines and browser regression suites.
- `design/` — current learning references and status plus historical release/audit evidence. Versioned APK evidence is labeled by release.
- `licenses/` and `NOTICE` — existing bundled font/icon notices; the notification sound notice is in `design/ASSET-LICENSES.md`.
