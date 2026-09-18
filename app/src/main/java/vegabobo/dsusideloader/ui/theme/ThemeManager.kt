package vegabobo.dsusideloader.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import vegabobo.dsusideloader.preferences.AppPrefs
import kotlinx.coroutines.runBlocking

object ThemeManager {
    fun loadAndApplyTheme(context: Context, dataStore: DataStore<Preferences>) {
        runBlocking {
            val colorScheme = dataStore.data.map {
                it[intPreferencesKey(AppPrefs.THEME_COLOR_SCHEME)] ?: 0
            }.first()
            val primaryColor = dataStore.data.map {
                it[longPreferencesKey(AppPrefs.THEME_PRIMARY_COLOR)] ?: ThemeConfig.DefaultColors.primaryLight.value.toLong()
            }.first()
            val secondaryColor = dataStore.data.map {
                it[longPreferencesKey(AppPrefs.THEME_SECONDARY_COLOR)] ?: ThemeConfig.DefaultColors.secondaryLight.value.toLong()
            }.first()
            val tertiaryColor = dataStore.data.map {
                it[longPreferencesKey(AppPrefs.THEME_TERTIARY_COLOR)] ?: ThemeConfig.DefaultColors.tertiaryLight.value.toLong()
            }.first()
            val cornerRadius = dataStore.data.map {
                it[intPreferencesKey(AppPrefs.THEME_CORNER_RADIUS)] ?: ThemeConfig.Shapes.medium.roundToInt()
            }.first()

            ThemeConfig.applyPreferences(
                colorScheme = colorScheme,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                tertiaryColor = tertiaryColor,
                cornerRadius = cornerRadius,
            )
        }
    }

    fun resetTheme(dataStore: DataStore<Preferences>) {
        ThemeConfig.resetToDefaults()
        runBlocking {
            dataStore.edit {
                it[intPreferencesKey(AppPrefs.THEME_COLOR_SCHEME)] = 0
                it[longPreferencesKey(AppPrefs.THEME_PRIMARY_COLOR)] = ThemeConfig.DefaultColors.primaryLight.value.toLong()
                it[longPreferencesKey(AppPrefs.THEME_SECONDARY_COLOR)] = ThemeConfig.DefaultColors.secondaryLight.value.toLong()
                it[longPreferencesKey(AppPrefs.THEME_TERTIARY_COLOR)] = ThemeConfig.DefaultColors.tertiaryLight.value.toLong()
                it[intPreferencesKey(AppPrefs.THEME_CORNER_RADIUS)] = ThemeConfig.Shapes.medium.roundToInt()
            }
        }
    }
}