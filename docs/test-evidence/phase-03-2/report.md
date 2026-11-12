# Phase 03.2 test report

Updated 2026-11-13 (Asia/Dhaka). Review PR: https://github.com/rahadul231/YouPlayerTube/pull/3 . Results describe their named commits/environments, not physical Samsung acceptance.

## Compatibility gate — TESTED before schema expansion
Existing baseline 37999db (merged planning PR #2). Official metadata/release comparison selected Room 2.8.5 Android-only stable line, KSP 2.3.13, Preferences DataStore 1.3.0 and coroutine tests 1.10.2. Existing AGP 9.5.0, Kotlin/Compose compiler 2.4.10, wrapper 9.6.0, JDK 17 and SDK configuration retained. Minimal Room probe generated DAO implementation and assembled a debug APK; original 17 JVM tests executed successfully. Gate log: compatibility-gate.txt. Full schema began after BUILD SUCCESSFUL in 2m 10s; probe removed.

## Codex Cloud — TESTED
Command: `sh ./gradlew --no-daemon :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug`, using /workspace/setup/android-env.sh (JDK 17).

Latest Cloud build with stable API 37-compatible test tooling: BUILD SUCCESSFUL in 2m 41s, 85 actionable tasks executed. Application and instrumentation APKs generated. 30 executed JVM tests, zero failures/errors/skips (raw JUnit XML alongside this report). Unit test task execution is distinguished from up-to-date tasks; earlier full runs and Actions logs independently confirm execution.

| JVM suite | Executed | Scope |
|---|---:|---|
| SourceBoundaryTest | 7 | YouTube/native typing, host rejection, eligibility/rights, invalid inputs |
| NavigationTest | 7 | Four roots, save/restore codec, Back, repeated selection, empty default summary |
| AppViewModelTest | 8 | Saved tab, Home fresh launch, reactive sort query, write/read errors, DB error/retry |
| MediaMappingTest | 6 | Three source round-trips, inconsistent payloads/identity, no signed URL persistence, metadata/query validation |
| PreferencesPersistenceTest | 2 | Real on-disk DataStore close/reopen, unknown enum default, corrupt bytes preserved and reported |

Lint: 0 errors, 6 warnings. Two newer-version recommendations (verified pins retained), existing backup data-extraction recommendation (backup policy deliberately unchanged), missing original launcher icon, two version-catalog recommendations. Raw lint XML retained. Native library stripping informational messages do not establish runtime correctness.

## GitHub Actions attempts
- [Run #12](https://github.com/rahadul231/YouPlayerTube/actions/runs/43310503010), ec71c3e: build/debug/lint/test APK and 30 JVM tests passed. Device jobs FAILED before execution because sdkmanager/adb were not on PATH. No Android database tests are claimed passed in that attempt. SDK discovery was corrected in 8f9eb79.
- Run #12 signing comparison: build #9 certificate differs from review certificate. In-place update of the owner's installed build #9 is BLOCKED; no uninstall, key change or backup change performed.
- [Run #13](https://github.com/rahadul231/YouPlayerTube/actions/runs/43311158626), 8f9eb79: build and 30 JVM tests passed; device startup failed and cleanup waited for a missing device. Force-cancelled using an isolated maintenance job to release concurrency. No Android test execution is claimed. A bounded evidence-collection timeout was added.
- Run #14 was superseded while queued; it supplies no test evidence.
- [Run #15](https://github.com/rahadul231/YouPlayerTube/actions/runs/43313636933): fail-fast logging identified Unknown AVD name / ini location mismatch. Explicit ANDROID_AVD_HOME and avdmanager output path are now set. A temporary workflow used only to force-cancel run #13 succeeded and has been removed from the implementation branch; no Actions write permission remains in the final workflow.
- [Run #16](https://github.com/rahadul231/YouPlayerTube/actions/runs/43314400129): both emulators booted and controlled baseline upgrades passed. Connected Gradle test installation rebuilt/replaced an APK with a mismatched signer; no database tests executed in that attempt. Device execution now uses the exact initially built APK/test pair via AndroidJUnitRunner, without a second Gradle APK build. Test-only APK installs use -t.
- Run #17 was superseded; no test result is claimed.
- [Run #18](https://github.com/rahadul231/YouPlayerTube/actions/runs/43315807104): 11 actual Android tests executed on API 28 and 36; 10 passed/1 failed on each. The duplicate-source test found Room @Upsert may return without throwing after a secondary unique-key conflict/zero-row update. Fixed by checking source ownership atomically inside the repository transaction (d1afd91). Corruption/schema preservation, reopen, reactive/collection tests and Compose navigation passed in that run; the suite as a whole did NOT pass. Reporting guard now labels executed failures accurately.
- [Run #20](https://github.com/rahadul231/YouPlayerTube/actions/runs/43317284282): all 10 Room tests passed on API 28/36/37.0. API 28/36 also passed Compose navigation and both package-replacement tests. API 37 Compose failed inside old Espresso's removed InputManager.getInstance call; production persistence tests passed. Test-only dependencies updated to stable runner 1.7.0/Espresso 3.7.0; inspected Espresso bytecode uses Context.getSystemService on API 23+. The first-frame UI hierarchy timing error in the external script was addressed with bounded retries.
- [Run #21](https://github.com/rahadul231/YouPlayerTube/actions/runs/43318760036): 30 JVM tests passed; 11 Android database/UI tests plus two separately sequenced package-replacement tests PASSED on EACH of API 28/36/37.0. External restoration checker failed at identifying initial Home before killing a process. Therefore this run does not establish actual process-death restoration. The checker now identifies destinations by unique page content instead of platform XML selected flags; Compose tests still verify selected-tab semantics.
- [Run #22](https://github.com/rahadul231/YouPlayerTube/actions/runs/43320059842), source commit `48383842c9d701ce89371af10c99409636ac7a60`: ALL FOUR JOBS PASSED. 30 JVM tests executed; native outcomes below. Official Google metadata confirms system-images;android-37.0;google_apis;x86_64 is available.

## Android runtime verification — TESTED in run #22

| Runtime (x86_64 Google APIs emulator) | Room + Compose suite | Upgrade prepare/verify tests | External process checks |
|---|---:|---:|---|
| API 28 / Android 9 | 11 passed, zero failures/skips | 1 + 1 passed | Actual background process terminated; Library restored; force-stop/fresh launch Home passed |
| API 36 | 11 passed, zero failures/skips | 1 + 1 passed | Same checks passed |
| API 37.0 | 11 passed, zero failures/skips | 1 + 1 passed | Same checks passed |

Each 11-test suite contains TEN real Android Room tests and ONE Compose navigation/activity-recreation test. The two upgrade tests run separately and sequentially around `adb install -r`; they are not included in the 11. The same named cases repeat across three runtimes; do not misrepresent them as different unique test designs. External process checks are script assertions, not JUnit tests. run22-proof.json is derived from actual status/log output and records job IDs and `OK (11 tests)`, `OK (1 test)`, `OK (1 test)` evidence per API.

Raw AndroidJUnitRunner transcripts and converted JUnit XML are saved here for each API (run22-api*.txt and TEST-android-api*.xml). The converter requires complete nonzero status events and a matching final success count; failed, skipped, truncated and missing-status fixtures are rejected. These parser checks are not counted as Android tests. Full CI artifacts android-api-28-reports, android-api-36-reports and android-api-37.0-reports also retain raw prepare.txt/verify.txt, hierarchy, logcat and emulator output. verification-reports retains JVM/lint/signing records.

Verified Room behaviours: CRUD/reactive counts; sort/filter/bounds; local-vs-provider counts; source uniqueness/reclassification; FK constraints; atomic playlist reorder; history clear/media-file safety; malformed stored source handling; live count invalidation; on-disk close/reopen; corrupt original bytes retained; unsupported schema user table retained. Same-key rebuilt Phase 03.1 -> 03.2 install retained private marker data without uninstall. Actual Room metadata and DataStore sort then survived package replacement with the exact review APK. The external script confirms PID absence after background `am kill` before restoring the task; this is separate from rotation and force-stop.

Stable test-only runner 1.7.0/Espresso 3.7.0 resolved the API 37 reflection failure. The external checker identifies unique page content; selected-tab semantics remain covered by Compose assertions. No production fixtures, import/playback/download workarounds or hidden state-reset logic were added.

## Review APK

Download app-debug.apk from [run #22 Artifacts](https://github.com/rahadul231/YouPlayerTube/actions/runs/43320059842). Artifact ID: 13990157542; 12,820,308 bytes. Package com.youplayertube.app, version 0.0.4-phase03.2 / versionCode 3, minSdk 26, compile/target SDK 37. File SHA-256: `abb91d2a0fd91e8361f2434a6b0ba7dde790d751fb07b291f25c45b19cd8db65`.

**Signing gate: this APK's certificate differs from installed build #9. Do not uninstall the owner's app or clear data. It is a code-review/emulator artifact until a compatible key or explicitly approved migration exists.** Backup policy remains allowBackup=false. DataStore native libraries include armeabi-v7a, arm64-v8a, x86 and x86_64; this packaging check is not a physical Samsung runtime result.

## Upgrade/signing and unverified scope
Same-key emulator upgrade evidence is separate from the certificate of the installed Actions #9 APK. Run #22 review certificate: `3999c2cd4d407fbc88051860623bc5113b7f737f19386b4e9bd985cbc1b4686a`; build #9: `f9a814f1a36023ac619cefb88b816fe310a798ef83dd240bc8ef4b128efd04b5`. The existing Cloud candidate also does not match (see signing.md). Existing debug signing configuration is unchanged and uses per-runner key material unless deliberately retained. Exact old-key recovery/reusable-key setup requires secure owner-approved work (see ../../signing.md). Do not uninstall or clear the owner's app to bypass a mismatch. Manifest allowBackup=false is preserved.

Samsung acceptance for this APK, OEM battery/background features, TalkBack/200% font checks, performance measurements and final branding fidelity are NOT verified by JVM tests. No media import/player/download execution is implemented. A v1 database is new: no fabricated v0 migration test; future v1 -> v2 migration tests are required before such a schema change ships.
