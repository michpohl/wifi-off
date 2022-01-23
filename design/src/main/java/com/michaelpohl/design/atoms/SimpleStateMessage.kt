package com.michaelpohl.design.atoms

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelpohl.design.util.appTextStyles

/**
 * A simple Composable to display State infos, such as "no albums available"
 */
@Composable
fun SimpleStateMessage(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 96.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        SafeSingleLineText(text = message, style = appTextStyles.h2)
    }
}
