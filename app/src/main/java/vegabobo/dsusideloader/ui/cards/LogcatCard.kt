package vegabobo.dsusideloader.ui.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vegabobo.dsusideloader.R

@Composable
fun LogcatCard(logs: String) {
    var filter by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val filteredLines = remember(logs, filter) {
        logs.lineSequence()
            .filter { filter.isBlank() || it.contains(filter, ignoreCase = true) }
            .toList()
    }

    Column {
        BasicTextField(
            value = filter,
            onValueChange = { filter = it },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            decorationBox = { innerTextField ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                ) {
                    androidx.compose.foundation.layout.Box(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                        if (filter.isEmpty()) {
                            Text(stringResource(R.string.search_logs), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        innerTextField()
                    }
                }
            },
        )
        Text(
            text = stringResource(R.string.log_line_count, filteredLines.count { it.isNotBlank() }),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Surface(
            color = MaterialTheme.colorScheme.inverseOnSurface,
            modifier = Modifier.clip(RoundedCornerShape(10.dp)).fillMaxWidth().height(360.dp),
        ) {
            if (filteredLines.none { it.isNotBlank() }) {
                Text(
                    text = stringResource(if (logs.isBlank()) R.string.logs_waiting else R.string.no_matching_logs),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp),
                )
            } else {
                LaunchedEffect(logs, filter) { if (filter.isBlank()) scrollState.animateScrollTo(scrollState.maxValue) }
                SelectionContainer {
                    Text(
                        text = filteredLines.joinToString("\n"),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.verticalScroll(scrollState).padding(10.dp).fillMaxSize(),
                    )
                }
            }
        }
    }
}
