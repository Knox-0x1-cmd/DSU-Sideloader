package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.AppPrefs
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.ColorPickerRow
import vegabobo.dsusideloader.ui.components.CornerRadiusSlider
import vegabobo.dsusideloader.ui.components.DialogLikeBottomSheet
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.components.Title
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.screen.Destinations
import vegabobo.dsusideloader.util.OperationMode
import vegabobo.dsusideloader.util.collectAsStateWithLifecycle
import vegabobo.dsusideloader.ui.theme.ThemeConfig
import androidx.compose.ui.graphics.Color as ComposeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    navigate: (String) -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        settingsViewModel.checkDevOpt()
    }

    ApplicationScreen(
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.settings),
                scrollBehavior = it,
                onClickBackButton = { navigate(Destinations.Up) },
            )
        },
    ) {
        Title(title = stringResource(id = R.string.installation))
        PreferenceItem(
            title = stringResource(id = R.string.builtin_installer),
            description =
            if (settingsViewModel.isAndroidQ()) {
                stringResource(id = R.string.unsupported)
            } else if (uiState.isRoot) {
                stringResource(id = R.string.builtin_installer_description)
            } else {
                stringResource(R.string.requires_root)
            },
            showToggle = true,
            isEnabled = uiState.isRoot && !settingsViewModel.isAndroidQ(),
            isChecked = uiState.preferences[AppPrefs.USE_BUILTIN_INSTALLER]!!,
            onClick = {
                if (!it) {
                    settingsViewModel.updateSheetDisplay(DialogSheetState.BUILT_IN_INSTALLER)
                }
                settingsViewModel.togglePreference(AppPrefs.USE_BUILTIN_INSTALLER, !it)
            },
        )
        PreferenceItem(
            title = stringResource(id = R.string.unmount_sd_title),
            description = stringResource(id = R.string.unmount_sd_description),
            showToggle = true,
            isChecked = uiState.preferences[AppPrefs.UMOUNT_SD]!!,
            onClick = { settingsViewModel.togglePreference(AppPrefs.UMOUNT_SD, !it) },
        )
        PreferenceItem(
            title = stringResource(id = R.string.keep_screen_on),
            showToggle = true,
            isChecked = uiState.preferences[AppPrefs.KEEP_SCREEN_ON]!!,
            onClick = { settingsViewModel.togglePreference(AppPrefs.KEEP_SCREEN_ON, !it) },
        )

        if (uiState.isDevOptEnabled) {
            Title(title = stringResource(id = R.string.developer_options))
            PreferenceItem(
                title = stringResource(id = R.string.storage_check_title),
                description = stringResource(id = R.string.storage_check_description),
                showToggle = true,
                isChecked = uiState.preferences[AppPrefs.DISABLE_STORAGE_CHECK]!!,
                onClick = {
                    if (!it) {
                        settingsViewModel.updateSheetDisplay(DialogSheetState.DISABLE_STORAGE_CHECK)
                    }
                    settingsViewModel.togglePreference(AppPrefs.DISABLE_STORAGE_CHECK, !it)
                },
            )
            if (settingsViewModel.getOperationMode() != OperationMode.ADB) {
                PreferenceItem(
                    title = stringResource(id = R.string.full_logcat_logging_title),
                    description = stringResource(id = R.string.full_logcat_logging_description),
                    showToggle = true,
                    isChecked = uiState.preferences[AppPrefs.FULL_LOGCAT_LOGGING]!!,
                    onClick = { settingsViewModel.togglePreference(AppPrefs.FULL_LOGCAT_LOGGING, !it) },
                )
            }
        }

        Title(title = stringResource(id = R.string.theme_settings))

        // Color scheme selector - simple dropdown style
        val currentColorScheme = uiState.intPreferences[AppPrefs.THEME_COLOR_SCHEME] ?: 0
        PreferenceItem(
            title = stringResource(id = R.string.theme_color_scheme),
            description = when (currentColorScheme) {
                0 -> stringResource(id = R.string.theme_color_scheme_system)
                1 -> stringResource(id = R.string.theme_color_scheme_light)
                2 -> stringResource(id = R.string.theme_color_scheme_dark)
                else -> stringResource(id = R.string.theme_color_scheme_system)
            },
            showToggle = false,
            onClick = {
                settingsViewModel.setIntPreference(AppPrefs.THEME_COLOR_SCHEME, (currentColorScheme + 1) % 3)
            },
        )

        // Dynamic color toggle
        PreferenceItem(
            title = stringResource(id = R.string.theme_dynamic_color),
            description = stringResource(id = R.string.theme_dynamic_color_description),
            showToggle = true,
            isChecked = uiState.preferences[AppPrefs.THEME_USE_DYNAMIC_COLOR] ?: true,
            onClick = { settingsViewModel.togglePreference(AppPrefs.THEME_USE_DYNAMIC_COLOR, !it) },
        )

        // Color pickers - cycle through preset colors on click
        ColorPickerRow(
            label = stringResource(id = R.string.theme_primary_color),
            color = ComposeColor((uiState.longPreferences[AppPrefs.THEME_PRIMARY_COLOR]?.toInt() ?: ThemeConfig.DefaultColors.primaryLight.value.toInt())),
            onColorClick = {
                settingsViewModel.cycleColor(AppPrefs.THEME_PRIMARY_COLOR)
            },
            showReset = true,
            resetColor = ThemeConfig.DefaultColors.primaryLight,
            onResetClick = {
                settingsViewModel.setLongPreference(AppPrefs.THEME_PRIMARY_COLOR, ThemeConfig.DefaultColors.primaryLight.value.toLong())
            },
        )
        ColorPickerRow(
            label = stringResource(id = R.string.theme_secondary_color),
            color = ComposeColor((uiState.longPreferences[AppPrefs.THEME_SECONDARY_COLOR]?.toInt() ?: ThemeConfig.DefaultColors.secondaryLight.value.toInt())),
            onColorClick = {
                settingsViewModel.cycleColor(AppPrefs.THEME_SECONDARY_COLOR)
            },
            showReset = true,
            resetColor = ThemeConfig.DefaultColors.secondaryLight,
            onResetClick = {
                settingsViewModel.setLongPreference(AppPrefs.THEME_SECONDARY_COLOR, ThemeConfig.DefaultColors.secondaryLight.value.toLong())
            },
        )
        ColorPickerRow(
            label = stringResource(id = R.string.theme_tertiary_color),
            color = ComposeColor((uiState.longPreferences[AppPrefs.THEME_TERTIARY_COLOR]?.toInt() ?: ThemeConfig.DefaultColors.tertiaryLight.value.toInt())),
            onColorClick = {
                settingsViewModel.cycleColor(AppPrefs.THEME_TERTIARY_COLOR)
            },
            showReset = true,
            resetColor = ThemeConfig.DefaultColors.tertiaryLight,
            onResetClick = {
                settingsViewModel.setLongPreference(AppPrefs.THEME_TERTIARY_COLOR, ThemeConfig.DefaultColors.tertiaryLight.value.toLong())
            },
        )

        // Corner radius slider
        CornerRadiusSlider(
            value = uiState.intPreferences[AppPrefs.THEME_CORNER_RADIUS] ?: 28,
            onValueChange = { value ->
                settingsViewModel.setIntPreference(AppPrefs.THEME_CORNER_RADIUS, value)
            },
            label = stringResource(id = R.string.theme_corner_radius),
        )

        // Reset theme button
        PreferenceItem(
            title = stringResource(id = R.string.theme_reset),
            description = stringResource(id = R.string.theme_corner_radius_description),
            showToggle = false,
            onClick = { settingsViewModel.resetThemeToDefaults() },
        )

        Title(title = stringResource(id = R.string.other))
        PreferenceItem(
            title = stringResource(id = R.string.operation_mode),
            description = settingsViewModel.checkOperationMode(),
        )
        PreferenceItem(
            title = stringResource(id = R.string.about),
            description = stringResource(id = R.string.about_description),
            onClick = { navigate(Destinations.About) },
        )
    }

    when (uiState.dialogSheetDisplay) {
        DialogSheetState.BUILT_IN_INSTALLER ->
            DialogLikeBottomSheet(
                title = stringResource(id = R.string.experimental_feature),
                icon = Icons.Outlined.NewReleases,
                text = stringResource(id = R.string.experimental_feature_description),
                confirmText = stringResource(id = R.string.yes),
                cancelText = stringResource(id = R.string.cancel),
                onClickCancel = {
                    settingsViewModel.togglePreference(AppPrefs.USE_BUILTIN_INSTALLER, false)
                    settingsViewModel.updateSheetDisplay(DialogSheetState.NONE)
                },
                onClickConfirm = { settingsViewModel.updateSheetDisplay(DialogSheetState.NONE) },
            )

        DialogSheetState.DISABLE_STORAGE_CHECK ->
            DialogLikeBottomSheet(
                title = stringResource(id = R.string.warning_storage_check_title),
                icon = Icons.Outlined.WarningAmber,
                text = stringResource(id = R.string.warning_storage_check_description),
                confirmText = stringResource(id = R.string.continue_anyway),
                cancelText = stringResource(id = R.string.cancel),
                onClickCancel = {
                    settingsViewModel.togglePreference(AppPrefs.DISABLE_STORAGE_CHECK, false)
                    settingsViewModel.updateSheetDisplay(DialogSheetState.NONE)
                },
                onClickConfirm = { settingsViewModel.updateSheetDisplay(DialogSheetState.NONE) },
            )

        else -> {}
    }
}