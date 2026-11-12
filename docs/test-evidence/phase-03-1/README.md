# Phase 03.1 Codex Cloud evidence

Date: 2026-11-12. JDK 17.0.12, checked-in Gradle wrapper 9.6.0, Android platform android-37.0/build-tools 36.0.0.

Command: `sh ./gradlew --no-daemon :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug`

Result: BUILD SUCCESSFUL in 1m 50s; 82 actionable tasks. 17 JVM tests, zero failures/errors/skips. Raw JUnit XML and lint XML accompany this record. Tests cover navigation decisions, saved-state ViewModel transitions, source boundaries and rights eligibility; they do not prove rendered Android UI or real process death.

Both debug APK and instrumentation test APK generated. App APK SHA-256: `57799bb2d36320d2c7bfc922c7cfd0ac3d715be543d6edf8d383de538ffb674f`. App version 0.0.3-phase03.1, versionCode 2. This hash identifies the Cloud artifact; separately built Actions APKs may differ (including debug signing).

Lint: zero errors, six warnings: newer Compose compiler available (existing pin preserved), Android backup data extraction rules, missing approved launcher icon, three version-catalog recommendations. Compiler also warns the Compose test rule has a newer v2 API; the compiled rule is retained for this milestone.

NOT EXECUTED: connected instrumentation, physical Samsung acceptance, real process-death, screenshot/accessibility/performance tests. No emulator/system images or /dev/kvm are installed; adb startup also attempts a write to read-only /home/agent/.android. No device verification is claimed.

GitHub Actions retains full HTML/JUnit/lint reports in verification-reports and the app APK separately. The approved original requirements and visual documents were verified unchanged against b689cd0.
