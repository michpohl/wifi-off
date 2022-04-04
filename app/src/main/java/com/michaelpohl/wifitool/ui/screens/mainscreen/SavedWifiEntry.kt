package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.michaelpohl.design.util.appColors
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifitool.R

@Composable
fun SavedWifiEntry(
    wifi: WifiData,
    onDeleteClicked: (WifiData) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val ssidText = stringResource(id = R.string.saved_wifi_ssid, wifi.ssid)
        val cellIDText = stringResource(
            id = R.string.saved_wifi_connected_cell_id,
            wifi.cellIDs.joinToString(", ")
        )

        Column(modifier = Modifier.fillMaxWidth(fraction = 0.7F),) {
            Text(text = ssidText, textAlign = TextAlign.Start)
            Text(text = cellIDText)
        }
        Button(
            onClick = { onDeleteClicked(wifi) },
            colors = ButtonDefaults.buttonColors(backgroundColor = appColors.backgroundContrast)
        ) {
            Text(
                text = stringResource(R.string.btn_delete_wifi),
                color = appColors.foregroundContrast
            )
        }
    }
}
