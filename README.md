# MIUI-Home-DT2S

A dedicated, lightweight LSPosed / Xposed module for MIUI and HyperOS Launcher (`com.miui.home`) that enables **Double Tap to Sleep (DT2S)** on empty home screen workspace areas.

Unlike heavy multi-purpose modding suites, this module is strictly single-purpose: it provides native, immediate screen locking without interfering with other system gestures, lock screen settings, screen timeout behaviors, or battery life.

---

## Features

- **Double Tap to Lock:** Double tap on any empty area of the MIUI home screen to immediately lock the screen.
- **Icon & Widget Aware:** Intelligently checks `lastDownOnOccupiedCell`—only empty space triggers DT2S, preventing accidental locks when launching apps or dragging widgets.
- **Native Implementation:** Uses native broadcast intent locking (`com.miui.powerkeeper` / `DevicePolicyManager` lock intent) running directly inside `com.miui.home` without requiring external accessibility services.
- **Zero Battery Drain:** Only executes during active touch interactions on the home screen workspace.
- **Clean & Lightweight:** Scoped exclusively to `com.miui.home` with zero background services.

---

## Requirements

- **Device:** Xiaomi / Redmi / POCO running MIUI 13/14 or HyperOS (Android 12/13/14)
- **Root Solution:** APatch, KernelSU, or Magisk
- **Xposed Framework:** Zygisk LSPosed (v1.9.2+)

---

## Installation

1. Download [`MiuiHomeDoubleTap.apk`](MiuiHomeDoubleTap.apk) from this repository.
2. Install the APK on your device.
3. Open **LSPosed Manager** and enable **MIUI-Home-DT2S**.
4. Set the scope to **System launcher (`com.miui.home`)**.
5. Restart the launcher (`killall com.miui.home` or reboot).
6. Double tap on any empty space on your home screen to lock your device!

---

## Technical Architecture

The module hooks MIUI Launcher's internal touch pipeline:
- Targets `com.miui.home.launcher.Workspace`:
  - Hooks `onTouchEvent(MotionEvent)`
- Dispatches touch coordinates through `DoubleTapController`:
  - Slop window: `mTouchSlop` scaled by display metrics
  - Multi-tap time window: `<= 380 ms`
- Validates empty space via `CellLayout.lastDownOnOccupiedCell()`
- Executes device sleep via system lock intent broadcast `com.miui.powerkeeper` / system power management without needing root execution on every tap.

---

## Building from Source

Built using standard Android SDK tools without Gradle:

```bash
python build.py
```

Requirements:
- Java JDK 17 / 21
- Android SDK Platform `android-34`
- Android Build Tools `34.0.0` (`aapt`, `d8`, `zipalign`, `apksigner`)

---

## License

MIT License. Free for personal and community use.
