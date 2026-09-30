# Radix 4.0 — completed upgrade pack

All seven announced groups are implemented in both Kotlin/Compose and the offline companion:
1. Boolean expression lab, equivalence/full tables/SOP-POS bridge.
2. Fixed-width unsigned/two’s-complement/Gray/BCD representation lab.
3. 36-question practice, hints/check/reveal/explanations and local mastery.
4. 24 named circuit sessions with inputs/clock/nullable memory.
5. Bounded atomic circuit/K-map undo/redo.
6. Common versioned offline JSON backup, validation and explicit replacement.
7. Larger controls, optional haptics, reduced motion and 11 illustrated lessons.

Status: native debug/release JVM tests **74 methods each / 148 executions** pass; assemblies/R8 and full debug/release lint pass. All JavaScript engine suites pass (**11,213,956 assertions**) and all browser UX suites pass (**125 checks**). Native/browser backup interchange passes. Signed 4.0.0/code4 APK audited against archived signed 3.0 with unchanged RSA-4096 release identity. Updated preview/reference/workbook/docs and clean packaging are the release deliverables.

Preserve all older conversions/working/maps/challenges/gates/circuits/memory/diagrams/tours. No circuit builder, IEEE-754 lab, cloud or real-time analog simulation was promised.

The combined native run exhausted this RAM-limited host; authoritative staged runs passed using separate no-daemon assembly/lint phases and a 512-MiB lint worker. Do not run Chromium concurrently with Gradle. `tools/validate-native.sh` reproduces this route.

Installed Android UI/R8-runtime/upgrade/data-retention and physical-device QA remain **not performed**. The host cannot boot the emulator. This limitation is preserved in all verification/release documentation; browser screenshots are not native screenshots. Credentials remain separately restricted and excluded from public files.
