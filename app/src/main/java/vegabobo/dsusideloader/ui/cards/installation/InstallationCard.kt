package vegabobo.dsusideloader.ui.cards.installation

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.CardBox
import vegabobo.dsusideloader.ui.screen.home.InstallationCardState

@Composable
fun InstallationCard(
    uiState: InstallationCardState,
    modifier: Modifier = Modifier,
    minPercentageOfFreeStorage: String,
    onClickClear: () -> Unit,
    onClickInstall: () -> Unit,
    onClickRetryInstallation: () -> Unit,
    onClickUnmountSdCardAndRetry: () -> Unit,
    onClickSetSeLinuxPermissive: () -> Unit,
    onClickCancelInstallation: () -> Unit,
    onClickDiscardInstalledGsiAndInstall: () -> Unit,
    onClickDiscardDsu: () -> Unit,
    onClickRebootToDynOS: () -> Unit,
    onSelectFileSuccess: (Uri) -> Unit,
    onClickViewLogs: () -> Unit,
    onClickViewCommands: () -> Unit,
) {
    val context = LocalContext.current
    val mimeTypes = arrayOf(
        "application/gzip",
        "application/x-gzip",
        "application/x-xz",
        "application/zip",
        "application/octet-stream",
    )
    val launcherSelectFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        it?.let(onSelectFileSuccess)
    }
    val launcherSelectFileFallback = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let(onSelectFileSuccess)
    }
    val onClickSelectFile: () -> Unit = {
        if (uiState.isTextFieldEnabled) {
            val openDocumentHandlerPkg = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }.resolveActivity(context.packageManager)?.packageName
            if (openDocumentHandlerPkg == "su.axi.documentsui") {
                launcherSelectFileFallback.launch("*/*")
            } else {
                runCatching { launcherSelectFile.launch(mimeTypes) }
                    .onFailure { launcherSelectFileFallback.launch("*/*") }
            }
        }
    }

    CardBox(
        cardTitle = stringResource(R.string.installation),
        addToggle = false,
        modifier = modifier,
    ) {
        InstallationCardStep(
            uiState = uiState,
            onClickSelectFile = onClickSelectFile,
            onClickClear = onClickClear,
            onClickInstall = onClickInstall,
            onClickRetryInstallation = onClickRetryInstallation,
            onClickUnmountSdCardAndRetry = onClickUnmountSdCardAndRetry,
            onClickSetSeLinuxPermissive = onClickSetSeLinuxPermissive,
            onClickCancelInstallation = onClickCancelInstallation,
            onClickDiscardInstalledGsiAndInstall = onClickDiscardInstalledGsiAndInstall,
            onClickDiscardDsu = onClickDiscardDsu,
            onClickRebootToDynOS = onClickRebootToDynOS,
            onClickViewLogs = onClickViewLogs,
            onClickViewCommands = onClickViewCommands,
            minPercentageOfFreeStorage = minPercentageOfFreeStorage,
        )
    }
}
