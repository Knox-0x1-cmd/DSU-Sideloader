# DSU Sideloader

A simple app to help users easily install GSIs via DSU's Android feature.

<div>
<img src="https://raw.githubusercontent.com/Knox-0x1-cmd/DSU-Sideloader/main/other/preview_1.png" alt="preview" width="200"/>
<img src="https://raw.githubusercontent.com/Knox-0x1-cmd/DSU-Sideloader/main/other/preview_2.png" alt="preview" width="200"/>
</div>

## Requirements
- Android 10 or higher
- Unlocked Bootloader
- Device with Dynamic Partitions
- A GSI you want to use!

Google GSIs: https://developer.android.com/topic/generic-system-image/releases

**Remember to use GSIs compatible with your architecture, VNDK implementation...**

## Downloads

Download the latest APK from the [Releases Section](https://github.com/Knox-0x1-cmd/DSU-Sideloader/releases/latest).
For testing builds, check artifacts at [Actions](https://github.com/Knox-0x1-cmd/DSU-Sideloader/actions) tab.

## How to Use

1. Install app
2. On first launch, grant read/write permission to a folder (used for temporary files like extracted GSIs)
3. Select a GSI to install  
   **Accepted formats**: `gz`, `xz`, `img`, `zip` , `lz4` , `bzip2` , `bz2` (DSU packages only)
4. Customize installation if needed  
   - Adjust userdata size for dynamic system  
   - Image size is auto-calculated (manual override not recommended)
5. Tap "Install"
6. Wait for completion
7. Post-install steps vary by operation mode:
   - **Built-in installer enabled**: No extra step
   - **Root/System/Shizuku**: DSU screen appears → confirm → check notifications
   - **ADB**: Run provided command → confirm on DSU screen

See [Operation Modes](#operation-modes) for details.

## Operation Modes

DSU Sideloader automatically picks the best available mode (priority order):

1. **ADB** (default): Prepare image → requires ADB command to start install
2. **Shizuku**: Same as ADB but no ADB command needed; tracks progress; diagnostics
3. **Root**: All Shizuku features + DynamicSystem API (check/install/discard DSU) + built-in installer
4. **System** (Magisk module): All Shizuku features + SELinux fixes + custom gsid binary
5. **System+Root** (Magisk module + root): All features combined

**Notes:**
- ¹ Requires READ_LOGS permission
- ² Partial support on Android 10/11
- ³ Android 13 requires "One-time log access"
- ⁴ Built-in installer not supported on Android 10
- ⁵ Experimental — code [here](https://github.com/Knox-0x1-cmd/DSU-Sideloader/blob/main/app/src/main/java/vegabobo/dsusideloader/installer/root/DSUInstaller.kt)
- ⁶ Custom gsid binary changes [here](https://github.com/Knox-0x1-cmd/DSU-Sideloader/tree/main/magisk-module/src/main/resources/aosp_patches)

### Recommendations

- **Non-rooted**: Use Shizuku (install [Shizuku app](https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api))
- **Rooted**: Root mode is sufficient for most
- **Issues with DSU**: Try System+Root
- **Magisk**: v24+ recommended (older may break DSU)
- **Stock ROM** recommended; some custom ROMs work

## Common Questions

1. **Install succeeds but device doesn't boot into DSU**  
   Likely AVB blocking. Try flashing disabled vbmeta. See [Android docs](https://developer.android.com/topic/generic-system-image#flash-gsi).

2. **Can't set high userdata value**  
   Android limits allocation (40% on most versions). Custom gsid binary reduces to 20%.

3. **Why "Unmount SD" option?**  
   DSU prefers SD allocation but it's unreliable. This forces internal storage allocation.

4. **Why built-in installer requires root?**  
   Uses internal `DynamicSystem` API requiring `MANAGE_DYNAMIC_SYSTEM` (signature permission). Root shell (UID 2000) has `INSTALL_DYNAMIC_SYSTEM` to invoke DSU system-app.

5. **Updates?**  
   Check "About" section for updates.

6. **Issues?**  
   Open an issue with logs (available during installation if mode supports diagnostics).

## About DSU

DSU (Dynamic System Updates) lets developers boot GSIs without touching the system partition. Introduced in Android 10, it creates new partitions for the GSI and a separate userdata.

Requirements:
- Dynamic Partitions support (mandatory)
- Unlocked bootloader (required for most GSIs; only OEM-signed GSIs boot on locked)

DSU is essentially a safe "dual-boot" — no read-only partitions modified. If GSI fails, just reboot to return to original system.

More info: [Android DSU docs](https://source.android.com/devices/tech/ota/dynamic-system-updates)

## Sticky Mode

Force device to always boot into Dynamic System:

```bash
# ADB
adb shell gsi_tool enable
# Local shell
gsi_tool enable
# Rooted shell (Termux)
su -c 'gsi_tool enable'
```

Disable with `gsi_tool disable`.

## Translation

Contribute translations via [Crowdin](https://crowdin.com/translate/dsu-sideloader/).

App icon by [WSTxda](https://github.com/WSTxda).

---

**Fork**: This is a maintained fork of [VegaBobo/DSU-Sideloader](https://github.com/VegaBobo/DSU-Sideloader) with updated toolchain, theming, and CI/CD.
