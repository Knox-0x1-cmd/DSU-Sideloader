package vegabobo.dsusideloader.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreUtils {

    companion object {

        suspend fun readBoolPref(
            dataStore: DataStore<Preferences>,
            key: String,
            default: Boolean,
        ): Boolean {
            return dataStore.data.map {
                it[booleanPreferencesKey(key)] ?: default
            }.first()
        }

        suspend fun readIntPref(
            dataStore: DataStore<Preferences>,
            key: String,
            default: Int,
        ): Int {
            return dataStore.data.map {
                it[intPreferencesKey(key)] ?: default
            }.first()
        }

        suspend fun readLongPref(
            dataStore: DataStore<Preferences>,
            key: String,
            default: Long,
        ): Long {
            return dataStore.data.map {
                it[longPreferencesKey(key)] ?: default
            }.first()
        }

        suspend fun readStringPref(
            dataStore: DataStore<Preferences>,
            key: String,
            default: String,
        ): String {
            return dataStore.data.map {
                it[stringPreferencesKey(key)] ?: default
            }.first().toString()
        }

        suspend fun readStringPref(
            dataStore: DataStore<Preferences>,
            key: String,
            default: String,
            onFinish: (String) -> Unit,
        ) {
            val result = readStringPref(dataStore, key, default)
            onFinish(result)
        }

        suspend fun updateBoolPref(
            dataStore: DataStore<Preferences>,
            key: String,
            value: Boolean,
            onFinish: () -> Unit = {},
        ) {
            dataStore.edit {
                it[booleanPreferencesKey(key)] = value
                return@edit
            }
            onFinish()
        }

        suspend fun updateIntPref(
            dataStore: DataStore<Preferences>,
            key: String,
            value: Int,
            onFinish: () -> Unit = {},
        ) {
            dataStore.edit {
                it[intPreferencesKey(key)] = value
                return@edit
            }
            onFinish()
        }

        suspend fun updateLongPref(
            dataStore: DataStore<Preferences>,
            key: String,
            value: Long,
            onFinish: () -> Unit = {},
        ) {
            dataStore.edit {
                it[longPreferencesKey(key)] = value
                return@edit
            }
            onFinish()
        }

        suspend fun updateStringPref(
            dataStore: DataStore<Preferences>,
            key: String,
            value: String,
            onFinish: () -> Unit = {},
        ) {
            dataStore.edit {
                it[stringPreferencesKey(key)] = value
                return@edit
            }
            onFinish()
        }
    }
}
