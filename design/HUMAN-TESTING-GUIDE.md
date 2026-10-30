# Radix Android 7.0.1 — Human Test Guide

**Release under test:** 7.0.1 (versionCode 8)  
**APK:** [Radix-release.apk](https://github.com/Jacinth-19/radix-android/releases/download/v7.0.1/Radix-release.apk)  
**Release page/checksum:** [GitHub release v7.0.1](https://github.com/Jacinth-19/radix-android/releases/tag/v7.0.1)  
**Purpose:** Manual exploratory and acceptance testing of the native Android app. This is a test plan, not a claim that all these checks have already been performed.

## What CI has and has not tested

GitHub Actions passed Android JVM tests, debug/release builds and lint, a compact Android emulator UI test, and the browser-companion regression suites. The emulator test exercised selected touch/accessibility routes, including toggling a live expression input. CI did **not** perform a complete manual walkthrough of every screen, real TalkBack evaluation, physical-device installation/upgrade QA, real notification delivery, or testing across every Android version. Please record findings from the checks below; mark untested items **Not run**, not Pass.

## Before starting

1. Use the signed **7.0.1** release APK above. Test on at least one phone; if possible, include an older supported Android device (minimum Android 8 / API 26) and a current Android device. A tablet is useful but optional.
2. If upgrading an existing installation, make a backup from **More → Settings, reminders & backup → Backup** first. Install over the previous signed Radix release without uninstalling it, then confirm existing study data remains. Stop and report any signature/install error—do not uninstall until the backup is verified.
3. Record the device model, Android version/API, display size, system language, app version, tester, date, and whether this was a fresh install or upgrade.
4. Where notification or accessibility behavior is being tested, use a physical device when possible. Emulator results do not prove OS notification delivery or TalkBack quality.
5. For each check, mark **Pass / Fail / Not run / N/A**. Include exact steps, expected vs actual result, screenshots/video, and reproduction frequency for failures. Avoid personal data in screenshots and exported backups.

### Test record

- Tester / date:
- Device / Android version (API):
- Display size / font scale:
- System language:
- Fresh install or upgrade from:
- App version shown in app:
- Overall result / blocking issue IDs:

## A. Install, first launch, navigation, and upgrade

- [ ] **Install** — Install the signed APK from Downloads. Launch Radix. Expected: app opens without a crash, network connection is not requested, and the main converter screen is usable.
- [ ] **Upgrade/data retention** — On a device with a previous signed Radix version, create at least one saved conversion, one saved formula or session, and a preference. Upgrade to 7.0.1 in place. Expected: install succeeds and the saved items/preferences remain. Reopen each item.
- [ ] **First-run experience** — On a fresh install, step through the welcome/onboarding flow, dismiss or complete it, close and reopen the app. Expected: no clipped/overlapping controls; the app reaches its normal screen and does not repeatedly force onboarding after completion.
- [ ] **Bottom navigation** — Visit **Convert**, **Logic**, **K-maps**, and **More**. Expected: each opens the right destination, selected state follows the active tab, and navigation remains usable in portrait.
- [ ] **More menu routes** — Open each card listed in sections B–I below, then return to More. Expected: correct screen opens; back navigation returns to a sensible destination; long pages scroll without hiding important controls under the bottom navigation.
- [ ] **Rotation and system bars** — Rotate portrait/landscape on a wide logic screen and a study screen; try edge-to-edge/system bars. Expected: no crash, lost state, inaccessible button, or content permanently obscured. Rotate back and confirm state is retained.

## B. Converter, saved conversions, history, and byte playground

Open **Convert**.

- [ ] **Integer conversion** — Convert decimal `255` to base 2. Expected result: `11111111`. Convert decimal `42` to base 16. Expected result: `2A` (case may be styled differently).
- [ ] **Fraction conversion** — Convert decimal `10.625` to base 2. Expected exact result: `1010.101`; inspect the working/details as well as the displayed answer.
- [ ] **Other bases** — Choose source and output bases from the pickers, including a custom base such as 3, 12, or 36. Expected: values and base labels update consistently; changing the source base reinterprets the current input as described in the picker.
- [ ] **Repeating/negative values** — Try a negative value and a repeating fraction supported by the input format. Expected: result is mathematically consistent and the working indicates any repetition/precision behavior; no app freeze.
- [ ] **Invalid input and boundaries** — Try blank input, an invalid digit for the selected base, a malformed fraction, and a very long input. Expected: clear validation or safe limit; no crash or corrupted previous result.
- [ ] **Copy/share and working** — Use the available copy/share and working/details controls. Expected: copied text matches the current input, bases, answer, and selected working; no stale previous result.
- [ ] **Randomize** — Use Randomize more than once. Expected: a valid value is placed in the converter; converting it produces a valid result.
- [ ] **Save and reopen** — Save a conversion, navigate away, open **More → Saved conversions**, reopen it, then delete it. Expected: saved item is accurate, survives navigation/relaunch, and delete affects only that item.
- [ ] **Conversion history** — Convert several valid values, open **More → Conversion history**, reopen one and delete/clear entries using the available controls. Expected: history reflects valid conversions, reopening restores the expected calculation, and deletion/clear works without changing unrelated saved conversions.
- [ ] **8-bit playground** — Open **More → The 8-bit playground**. Toggle several bits. Expected: bit labels, byte value, and displayed representation stay synchronized. Send the byte to the converter if offered; expected: the corresponding value appears.

## C. Number tools

Open the named cards from **More → Number tools**.

- [ ] **Number representations** — Open **Number representations**. Try unsigned and two's-complement values at more than one width, a negative value, Gray conversion, and BCD. Expected: width/range validation is clear; encoded and decoded values agree; changing width updates the interpretation without a crash.
- [ ] **Base arithmetic lab** — Open **Base arithmetic lab**. In base 2, check `1011 + 110 = 10001`, then subtraction, multiplication, and division with exact results. Repeat an operation in a non-binary base. Try division by zero and malformed digits. Expected: correct arithmetic and understandable validation.
- [ ] **IEEE-754 float lab** — Open **IEEE-754 float lab**. Try `0`, `-3.5`, and a value such as `0.1`; inspect sign, exponent, fraction, and decoded value. Use any available precision/format controls. Expected: fields correspond to the entered value and invalid inputs are handled safely.

## D. Boolean expression and circuit lab

Open **More → Expression-to-circuit lab**.

- [ ] **Expression evaluation** — Enter `A'B + AB'`. Expected: a two-input truth table with output 1 when A and B differ; the simplified expression is equivalent to XOR. Compare with `A XOR B` and confirm equivalent.
- [ ] **Operators and precedence** — Try equivalent forms using `!`, prime/apostrophe, `+`, `.`, adjacency, parentheses, XOR (`XOR`, `^`, or `⊕`), and XNOR where offered. Expected: documented spellings parse consistently; invalid syntax reports a helpful error without replacing the last valid result.
- [ ] **Non-equivalent comparison** — Compare two expressions that differ on one input row. Expected: the app identifies non-equivalence and shows a counterexample row.
- [ ] **SOP/POS and learning views** — Change the requested form and inspect simplified results, minterm/maxterm notation, derivation, and copyable table/working. Expected: the form and explanation match the truth table.
- [ ] **Live input switches (release focus)** — Scroll to **Live input values**. Toggle A and B using touch; inspect the switch state description/accessibility state and gate diagram/output. Expected: each tap changes On↔Off once and the output updates immediately. The controls should remain visible and tappable above the bottom navigation after scrolling.
- [ ] **K-map handoff** — Send an expression to the K-map tool using the available handoff. Expected: variable count, labels, and truth values are consistent between tools.
- [ ] **Saved solutions** — Save an expression/formula, navigate away, reopen it from the shared Boolean-solution library, then delete it. Expected: source expression and SOP/POS choice are retained; deletion removes only the selected item.
- [ ] **Larger input count** — Try a formula with 5–6 distinct variables and verify row count/output; try a seventh distinct variable if the UI permits entry. Expected: supported limit is clear and an unsupported expression is rejected safely.

## E. Karnaugh maps

Open **K-maps** or the map handoff from the expression lab.

- [ ] **Basic SOP map** — Use two variables and enter/select a simple function such as `Σm(1,2)`. Expected: map cells, groupings, and minimized expression represent the same truth table (for this example, XOR).
- [ ] **POS and don't-cares** — Switch between SOP and POS; enter a small function with don't-care terms. Expected: labels and output form update; don't-cares are used only as allowed and are not treated as required 0/1 values.
- [ ] **Group controls** — Select/highlight prime implicants/implicates, toggle outlines and cell indices, and try a manual group check. Expected: selected term labels and highlighted cells agree; toggles do not change the underlying function.
- [ ] **Undo/redo** — Change a cell or group, undo, then redo. Expected: each restores the previous/next valid state.
- [ ] **Custom labels** — Rename variables with distinct uppercase A–Z labels, including a non-default order. Expected: labels and significance order stay consistent in map, expression handoff, truth table, and backup.
- [ ] **Wide map** — Try a 5- or 6-variable map in portrait and landscape. Expected: controls remain usable; landscape guidance is available; scrolling does not trap the user or hide actions.
- [ ] **Save formula/backup** — Save the minimized formula to the shared Boolean-solution library and check it appears in the expression lab. Expected: formula and SOP/POS form match; backup/restore later retains custom labels and cells.

## F. Logic, circuits, and builder

Open **Logic**; also test the dedicated BCD card and **Circuit builder** in More.

- [ ] **Gate catalog** — Try AND, OR, NOT, XOR/XNOR, NAND/NOR and the remaining listed gates. Toggle inputs. Expected: truth-table output, diagram, and visible explanation agree.
- [ ] **Combinational circuits** — Try at least one multiplexer, one decoder/encoder, a comparator, parity, and the BCD 4-in/7-out decoder. Expected: changing inputs changes output/table correctly; large tables page without freezing.
- [ ] **BCD display** — Open **BCD 4-in / 7-out decoder**. Try a valid BCD digit and an invalid BCD nibble. Expected: the seven active-high segment outputs/display match the documented convention and invalid-input behavior is explained.
- [ ] **Sequential circuits** — Try at least one latch/flip-flop/counter. Change data while clock is low/high and test a rising edge if controls are present. Expected: state changes follow the selected model; reset/clock controls and next-state table agree.
- [ ] **Circuit builder basics** — Open **Circuit builder**. Add a gate, change inputs, connect/edit using the displayed controls, and simulate. Expected: the diagram responds; invalid/incomplete wiring is handled with feedback; rotate to landscape for the wide canvas.
- [ ] **Circuit checkpoint** — Save a checkpoint with non-default inputs/state, leave the screen, and reopen from **More → Circuit checkpoints**. Expected: saved model, inputs, clock, and memory (where relevant) restore; rename/delete/clear actions affect the intended checkpoint only.
- [ ] **Landscape/table paging** — On the widest model (including the 16:1 MUX if available), rotate and page forward/back. Expected: no full-table hang; page number/rows and outputs stay consistent.

## G. Learning, practice, laws, and rewards

- [ ] **Learning path** — Open **More → Learning path & progress**. Visit all seven topics, open a lesson/activity, return to the path, and revisit progress. Expected: topic links work and completed/mastered progress persists as designed.
- [ ] **Objective practice** — Open **More → Practice & self-tests**. Try Easy, Intermediate, and Advanced questions; submit a correct and an incorrect answer; inspect hints/explanations. Expected: feedback corresponds to the answer; a reveal alone does not count as mastery.
- [ ] **Written self-checks** — Complete a written prompt and compare with its model answer. Expected: the UI presents this as self-checking rather than claiming automatic grading; answer/reveal state behaves consistently.
- [ ] **Boolean laws** — Open **More → Boolean laws lab**. Review laws and exercise any live truth-table verification. Expected: both sides agree for every row.
- [ ] **Leaderboard/rewards** — Open **More → Leaderboard & rewards**. Inspect XP, level, profiles, and score-sharing controls; create/switch profiles if offered. Expected: local values persist and actions clearly distinguish local progress from sharing; no unexpected account/network requirement.
- [ ] **Visual helper and tour** — Open **More → Visual helper & app tour**, start/advance/back/close the tour. Expected: overlays point to sensible controls, close/back works, and leaving the tour restores the prior tab/section.

## H. Settings, reminders, backups, localization, and privacy

Open **Settings** from the top bar and from **More → Settings, reminders & backup**.

- [ ] **Settings entry points** — Open Settings from both places. Expected: both reach the same preferences and the route back is usable.
- [ ] **Theme and larger controls** — Change theme and larger-control setting. Navigate through several screens and relaunch. Expected: visual setting applies consistently and persists; larger controls do not clip labels/actions.
- [ ] **Haptics** — Toggle optional haptics and test a supported control on hardware that supports vibration. Expected: preference is respected; if haptics are unavailable, functionality still works.
- [ ] **Reminders** — Opt in, choose/edit a reminder time, save, then opt out. Expected: permission and scheduling feedback are clear; schedule reflects the selected time; turning reminders off cancels them.
- [ ] **Notification preview and channel settings** — Use preview and open Android channel controls. Test channel sound/importance/mute if permitted. Expected: preview/settings links work; the OS channel remains authoritative; app does not falsely claim delivery when permission/channel is disabled. Wait for a scheduled reminder only if the test device and time window permit.
- [ ] **Tips/streak/inbox** — Exercise available toggles and inspect inbox/streak state. Expected: toggles persist and do not erase unrelated progress.
- [ ] **Backup export** — Create a backup containing a saved conversion, custom-label map, saved Boolean formula, practice progress, settings, and a circuit checkpoint. Save the JSON somewhere private. Expected: export succeeds and includes intended study fields; it is plain unencrypted JSON.
- [ ] **Backup validation/restore** — Validate the backup, review it, cancel once, then restore it with explicit replacement on a clean/test profile. Expected: cancel leaves data unchanged; accepted restore restores intended fields; malformed/oversized/unsupported input is rejected without corrupting current data.
- [ ] **Locale** — On devices that support it, test English, French, Spanish, and Portuguese system languages; also test an unsupported language. Restart if necessary. Expected: Android visible copy follows the supported system locale and unsupported locale falls back to English; browser companion remains English-only.
- [ ] **Offline/privacy** — Enable airplane mode and use converter, expressions, maps, lessons, practice, and local saves. Expected: core app remains usable offline; no sign-in/account is required. Confirm testers do not share personal backup data.

## I. Accessibility, resilience, and regression sweep

- [ ] **TalkBack** — Enable Android TalkBack. Navigate bottom tabs, live expression switches, map controls, dialogs, and Settings. Expected: controls have meaningful names/roles/states, switches announce On/Off and toggle once, focus order is logical, and no essential action is touch-only. Record exact confusing announcements; this guide does not imply prior full TalkBack certification.
- [ ] **Font/display scaling** — Test default and enlarged Android font/display sizes. Expected: text remains readable and primary actions remain visible/reachable; note any overlap or truncation.
- [ ] **Touch and scroll** — On a compact phone, scroll long More/map/expression/circuit pages to the last control. Expected: last controls can be brought above the bottom bar and activated; no control is permanently obscured.
- [ ] **Lifecycle** — While editing a formula or map, rotate, background/restore, and force-close/relaunch once. Expected: no crash; persisted data is retained; unsaved-state behavior is understandable.
- [ ] **Repeated navigation** — Rapidly switch tabs, open/close sheets, use Android Back, and return to the home screen. Expected: no crash, stuck overlay, duplicate dialog, or accidental data deletion.
- [ ] **Clean-state and persistence** — Reopen the app after a normal close. Expected: intentional saved data/preferences remain; transient dialogs and keyboards do not reappear unexpectedly.

## J. Optional browser companion smoke test

The HTML companion is a separate offline web UI, not the native APK. If it is in the human-test scope, open the repository's hosted/local companion and verify conversion, K-maps, Boolean expression, practice, saved formulas, and backup basics in a mobile browser. Do not use this as a substitute for Android testing, notifications, TalkBack, Android upgrade, or native touch/rotation checks.

## Defect report template

```text
Title:
Severity: Blocker / Major / Minor / Cosmetic
Device and Android/API:
App version:
System language / font scale:
Fresh install or upgrade:
Preconditions / test section:
Steps to reproduce:
Expected:
Actual:
Frequency:
Screenshot/video/log (remove personal data):
```

### Suggested severity

- **Blocker:** crash/data loss, cannot install/launch, or core learning flow unusable.
- **Major:** wrong conversion/logic answer, broken save/restore, inaccessible primary task, or important feature cannot be completed.
- **Minor:** workaround exists; limited screen/layout/state issue.
- **Cosmetic:** visual/copy inconsistency without functional impact.
