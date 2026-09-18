package vegabobo.dsusideloader.preferences

object AppPrefs {
    /**
     * Update feature only works if AUTHOR_SIGN_DIGEST is set
     * with same SHA1 digest from signed apk OR is a DEBUG build
     * if AUTHOR_SIGN_DIGEST doesn't match, also no problem
     * app will work as expected, just without update feature.
     * check AboutViewModel init
     */
    const val UPDATE_CHECK_URL =
        "https://raw.githubusercontent.com/VegaBobo/DSU-Sideloader/master/other/updater.json"
    const val AUTHOR_SIGN_DIGEST = "0da046eb480972124e2fe2251ebc5b19ea9e13d9"
    const val USER_PREFERENCES = "user_preferences"
    const val BOOTLOADER_UNLOCKED_WARNING = "bootloader_unlocked_warning"
    const val SAF_PATH = "writable_path"
    const val DEVELOPER_OPTIONS = "developer_options"
    const val USE_BUILTIN_INSTALLER = "builtin_installer"
    const val KEEP_SCREEN_ON = "keep_screen_on"
    const val UMOUNT_SD = "umount_sd"
    const val DISABLE_STORAGE_CHECK = "disable_storage_check"
    const val FULL_LOGCAT_LOGGING = "full_logcat_logging"

    // Theme preferences
    const val THEME_COLOR_SCHEME = "theme_color_scheme" // 0=system, 1=light, 2=dark
    const val THEME_PRIMARY_COLOR = "theme_primary_color"
    const val THEME_SECONDARY_COLOR = "theme_secondary_color"
    const val THEME_TERTIARY_COLOR = "theme_tertiary_color"
    const val THEME_CORNER_RADIUS = "theme_corner_radius"
    const val THEME_USE_DYNAMIC_COLOR = "theme_use_dynamic_color"
}
