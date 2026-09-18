package vegabobo.dsusideloader.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vegabobo.dsusideloader.preferences.AppPrefs

object ThemeConfig {
    // Default colors (Material 3 baseline)
    object DefaultColors {
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

    // Runtime colors - these will be overridden by preferences
    var primaryLight: Color = DefaultColors.primaryLight
        @Suppress("unused") set(value) { field = value }
    var primaryDark: Color = DefaultColors.primaryDark
        @Suppress("unused") set(value) { field = value }
    var secondaryLight: Color = DefaultColors.secondaryLight
        @Suppress("unused") set(value) { field = value }
    var secondaryDark: Color = DefaultColors.secondaryDark
        @Suppress("unused") set(value) { field = value }
    var tertiaryLight: Color = DefaultColors.tertiaryLight
        @Suppress("unused") set(value) { field = value }
    var tertiaryDark: Color = DefaultColors.tertiaryDark
        @Suppress("unused") set(value) { field = value }

    object Shapes {
        var extraSmall = 16.dp
        var small = 24.dp
        var medium = 28.dp
        var large = 32.dp
        var extraLarge = 36.dp
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

    fun applyPreferences(
        colorScheme: Int, // 0=system, 1=light, 2=dark
        primaryColor: Long,
        secondaryColor: Long,
        tertiaryColor: Long,
        cornerRadius: Int,
    ) {
        primaryLight = Color(primaryColor.toInt())
        secondaryLight = Color(secondaryColor.toInt())
        tertiaryLight = Color(tertiaryColor.toInt())

        // For dark, use lighter variants
        primaryDark = Color(primaryColor.toInt()).copy(alpha = 0.8f)
        secondaryDark = Color(secondaryColor.toInt()).copy(alpha = 0.8f)
        tertiaryDark = Color(tertiaryColor.toInt()).copy(alpha = 0.8f)

        val radius = cornerRadius.dp
        Shapes.extraSmall = radius
        Shapes.small = radius
        Shapes.medium = radius
        Shapes.large = radius
        Shapes.extraLarge = radius
    }

    fun resetToDefaults() {
        primaryLight = DefaultColors.primaryLight
        primaryDark = DefaultColors.primaryDark
        secondaryLight = DefaultColors.secondaryLight
        secondaryDark = DefaultColors.secondaryDark
        tertiaryLight = DefaultColors.tertiaryLight
        tertiaryDark = DefaultColors.tertiaryDark

        Shapes.extraSmall = 16.dp
        Shapes.small = 24.dp
        Shapes.medium = 28.dp
        Shapes.large = 32.dp
        Shapes.extraLarge = 36.dp
    }
}