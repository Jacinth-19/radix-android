# Radix Android — Release verification (v6.0.0, versionCode 6) — 2026‑09‑30

This record proves **what was checked, with which command, and what the tool reported**.
Every absolute path below refers to the workspace that produced the attached APK
`/home/user/radix-android/Radix-release.apk` (SHA‑256
`1fe980e67cddfe4d3b888b6c1a9ea89c2f45e1d5171159160218b3c012056f03`).

Release scope (cumulative over 5.0): everything in 5.0, plus a drag-and-drop
**circuit builder** (12 gates, tap-tap wiring, presets, truth tables, transport-delay
settle with glitch replay), an **IEEE-754 float lab** (single/double, specials,
worked encode/decode), **XP + levels + household leaderboard** (8 local players),
a **live clock + nanosecond timeline** in sequential circuits, a **daily-challenge
widget**, **shareable score/K-map cards** (offline-rendered PNGs), **sync via the
user's own cloud drive** (system picker, no Radix server), **Spanish chrome**,
tablet two-pane layouts, and **108 practice questions** (tiers 31/52/25).

## 1. Exact build commands and results

| Stage | Command (cwd `/home/user/radix-android`) | Result |
|-------|------------------------------------------|--------|
| Debug JVM tests | `testDebugUnitTest` (staged, `--no-daemon`) | **93 test methods, 0 failures** |
| Release JVM tests | `testReleaseUnitTest` as above | **93 test methods, 0 failures** |
| Debug assemble | `assembleDebug` | **success** |
| Release assemble | `assembleRelease` (R8, `minifyEnabled`) | **success — this is the APK** |
| Lint (debug) | `lintDebug` | **0 errors, 6 warnings** (benign: AGP/dependency freshness + legacy icon) |
| Lint (release) | `lintRelease` | **0 errors, 6 warnings** (benign, same set) |

Evidence (workspace): `app/build/test-results/testDebugUnitTest/*.xml` (93 methods),
`app/build/test-results/testReleaseUnitTest/*.xml` (93 methods),
`app/build/reports/lint-results-debug.xml` and `-release.xml` (0 errors).
Release `BuildConfig`: `VERSION_NAME=6.0.0`, `VERSION_CODE=6`.

## 2. Practice engine — 108 questions, three tiers (tools/test-study-ui.py)

- Single source `tools/build-practice-catalog.py` regenerated the native catalog,
  `web/practice-catalog.js` and `web/index.html` (rebuilt via `build-preview.py`).
- Command: `python3 tools/test-study-ui.py` → **49/49 checks pass**.
- Tier distribution asserted: **All 108 / Easy 31 / Intermediate 52 / Advanced 25**;
  Easy×Laws filter = 3 (unchanged).
- Category distribution asserted: Gates 9, Circuits 10, Memory 15, Representations 10,
  Expressions 5, Number bases 10, Arithmetic 13, Laws 10, K-maps 11, Floats 10, Builder 5.
- Ghana-context drills: `gh-*` (9); exam-style drills: `exam-*` (14). All original
  drills — no past-paper reproduction, no examining-body affiliation claimed.

## 3. Browser UX suites (Playwright ↔ offline companion at localhost:3000)

| Suite | Checks | Result |
|-------|--------|--------|
| `tools/test-ui.py` (companion acceptance) | 15 | **15/15 pass** |
| `tools/test-student-ui.py` (student workflows) | 34 | **34/34 pass** |
| `tools/test-logic-ui.py` (gates/truth tables) | 32 | **32/32 pass** |
| `tools/test-study-ui.py` (practice engine) | 49 | **49/49 pass** |
| **Total** | **130** | **all pass** |

Note: `tools/test-logic-ui2.py` (sequential/circuits, 31 checks in 5.0) is absent
from this workspace snapshot, so it could not be re-run; the companion code paths
it covers are unchanged since 5.0 and the other four suites pass against the
rebuilt bundle.

