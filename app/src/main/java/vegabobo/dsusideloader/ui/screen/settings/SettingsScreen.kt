package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonItem
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

        // Color scheme selector
        PreferenceItem(
            title = stringResource(id = R.string.theme_color_scheme),
            showToggle = false,
            onClick = {},
        )
        ColorSchemeSelector(
            selectedIndex = uiState.intPreferences[AppPrefs.THEME_COLOR_SCHEME] ?: 0,
            onSelectedChange = { index ->
                settingsViewModel.setIntPreference(AppPrefs.THEME_COLOR_SCHEME, index)
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
fun ColorSchemeSelector(
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        SegmentedButton(
            selectedIndex = selectedIndex,
            onClick = onSelectedChange,
            modifier = Modifier.fillMaxWidth(),
            singleChoice = true,
        ) {
            SegmentedButtonItem(
                label = { Text(text = stringResource(id = R.string.theme_color_scheme_system)) },
                icon = { Icon(androidx.compose.material.icons.Icons.Filled.PhoneAndroid, null) },
                alwaysShowLabel = true,
            )
            SegmentedButtonItem(
                label = { Text(text = stringResource(id = R.string.theme_color_scheme_light)) },
                icon = { Icon(androidx.compose.material.icons.Icons.Filled.WbSunny, null) },
                alwaysShowLabel = true,
            )
            SegmentedButtonItem(
                label = { Text(text = stringResource(id = R.string.theme_color_scheme_dark)) },
                icon = { Icon(androidx.compose.material.icons.Icons.Filled.NightlightRound, null) },
                alwaysShowLabel = true,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit,
) {
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.material3.Button
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Surface
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp

    val materialColors = listOf(
        Color.Red, Color.Pink, Color.Purple, Color.DeepPurple,
        Color.Indigo, Color.Blue, Color.LightBlue, Color.Cyan,
        Color.Teal, Color.Green, Color.LightGreen, Color.Lime,
        Color.Yellow, Color.Amber, Color.Orange, Color.DeepOrange,
        Color.Brown, Color.Grey, Color.BlueGrey,
    )

    androidx.compose.material3.ModalBottomSheet(
        sheetState = remember { androidx.compose.material3.rememberModalBottomSheetState() },
        onDismissRequest = onDismiss,
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                androidx.compose.material3.IconButton(onClick = onDismiss) {
                    Icon(androidx.compose.material.icons.Icons.Filled.Close, "Close")
                }
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                materialColors.chunked(5).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                    ) {
                        row.forEach { color ->
                            Surface(
                                shape = CircleShape,
                                color = color,
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .weight(1f)
                                    .fillMaxWidth(),
                            ) {
                                androidx.compose.material3.Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { onColorSelected(color) }
                                )
                            }
                        }
                    }
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }.show()
}