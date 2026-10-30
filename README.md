# Radix 7.0.1 — source tree

**One number. Every base. A digital-logic studio that keeps your learning.**

This repository contains an offline-first Kotlin/Jetpack Compose Android app and a self-contained HTML companion. Android source targets API 26+, compiles against API 36, and intentionally has no `INTERNET` permission. The current source release metadata is **7.0.1 / versionCode 8**. GitHub Actions builds and tests the current source on this branch; the checked-in `Radix-release.apk` and `design/VERIFICATION.md` describe the historical **6.0.0 / versionCode 6** artifact, not the current source. The current signed APK is published through the protected GitHub Actions release workflow.

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
| Settings and help | A direct top-bar Settings shortcut and first card in More. Android visible UI—including onboarding, settings, lessons, study and circuit tools, catalogs, accessibility labels, generated validation errors and copied reports—is localized in English, French, Spanish and Portuguese from the first supported device locale (no in-app language toggle; otherwise English). The browser companion is English-only. Themes, larger controls, optional haptics, reminder scheduling/tips, eleven illustrated lessons and a replayable twelve-step tour are included. |

The separate offline logic reference is generated at `design/DIGITAL-LOGIC-REFERENCE.html`; the learner workbook is `design/STUDENT-WORKBOOK.md`. Source and bundled third-party asset notices, including the optional CC0 notification chime, are in `design/ASSET-LICENSES.md`.

## Getting started: a complete learner's route

### Install and set up

