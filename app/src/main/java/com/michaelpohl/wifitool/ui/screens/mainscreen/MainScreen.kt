package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.michaelpohl.design.util.appColors
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifitool.R
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun MainScreen(viewModel: MainScreenViewModel) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val flow = viewModel.stateFlow

    val stateFlow = remember(flow, lifecycleOwner) {
        flow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }

    val state by stateFlow.collectAsState(MainScreenState())

    if (state.isInitialState) {
        Timber.d("initial state")
        viewModel.getSavedWifis()
    }

    Column {
        MainToggle(state) { viewModel.toggleServiceEnabled(it) }
        StatusInfo(state) { viewModel.saveWifi(it) }
        SavedWifiList(state) { viewModel.deleteWifi(it) }
        LogList(state)
    }
}

@Composable
private fun MainToggle(
    state: MainScreenState,
    onToggle: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val toggleState = remember { mutableStateOf(state.isServiceEnabled) }

        Switch(checked = toggleState.value, onCheckedChange = {
            toggleState.value = it
            onToggle(toggleState.value)
        })
    }
}


@Composable
private fun LogList(
    state: MainScreenState
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            .background(color = appColors.background)
    ) {
        items(count = state.timberMessages.get().size, itemContent = { index ->
            with(state.timberMessages.get()[index]) {
                Text(
                    text = this,
                    fontSize = 9.sp,
                )
                Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.6.dp)
            }
        })
        if (state.timberMessages.get().size > 1) {
            scope.launch {
                listState.animateScrollToItem(state.timberMessages.get().size - 1)
            }
        }
    }
}

@Composable
private fun SavedWifiList(
    state: MainScreenState,
    onDeleteWifiClicked: (WifiData) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            .background(color = appColors.background)
    ) {
        Text(text = stringResource(R.string.saved_wifis_info))
        state.wifis.wifis.forEach {
            SavedWifiEntry(it, onDeleteWifiClicked)
        }
    }
}

@Composable
private fun StatusInfo(
    state: MainScreenState,
    onSaveWifiClicked: (WifiData) -> Unit

) {
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
        Text(text = state.wifiSituation.toString(), Modifier.padding(16.dp))
        Text(text = ssidText, Modifier.padding(all = 16.dp))
        Text(text = cellText, Modifier.padding(all = 16.dp))

        state.currentConnectedWifi?.let {
            if (!state.isCurrentWifiAlreadySaved) {

                Button(onClick = { onSaveWifiClicked(it) }, Modifier.padding(all = 16.dp)) {
                    Text(text = stringResource(R.string.btn_save_current_wifi))
                }
            }
        }
    }
}

@Composable
private fun SavedWifiEntry(
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

        