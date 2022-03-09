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
import androidx.compose.ui.unit.dp
import com.michaelpohl.design.util.appColors
import com.michaelpohl.design.util.appTextStyles
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

        val ssidText = state.currentConnectedWifi?.ssid ?: stringResource(id = R.string.unknown)

        val cellText = state.currentConnectedWifi?.cellIDs?.joinToString(", ")
            ?: stringResource(id = R.string.unknown)

        Text(
            text = state.wifiSituation.asString(LocalContext.current),
            Modifier.padding(16.dp),
            style = appTextStyles.h2
        )
        Column(Modifier.padding(all = 16.dp)) {
            Text(
                text = stringResource(id = R.string.current_wifi_info),
                style = appTextStyles.subtitle1
            )
            Text(
                text = ssidText, style = appTextStyles.body2
            )
            Text(
                text = stringResource(id = R.string.wifi_known_close_cells),
                style = appTextStyles.subtitle1,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = cellText, style = appTextStyles.body2
            )
        }

        state.currentConnectedWifi?.let {
            if (!state.isCurrentWifiAlreadySaved) {
                Button(onClick = { onSaveWifiClicked(it) }, Modifier.padding(all = 16.dp)) {
                    Text(text = stringResource(R.string.btn_save_current_wifi))
                }
            }
        }
    }

}