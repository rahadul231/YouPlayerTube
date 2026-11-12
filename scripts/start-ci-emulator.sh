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
export ANDROID_AVD_HOME="${RUNNER_TEMP:-/tmp}/ypt-avd"
mkdir -p "$ANDROID_AVD_HOME"
echo no | avdmanager create avd -n ypt-test -k "$image" -p "$ANDROID_AVD_HOME/ypt-test.avd" --force
# Explicit ini location bridges command-line-tools/emulator location differences.
printf 'avd.ini.encoding=UTF-8\npath=%s\n' "$ANDROID_AVD_HOME/ypt-test.avd" > "$ANDROID_AVD_HOME/ypt-test.ini"
if [ ! -e /dev/kvm ]; then echo 'KVM is unavailable' >&2; exit 1; fi
sudo chmod 666 /dev/kvm
"$ANDROID_HOME/emulator/emulator" -avd ypt-test -no-window -no-audio -no-boot-anim -no-snapshot -gpu swiftshader_indirect -accel on -cores 2 -memory 2048 > emulator.log 2>&1 &
emulator_pid=$!
connected=false
for attempt in $(seq 1 180); do
  if ! kill -0 "$emulator_pid" 2>/dev/null; then tail -100 emulator.log >&2; exit 1; fi
  if timeout 5 adb get-state 2>/dev/null | grep -qx device; then connected=true; break; fi
  sleep 2
done
if [ "$connected" != true ]; then tail -100 emulator.log >&2; exit 1; fi
trap 'tail -100 emulator.log >&2' ERR
timeout 360 bash -c 'until [[ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d "\r")" == 1 ]]; do sleep 2; done'
adb shell input keyevent 82
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
