package vegabobo.dsusideloader.ui.screen.settings

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vegabobo.dsusideloader.core.BaseViewModel
import vegabobo.dsusideloader.model.Session
import vegabobo.dsusideloader.preferences.AppPrefs
import vegabobo.dsusideloader.util.OperationMode
import vegabobo.dsusideloader.util.OperationModeUtils
import vegabobo.dsusideloader.ui.theme.ThemeConfig

@HiltViewModel
class SettingsViewModel @Inject constructor(
    override val dataStore: DataStore<Preferences>,
    private val session: Session,
    val application: Application,
) : BaseViewModel(dataStore) {

    private val tag = this.javaClass.simpleName

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun reloadPreferences() {
        viewModelScope.launch {
            uiState.value.preferences.forEach { entry ->
                val isEnabled = readBoolPref(entry.key)
                togglePreference(entry.key, isEnabled)
            }
            uiState.value.intPreferences.forEach { entry ->
                val value = readIntPref(entry.key)
                _uiState.update {
                    val cloneMap = hashMapOf<String, Int>()
                    cloneMap.putAll(uiState.value.intPreferences)
                    cloneMap[entry.key] = value
                    it.copy(intPreferences = cloneMap)
                }
            }
            uiState.value.longPreferences.forEach { entry ->
                val value = readLongPref(entry.key)
                _uiState.update {
                    val cloneMap = hashMapOf<String, Long>()
                    cloneMap.putAll(uiState.value.longPreferences)
                    cloneMap[entry.key] = value
                    it.copy(longPreferences = cloneMap)
                }
            }
        }

        if (session.isRoot()) {
            _uiState.update { it.copy(isRoot = true) }
        }
    }

    init {
        reloadPreferences()
    }

    fun togglePreference(preference: String, value: Boolean) {
        viewModelScope.launch {
            updateBoolPref(preference, value) {
                _uiState.update {
                    val cloneMap = hashMapOf<String, Boolean>()
                    cloneMap.putAll(uiState.value.preferences)
                    cloneMap[preference] = value
                    Log.d(tag, "preference: $preference, isEnabled: $value")
                    it.copy(preferences = cloneMap)
                }
            }
        }
    }

    fun setIntPreference(preference: String, value: Int) {
        viewModelScope.launch {
            updateIntPref(preference, value) {
                _uiState.update {
                    val cloneMap = hashMapOf<String, Int>()
                    cloneMap.putAll(uiState.value.intPreferences)
                    cloneMap[preference] = value
                    Log.d(tag, "int preference: $preference, value: $value")
                    it.copy(intPreferences = cloneMap)
                }
            }
        }
    }

    fun setLongPreference(preference: String, value: Long) {
        viewModelScope.launch {
            updateLongPref(preference, value) {
                _uiState.update {
                    val cloneMap = hashMapOf<String, Long>()
                    cloneMap.putAll(uiState.value.longPreferences)
                    cloneMap[preference] = value
                    Log.d(tag, "long preference: $preference, value: $value")
                    it.copy(longPreferences = cloneMap)
                }
            }
        }
    }

    fun resetThemeToDefaults() {
        viewModelScope.launch {
            setIntPreference(AppPrefs.THEME_COLOR_SCHEME, 0)
            setIntPreference(AppPrefs.THEME_CORNER_RADIUS, ThemeConfig.Shapes.medium.roundToInt())
            setLongPreference(AppPrefs.THEME_PRIMARY_COLOR, ThemeConfig.Colors.primaryLight.value.toLong())
            setLongPreference(AppPrefs.THEME_SECONDARY_COLOR, ThemeConfig.Colors.secondaryLight.value.toLong())
            setLongPreference(AppPrefs.THEME_TERTIARY_COLOR, ThemeConfig.Colors.tertiaryLight.value.toLong())
        }
    }

    fun isAndroidQ(): Boolean = Build.VERSION.SDK_INT == 29

    fun updateSheetDisplay(sheet: DialogSheetState) {
        _uiState.update { it.copy(dialogSheetDisplay = sheet) }
    }

    fun checkOperationMode(): String {
        return OperationModeUtils.getOperationModeAsString(session.getOperationMode())
    }

    fun getOperationMode(): OperationMode {
        return session.getOperationMode()
    }

    fun checkDevOpt() {
        viewModelScope.launch {
            val isDevOptEnabled = readBoolPref(AppPrefs.DEVELOPER_OPTIONS)
            _uiState.update { it.copy(isDevOptEnabled = isDevOptEnabled) }
            if (isDevOptEnabled) {
                reloadPreferences()
            }
        }
    }
}