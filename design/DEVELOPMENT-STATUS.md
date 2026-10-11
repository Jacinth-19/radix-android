# Development status — Radix 7.0 source

**Status date:** 2026-10-11  
**Working branch:** `arena/1469181a-radix-android`  
**Purpose:** Source-change and test report; this is not a release certification.

## Summary

The current worktree includes six-variable Boolean-expression and Karnaugh-map work, arbitrary A–Z labels, custom-label map handoff/backup, bounded/paged large tables, and revised 7.0 onboarding. Native Android source also contains saved Boolean solutions, local conversion history, guided and written self-assessment flows, an expression gate diagram with live inputs, and direct Settings access. The browser companion now saves minimized K-map formulas into its shared Boolean-solution library, with list/open/delete and backup support, but is not full UI parity: conversion history, the guided path, written self-assessment, the expression gate diagram and native notification workflows remain absent or different in the browser UI. Localization includes Portuguese core chrome/onboarding/reminder copy but remains partial across the app. These are source and browser-test findings, not proof of native Android runtime behavior.

The current feature commit has been pushed to `arena/1469181a-radix-android` after the user explicitly authorized CI compilation. The latest GitHub Actions run [38139631080](https://github.com/Jacinth-19/radix-android/actions/runs/38139631080) failed at `:app:compileDebugKotlin` in `StudyScreens.kt`: missing `Color`/`clip` imports and a missing Material 3 opt-in. These source-level compiler diagnostics have been fixed locally and will be pushed for a fresh CI run; no local Gradle build was run. The earlier passing run [38110656003](https://github.com/Jacinth-19/radix-android/actions/runs/38110656003) passed native debug/release unit tests, lint, and debug/release APK assembly for commit `11584f89708ae73144a299e292fd0707697255df`; it predates the current feature changes and does not validate them. No APK was signed or installed on a device. The checked-in signed APK and `design/VERIFICATION.md` are for version 6.0.0 / versionCode 6 and remain historical evidence only.

## GitHub Actions Android validation

| Item | Result |
|---|---|
| Latest workflow run | [38139631080](https://github.com/Jacinth-19/radix-android/actions/runs/38139631080) — **failed at Kotlin compilation** on merge commit `8d3d43c`; `StudyScreens.kt` lacked `Color`/`clip` imports and the Material 3 experimental opt-in. Fixes are in the next pushed commit; rerun is pending. |
| Previous successful workflow | [38110656003](https://github.com/Jacinth-19/radix-android/actions/runs/38110656003) — **passed** on older commit `11584f89708ae73144a299e292fd0707697255df`; not validation of the latest feature source. |
| Toolchain | JDK 17; Android SDK/API 36. |
| Native tests | `testDebugUnitTest` and `testReleaseUnitTest` — passed. |
| Lint | `lintDebug` and `lintRelease` — passed. |
| APK assembly | `assembleDebug` and `assembleRelease` — passed. Outputs are unsigned; this is not release signing or device validation. |
| Artifact | [`radix-android-11584f89708ae73144a299e292fd0707697255df`](https://github.com/Jacinth-19/radix-android/actions/runs/38110656003/artifacts/11690449142), 12,671,402 bytes. Includes the configured APK, test/lint report, and Gradle-log paths. |

The successful run emitted non-blocking notices that Gradle 8.13 is out of date and `ubuntu-latest` will migrate to Ubuntu 26 on 2026-10-19. Neither affected the build.

## Implemented in source

- **Boolean-expression lab (Android):** bounded parser accepts any A–Z labels, up to six distinct variables in first-appearance order; unused input lines are filled consistently. NOT accepts apostrophe/prime or !, OR +, AND . or adjacency, XOR XOR/^/⊕, XNOR XNOR/⊙ and parentheses. It provides 4–64-row evaluation, equivalence/counterexamples, minimum SOP/POS, Σm/ΠM summaries, copyable derivation/table/K-map views, reversible K-map handoff, saved source-expression/form entries (up to 50), and a live SOP/POS gate-level diagram driven by editable input switches. The saved library is an expression-solution library, not a separate named K-map library.
- **Karnaugh maps:** 2–6 variables, arbitrary distinct uppercase labels, 64-cell-safe masks, Σm/ΠM notation, minterm/maxterm and don't-care input, exact grouping, manual group checks, undo/redo, truth tables and 24 worked challenges. Native and browser controls label selected SOP prime implicants or POS prime implicates, toggle outlines/indices and highlight groups; both copy all working and save the minimized formula to the shared Boolean-solution library. This saves the formula/form, not a separately named K-map grid. Current custom map labels and cells persist locally and in portable backups. Five-/six-variable maps include horizontal/landscape guidance.
- **Converter and history:** the native source offers an explicitly labelled Randomize action, up to 50 hand-saved conversions, and a separate 100-entry local history recorded after a short typing pause; entries can be reopened/deleted/cleared and are included in backups. The browser companion has the visible Randomize label and saved-conversion UI, and now supports up to 50 saved Boolean formulas from either the expression lab or K-maps, with list/open/delete and portable-backup support. Browser conversion-history browsing remains unimplemented.
- **Logic and learner tools:** source catalogs 8 gates, 23 combinational models (including MUX/DEMUX, priority encoder, decoder, magnitude comparator, parity and BCD-to-seven-segment) and 11 sequential models. Large tables are paged in 16-row slices; the 16:1 MUX's 1,048,576 rows are generated on demand. Wide circuits and the builder tell users to rotate to landscape for more room/full lab access. The app has 108 objective cards across Easy/Intermediate/Advanced, three written short-answer self-checks across the same levels (model answers, no automatic grading), and a seven-topic guided path. The path, written assessment and expression gate diagram remain native-only; browser objective practice/progress is present but not full parity. A catalog-generator edge case that emitted a one-input OR node in the BCD decoder was corrected earlier; independent circuit tests cover all 23 models.
- **Settings, onboarding and reminders:** direct top-bar shortcut and first card in More open Settings; translated Android core chrome and reminders follow the first supported device locale (English/French/Spanish/Portuguese, otherwise English), with no in-app language toggle. The browser companion remains English-only. Native settings include theme, larger controls, optional haptics, daily reminders with editable time, inbox, streak/tip toggles, preview and direct Android channel settings. Localization is partial; many study, circuit and lesson screens remain English. Reminders are local and the manifest still has no `INTERNET` permission; alarms are scheduled/rescheduled locally.
- **Backups and privacy:** bounded version-1 JSON backup/restore validates before explicit replacement and includes saved conversions, circuit sessions, practice mastery, custom K-map labels, expression form, saved Boolean solutions and conversion history. Legacy backups default new optional fields. JSON is unencrypted; no account/cloud upload is built in.
- **Sound licensing:** the 0.295-second mono WAV is bundled from Kenney's *Interface Sounds 1.0*, pinned to a CC0 1.0 source commit. Its SHA-256 and notice are recorded in `design/ASSET-LICENSES.md`, `licenses/KENNEY-INTERFACE-SOUNDS-CC0.txt`, and `NOTICE`. It is not downloaded or streamed at runtime. The clip was **not auditioned or device-tested** during this update.
- **Generated companion and learning copy:** rebuilt `web/index.html` and regenerated `design/STUDENT-WORKBOOK.md` from their updated modular source/build script. The standalone digital-logic reference is unchanged.

Localization is strongest for app chrome, settings and reminders; many technical lessons remain in English for precision, as documented in `README.md`.

## Validation actually run for this worktree

Results below include Chromium browser automation as well as source/engine/static checks, but are not Android runtime evidence. Playwright 1.63.0 was installed in the local `.venv` from `tools/requirements-browser.txt`. Playwright's Chromium download failed with `ECONNRESET` to `cdn.playwright.dev`; the browser suites were therefore run using the standalone `@sparticuz/chromium@153.0.0` binary (Chromium 153.0.8010.0) connected to Playwright's headless-shell path. This is a successful compatibility workaround, not Playwright's downloaded browser build: Playwright 1.63.0 expects Chromium 153.0.8010.12 (revision 1243), so the binary patch version differs. The suites were launched through a one-off Python wrapper that supplied the extracted executable path and packaged Chromium flags; that test-only shim was not added to the repository.

| Command / suite | Result |
|---|---|
| `node tools/test-engine.js` | Pass — 2,972 exact conversion, validation, precision and round-trip checks. |
| `node tools/test-student-engines.js` | Pass — 10,844,878 assertions; exhaustive 2-/3-variable ternary and 4-variable Boolean maps, seeded six-variable maps, exact representations and independent minimum-cover checks. |
| `node tools/test-study-engines.js` | Pass — 429,941 assertions; 14,880 independent expression rows, 65,808 representation words, 65,536 Gray round trips, 10,000 BCD checks, and browser/native backup fixture interoperability. |
| `node tools/test-logic-engine.js` | Pass — 8,791 assertions; 172 gate rows, 3,490 independently checked circuit rows, 336 next-state rows and 1,344 clock transitions. |
| `python3 tools/test-notification-assets.py` | Pass — WAV hash/format/duration, pinned CC0 notice, channel migration, localized preview, automatic `LocaleList` selection/reminder wiring, removal of the in-app language selector, package-replacement action and absence of `INTERNET` permission. Static only; not an Android notification or device-locale test. |
| `node --check` on changed browser modules | Pass — `app.js`, `student-ui.js`, `study-ui.js`, and `visual-help.js`. |
| Python syntax parse of modified test scripts | Pass — `test-ui.py`, `test-student-ui.py`, `test-logic-ui.py`, `test-study-ui.py`, and `test-notification-assets.py`; syntax only. |
| `python3 tools/build-preview.py` | Pass — standalone `web/index.html` regenerated from current modular sources. |
| `node tools/build-workbook.js` | Pass — workbook regenerated with corrected A–Z syntax, 2ⁿ row counts, K-map notation and landscape copy. |
| `python tools/test-ui.py` | Pass — 16 Chromium browser UX checks, including visible Randomize text and Settings as the first More card. |
| `python tools/test-student-ui.py` | Pass — 35 conversion/K-map browser UX checks, including prime implicant/implicate labels, term-labelled group pills, group/index toggles and saving a K-map formula. |
| `python tools/test-logic-ui.py` | Pass — 34 gates/circuits/sequential/guide browser UX checks, including dedicated parity, BCD seven-segment and landscape-help assertions. |
| `python tools/test-study-ui.py` | Pass — 52 expression/labs/practice/backup/responsive/sandbox browser UX checks, including caret-XOR syntax and save/list/open/delete of Boolean formulas. |
| **Current browser UI total** | **137 checks passed** across the four scripts; tests exercise the offline web companion, not native Android UI. |
| Android validation | The latest GitHub CI run **was run and failed at Kotlin compilation** (see above); source fixes are in the working tree and awaiting a follow-up commit/push and CI rerun. No local Gradle build, device/emulator install, or device QA was performed. |

The browser tests do not exercise Compose, Android notifications, OS upgrades, TalkBack, installed-app permissions, or device-specific landscape behavior. The six-variable notation checks now verify the documented behavior: any distinct A–Z labels are accepted in the written significance order, with the leftmost input as MSB; this replaces stale assertions that incorrectly expected shuffled labels to be rejected. Browser test setup is documented in `README.md`; Playwright is pinned in `tools/requirements-browser.txt`.

## Remaining verification

1. Push the compile-import/opt-in correction and re-run Android unit tests, lint and debug/release assembly in GitHub Actions; the latest workflow currently failed before those stages. The earlier successful workflow artifact is from an older source commit and is not a current APK.
2. Install the current build on Android API 26+ devices/emulators and verify system-locale startup/change (English/French/Spanish/Portuguese and unsupported-locale fallback), custom-label six-variable maps, saved-expression/history restore, reminder opt-in/permission handling, notification preview, sound/mute settings, channel migration, and rescheduling after reboot/package replacement.
3. Check TalkBack, font scaling, keyboard/IME behavior, 5-/6-variable map landscape use, and wide-circuit touch/scroll behavior on device.
4. Audition the bundled chime and validate volume/timing on device before describing it as pleasant or shipping it. It has **not** been auditioned or device-tested.
5. The historical CI release APK is unsigned. Complete any release signing and APK audit through the trusted release process; verify upgrade/data retention on device. The version-6 signed APK and historical version-7 CI run must not substitute for checks on this worktree.
