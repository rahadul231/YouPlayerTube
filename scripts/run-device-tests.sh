#!/usr/bin/env bash
set -euo pipefail
# Use exactly the APK pair assembled together, with no Gradle rebuild after installation.
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -r -w -e notClass com.youplayertube.app.UpgradePersistenceTest com.youplayertube.app.test/androidx.test.runner.AndroidJUnitRunner | tee device-tests.txt
python scripts/instrumentation-report.py device-tests.txt app/build/outputs/androidTest-results/connected/TEST-device.xml
