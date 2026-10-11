# Development status — Radix 7.0 source

**Status date:** 2026-10-11  
**Working branch:** `arena/1469181a-radix-android`  
**Purpose:** Source-change and test report; this is not a release certification.

## Summary

The current source tree contains the requested six-variable Boolean-expression and Karnaugh-map work, bounded/paged large truth tables, landscape guidance, revised 7.0 onboarding, localized reminder copy, and an optional locally bundled CC0 reminder chime with preview and Android channel controls. Existing conversion, practice, history/checkpoint, backup, gate, and circuit functionality was checked against the repository's browser and engine suites rather than assumed to be absent.

**The Android build was completed successfully through GitHub Actions.** Run [38110656003](https://github.com/Jacinth-19/radix-android/actions/runs/38110656003) passed native debug/release unit tests, both lint variants, and debug/release APK assembly. The unsigned APKs and reports were uploaded as one workflow artifact; no APK was signed or installed on a device. The checked-in signed APK and `design/VERIFICATION.md` are for version 6.0.0 / versionCode 6 and remain historical evidence only.

## GitHub Actions Android validation

| Item | Result |
|---|---|
| Workflow run | [38110656003](https://github.com/Jacinth-19/radix-android/actions/runs/38110656003) — **passed** on commit `11584f89708ae73144a299e292fd0707697255df`. |
| Toolchain | JDK 17; Android SDK/API 36. |
| Native tests | `testDebugUnitTest` and `testReleaseUnitTest` — passed. |
| Lint | `lintDebug` and `lintRelease` — passed. |
| APK assembly | `assembleDebug` and `assembleRelease` — passed. Outputs are unsigned; this is not release signing or device validation. |
| Artifact | [`radix-android-11584f89708ae73144a299e292fd0707697255df`](https://github.com/Jacinth-19/radix-android/actions/runs/38110656003/artifacts/11690449142), 12,671,402 bytes. Includes the configured APK, test/lint report, and Gradle-log paths. |

The successful run emitted non-blocking notices that Gradle 8.13 is out of date and `ubuntu-latest` will migrate to Ubuntu 26 on 2026-10-19. Neither affected the build.

## Implemented in source

- **Boolean-expression lab:** selectable 2–6 input prefix (`A` through `F`), bounded Boolean parsing, live 4–64-row evaluation, equivalence/counterexamples, minimum SOP/POS, Σm/ΠM summaries, report copying, and reversible K-map handoff.
- **Karnaugh maps:** 2–6 variables, 64-cell-safe masks, Σm/ΠM notation, minterm/maxterm and don't-care input, exact grouping, manual group checks, undo/redo, truth tables and the 24 existing worked challenges. Five-/six-variable maps include horizontal/landscape guidance.
- **Combinational logic:** 8 gates, 23 combinational models and 11 sequential models. Large tables are paged in 16-row slices; the 16:1 MUX's 1,048,576 rows are generated on demand rather than allocated as one table. Wide circuits show landscape guidance. A catalog-generator edge case that emitted a one-input OR node in the BCD decoder was corrected; independent circuit tests now exercise all 23 models.
- **Learner tools and settings:** the existing 108-card practice catalog, filters, local mastery, undo/redo, named circuit checkpoints, preferences and validated local backup/restore remain present. Backup bounds now accept six-variable maps/expressions and 20-input circuit sessions. The browser backup fixtures identify app version 7.0.0.
- **Onboarding and reminders:** version-7.0 onboarding/what's-new copy, reminder preview and a direct Android notification-channel settings link. Reminder text/tips now cover English, French and Spanish. Reminders still work locally and the manifest still has no `INTERNET` permission. The new channel uses the bundled sound; migration copies an old channel's non-default importance and silent/custom sound where available, and alarms reschedule on boot/package replacement.
- **Sound licensing:** the 0.295-second mono WAV is bundled from Kenney's *Interface Sounds 1.0*, pinned to a CC0 1.0 source commit. Its SHA-256 and notice are recorded in `design/ASSET-LICENSES.md`, `licenses/KENNEY-INTERFACE-SOUNDS-CC0.txt`, and `NOTICE`. It is not downloaded or streamed at runtime. The clip was **not auditioned or device-tested** during this update.
- **Generated companion materials:** rebuilt `web/index.html`, `design/STUDENT-WORKBOOK.md` (27 worked conversions, 24 K-map answers, and 108 practice solutions), and `design/DIGITAL-LOGIC-REFERENCE.html` from their generators.

Localization is strongest for app chrome, settings and reminders; many technical lessons remain in English for precision, as documented in `README.md`.

## Engine, browser and static validation completed

All results below are source/engine/browser-companion evidence, not Android runtime evidence.

| Command / suite | Result |
|---|---|
| `node tools/test-engine.js` | Pass — 2,972 exact conversion, validation, precision and round-trip checks. |
| `node tools/test-student-engines.js` | Pass — 10,844,878 assertions; includes exhaustive 2-/3-variable ternary and 4-variable Boolean maps, seeded six-variable maps, exact representations and independent minimum-cover checks. |
| `node tools/test-study-engines.js` | Pass — 429,935 assertions across expression/representation, session, practice and backup engines; six-variable browser/native backup-fixture interoperability covered. |
| `node tools/test-logic-engine.js` | Pass — 8,791 assertions; 172 gate rows, 3,490 independently checked circuit rows, 336 next-state rows and 1,344 clock transitions. |
| `python tools/test-notification-assets.py` | Pass — WAV hash/format/duration, pinned CC0 notice, channel migration wiring, localized preview/settings wiring, package-replacement action and absence of `INTERNET` permission. Static only; not an Android notification test. |
| `python tools/test-ui.py` | Pass — 14 browser UX checks. |
| `python tools/test-student-ui.py` | Pass — 34 browser UX checks, including 64-cell notation/axis-order behavior and landscape hint. |
| `python tools/test-logic-ui.py` | Pass — 32 browser UX checks, including all 23 circuits, paged 16:1 MUX, sequential tables, landscape/touch layouts and tour. |
| `python tools/test-study-ui.py` | Pass — 50 browser UX checks, including six-variable expressions, 64-row handoff, backups, all 108 cards, histories and responsive layouts. |
| Browser UX total | **130 checks passed** across the four Chromium companion suites. |
| Generators | Pass — practice catalog (108), logic catalog (8 gates / 23 combinational / 11 sequential), standalone preview, workbook and offline reference generated. |

The browser tests use Chromium and Playwright and do not exercise Compose, Android notifications, OS upgrades, TalkBack, installed-app permissions, or device-specific landscape behavior. Browser test setup is documented in `README.md`; the Playwright version is pinned in `tools/requirements-browser.txt`.

## Remaining device and release verification

1. Install/update the uploaded debug APK on Android API 26+ devices/emulators and verify six-variable maps, reminder opt-in/permission handling, notification preview, sound/mute settings, old-channel preference migration, and rescheduling after reboot/package replacement.
2. Check TalkBack, font scaling, keyboard/IME behavior, real 5-/6-variable map landscape use, and wide-circuit touch/scroll behavior on device.
3. Audition the bundled chime and validate volume/loop/timing on device before describing it as pleasant or shipping it in a release. It has **not** been auditioned or device-tested.
4. The CI release APK is unsigned. Complete any release signing and APK audit through the trusted release process; verify upgrade/data-retention behavior separately on device. The version-6 signed APK and its audit must not substitute for version-7 checks.
