# Radix 4.0

**One number. Every base. A digital-logic studio that keeps your learning.**

Offline native Android app, built with **Kotlin + Jetpack Compose**, with a self-contained offline HTML companion. Android **8.0/API 26+**, compile/target API 36. No Internet permission, accounts, ads, tracking, cloud or dangerous permissions.

## Seven upgrades in 4.0

| Upgrade | What you can do |
|---|---|
| Boolean expression lab | Write expressions with 2–4 variables; inspect the live output/full truth table, minimum SOP and POS; prove equivalence or see counterexample rows; send either form to K-maps. |
| Number representations | Encode/decode 4/8/16/32/64-bit words as unsigned or two’s complement. See exact ranges, hex, Gray and per-decimal-digit BCD with working. Overflow is rejected, never silently wrapped. |
| Practice and progress | 36 cards: 8 gates, 8 circuits, 12 memory, 4 representations, 4 expressions. Hints, checks, reveals and explanations. Correct checks record local mastery; revealing does not. |
| Named circuit sessions | Save up to 24 checkpoints from Logic and restore the exact model, inputs, clock and nullable memory. A restored sequential session starts a fresh one-sample timing trace. |
| Undo/redo | The latest 32 circuit and K-map edits are reversible. A pulse and simultaneous SR release are each one action. New edits clear redo. |
| Offline backup/restore | Export versioned JSON. Import a file or paste text, validate its contents, review a summary, then explicitly confirm replacement. No accounts or cloud. |
| Mobile/accessibility | Larger learning controls, optional haptic feedback, reduced motion in the companion, eleven illustrated lessons and the replayable 12-step spotlight tour. |

All seven groups exist in **both** the native app and companion. The mobile navigation remains **Convert · Logic · K-maps · More**. More holds Expression Lab, Representations, Practice, Sessions, Saved, Bit Lab, Preferences/Backup and Visual Helper.

## Existing features preserved

- Instant conversion in **all bases 2–36**; arbitrary-precision signed integers, fractions and exact recurring notation using rational arithmetic, not floating point.
- Binary/octal/decimal/hex cards, optional custom output, raw copy/paste, 50 saved conversions, dark/light themes and eight-bit playground.
- Complete conversion working: positional weights, repeated division, fraction multiplication, repeating-tail geometric series, cycle detection, sign restoration and bit-grouping shortcuts. Copy the whole worksheet.
- **2-, 3-, 4-variable SOP/POS K-maps**: Gray order, 0/1/X edits, minterm/maxterm/don’t-care lists, minimum forms, colored groups, wrapping/overlap explanations, manual grouping checker, full truth table and 24 beginner challenges.
- **8 gates**: AND, OR, NOT, NAND, NOR, XOR, XNOR, BUFFER. Variable-input gates support 2–4 inputs; XOR/XNOR use odd/even parity.
- **12 combinational circuits**: half/full adders and subtractors, MUXes, DEMUX, enabled decoder, priority encoder with Valid, comparators and ripple-carry adder.
- **11 sequential models**: NOR/NAND SR latches, D latch, D/SR/JK/T flip-flops, counter, shift register, ring counter and Johnson counter.
- Phone-fit block views, locally pannable gate-level schematics, live wires, complete truth/next-state tables, clock controls, bounded 12-sample timing traces and combinational-to-K-map bridge.
- Offline diagram/table reference and student workbook. Immediate useful defaults: **255**, **F=A+B**, XOR equivalence and **8-bit signed −13**.

## Install/update

The packaged **`Radix.apk` is 4.0.0/code 4**, a non-debuggable, R8/resource-shrunk release signed with the **unchanged RSA-4096 Radix 2.0/3.0 certificate**. Open it on a trusted Android phone, or:

```sh
adb devices
adb install -r Radix.apk
```

**If you have signed 2.0 or 3.0, update over it; do not uninstall.** The unchanged package/key and higher versionCode satisfy the static update prerequisites. Installed Android upgrade/data-retention testing has **not** been performed here. Export a study backup before future updates when possible.

Only the original **1.0 debug-signed** build needs uninstalling first; its different key cannot update in place, and uninstalling deletes local data. Never regenerate the release key for another update. The encrypted key archive/password handoff remains separate from this source archive.

This is a sideloadable release, not a Google Play publication or endorsement. Native device QA and publisher setup remain necessary before public distribution.

## Use the new tools

### Expression Lab

More → Expression Lab. Choose 2–4 variables, edit F/G, toggle inputs and inspect every truth-table row. Examples: `A'B + AB'`, `A XOR B`, `!(A B)`, `A(B+C)`. Constants 0/1 and A–D only; no scripting is evaluated.

