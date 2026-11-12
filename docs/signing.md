# Signing and safe upgrade decision — Phase 03.2

Updated 2026-11-13 (Asia/Dhaka). No custom signing configuration, key generation, key rotation or uninstall is authorized by this document.

## Inspected evidence
The main baseline's app/build.gradle.kts defines no signingConfigs or custom signing assignment. Its workflow assembles the debug variant and references no signing secrets. Android tooling therefore uses its default debug signing identity. Codex Cloud has an existing private debug.keystore outside Git at /workspace/toolchains/android-user/debug.keystore; this is not proof it matches the APK installed from Actions build #9. Gradle dependency caching does not establish retention of the Actions runner's ~/.android/debug.keystore.

We cannot infer that GitHub Secrets is empty from the build files: no existing workflow consumes signing secrets, but secret inventory/key recoverability is unverified. The owner reports no personally configured reusable signing key. Build #9 artifact contains a public certificate, not the private keystore. A private key cannot be recovered from that certificate/APK. If a runner was ephemeral and no secure key backup/cache was retained, that key may be unavailable. Do not claim it is recovered or lost without evidence.

Current branch keeps the existing signing configuration unchanged. It adds a certificate comparison against build #9 and a separate same-key emulator upgrade test. The latter establishes update/data behaviour only for APKs built with the same controlled key in one CI job; it does NOT establish compatibility with the owner's installed APK. The manifest's allowBackup=false remains unchanged.

## Recommended reusable configuration — approval/setup required
1. First search secure backups/runner retention or any deliberately configured secret for the exact build #9 keystore. Compare certificate SHA-256 before using a candidate; never rotate or replace the candidate by assumption.
2. If available, securely retain that key for this package's upgrade line. Otherwise choose a new, long-lived signing key only after the owner approves the migration consequences.
3. Generate/retain a private keystore in secure storage, never the repository or a publicly downloadable Actions artifact. Keep encrypted independent backups, recovery instructions, alias and public certificate fingerprint. Never print passwords/private key material in logs.
4. Supply it to trusted CI through GitHub encrypted secrets (suggested names ANDROID_KEYSTORE_BASE64, ANDROID_KEYSTORE_PASSWORD, ANDROID_KEY_ALIAS, ANDROID_KEY_PASSWORD). Base64 alone is not encryption. Materialize only in RUNNER_TEMP with restricted permissions, sign using injected secrets, remove the temporary file, and verify the expected certificate on every release/review APK.
5. Restrict secret access to reviewed, trusted workflow executions; do not run secret-bearing builds of arbitrary fork code or use pull_request_target to execute untrusted PR sources. Design the signing job/environment for owner review before enabling it.
6. No private keystore, passwords or secret values enter public Git history. The existing signing setup has NOT been changed to this proposal.

## If the build #9 key cannot be recovered
Android requires a compatible signing identity for an in-place update. A newly generated key cannot transparently update that installation under the same package. Signing lineage/key rotation also requires control of the old key; it is not a recovery bypass, particularly on the Android 9 test device.

Safest sequence: leave the installed application intact while reviewing code and testing persistence in isolated emulators. Determine whether it has valuable data before any migration decision. Phase 03.1 did not contain Room/DataStore persistence or media import, but do not assume the device has no valuable state. With allowBackup=false, do not promise a normal Android backup or export path that does not exist. ADB access/export might be possible for a debuggable app but must be assessed and explicitly authorized; never uninstall to discover what was lost.

If needed, agree on and verify an explicit user-authorized export/restore procedure. A temporary separate application ID for side-by-side migration would be a scope/package change and requires separate approval; none is implemented here. If the owner ultimately accepts starting a new signing line by uninstall/reinstall, explain that uninstall deletes private app data and URI grants, verify any available exports first, then obtain explicit approval. Codex will not perform that uninstall or silently change the package/key.

## Current review boundary
Persistence development and emulator validation may continue with the unchanged debug configuration after this documentation. The review APK is not claimed installable over build #9 until certificate equality is demonstrated. New reusable signing/migration configuration remains PLANNED pending owner approval and secure key setup. No device uninstall, data extraction or signing change is performed.