1. Use an Android device or emulator on **Android 8.0 (API 26) or newer**. This app is fully offline and does not request Internet access.
2. For the current signed release, download `Radix-release.apk` from the [Radix 7.0.1 GitHub release](https://github.com/Jacinth-19/radix-android/releases/download/v7.0.1/Radix-release.apk). Optionally verify it against the `SHA256SUMS.txt` asset in that release. Open the APK on the device and approve installation when Android prompts; the established release certificate allows an in-place upgrade from the prior signed release.
3. For an **unsigned current-source debug build**, open the latest successful **Android compile and test** run for this branch in GitHub Actions, download the `radix-android-<commit>` artifact, unzip it, and install `app-debug.apk`. A debug APK is not signed with the release key and may not install over the signed release. The checked-in `Radix-release.apk` is still a historical **6.0.0** artifact; do not use it to judge current features.
4. Take the illustrated tour from the helper when offered (or open **More → Visual helper & app tour**). Use **Settings** from the top bar or the first card in More to pick a theme, larger controls, optional haptics, and reminder preferences.

### Try the core tools

1. **Convert a value:** open the converter, enter an integer, fraction, or repeating value, choose source and destination bases (2–36), and inspect the exact working. Try `255` from base 10 to base 2, then use Randomize. Save useful results; reopen recent conversions from history. Use the 8-bit playground to toggle bits and send the byte back to the converter.
2. **Learn expressions:** in **More → Boolean expression lab**, enter a formula such as `A'B + AB'` (XOR), compare two expressions, and inspect the truth table, simplified SOP/POS, minterm/maxterm notation, and derivation. NOT accepts apostrophe, prime, or `!`; OR is `+`; AND is `.` or adjacency; XOR accepts `XOR`, `^`, or `⊕`; parentheses control grouping. Start with 2–3 variables; the lab supports 2–6.
3. **Make a K-map:** open the K-map tool from the Boolean lab or the learning tools. Pick 2–6 variables, choose SOP or POS, enter minterms/maxterms and optional don't-cares, then review exact groups. Try selecting/highlighting groups, showing cell indices, checking a manual group, undo/redo, and saving the minimized formula to the shared Boolean-solution library. Five- and six-variable maps are easier in landscape.
4. **Explore logic:** open the logic lab, choose a gate or circuit, toggle inputs, and watch outputs, diagrams, and tables respond. Try a MUX/DEMUX, comparator, parity circuit, and BCD-to-seven-segment. Use the clock controls in sequential models to distinguish a clock level from a rising edge. Rotate to landscape for wide circuits; very large tables are paged rather than loaded all at once.
5. **Build and represent:** use the builder for interactive gates/circuits and teaching-delay behavior; open number representations for fixed-width unsigned/two's-complement, Gray, and BCD; use base arithmetic and Boolean laws for worked examples.
6. **Practice deliberately:** open practice/progress, choose a level, attempt a question before revealing its hint or explanation, then use Check. A reveal by itself does not count as mastery. Written short-answer prompts are self-checked against model answers rather than automatically graded. Follow the guided topic path and revisit missed concepts to build local mastery, streaks, and XP. The app supports up to eight local profiles; the daily-challenge widget and score sharing are optional extras.
7. **Save and revisit:** save expressions and conversions, and name circuit checkpoints so you can reopen their model, inputs, clock, and memory later. Use undo/redo where offered. Export a JSON backup from Settings before changing devices or uninstalling. Restore validates the file and asks before replacing current study data; it does **not** merge. The JSON backup is plain, unencrypted text, so store or share it accordingly. There is no account or automatic cloud sync.
8. **Make it comfortable:** the Android UI uses the first supported system locale (English, French, Spanish, or Portuguese; otherwise English). Change your Android system language to switch; there is no in-app language selector. In Settings, adjust theme, larger controls, haptics, reminders, and tips; preview the reminder sound and check Android notification permission/channel settings if reminders do not arrive. Android's channel settings remain authoritative for delivery and sound.

### Suggested first 30 minutes

- Convert `42` between decimal, binary, octal, and hexadecimal; explain the place values to yourself.
- Build `A'B + AB'`, confirm it is XOR, and simplify it in a K-map.
- Simulate a 4:1 MUX and a D flip-flop; explain why changing data while the clock is high is not another rising edge.
- Complete one practice set without revealing answers first, then save a circuit checkpoint and export a backup.

## Notification sound and offline behavior

The study reminder uses a short bundled confirmation sound from Kenney's Interface Sounds collection, licensed CC0 1.0; it is packaged in the app, not streamed. Source 7.0 includes an in-app notification preview and a direct link to Android's channel controls. Android's channel controls remain authoritative: a user can silence or replace the sound. When upgrading, the app carries forward an existing channel's muted/low-importance state or a custom/silent sound before creating the new sound-capable channel. The selected 0.295-second clip was not auditioned or device-tested during this source update. The app keeps its no-Internet-permission posture.

## Source validation and current work status

Current scope and known limits are tracked in [`design/DEVELOPMENT-STATUS.md`](design/DEVELOPMENT-STATUS.md). GitHub Actions runs on pushes to this working branch, pull requests to `main`, and manual dispatch. It runs native JVM tests, Android debug/release assembly and lint, a compact Android emulator UI test, plus the independent engine, asset and Playwright browser-companion suites. Unsigned debug/release APKs and reports are available from a successful run's `radix-android-<commit>` artifact; the protected release job signs and publishes version 7.0.1 only after the complete build-and-test job succeeds. The checked-in signed APK and historical native APK audit are version-6-specific and must not be presented as current release evidence.

For quick local engine/asset checks, run:

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

The Playwright suites validate the **browser companion**, not the Compose Android app. Passing CI does not certify a signed release APK or replace hands-on device QA for TalkBack, large font scales, installed-app upgrades/data retention, or real notification delivery and sound.

## Repository map

- `app/src/main/java/com/radix/app/` — Compose UI, ViewModel, exact math/logic/study engines, reminders and bounded backup codec.
- `app/src/test/java/com/radix/app/` — native JVM tests (not run as part of the non-compiling browser/engine suites).
- `web/` — modular offline companion sources; `web/index.html` is regenerated by `tools/build-preview.py`.
- `tools/` — workbook/logic-reference/preview generators, independent engines and browser regression suites.
- `design/` — current learning references and status plus historical release/audit evidence. Versioned APK evidence is labeled by release.
- `licenses/` and `NOTICE` — existing bundled font/icon notices; the notification sound notice is in `design/ASSET-LICENSES.md`.
