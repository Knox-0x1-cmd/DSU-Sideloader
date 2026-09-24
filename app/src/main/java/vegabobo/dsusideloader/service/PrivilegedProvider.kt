package vegabobo.dsusideloader.service

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vegabobo.dsusideloader.IPrivilegedService

object PrivilegedProvider {

    private val tag = this.javaClass.simpleName

    var connection = Connection()

    fun run(
        onFail: () -> Unit = {},
        onConnected: suspend IPrivilegedService.() -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            if (connection.SERVICE == null) {
                var timeout = 0
                while (connection.SERVICE == null) {
                    timeout += 1000
                    if (timeout > 20000) {
                        Log.e(tag, "Service unavailable.")
                        onFail()
                        return@launch
                    }
                    delay(1000)
                    Log.d(tag, "Service unavailable, checking again in 1s.. [${timeout / 1000}s/20s]")
                }
            }
            val service = connection.SERVICE ?: return@launch
            Log.d(tag, "IPrivilegedService available, uid: ${service.uid}")
            onConnected(service)
        }
    }

    // Non-blocking version for coroutine contexts
    suspend fun getService(): IPrivilegedService {
        var timeout = 0
        while (connection.SERVICE == null) {
            timeout += 1000
            if (timeout > 20000) {
                throw Exception("Service unavailable.")
            }
            delay(1000)
        }
        return connection.SERVICE ?: throw Exception("Service unavailable.")
    }

    suspend fun isRoot(): Boolean {
        val service = getService()
        return service.uid == 0
    }

    // Blocking version for non-coroutine contexts (used by DynamicSystemImpl)
    // Uses short sleep intervals to remain responsive
    fun getServiceBlocking(): IPrivilegedService {
        var timeout = 0
        while (connection.SERVICE == null) {
            timeout += 100
            if (timeout > 20000) {
                throw Exception("Service unavailable.")
            }
            Thread.sleep(100)
        }
        return connection.SERVICE ?: throw Exception("Service unavailable.")
    }

    fun isRootBlocking(): Boolean {
        return getServiceBlocking().uid == 0
    }

    fun isConnected(): Boolean {
        return this.connection.SERVICE != null
    }
}