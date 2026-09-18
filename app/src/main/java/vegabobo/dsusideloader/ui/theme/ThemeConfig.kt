package vegabobo.dsusideloader.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object ThemeConfig {
    object Colors {
        val primaryLight = Color(0xFF275CAF)
        val primaryDark = Color(0xFFACC7FF)
        val secondaryLight = Color(0xFF565E71)
        val secondaryDark = Color(0xFFBEC6DC)
        val tertiaryLight = Color(0xFF715574)
        val tertiaryDark = Color(0xFFDEBCDF)

        val surfaceLight = Color(0xFFFFFBFE)
        val surfaceDark = Color(0xFF1C1B1F)
        val onSurfaceLight = Color(0xFF1C1B1F)
        val onSurfaceDark = Color(0xFFFFFBFE)
    }

    object Shapes {
        val extraSmall = 16.dp
        val small = 24.dp
        val medium = 28.dp
        val large = 32.dp
        val extraLarge = 36.dp
    }

    object Spacing {
        val xs = 4.dp
        val sm = 8.dp
        val md = 16.dp
        val lg = 24.dp
        val xl = 32.dp
    }

    object Typography {
        val bodyLarge = 16.sp
        val titleLarge = 22.sp
        val headlineSmall = 24.sp
    }
}