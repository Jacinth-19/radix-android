# Radix Android — Release notes v6.0.0 (versionCode 6) — 2026‑09‑30

## What's new

- **Circuit builder:** drag gates onto a canvas, tap-tap to wire them, and simulate —
  with presets (XOR-from-NANDs, half adder, MUX), live values, critical-path timing,
  your circuit's own truth table, and pulse replay that shows **glitches**.
- **IEEE-754 float lab:** single and double floats decoded into sign, exponent and
  fraction — with zeros, subnormals, infinities, NaN, and the reason 0.1 + 0.2 ≠ 0.3.
- **XP, levels & leaderboard:** every mastered card earns XP (Easy 10 / Intermediate 20 /
  Advanced 30 + streak bonus). Up to 8 players share one offline household table.
- **Live sequential clock:** auto-pulse any clocked circuit at 1×/2×/4× while the timing
  diagram grows, plus a nanosecond timeline readout.
- **Daily-challenge widget:** today's card, your progress and streak on the home screen.
- **Shareable cards:** score cards and K-map solutions rendered offline as images.
- **Sync via your own Drive:** save the portable backup into Google Drive/Dropbox with
  the system picker and reopen it anywhere. Radix runs no server and uploads nothing.
- **24 new practice cards (108 total):** Floats and Builder topics, more Ghana-life and
  exam-style drills across all three tiers.
- **Español + tablet layouts:** Spanish joins English/Français; wide screens get two-pane
  grids. Reminder and level-up messages follow your language.

## Privacy & permissions

- **No account, no Radix cloud, no network permission, no tracking, no ads.**
- Permissions unchanged from 5.0: `POST_NOTIFICATIONS` + `RECEIVE_BOOT_COMPLETED`
  (your opt-in reminders only). Widget, sharing and Drive-picker sync add nothing.
- SHA‑256 of the signed APK: `1fe980e67cddfe4d3b888b6c1a9ea89c2f45e1d5171159160218b3c012056f03`
  (RSA‑4096 release signature, same certificate as v5.0 — installs as a clean upgrade).

## Full learning content (cumulative)

Bases 2–36 with working · base +/−/×/÷ · expression lab · representation lab ·
1's/2's complement · Boolean laws · IEEE-754 floats · 8 gates · 12 combinational ·
11 sequential · **circuit builder** · 2–4 var K-maps (SOP+POS) · truth table for every
circuit · **108 tiered practice questions** · XP/levels/leaderboard · 24 named sessions ·
undo/redo · backup/restore/**Drive sync** · widget · share cards · helper/tour ·
mobile-first + accessible.

## Verification summary

- 93 native JVM tests per variant (debug + release), 0 failures; lint 0 errors.
- 130 browser UX checks pass (15 + 34 + 32 + 49).
- 11,214,349 deterministic JS engine assertions pass.
- Static APK audit passes against the v5.0 baseline: same release certificate,
  v2+v3 signatures verified, no network/dangerous permissions, no WebView.
- Full evidence: `design/VERIFICATION.md`, `design/apk-static-checks.json`,
  `design/upgrade-certificate-comparison.json`.
