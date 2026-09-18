package vegabobo.dsusideloader.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onDismiss: () -> Unit = {},
    content: @Composable ColumnScope.(hideSheet: suspend () -> Unit) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    val isFirst = remember { mutableStateOf(true) }
    val shouldCallOnDismiss = remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.currentValue }
            .collect { currentValue ->
                if (!sheetState.isVisible) {
                    if (isFirst.value) {
                        sheetState.show()
                        isFirst.value = false
                        return@collect
                    }
                    if (shouldCallOnDismiss.value) {
                        onDismiss()
                        return@collect
                    }
                }
            }
    }

    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheet(
        onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
        sheetState = sheetState,
    ) {
        BottomSheetContent(
            title = title,
            icon = icon,
        ) {
            val insets = WindowInsets
                .systemBars
                .only(WindowInsetsSides.Vertical)
                .asPaddingValues()
            Column(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 18.dp, start = 18.dp, bottom = insets.calculateBottomPadding() + 14.dp, top = 14.dp),
            ) {
                content { sheetState.hide(); shouldCallOnDismiss.value = false; }
            }
        }
    }

    if (isFirst.value) {
        androidx.compose.material3.Surface(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0F),
        ) {
            BackHandler {}
        }
    }
}