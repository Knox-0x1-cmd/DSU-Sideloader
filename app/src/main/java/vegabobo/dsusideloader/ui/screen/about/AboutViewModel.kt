package vegabobo.dsusideloader.ui.screen.about

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import vegabobo.dsusideloader.BuildConfig
import vegabobo.dsusideloader.core.BaseViewModel
import vegabobo.dsusideloader.preferences.AppPrefs
import vegabobo.dsusideloader.util.isBuildSignedByAuthor

@Serializable
data class UpdaterResponse(
    val identifier: String = "",
    val versionCode: Int = -1,
    val versionName: String = "",
    val apkUrl: String = "",
    val changelogUrl: String = "",
)

@HiltViewModel
class AboutViewModel @Inject constructor(
    val application: Application,
    override val dataStore: DataStore<Preferences>,
) : BaseViewModel(dataStore) {
    private val tag = this.javaClass.simpleName

    private val _uiState = MutableStateFlow(AboutScreenUiState())
    val uiState: StateFlow<AboutScreenUiState> = _uiState.asStateFlow()
    var response = UpdaterResponse()

    var developerOptionsCounter = 0

    init {
        val isSignedByAuthor = application.isBuildSignedByAuthor()
        _uiState.update { it.copy(isUpdaterAvailable = isSignedByAuthor || BuildConfig.DEBUG) }
    }

    fun resetDeveloperOptionsCounter() {
        developerOptionsCounter = 0
    }

    private fun updateUpdaterCard(update: (UpdaterCardState) -> UpdaterCardState) =
        _uiState.update { it.copy(updaterCardState = update(it.updaterCardState.copy())) }

    fun onClickCheckUpdates() {
        Log.d(tag, "Fetching updates from: ${AppPrefs.UPDATE_CHECK_URL}")
        updateUpdaterCard { it.copy(updateStatus = UpdateStatus.CHECKING_FOR_UPDATES) }
        viewModelScope.launch(Dispatchers.IO) {
            val apiResponse = try {
                URL(AppPrefs.UPDATE_CHECK_URL).readText()
            } catch (e: Exception) {
                updateUpdaterCard { it.copy(updateStatus = UpdateStatus.NO_UPDATE_FOUND) }
                return@launch
            }
            response = try {
                Json.decodeFromString(UpdaterResponse.serializer(), apiResponse)
            } catch (e: Exception) {
                updateUpdaterCard { it.copy(updateStatus = UpdateStatus.NO_UPDATE_FOUND) }
                return@launch
            }
            updateUpdaterCard { it.copy(updateVersion = response.versionName) }
            if (response.versionCode > BuildConfig.VERSION_CODE) {
                updateUpdaterCard { it.copy(updateStatus = UpdateStatus.UPDATE_FOUND) }
            } else {
                updateUpdaterCard { it.copy(updateStatus = UpdateStatus.NO_UPDATE_FOUND) }
            }

            Log.d(tag, "$response")
        }
    }

    fun onClickDownloadUpdate() {
        if (uiState.value.updaterCardState.isDownloading) {
            return
        }
        if (!response.apkUrl.startsWith("https://")) {
            Log.e(tag, "Refusing to download update from non-https URL: ${response.apkUrl}")
            return
        }
        updateUpdaterCard { it.copy(isDownloading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val finalFile = File(application.filesDir.path + "/update.apk")
            try {
                val connection = URL(response.apkUrl).openConnection() as HttpURLConnection
                connection.connectTimeout = 30_000
                connection.readTimeout = 60_000
                connection.instanceFollowRedirects = true
                val length = connection.contentLengthLong
                connection.inputStream.use { input ->
                    if (connection.url.protocol != "https") {
                        throw IOException("Refusing non-https redirect: ${connection.url}")
                    }
                    FileOutputStream(finalFile).use { output ->
                        val buffer = ByteArray(8 * 1024)
                        var n: Int
                        var readed: Long = 0
                        while (-1 != input.read(buffer)
                                .also { n = it }
                        ) {
                            readed += n
                            output.write(buffer, 0, n)
                            val progress = if (length > 0) readed.toFloat() / length.toFloat() else 0F
                            updateUpdaterCard { it.copy(progressBar = progress) }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to download update", e)
                updateUpdaterCard { it.copy(isDownloading = false) }
                return@launch
            }

            updateUpdaterCard { it.copy(isDownloading = false) }
            val apkUri = FileProvider.getUriForFile(
                application,
                BuildConfig.APPLICATION_ID + ".provider",
                finalFile,
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            intent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags += Intent.FLAG_GRANT_READ_URI_PERMISSION
            application.startActivity(intent)
        }
    }

    fun onClickImage() {
        developerOptionsCounter++
        if (developerOptionsCounter > 7) {
            resetDeveloperOptionsCounter()
            viewModelScope.launch {
                val newDevOptPrefValue = !readBoolPref(AppPrefs.DEVELOPER_OPTIONS)
                Log.d(tag, "newDevOptPrefValue: $newDevOptPrefValue")
                updateBoolPref(
                    AppPrefs.DEVELOPER_OPTIONS,
                    newDevOptPrefValue,
                ) { preferenceValue ->
                    _uiState.value.toastDisplay.update { if (preferenceValue) DevOptToastDisplay.ENABLED_DEV_OPT else DevOptToastDisplay.DISABLED_DEV_OPT }
                }
                // if developer options have been disabled
                // then restore developer preferences to their default values
                if (!newDevOptPrefValue) {
                    updateBoolPref(AppPrefs.DISABLE_STORAGE_CHECK, false)
                    updateBoolPref(AppPrefs.FULL_LOGCAT_LOGGING, false)
                }
            }
        }
    }
}