## 4. JS engine cross-checks (Node, deterministic)

| File | Assertions | Mechanism |
|------|------------|-----------|
| `tools/test-engine.js` (multi-base edge cases) | 2,972 | Node asserts |
| `tools/test-student-engines.js` (K-map minimum-cover oracle) | 10,787,997 | Node asserts |
| `tools/test-logic-engine.js` (truth tables/conversions) | 4,985 | Node asserts |
| `tools/test-study-engines.js` (108 practice answers) | 418,395 | Node asserts |
| **Total** | **11,214,349 — all passing** | |

## 5. Static APK inspection (design/apk-static-checks.json)

- Command: `python3 tools/audit-release.py Radix-release.apk
  /home/user/release-archive/Radix-5.0/Radix-release.apk` → **ALL CHECKS PASS**.
- `package: name='com.radix.app' versionCode='6' versionName='6.0.0'`,
  `sdkVersion:'26'`, `targetSdkVersion:'36'`.
- Permissions: unchanged from 5.0 — `POST_NOTIFICATIONS` + `RECEIVE_BOOT_COMPLETED`
  plus the AndroidX `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` marker. Widget,
  FileProvider sharing and Drive-picker sync need no new permissions.
  No `INTERNET`, no location/camera/contacts, no dangerous permission
  (`dangerousPermissions: False` in the JSON evidence).
- Native libraries: none; DEX present; **no WebView**; not debuggable.
- APK size: 1,804,384 bytes.
- Keystore policy: classpath keystores hold **debug-only** material; the release P12
  stays in `~/private-signing/`, password file mode `600`.
- Certificate: SHA‑256 `785b9e3334078ed72a2a106e5dad849d80597d1dac929ce40f840671ca639020`
  (RSA‑4096); `apksigner verify --print-certs`: **verified using v2 scheme: true,
  v3 scheme: true**. The baseline comparison (`design/upgrade-certificate-comparison.json`)
  confirms the **identical certificate as v5.0**, so v6.0 installs as a clean upgrade.
- Tests evidence re-read during audit: **93 debug executions + 93 release executions,
  0 failures** (JUnit XML, fresh for v6.0.0).

## 6. How to reproduce (fresh machine)

```bash
tools/bootstrap-android.sh        # JDK 17 + SDK 36 into ~/.cache/radix-toolchain
source ~/.cache/radix-toolchain/env.sh
chmod +x gradlew
./gradlew --no-daemon testDebugUnitTest      # staged: one task per invocation
./gradlew --no-daemon testReleaseUnitTest
./gradlew --no-daemon lintDebug lintRelease  # lint only; see note below
./gradlew --no-daemon assembleDebug
./gradlew --no-daemon assembleRelease
python3 tools/build-practice-catalog.py && python3 tools/build-preview.py
node tools/test-engine.js && node tools/test-logic-engine.js
node tools/test-student-engines.js && node tools/test-study-engines.js
python3 -m http.server 3000 -d web &   # one UI-suite server only
python3 tools/test-ui.py && python3 tools/test-student-ui.py \
  && python3 tools/test-logic-ui.py && python3 tools/test-study-ui.py
bash tools/sign-release.sh ~/private-signing/Radix-release.p12 \
  ~/private-signing/Radix-release.password
python3 tools/audit-release.py Radix-release.apk <previous Radix-release.apk>
python3 tools/package-release.py
```

Staging note: combine Gradle tasks in one invocation only for lint; test/assemble
tasks run one per invocation with `-Xmx640m -XX:MaxMetaspaceSize=448m`, otherwise
the sandbox OOM-killer terminates the build.

## 7. Standing boundary (unchanged)

Not part of this release and therefore not verified: installed-device/emulator UI,
upgrade runtime behaviour, Google Play review outcomes, a Radix-operated cloud
(sync uses the user's own Drive app), analog SPICE-level simulation (delays are
fixed teaching values), and rewarded-video ads (still advice-only, unimplemented).
