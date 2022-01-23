package com.michaelpohl.design.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

/**
 * Data class for color definitions
 */
data class AppColors(
    val material: Colors,
    val background: Color,
    val backgroundContrast: Color,
    val foregroundPrimary: Color,
    val foregroundSecondary: Color,
    val foregroundContrast: Color,
    val effectColor: Color
)
