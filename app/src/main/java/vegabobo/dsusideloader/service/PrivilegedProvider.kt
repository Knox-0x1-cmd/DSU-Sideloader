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

    // Blocking
    fun getService(): IPrivilegedService {
        var timeout = 0
        while (connection.SERVICE == null) {
            timeout += 1000
            if (timeout > 20000) {
                throw Exception("Service unavailable.")
            }
            Thread.sleep(1000)
        }
        return connection.SERVICE ?: throw Exception("Service unavailable.")
    }

    // Blocking
    fun isRoot(): Boolean {
        return this.getService().uid == 0
    }

    fun isConnected(): Boolean {
        return this.connection.SERVICE != null
    }
}
