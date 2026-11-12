# Phase 03.2 review and Samsung acceptance

Status: PLANNED until user reports results for the new APK. Do not carry Phase 03.1 results forward.

## Signing gate before installation
Read signing.md and the PR's signing-result report. Do not uninstall the installed YouPlayerTube app or clear data. Do not use a different application ID without approval. If the new certificate differs from installed build #9, an in-place update is blocked; stop installation planning and resolve original-key recovery or an explicitly approved migration. A certificate match still requires the physical installation check. No signing key/password should be sent in chat or committed to Git.

## Once a compatible upgrade is available
1. Record run URL, APK hash/version (0.0.4-phase03.2, versionCode 3) and device SM-J415F/API 28. Install over the existing app without uninstalling. Report signature/version failures without deleting anything.
2. Fresh launch opens Home. Four tabs, Back, scrolling and rotation behave as Phase 03.1. Library shows real empty counts; there is no media-import UI or demo media.
3. Choose Sort by name in Library. Switch tabs, rotate, return: choice remains. Background and restore from Recents: navigation/scroll should restore where Android saves the task.
4. Force-stop/relaunch: Home opens; revisit Library and confirm persisted sort. Reboot and confirm the sort remains. Fresh-task navigation is distinct from persistent preferences.
5. If ADB is available, test actual process death: background first, use am kill (not force-stop), confirm the process terminated and restore the task. Record exactly what was tested; rotation alone is not process-death evidence.
6. Test TalkBack, large font settings/200%, portrait/landscape and usable tap targets. Observe startup/tab responsiveness; do not declare timing targets passed without measurements.
7. Confirm no broad-storage prompt, fake playback/download, background service or Google login. Do not intentionally corrupt/delete the real database to test errors on your phone; these checks belong in isolated emulator test data.
8. Report pass/fail, screenshots and any errors. Real media import, playlist editors, playback and downloads remain later work, so the phone checklist cannot verify those features yet.

Real Room/upgrade tests seed isolated emulator fixtures through the test APK only; the production app contains no fixture insertion controls. Samsung acceptance does not require running Gradle or an emulator on the 4 GB laptop.
