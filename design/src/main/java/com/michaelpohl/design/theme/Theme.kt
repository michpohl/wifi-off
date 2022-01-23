package com.michaelpohl.design.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Theme data for DeezerViewer. This theme definition deviates slightly
 * from the Material standard by using a custom class [AppColors] for color
 * definitions. This way the colors can be given more meaningful names
 */
private val DeezerViewerColorPalette = AppColors(
    material = darkColors(
        background = Black,
        primary = Purple200,
        primaryVariant = Purple700,
        secondary = Teal200
    ),
    background = Purple700,
    backgroundContrast = White,
    foregroundPrimary = White,
    foregroundSecondary = MidGrey,
    foregroundContrast = Black,
    effectColor = TransparentWhite
)

val LocalColors = staticCompositionLocalOf { DeezerViewerColorPalette }

val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current

@Composable
fun DeezerViewerTheme(
    content: @Composable () -> Unit,
) {
    val colors = DeezerViewerColorPalette
    CompositionLocalProvider(LocalColors provides colors) {
        MaterialTheme(
            colors = colors.material,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}


