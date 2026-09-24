package vegabobo.dsusideloader.installer.adb

import vegabobo.dsusideloader.model.DSUConstants

class InstallationCmdline(
    private val parameters: Triple<Long, String, Long>,
) {

    fun getCmd(): String {
        return "am start-activity " +
            "-n com.android.dynsystem/com.android.dynsystem.VerificationActivity " +
            "-a android.os.image.action.START_INSTALL " +
            "" + genInstallationArguments()
    }

    private fun genInstallationArguments(): String {
        val userdataSize = parameters.first
        val gsiFileAbsolutePath = parameters.second
        val imageFileSize = parameters.third

        var arguments = ""

        arguments += addArgument("-d", shellEscape(gsiFileAbsolutePath))
        arguments += addArgument("--el", "KEY_USERDATA_SIZE", userdataSize.toString())
        if (imageFileSize != DSUConstants.DEFAULT_IMAGE_SIZE) {
            arguments += addArgument("--el", "KEY_SYSTEM_SIZE", imageFileSize.toString())
        }

        return arguments.trim()
    }

    private fun addArgument(argument: String, property: String, value: String): String {
        return "$argument $property $value "
    }

    private fun addArgument(argument: String, value: String): String {
        return "$argument $value "
    }

    private fun shellEscape(input: String): String {
        // Escape single quotes by closing the quote, adding escaped quote, reopening
        return "'${input.replace("'", "'\\''")}'"
    }
}