**Precedence:** NOT/prefix/postfix prime → AND/NAND (including adjacency) → XOR/XNOR → OR/NOR. Binary operators at each level are left-associative. Use parentheses when mixing NAND/NOR. `+`, `·`, adjacency and `'` are supported; `'`, `′`, `’` all mean NOT. Drafts may be invalid while editing; stale results are hidden. SOP/POS handoff keeps the same truth table, not its complement.

### Representations

More → Representations. Select width, signed/unsigned interpretation and Decimal/Binary word/Gray mode. For 8-bit signed −13: word **11110011**, unsigned **243**, hex **F3**, Gray **10001010**; BCD is unavailable for negative selected values. For unsigned decimal 12, BCD is **0001 0010**, not ordinary binary 1100.

Short binary/Gray input is **zero-padded, not sign-extended**. `1111` is −1 at signed width 4 but +15 at signed width 8. The Convert tab still represents negative values with an ordinary sign; fixed-width two’s complement belongs in this lab.

### Practice/checkpoints/history

More → Practice. Choose a category/card, try an answer, ask for a hint, then check or reveal. No punitive streaks, account or score penalties. Resetting mastery requires confirmation.

Logic → Save session → name it. More → Sessions → Restore. Library deletion confirms separately; library actions are not circuit undo entries. Restoring a session is a reversible circuit action. Clocked models capture on rising edges; forbidden SR conditions retain **X/unknown**, never a fabricated zero. Timing shows actions, not real time or analog behavior.

Undo/redo is in Logic and K-maps. History lasts for the current app/browser session, is bounded, and is intentionally absent from backups. K-map undo preserves invalid list drafts/errors within that session; portable backup includes valid cells/form, not transient manual selections or invalid index-list text.

### Preferences/backup

More → Preferences & Backup. Choose comfort settings, then Save backup file or Copy backup text. Native file access uses Android’s document picker; no broad storage permission is requested. The companion exposes selectable text if embedded previews block downloads or clipboard access.

JSON is **plain, unencrypted data**. Keep it private. It includes saved conversions, circuit checkpoints, mastered card IDs, current K-map, expression/representation drafts, comfort preferences and converter settings. It excludes traces, history, credentials and signing material. Limit: **256 KiB UTF-8**, schema `radix-study` version 1. Duplicate keys, unknown models/IDs, invalid saved numbers, wrong dimensions, inconsistent sessions and unsafe numeric fields are rejected.

Restore **replaces**, not merges. Validation and Cancel do not change data. Confirmation replaces the listed study domains and clears histories; the active circuit remains unchanged. Export current data first. Browser storage-denied previews are explicitly session-only; export a backup to keep changes.

## Build and verify

```sh
chmod +x gradlew tools/*.sh
./tools/bootstrap-android.sh
source /home/user/.cache/radix-toolchain/env.sh
./tools/validate-native.sh
```

The script stages JVM tests, APK assembly/R8 and each lint variant in separate no-daemon invocations to fit a 2 GB workspace. Do **not** run Chromium concurrently. Release assembly defers vital lint; the subsequent **full debug/release lint** must pass before signing/packaging.

```sh
node tools/test-engine.js
node tools/test-student-engines.js
node tools/test-logic-engine.js
node tools/test-study-engines.js
python tools/build-preview.py
python -m http.server 3000 --bind 0.0.0.0 --directory web
# In a separate terminal, with Playwright and Chromium installed:
python tools/test-ui.py
python tools/test-student-ui.py
python tools/test-logic-ui.py
python tools/test-study-ui.py
```

Keep keys outside the project. `tools/sign-release.sh`, `tools/audit-release.py` and `tools/package-release.py` sign/audit/package an already built release without creating keys. The audit compares the archived signed 3.0 APK. Read `design/VERIFICATION.md` for exact evidence and limits.

**Testing scope:** native engine/data JVM tests, compilation, R8, lint and static signature/APK audit; browser UX on phone/landscape/tablet/desktop and offline sandbox. **No installed native UI, device, emulator or upgrade-runtime QA**: this host cannot boot the emulator with its available RAM. Browser screenshots are not native screenshots.

## Project map

- `app/src/main/java/com/radix/app/`: Compose screens, ViewModel, exact math/logic/study engines, bounded JSON/backup codec, practice catalog and visual guide.
- `app/src/test/java/com/radix/app/`: native JVM engine/data regressions.
- `web/`: modular offline companion; `index.html` embeds all dependencies/fonts/icons.
- `tools/`: shared catalog/reference/preview generators, tests, toolchain/bootstrap, staged native validation, release tools.
- `design/`: release notes, workbook, reference, verification evidence and **browser** screenshots.
- `licenses/` and `NOTICE`: locally bundled font/icon notices.

No circuit builder, floating-point/IEEE-754 lab, live analog simulation or cloud sync is claimed in 4.0.
