package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelpohl.design.util.appColors
import com.michaelpohl.design.util.appTextStyles
import com.michaelpohl.wifiservice.looper.WifiSituation
import com.michaelpohl.wifiservice.looper.WifiSituation.*
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifitool.R
import com.michaelpohl.wifitool.common.util.asString

@Composable
fun StatusInfo(
    state: MainScreenState,
    onSaveWifiClicked: (WifiData) -> Unit

) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(color = appColors.background)
    ) {
        val ssidText = stringResource(
            R.string.current_wifi_info,
            state.currentConnectedWifi?.ssid ?: stringResource(id = R.string.unknown)
        )
        val cellText = stringResource(
            R.string.current_cell_info,
            state.currentConnectedWifi?.cellID ?: stringResource(id = R.string.unknown)
        )
        Text(text = state.wifiSituation.asString(LocalContext.current), Modifier.padding(16.dp),)
        Text(text = ssidText, Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp))
        Text(text = cellText, Modifier.padding(start = 16.dp, bottom = 16.dp, end = 16.dp)     )

        state.currentConnectedWifi?.let {
            if (!state.isCurrentWifiAlreadySaved) {
                Button(onClick = { onSaveWifiClicked(it) }, Modifier.padding(all = 16.dp)) {
                    Text(text = stringResource(R.string.btn_save_current_wifi))
                }
            }
        }
    }

}