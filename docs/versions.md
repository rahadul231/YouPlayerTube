## Phase 03.2 verified compatibility gate — 2026-11-13

Selected stable Android-only Room 2.8.5 (androidx.room), KSP 2.3.13, Preferences DataStore 1.3.0, coroutines-test 1.10.2. Existing JDK 17, wrapper 9.6.0, AGP 9.5.0, Kotlin/Compose 2.4.10, lifecycle/Compose pins and SDK values retained. Official Maven metadata and release pages confirmed stable artifacts. Room 3's new namespace/API line would require more structural change; this application needs no KMP module, so the supported 2.8 stable Android line was chosen and integration-tested rather than assuming the new major is necessary.

Gate used a minimal Room entity/DAO/database with KSP generation and :app:assembleDebug/:app:testDebugUnitTest. BUILD SUCCESSFUL in 2m 10s, 17 original JVM tests executed. Full schema was implemented only after that result. Probe removed. See test-evidence/phase-03-2/compatibility-gate.txt and final report for later results. No signing/backup policy or minSdk change.

Sources: https://developer.android.com/jetpack/androidx/releases/room ; https://github.com/google/ksp/releases ; https://developer.android.com/jetpack/androidx/releases/datastore ; official Maven version metadata. All dependency additions compile with the existing toolchain; device behaviour is a separate test scope.

## Phase 03.1 integration verification
Cloud wrapper build on 2026-11-12 succeeded using JDK 17.0.12. Added lifecycle-viewmodel-compose/runtime-compose 2.11.0, JUnit 4.13.2, Compose UI test artifacts aligned to existing BOM, Android test runner 1.6.2. App and instrumentation APKs compile; 17 JVM tests pass. Device execution remains unverified. Existing toolchain pins retained. See test-evidence/phase-03-1/README.md for scope and warnings.

# Verified toolchain evidence — 2026-11-12

Actions [build #6](https://github.com/rahadul231/YouPlayerTube/actions/runs/42660941551), commit b689cd0ef3643150fd63aae6b5679d29591b44a7, successfully assembled the existing configuration: AGP 9.5.0, Kotlin/Compose compiler 2.4.10, Compose BOM 2026.09.00, activity-compose 1.13.0, JDK 17, Gradle 9.6.0, compileSdk 37, minSdk 26, targetSdk 37 provisional. Wrapper files exist and wrapper execution/build succeeded. Existing Actions tags resolved successfully. Unit tests were NO-SOURCE, not passing tests.

Cloud has JDK 17 at /workspace/toolchains/jdk17 and SDK platform android-37.0/build-tools 36.0.0. Source /workspace/setup/android-env.sh before builds; default system Java is 21. Dependency additions are verified only by subsequent builds/tests, recorded in progress.md.

## Historical version-selection notes
The original notes below predate build #6; their UNTESTED/missing-wrapper statements are superseded above. Pinned values remain in the version catalog and build files.

# YouPlayerTube — Selected Versions (Phase 02)

Updated: 2026-11-07. Source of truth for pinned values: `gradle/libs.versions.toml` (+ the KGP line in `build.gradle.kts`).
"Verified" means read from an official page on 2026-11-07. **Nothing here is proven to build together until the first GitHub Actions run succeeds (UNTESTED).**

| Item | Value | Evidence | Status |
|---|---|---|---|
| Build host | GitHub Actions `ubuntu-24.04` | GitHub changelog 2026-09-17: `ubuntu-latest` migrates to 26.04 between Oct 19 and Nov 19, 2026 | Verified; pinned |
| JDK | Temurin 17 | AGP 9.5.0 notes: minimum and default 17 | Verified |
| AGP | 9.5.0 | AGP 9.5.0 release notes (October 2026, last updated 2026-10-28). 9.5.1 not found in any source; 9.6 is preview | Verified as released |
| Gradle | 9.6.0 | AGP 9.5.0 notes: minimum and default 9.6.0 | Verified. First build uses `setup-gradle` with this version; wrapper added after first success |
| SDK Build Tools | 36.0.0 | AGP 9.5.0 default | Verified |
| compileSdk | 37 | AGP 9.5 max API 37; Compose 1.12+ requires compileSdk 37 and AGP 9 | Verified |
| targetSdk | 37 | Playbook | **Provisional / UNVERIFIED** |
| minSdk | 26 | Design decision (Galaxy J4+ is API 28) | Decision |
| Kotlin plugin strategy | AGP built-in Kotlin; no `org.jetbrains.kotlin.android` | AGP "Migrate to built-in Kotlin" | Verified |
| Kotlin (KGP on buildscript classpath) | 2.4.10 | Compose compiler setup page. AGP 9.5's bundled KGP version not stated in the sources found | Documented; **compatibility with AGP 9.5 proven only by a build** |
| Compose compiler plugin | `org.jetbrains.kotlin.plugin.compose` 2.4.10 | Same page (must equal Kotlin version) | Documented |
| Compose BOM | 2026.09.00 | Compose setup page | Verified as documented |
| activity-compose | 1.13.0 | Compose setup page | Verified as documented |
| GitHub Actions | checkout@v7, setup-java@v6, setup-gradle@v6, upload-artifact@v7 | Secondary sources (release mirrors, dependabot PRs) | **UNVERIFIED until the run resolves them** |
| Android SDK on runner | platform 37, build-tools 36.0.0 | Not checked | UNVERIFIED; workflow prints SDK state and tries `sdkmanager` (best effort) |

## If the first run fails (likely suspects, in order)
1. An action tag does not exist (error: "Unable to resolve action"): change the tag (e.g. `setup-java@v5`).
2. Android SDK platform 37 / licenses missing on the runner.
3. Kotlin 2.4.10 vs AGP 9.5 built-in Kotlin conflict: remove the `buildscript` KGP block in `build.gradle.kts` and retest, or align `kotlin` in the catalog to what AGP bundles.
4. `android.useAndroidX` / DSL deprecation errors from AGP 9.5.

Wrapper files (`gradlew`, `gradlew.bat`, `gradle/wrapper/*`) are not included yet: they cannot be generated without Gradle. Added after the first successful run.
