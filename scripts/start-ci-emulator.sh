#!/usr/bin/env bash
set -euo pipefail
api="${1:?API level required}"
sdk_root="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
manager="$(find "$sdk_root/cmdline-tools" -type f -name sdkmanager -print -quit)"
[ -n "$manager" ] || { echo 'Android SDK command-line tools missing' >&2; exit 1; }
tools_bin="$(dirname "$manager")"
export PATH="$tools_bin:$sdk_root/platform-tools:$sdk_root/emulator:$PATH"
if [ -n "${GITHUB_PATH:-}" ]; then
  printf '%s\n' "$tools_bin" "$sdk_root/platform-tools" "$sdk_root/emulator" >> "$GITHUB_PATH"
fi
image="system-images;android-${api};google_apis;x86_64"
yes | sdkmanager --licenses >/dev/null || true
sdkmanager emulator "$image"
echo no | avdmanager create avd -n ypt-test -k "$image" --force
if [ ! -e /dev/kvm ]; then echo 'KVM is unavailable' >&2; exit 1; fi
sudo chmod 666 /dev/kvm
"$ANDROID_HOME/emulator/emulator" -avd ypt-test -no-window -no-audio -no-boot-anim -no-snapshot -gpu swiftshader_indirect -accel on -cores 2 -memory 2048 > emulator.log 2>&1 &
timeout 360 adb wait-for-device
timeout 360 bash -c 'until [[ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d "\r")" == 1 ]]; do sleep 2; done'
adb shell input keyevent 82
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
