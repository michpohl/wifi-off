package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifitool.R

@Composable
fun SavedWifiEntry(
    it: WifiData,
    onDeleteClicked: (WifiData) -> Unit
) {
    Row() {
        Text(text = it.ssid)
        Text(text = it.cellID)
        Button(onClick = { onDeleteClicked(it) }) {
            Text(text = stringResource(R.string.btn_delete_wifi))
        }
    }
}