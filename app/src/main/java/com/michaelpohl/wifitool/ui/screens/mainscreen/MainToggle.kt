package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.michaelpohl.design.util.appTextStyles
import com.michaelpohl.wifitool.R

@Composable
fun MainToggle(
    state: MainScreenState,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        val isEnabled = state.isServiceEnabled
        val enabledText = stringResource(if (isEnabled) R.string.enabled else R.string.disabled)
        Text(
            text = stringResource(id = R.string.wifi_monitoring_status_prefix, enabledText),
            style = appTextStyles.h6,
            modifier = Modifier.align(
                Alignment.CenterVertically
            )
        )
        Switch(checked = state.isServiceEnabled, onCheckedChange = {
            onToggle(it)
        })
    }
}