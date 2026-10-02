# DSU Sideloader

A utility application for installing Generic System Images (GSIs) using Android Dynamic System Updates (DSU).

## Requirements

- Android 10 (API level 29) or higher
- Unlocked bootloader
- Device with Dynamic Partitions support
- A compatible GSI image

Google GSIs: https://developer.android.com/topic/generic-system-image/releases

Note: Use GSIs compatible with your device architecture and VNDK implementation.

## Downloads

Latest stable APK: [Releases Section](https://github.com/Knox-0x1-cmd/DSU-Sideloader/releases/latest)

Testing builds: [Actions tab](https://github.com/Knox-0x1-cmd/DSU-Sideloader/actions) artifacts

## Usage

1. Install the application
2. On first launch, grant read/write permission to a folder (used for temporary files such as extracted GSIs)
3. Select a GSI to install
   - Accepted formats: `gz`, `xz`, `img`, `zip`, `lz4`, `bzip2`, `bz2` (DSU packages only)
4. Customize installation parameters if needed
   - Adjust userdata size for the dynamic system
   - Image size is auto-calculated (manual override not recommended)
5. Tap Install
6. Wait for completion
7. Post-installation steps vary by operation mode:
   - Built-in installer enabled: No additional steps
   - Root / System / Shizuku: DSU confirmation screen appears → confirm → check notifications
   - ADB: Execute provided command → confirm on DSU screen

See Operation Modes for details.

## Operation Modes

DSU Sideloader automatically selects the best available mode (priority order):

1. ADB (default): Prepares image; requires ADB command to start installation
2. Shizuku: Same as ADB but no ADB command needed; tracks progress; provides diagnostics
3. Root: All Shizuku features plus DynamicSystem API (check, install, discard DSU) plus built-in installer
4. System (Magisk module): All Shizuku features plus SELinux fixes plus custom gsid binary
5. System+Root (Magisk module + root): All features combined

Notes:
- Requires READ_LOGS permission
- Partial support on Android 10/11
- Android 13 requires one-time log access
- Built-in installer not supported on Android 10
- Experimental built-in installer implementation
- Custom gsid binary changes available in magisk-module resources

### Recommendations

- Non-rooted devices: Use Shizuku (install Shizuku app from Play Store)
- Rooted devices: Root mode is sufficient for most use cases
- DSU issues: Try System+Root mode
- Magisk v24 or higher recommended (older versions may break DSU)
- Stock ROM recommended; some custom ROMs may work

## Common Questions

1. Installation succeeds but device does not boot into DSU
   Likely AVB blocking. Try flashing disabled vbmeta. See Android documentation.

2. Cannot set high userdata value
   Android limits allocation (40% on most versions). Custom gsid binary reduces to 20%.

3. Why "Unmount SD" option?
   DSU prefers SD card allocation but it is unreliable. This forces internal storage allocation.

4. Why built-in installer requires root?
   Uses internal DynamicSystem API requiring MANAGE_DYNAMIC_SYSTEM (signature permission). Root shell (UID 2000) has INSTALL_DYNAMIC_SYSTEM to invoke DSU system app.

5. Updates?
   Check About section for updates.

6. Issues?
   Open an issue with logs (available during installation if mode supports diagnostics).

## About DSU

DSU (Dynamic System Updates) allows developers to boot GSIs without modifying the system partition. Introduced in Android 10, it creates new partitions for the GSI and a separate userdata.

Requirements:
- Dynamic Partitions support (mandatory)
- Unlocked bootloader (required for most GSIs; only OEM-signed GSIs boot on locked)

DSU is essentially a safe dual-boot mechanism — no read-only partitions are modified. If the GSI fails, a simple reboot returns to the original system.

More information: Android DSU documentation

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

App icon by WSTxda.

---

Fork: Maintained fork of [VegaBobo/DSU-Sideloader](https://github.com/VegaBobo/DSU-Sideloader) with updated toolchain, Material You dynamic theming, and CI/CD.