# YouPlayerTube — Development State

Updated: 2026-11-12 (Asia/Dhaka). This file is the authoritative progress record.

## Status definitions
- PLANNED: approved scope without completed code.
- IMPLEMENTED: code exists, validation incomplete.
- TESTED: named checks executed with evidence; scope and environment recorded.
- BLOCKED: prerequisite missing; reason and next action recorded.

## Baseline reconciliation
Phase 01: APPROVED and COMPLETE on 2026-11-07. All 58 requirements YPT-001–YPT-058 and the original ten-screen design remain unchanged.
Phase 02: TESTED cloud build foundation. GitHub main baseline b689cd0ef3643150fd63aae6b5679d29591b44a7.
- Actions [build #6](https://github.com/rahadul231/YouPlayerTube/actions/runs/42660941551): successful debug assembly, APK upload and Gradle 9.6.0 wrapper execution/assembly.
- APK SHA-256: a77cb9c15d2e5875a711a559dc1b1c0d09973bdc5e718490c87eaa5b4a3d06fd.
- User confirmed installation and launch on Samsung Galaxy J4+ SM-J415F, Android 9/API 28. This is user-reported device evidence, not a Codex device test.
- Unit-test task reported NO-SOURCE: zero tests executed; no passing test suite was established.
- Release signing, release APK and modern-device behaviour remain PLANNED.

## Phase 03.1
APPROVED by user on 2026-11-12. IMPLEMENTED: MVVM foundation, four-tab navigation, per-tab scroll state and selected-tab saved state, honest empty screens, typed source boundaries and eligibility contracts. No merge authorized.

TESTED in Codex Cloud with JDK 17 and Gradle wrapper 9.6.0: debug assembly, instrumentation APK compilation, 17 JVM tests (0 failures/errors/skips), lint (0 errors, 6 warnings). [Raw evidence](test-evidence/phase-03-1/README.md). Navigation model and SavedStateHandle transitions are unit-tested; real activity UI instrumentation is compiled but NOT EXECUTED. No physical-device test of this APK is claimed.

PLANNED: user Samsung acceptance and next state/persistence milestone. BLOCKED: instrumented runtime tests (no configured emulator/system image/device; adb also cannot initialize its default preferences directory). [Review PR #1](https://github.com/rahadul231/YouPlayerTube/pull/1) targets main from codex/phase-03-1-foundation; merge requires user approval. Final Cloud review build also passed after correcting the wide-screen content constraint.

## Preserved scope
Home, Downloads, Library, Settings; authorized YouTube search/embed/fallback; native local/authorized playback, PiP/background audio, authorized downloads/real qualities, library, playlists, favorites, history, timer and settings remain PLANNED unless explicitly evidenced below.
No YouTube extraction/downloads, ad interference, forced background play or DRM bypass. Account features, MP3 conversion and other platforms remain deferred.

## BLOCKED / pending evidence
- Original visual PDF, logo masters and playbook are absent from repository. Obtain originals before pixel/branding acceptance; written visual decisions remain baseline.
- Physical verification of Phase 03.1 awaits user installation. Phase 02 evidence does not apply to the new APK.
- YouTube credentials, identity/Media Integrity and quota checks await the approved online spike.
- targetSdk 37 is provisional; platform behaviour review is still required.

## Historical Phase 01/02 record (superseded status statements)
The following original record is retained as history. Statements about an empty repository, missing wrapper, unrun builds and unknown phone model are superseded by the reconciliation above.

# YouPlayerTube — Development State
Updated UTC: 2026-11-07 13:20 (local 2026-11-07 19:20 Asia/Dhaka)

## Current phase
Phase 01 — Requirements and feasibility (documentation only): **APPROVED and COMPLETE (2026-11-07)**.
Phase 02 — Windows environment and first build: **IN PROGRESS. Milestone 2a DONE (audit, workflow choice, documentation-level toolchain check). Milestone 2b PREPARED, UNTESTED: minimal project + workflow files written, not yet uploaded or run. Next: user uploads files to the repo, runs the workflow, pastes the result.**

## Approved decisions (2026-11-07)
| ID | Decision |
|---|---|
| Q1 | YouTube-focused Home, no address bar or chooser; Data API search + IFrame embed + Open-in-YouTube fallback (feasibility.md section 3, APPROVED) |
| Q3 | Personal sideload on Samsung; Google Play not required; GitHub source sharing possible |
| Q4 | Samsung phone + Windows PC; specs supplied (see Phase 02 log) |
| Q7 | Supplied logo is the branding reference; transparent/vector version prepared in Prompt 05 |
| Q8 | Wording separates native features from YouTube restrictions; name stays YouPlayerTube |
| Q9 | Keep Privacy & Security row in Settings |
| Q10 | Package `com.youplayertube.app`; Hilt only if justified and compatible with verified versions (Prompt 03) |
| Q13 (new) | **Primary build workflow: GitHub Actions cloud build, sideload APK to Galaxy J4+.** No Android Studio, no emulator, no local Gradle on the 4 GB laptop (user preference, 2026-11-07) |
Target: Android only, Kotlin, original ten-screen mockup, supplied orbital-play logo. Devices: Galaxy J4+ (API 28) plus newer Samsung/Xiaomi/Redmi/Realme/Vivo/Oppo/OnePlus/Pixel.

## Repository / commit status
Repository https://github.com/rahadul231/YouPlayerTube: **VERIFIED 2026-11-07 ~19:05 local by page fetch: public, empty ("This repository is empty"), no workflow file.** (Earlier statement that Actions preparation had started is not supported by the repo: it is empty.) Nothing committed. Commit hash: none. Default branch name unknown until first commit (workflow assumes `main`).

## Actual files created/modified
Phase 01: docs/requirements.md (YPT-001–058), docs/feasibility.md, docs/visual-qa.md, docs/provider-boundaries.md, docs/progress.md.
Phase 02 (prepared, UNTESTED, delivered as zip, not yet in any repo): settings.gradle.kts, build.gradle.kts, app/build.gradle.kts, gradle/libs.versions.toml, gradle.properties, app/src/main/AndroidManifest.xml, app/src/main/res/values/strings.xml, app/src/main/res/values/themes.xml, app/src/main/java/com/youplayertube/app/MainActivity.kt, .gitignore, .github/workflows/android.yml, docs/versions.md, docs/progress.md.
Not created yet: gradlew, gradlew.bat, gradle/wrapper/* (need Gradle to generate; planned after first successful run), launcher icon (Prompt 05), unit tests (Prompt 03). `:app:testDebugUnitTest` will report no test sources.
Static checks run in the Claude sandbox only: YAML parse, TOML parse, XML well-formedness (results in the Phase 02 log). No Gradle build was possible there.

## Completed tests / verification evidence
- Phase 01: mockup PDF viewed; official YouTube pages fetched 2026-11-07.
- Phase 02: laptop PowerShell audit (2 runs) and phone Storage/Memory screenshots inspected. Documentation-level toolchain verification below.
- Gradle builds, GitHub Actions runs, APK installs, app tests: **none run. UNTESTED.**

## Phase 02 log (2026-11-07)

### Laptop audit — VERIFIED from user's screenshots
| Item | Result |
|---|---|
| OS / CPU | Windows 10 Pro 64-bit build 19045; Intel Core i3-7130U (user-reported, not re-measured) |
| RAM | Total 4,011 MB, available 594 MB at test time (systeminfo) |
| Disks | HP SSD S700 120 GB (112 GB, SSD); TOSHIBA MQ01ABD100 (932 GB, HDD) |
| Free space | C: 45 GB, D: 91 GB, E: 387.4 GB, F: 117.5 GB |
| java / git / adb on PATH | Not found (Get-Command errors for all three) |
| JAVA_HOME / ANDROID_HOME | Empty |
| Android Studio | `C:\Program Files\Android\Android Studio` exists with bin, jbr, lib, license, modules, plugins, build.txt, product-info.json, uninstall.exe. Version not read. Earlier claim "no installer entry, may be leftover" is superseded: uninstall.exe present suggests a normal install; registry search likely missed it (per-user entry or different key). Not used for this project |
| Bundled JBR | `openjdk 25.0.4 2026-07-21` runs. Not used: build JDK is 17 in CI |
| Android SDK | `%LOCALAPPDATA%\Android\Sdk`, 4.16 GB: .temp, build-tools, emulator, licenses, platform-tools, platforms, sources, system-images, .knownPackages. Versions inside not listed. Not used for CI; platform-tools may provide adb.exe (UNVERIFIED) |
| Below Android Studio official minimum (8 GB RAM; U-series CPU discouraged) | Yes, so Android Studio is not recommended |

### Phone — user screenshots
Galaxy J4+ (model number NOT yet provided), Android 9 / API 28 (user-reported). Storage 32 GB total, 13.2 GB available. RAM 2 GB total, 745 MB available.

### Toolchain compatibility (documentation level; official pages fetched 2026-11-07)
| Item | Chosen | Evidence | Status |
|---|---|---|---|
| AGP | 9.5.0 | developer.android.com AGP 9.5.0 notes, titled October 2026, rc01/rc02 then 9.5.0 entry, last updated 2026-10-28. AGP 9.6 is a preview page | 9.5.0 documented as released. **9.5.1 UNVERIFIED** (playbook claim; no source found) |
| Gradle | 9.6.0 | AGP 9.5.0 notes: minimum and default 9.6.0. 9.6.1 exists (secondary sources) | Verified minimum; 9.6.1 not chosen |
| JDK in CI | Temurin 17 | AGP 9.5.0 notes: JDK minimum and default 17. Gradle runs on JVM 17–25 (Gradle compatibility page snippet) | Verified |
| SDK Build Tools | 36.0.0 (AGP default) | AGP 9.5.0 notes | Verified |
| compileSdk | 37 | AGP 9.5 max API 37; Compose 1.12.0+ requires compileSdk 37 and AGP 9 | Verified |
| targetSdk | 37 provisional | Playbook only; Android 17 behaviour changes not reviewed | UNVERIFIED; revisit before Prompt 08/09 |
| minSdk | 26 | Design decision; covers J4+ (API 28) | Decision |
| Kotlin plugin strategy | AGP built-in Kotlin; do NOT apply org.jetbrains.kotlin.android | AGP "Migrate to built-in Kotlin" page | Verified |
| Kotlin | 2.4.10 | Compose compiler setup page (last updated 2026-10-28) | Documented. AGP 9.5's own bundled KGP version not found; AGP 9.0 bundled 2.2.10. To use 2.4.10 add KGP to the top-level buildscript classpath (AGP guidance). Compatibility proven only by a real build |
| Compose compiler plugin | `org.jetbrains.kotlin.plugin.compose` 2.4.10 (same as Kotlin) | Same page | Verified as documented |
| Compose BOM | 2026.09.00 | Same page ("latest") | Verified |
| activity-compose / lifecycle-viewmodel-compose | 1.13.0 / 2.11.0 | Same page | Verified as documented |
| GitHub runner | `ubuntu-24.04` (pinned) | GitHub changelog 2026-09-17: ubuntu-latest migrating 24.04 to 26.04 gradually Oct 19–Nov 19, 2026. Runner default Java differs on 26.04 (secondary source) | Verified (changelog snippet). Do NOT use ubuntu-latest |
| Actions | checkout@v7, setup-java@v6, setup-gradle@v6, upload-artifact@v7 | Secondary sources only (repo mirrors, dependabot PRs, release listings). upload-artifact v7 supports uploading a single file unzipped (`archive: false`) per release notes | UNVERIFIED until the first run resolves them |
| Android SDK on runner (platform 37, licenses) | Unknown | Not checked | UNVERIFIED; workflow will print SDK state and install platform via sdkmanager if needed |
Playbook values superseded: Android Studio "Rabbit 2" (irrelevant), AGP 9.5.1 (unverified), Gradle wrapper (replaced by setup-gradle for first build).

### Workflow decision
GitHub Actions (Option A) is primary. "Working" status requires: successful run, downloaded APK, installed and launched on the J4+. Hand-written Gradle config replaces an Android Studio template (playbook warns this is riskier); mitigated by pinning only documented versions and treating the first run as the test.

### Milestone 2b — prepared 2026-11-07 (UNTESTED)
- Prompt 02 text received from user; identical to the playbook's Part G Prompt 02. Adapted: no Android Studio/emulator/gradlew.bat for the first build; Gradle 9.6.0 via setup-gradle; wrapper deferred; Git baseline deferred (web upload).
- Workflow `.github/workflows/android.yml`: runner ubuntu-24.04, JDK 17, Gradle 9.6.0, `:app:assembleDebug`, `:app:testDebugUnitTest`, APK inspection (aapt2 badging), unzipped APK artifact, build-reports on failure. Diagnostic SDK printout and best-effort sdkmanager step included.
- The app shows Android version, API level and manufacturer/model, so the first launch also reveals the exact J4+ model number.
- Success criteria: green run + downloaded APK + installs and launches on the J4+. None met yet.

### Open items
- User: upload files to the repo, run workflow (Actions tab), paste run result (green or the first error).
- Samsung exact model number (the app will display it after install).
- Gradle wrapper (gradlew, gradle-wrapper.jar) cannot be generated without Gradle; generate in Actions after first success.
- Verify action tags (checkout@v7, setup-java@v6, setup-gradle@v6, upload-artifact@v7) by the run itself.
- API quota/Media Integrity/PiP open questions Q5, Q6, Q11 remain for Prompts 06 and 08.

## Known provider restrictions
No YouTube download/caching, audio separation, ad interference, player modification, background play; see provider-boundaries.md.

## Currently working (tested)
Nothing.

## Next milestone
Phase 02 milestone 2c: first Actions run result (green or first error), then APK download, install and launch on the J4+. Prompt 03 is NOT started. Secrets/credentials shared? NO
