# YouPlayerTube

Existing Android app, package `com.youplayertube.app`. Phase 03.2 adds a Room v1 library foundation and a persistent Library sort preference to Home, Downloads, Library and Settings. Library observes real metadata/counts; a fresh database is empty. Media import, playback, download execution and collection editor screens are not implemented yet.

Home is planned to search YouTube and play videos in YouTube's own embedded player. Downloads, native PiP and background play are for owned or authorized native media only. No extraction, ad blocking or DRM bypass. This independent application is not affiliated with YouTube or Google.

## Cloud development
Use JDK 17, Android SDK 37/build-tools 36.0.0 and the checked-in Gradle 9.6.0 wrapper. In the configured Codex Cloud environment:

```sh
source /workspace/setup/android-env.sh
./gradlew --no-daemon :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug
```

Read [signing and upgrade safety](docs/signing.md) before installing: default debug signing does not guarantee compatibility with previously installed Actions APKs. No uninstall or key change is authorized.

No Android Studio or Gradle build is required on the user's 4 GB Windows laptop. GitHub Actions runs wrapper builds/unit tests/lint and API 28/36 emulator database/UI/upgrade/restoration jobs, rejects zero-executed-test results and retains reports. Check each job result; compilation is not test execution. Download `app-debug.apk` from a successful PR Actions run. Debug signing is for review; a stable release signing key remains planned.

Instrumented UI tests require a connected Android device/emulator:

```sh
bash scripts/run-device-tests.sh
```

See [progress](docs/progress.md), [architecture](docs/architecture.md), [all 58 approved requirements](docs/requirements.md), [provider boundaries](docs/provider-boundaries.md), [visual QA](docs/visual-qa.md), and [Phase 03.2 Samsung test checklist](docs/phase-03-2-device-checklist.md). Original visual PDF/logo assets are missing; no substitute branding has been invented.
