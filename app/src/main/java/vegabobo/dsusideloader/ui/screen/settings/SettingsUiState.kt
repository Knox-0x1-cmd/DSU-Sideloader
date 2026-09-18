package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.ui.graphics.Color
import vegabobo.dsusideloader.preferences.AppPrefs
import vegabobo.dsusideloader.ui.theme.ThemeConfig

enum class DialogSheetState {
    NONE,
    BUILT_IN_INSTALLER,
    DISABLE_STORAGE_CHECK,
    THEME_COLOR_PICKER_PRIMARY,
    THEME_COLOR_PICKER_SECONDARY,
    THEME_COLOR_PICKER_TERTIARY,
}

data class SettingsUiState(
    val preferences: HashMap<String, Boolean> = hashMapOf(
        AppPrefs.USE_BUILTIN_INSTALLER to false,
        AppPrefs.KEEP_SCREEN_ON to false,
        AppPrefs.UMOUNT_SD to false,
        AppPrefs.DISABLE_STORAGE_CHECK to false,
        AppPrefs.FULL_LOGCAT_LOGGING to false,
    ),
    val intPreferences: HashMap<String, Int> = hashMapOf(
        AppPrefs.THEME_COLOR_SCHEME to 0,
        AppPrefs.THEME_CORNER_RADIUS to ThemeConfig.Shapes.medium.roundToInt(),
    ),
    val longPreferences: HashMap<String, Long> = hashMapOf(
        AppPrefs.THEME_PRIMARY_COLOR to ThemeConfig.Colors.primaryLight.value.toLong(),
        AppPrefs.THEME_SECONDARY_COLOR to ThemeConfig.Colors.secondaryLight.value.toLong(),
        AppPrefs.THEME_TERTIARY_COLOR to ThemeConfig.Colors.tertiaryLight.value.toLong(),
    ),
    val dialogSheetDisplay: DialogSheetState = DialogSheetState.NONE,
    val isRoot: Boolean = false,
    val isDevOptEnabled: Boolean = false,
)