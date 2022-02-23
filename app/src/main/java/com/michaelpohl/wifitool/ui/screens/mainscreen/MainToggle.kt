package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainToggle(
    state: MainScreenState,
    onToggle: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Switch(checked = state.isServiceEnabled, onCheckedChange = {
            onToggle(it)
        })
    }
}