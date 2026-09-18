package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.ui.graphics.Color
import vegabobo.dsusideloader.preferences.AppPrefs

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
        AppPrefs.THEME_USE_DYNAMIC_COLOR to true,
    ),
    val intPreferences: HashMap<String, Int> = hashMapOf(
        AppPrefs.THEME_COLOR_SCHEME to 0,
        AppPrefs.THEME_CORNER_RADIUS to 28,
    ),
    val longPreferences: HashMap<String, Long> = hashMapOf(
        AppPrefs.THEME_PRIMARY_COLOR to Color(0xFF275CAF).value.toLong(),
        AppPrefs.THEME_SECONDARY_COLOR to Color(0xFF565E71).value.toLong(),
        AppPrefs.THEME_TERTIARY_COLOR to Color(0xFF715574).value.toLong(),
    ),
    val dialogSheetDisplay: DialogSheetState = DialogSheetState.NONE,
    val isRoot: Boolean = false,
    val isDevOptEnabled: Boolean = false,
)