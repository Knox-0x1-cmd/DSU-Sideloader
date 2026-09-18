package vegabobo.dsusideloader.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import vegabobo.dsusideloader.util.DataStoreUtils

open class BaseViewModel(
    open val dataStore: DataStore<Preferences>,
) : ViewModel() {

    suspend fun readStringPref(
        key: String,
    ): String {
        return DataStoreUtils.readStringPref(dataStore, key, "")
    }

    suspend fun readBoolPref(
        key: String,
    ): Boolean {
        return DataStoreUtils.readBoolPref(dataStore, key, false)
    }

    suspend fun readIntPref(
        key: String,
    ): Int {
        return DataStoreUtils.readIntPref(dataStore, key, 0)
    }

    suspend fun readLongPref(
        key: String,
    ): Long {
        return DataStoreUtils.readLongPref(dataStore, key, 0L)
    }

    suspend fun updateBoolPref(
        key: String,
        value: Boolean,
        onRead: (Boolean) -> Unit = {},
    ) {
        DataStoreUtils.updateBoolPref(dataStore, key, value) {
            onRead(value)
        }
    }

    suspend fun updateIntPref(
        key: String,
        value: Int,
        onRead: (Int) -> Unit = {},
    ) {
        DataStoreUtils.updateIntPref(dataStore, key, value) {
            onRead(value)
        }
    }

    suspend fun updateLongPref(
        key: String,
        value: Long,
        onRead: (Long) -> Unit = {},
    ) {
        DataStoreUtils.updateLongPref(dataStore, key, value) {
            onRead(value)
        }
    }

    suspend fun updateStringPref(
        key: String,
        value: String,
        onRead: (String) -> Unit = {},
    ) {
        DataStoreUtils.updateStringPref(dataStore, key, value) {
            onRead(value)
        }
    }
}
