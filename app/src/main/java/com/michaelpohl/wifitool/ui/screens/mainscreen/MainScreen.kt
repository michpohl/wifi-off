package com.michaelpohl.wifitool.ui.screens.mainscreen

import android.widget.ToggleButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.michaelpohl.design.util.appColors
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifitool.R

@Composable
fun MainScreen(viewModel: MainScreenViewModel) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val flow = viewModel.stateFlow

    val stateFlow = remember(flow, lifecycleOwner) {
        flow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }

    val state by stateFlow.collectAsState(MainScreenState())

    if (state.isInitialState) {
        viewModel.getSavedWifis()
        viewModel.getCurrentConnectedWifi()
    }
    Column {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
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

            Text(text = ssidText, Modifier.padding(all = 16.dp))
            Text(text = cellText, Modifier.padding(all = 16.dp))

            state.currentConnectedWifi?.let {
                if (!state.isCurrentWifiAlreadySaved) {

                    Button(onClick = { viewModel.saveWifi(it) }, Modifier.padding(all = 16.dp)) {
                        Text(text = stringResource(R.string.btn_save_current_wifi))
                    }
                }
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                .background(color = appColors.background)
        ) {
            Text(text = stringResource(R.string.saved_wifis_info))
            state.wifis.wifis.forEach {
                SavedWifiEntry(it, viewModel)
            }
        }
    }
}

@Composable
private fun SavedWifiEntry(
    it: WifiData,
    viewModel: MainScreenViewModel
) {
    Row() {
        Text(text = it.ssid)
        Text(text = it.cellID)
        Button(onClick = { viewModel.deleteWifi(it) }) {
            Text(text = stringResource(R.string.btn_delete_wifi))
        }
    }
}

        