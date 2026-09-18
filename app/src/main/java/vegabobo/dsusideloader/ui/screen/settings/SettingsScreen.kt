package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Menu
import androidx.compose.material3.MenuItem
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
        PreferenceItem(
            title = stringResource(id = R.string.theme_color_scheme),
            description = when (uiState.intPreferences[AppPrefs.THEME_COLOR_SCHEME] ?: 0) {
                0 -> stringResource(id = R.string.theme_color_scheme_system)
                1 -> stringResource(id = R.string.theme_color_scheme_light)
                2 -> stringResource(id = R.string.theme_color_scheme_dark)
                else -> stringResource(id = R.string.theme_color_scheme_system)
            },
            showToggle = false,
            onClick = {
                settingsViewModel.setIntPreference(
                    AppPrefs.THEME_COLOR_SCHEME,
                    (uiState.intPreferences[AppPrefs.THEME_COLOR_SCHEME] ?: 0 + 1) % 3
                )
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

        // Color pickers
        ColorPickerRow(
            label = stringResource(id = R.string.theme_primary_color),
            color = Color(uiState.longPreferences[AppPrefs.THEME_PRIMARY_COLOR]?.toInt() ?: ThemeConfig.DefaultColors.primaryLight.value),
            onColorClick = {
                settingsViewModel.updateSheetDisplay(DialogSheetState.THEME_COLOR_PICKER_PRIMARY)
            },
            showReset = true,
            resetColor = ThemeConfig.DefaultColors.primaryLight,
            onResetClick = {
                settingsViewModel.setLongPreference(AppPrefs.THEME_PRIMARY_COLOR, ThemeConfig.DefaultColors.primaryLight.value.toLong())
            },
        )
        ColorPickerRow(
            label = stringResource(id = R.string.theme_secondary_color),
            color = Color(uiState.longPreferences[AppPrefs.THEME_SECONDARY_COLOR]?.toInt() ?: ThemeConfig.DefaultColors.secondaryLight.value),
            onColorClick = {
                settingsViewModel.updateSheetDisplay(DialogSheetState.THEME_COLOR_PICKER_SECONDARY)
            },
            showReset = true,
            resetColor = ThemeConfig.DefaultColors.secondaryLight,
            onResetClick = {
                settingsViewModel.setLongPreference(AppPrefs.THEME_SECONDARY_COLOR, ThemeConfig.DefaultColors.secondaryLight.value.toLong())
            },
        )
        ColorPickerRow(
            label = stringResource(id = R.string.theme_tertiary_color),
            color = Color(uiState.longPreferences[AppPrefs.THEME_TERTIARY_COLOR]?.toInt() ?: ThemeConfig.DefaultColors.tertiaryLight.value),
            onColorClick = {
                settingsViewModel.updateSheetDisplay(DialogSheetState.THEME_COLOR_PICKER_TERTIARY)
            },
            showReset = true,
            resetColor = ThemeConfig.DefaultColors.tertiaryLight,
            onResetClick = {
                settingsViewModel.setLongPreference(AppPrefs.THEME_TERTIARY_COLOR, ThemeConfig.DefaultColors.tertiaryLight.value.toLong())
            },
        )

        // Corner radius slider
        CornerRadiusSlider(
            value = uiState.intPreferences[AppPrefs.THEME_CORNER_RADIUS] ?: ThemeConfig.Shapes.medium.roundToInt(),
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

        DialogSheetState.THEME_COLOR_PICKER_PRIMARY ->
            ColorPickerBottomSheet(
                title = stringResource(id = R.string.theme_primary_color),
                onDismiss = { settingsViewModel.updateSheetDisplay(DialogSheetState.NONE) },
                onColorSelected = { color ->
                    settingsViewModel.setLongPreference(AppPrefs.THEME_PRIMARY_COLOR, color.value.toLong())
                    settingsViewModel.updateSheetDisplay(DialogSheetState.NONE)
                },
            )

        DialogSheetState.THEME_COLOR_PICKER_SECONDARY ->
            ColorPickerBottomSheet(
                title = stringResource(id = R.string.theme_secondary_color),
                onDismiss = { settingsViewModel.updateSheetDisplay(DialogSheetState.NONE) },
                onColorSelected = { color ->
                    settingsViewModel.setLongPreference(AppPrefs.THEME_SECONDARY_COLOR, color.value.toLong())
                    settingsViewModel.updateSheetDisplay(DialogSheetState.NONE)
                },
            )

        DialogSheetState.THEME_COLOR_PICKER_TERTIARY ->
            ColorPickerBottomSheet(
                title = stringResource(id = R.string.theme_tertiary_color),
                onDismiss = { settingsViewModel.updateSheetDisplay(DialogSheetState.NONE) },
                onColorSelected = { color ->
                    settingsViewModel.setLongPreference(AppPrefs.THEME_TERTIARY_COLOR, color.value.toLong())
                    settingsViewModel.updateSheetDisplay(DialogSheetState.NONE)
                },
            )

        else -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    onColorSelected: (androidx.compose.ui.graphics.Color) -> Unit,
) {
    val materialColors = listOf(
        androidx.compose.ui.graphics.Color.Red,
        androidx.compose.ui.graphics.Color.Pink,
        androidx.compose.ui.graphics.Color.Purple,
        androidx.compose.ui.graphics.Color.DeepPurple,
        androidx.compose.ui.graphics.Color.Indigo,
        androidx.compose.ui.graphics.Color.Blue,
        androidx.compose.ui.graphics.Color.LightBlue,
        androidx.compose.ui.graphics.Color.Cyan,
        androidx.compose.ui.graphics.Color.Teal,
        androidx.compose.ui.graphics.Color.Green,
        androidx.compose.ui.graphics.Color.LightGreen,
        androidx.compose.ui.graphics.Color.Lime,
        androidx.compose.ui.graphics.Color.Yellow,
        androidx.compose.ui.graphics.Color.Amber,
        androidx.compose.ui.graphics.Color.Orange,
        androidx.compose.ui.graphics.Color.DeepOrange,
        androidx.compose.ui.graphics.Color.Brown,
        androidx.compose.ui.graphics.Color.Grey,
        androidx.compose.ui.graphics.Color.BlueGrey,
    )

    val sheetState = remember { androidx.compose.material3.rememberModalBottomSheetState() }

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    androidx.compose.material3.ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            sheetState.hide()
            onDismiss()
        },
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier.padding(16.dp).fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                androidx.compose.material3.Text(text = title, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                androidx.compose.material3.IconButton(onClick = { sheetState.hide(); onDismiss() }) {
                    androidx.compose.material3.Icon(Icons.Filled.Close, "Close")
                }
            }
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.Modifier.padding(top = 8.dp))
            androidx.compose.foundation.layout.Column(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
                materialColors.chunked(5).forEach { row ->
                    androidx.compose.foundation.layout.Row(
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                    ) {
                        row.forEach { color ->
                            androidx.compose.material3.Surface(
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = color,
                                modifier = androidx.compose.ui.Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .weight(1f)
                                    .fillMaxWidth(),
                            ) {
                                androidx.compose.foundation.layout.Box(
                                    modifier = androidx.compose.ui.Modifier
                                        .fillMaxSize()
                                        .clickable { onColorSelected(color) }
                                )
                            }
                        }
                    }
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}