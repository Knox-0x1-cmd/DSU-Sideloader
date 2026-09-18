package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.height
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.theme.ThemeConfig

@Composable
fun ColorPickerRow(
    label: String,
    color: Color,
    onColorClick: () -> Unit,
    showReset: Boolean = true,
    resetColor: Color = Color.Unspecified,
    onResetClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.padding(16.dp).weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = "#${color.value.toString(16).uppercase().substring(2)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            Surface(
                shape = CircleShape,
                color = color,
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
            ) {}
            IconButton(onClick = onColorClick) {
                Icon(
                    imageVector = Palette,
                    contentDescription = stringResource(id = R.string.theme_primary_color),
                )
            }
            if (showReset && resetColor != Color.Unspecified) {
                IconButton(onClick = onResetClick) {
                    Icon(
                        imageVector = Restore,
                        contentDescription = stringResource(id = R.string.theme_reset),
                    )
                }
            }
        }
    }
}

@Composable
fun CornerRadiusSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String = "",
) {
    Column(modifier = Modifier.padding(16.dp)) {
        if (label.isNotEmpty()) {
            Text(
                text = "$label: ${value}dp",
                style = MaterialTheme.typography.titleSmall,
            )
        }
        androidx.compose.material3.Slider(
            modifier = Modifier.fillMaxWidth(),
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..64f,
            steps = 64,
            colors = androidx.compose.material3.SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colorScheme.primary,
                activeTickColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
                inactiveTickColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            ),
        )
    }
}