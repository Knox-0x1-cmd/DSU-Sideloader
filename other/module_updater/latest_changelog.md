### DSU Sideloader v2.04 (8)

- **New app icon** applied to all mipmap densities
- **Custom theme colors**: #151713, #2B2E29, #718268, #E6E2DA (dark/light)
- **Dynamic color (Material You) disabled by default** — uses custom palette instead of wallpaper colors
- **Fixed night theme** secondary color to match palette
- **Removed v31 color overrides** that forced system colors
- **ProGuard rules** for Hilt, AboutLibraries, Serialization, Shizuku, LibSu
- **Fixed adaptive icon** (removed mipmap background PNGs, uses themed drawable)
- **Fixed ic_launcher_round.xml** background reference
- **Build toolchain updated**: Gradle 8.13, AGP 8.13.2, Kotlin 2.2.20, Compose 1.11.4
- **Release workflow** for tagged builds (auto-publishes APKs)
- **Updated dependencies**: AboutLibraries 12.0.0, Shizuku 13.5.4, Hilt 2.57.2, etc.
- **Magisk module**: updated author and updateJson to fork repository

Read more at: https://github.com/Knox-0x1-cmd/DSU-Sideloader/releases/tag/2.04