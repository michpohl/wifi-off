package com.michaelpohl.design.atoms

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.intellij.lang.annotations.JdkConstants

/**
 * A very basic circular loading indicator
 */
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().padding(top = 96.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        CircularProgressIndicator(
            modifier = Modifier
                .width(96.dp)
                .height(96.dp)
        )
    }
}
