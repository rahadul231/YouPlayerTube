# YouPlayerTube

Existing Android app, package `com.youplayertube.app`. Phase 03.1 provides an MVVM foundation and Home, Downloads, Library and Settings navigation. Feature screens are honest empty/planned states; no media playback or downloads are implemented yet.

Home is planned to search YouTube and play videos in YouTube's own embedded player. Downloads, native PiP and background play are for owned or authorized native media only. No extraction, ad blocking or DRM bypass. This independent application is not affiliated with YouTube or Google.

## Cloud development
Use JDK 17, Android SDK 37/build-tools 36.0.0 and the checked-in Gradle 9.6.0 wrapper. In the configured Codex Cloud environment:

```sh
source /workspace/setup/android-env.sh
./gradlew --no-daemon :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug
```

No Android Studio or Gradle build is required on the user's 4 GB Windows laptop. GitHub Actions runs the wrapper, assembles the app/test APKs, runs unit tests and lint, rejects zero-test results and retains verification reports. Download `app-debug.apk` from a successful PR Actions run. Debug signing is for review; a stable release signing key remains planned.

Instrumented UI tests require a connected Android device/emulator:

```sh
./gradlew :app:connectedDebugAndroidTest
```

See [progress](docs/progress.md), [architecture](docs/architecture.md), [all 58 approved requirements](docs/requirements.md), [provider boundaries](docs/provider-boundaries.md), [visual QA](docs/visual-qa.md), and [Samsung test checklist](docs/phase-03-1-device-checklist.md). Original visual PDF/logo assets are missing; no substitute branding has been invented.
