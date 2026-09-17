package vegabobo.dsusideloader.util

import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import java.io.IOException

object CmdRunner {

    var process: Process? = null

    fun run(cmd: String): String {
        return if (Shell.getShell().isRoot) {
            Shell.cmd(cmd).exec().out.toString()
        } else {
            runCommand(cmd)
        }
    }

    fun runReadEachLine(cmd: String, onReceive: (String) -> Unit) {
        if (Shell.getShell().isRoot) {
            val callbackList: CallbackList<String> = object : CallbackList<String>() {
                override fun onAddElement(s: String) {
                    onReceive(s)
                }
            }
            Shell.cmd(cmd).to(callbackList).submit()
        } else {
            runCommand(cmd, onReceive)
        }
    }

    private fun runCommand(cmd: String, onReceive: (String) -> Unit) {
        val currentProcess = ProcessBuilder("/bin/sh", "-c", cmd)
            .redirectErrorStream(true)
            .start()
        process = currentProcess
        try {
            currentProcess.inputStream.bufferedReader().useLines { lines ->
                for (line in lines) {
                    if (line.isNotEmpty()) onReceive(line)
                }
            }
            currentProcess.waitFor()
        } catch (_: IOException) {
        } finally {
            if (process === currentProcess) process = null
        }
    }

    private fun runCommand(cmd: String): String {
        var output = ""
        runCommand(cmd) {
            output += "$it\n"
        }
        return output
    }

    fun destroy() {
        if (Shell.getShell().isRoot) {
            Shell.getShell().close()
            Shell.getShell()
            return
        }
        if (process != null) {
            process!!.destroy()
            process = null
        }
    }
}
