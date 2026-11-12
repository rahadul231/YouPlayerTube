# Phase 03.1 Samsung acceptance checklist

Status: PLANNED; user must report results for this APK. Phase 02 launch evidence does not verify Phase 03.1.

1. Download app-debug.apk from the PR's successful Android debug build run. Check the run commit matches the PR head. Install on SM-J415F / Android 9. If Android reports a signature conflict, do not uninstall automatically: report it; debug signing identities can differ between Cloud and Actions.
2. Launch: Home appears, black background/red accents, no crash or fabricated videos.
3. Tap Downloads, Library, Settings and Home. Selected label/icon changes. Library counts are zero; Downloads has an empty state; Settings lists seven planned sections.
4. Back from each non-Home tab returns Home. Back from Home exits according to Android. Relaunch returns a valid Home/restored state.
5. On Settings scroll down, switch tabs and return: scroll position remains. Rotate on each tab and verify selected tab/content. For actual process restoration: background the app, use adb shell am kill com.youplayertube.app if available, then restore from Recents. Do not equate activity recreation or force-stop with process-restoration testing.
6. Increase font size to 200%; confirm navigation labels and primary content remain usable by scrolling. Test portrait/landscape, gesture/three-button navigation where available, TalkBack selection labels and tap targets.
7. Confirm no playback, download, background service, broad-storage permission or Google login is claimed or requested.
8. Record APK SHA-256/run URL, device/OS, steps, pass/fail and screenshots. Startup/tab timing targets remain unmeasured until collected.

Automated device suite, when connected: sh ./gradlew :app:connectedDebugAndroidTest. The test checks tab semantics, empty states, activity recreation and Back; it does not establish real process-death, OEM background behaviour or full accessibility acceptance.
