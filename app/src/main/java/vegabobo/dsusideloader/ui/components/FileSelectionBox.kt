package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileSelectionBox(
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    isEnabled: Boolean,
    isError: Boolean,
    textFieldTitle: String,
    textFieldValue: String,
    onValueChange: (String) -> Unit = {},
    onClick: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val currentOnClick = rememberUpdatedState(onClick)

    LaunchedEffect(interactionSource, isEnabled) {
        if (!isEnabled) return@LaunchedEffect
        interactionSource.interactions.collectLatest { interaction ->
            if (interaction is PressInteraction.Release) currentOnClick.value?.invoke()
        }
    }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = textFieldValue,
        placeholder = { Text(text = "") },
        onValueChange = onValueChange,
        enabled = isEnabled,
        isError = isError,
        singleLine = true,
        readOnly = isReadOnly,
        keyboardOptions = keyboardOptions,
        interactionSource = interactionSource,
        label = { Text(text = textFieldTitle) },
    )
}
