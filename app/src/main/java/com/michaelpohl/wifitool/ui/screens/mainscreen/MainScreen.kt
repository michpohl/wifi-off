package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.michaelpohl.wifiservice.looper.TimingThresholds
import timber.log.Timber

@Composable
fun MainScreen(viewModel: MainScreenViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val flow = viewModel.stateFlow
    val stateFlow = remember(flow, lifecycleOwner) {
        flow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }
    val state by stateFlow.collectAsState(MainScreenState())

    Column {
        MainToggle(state) { viewModel.toggleServiceEnabled(it) }
        StatusInfo(state) { viewModel.saveWifi(it) }
        TimingSettings(state) { viewModel.onTimingsChanged(it) }
        SavedWifiList(state = state,
            onExpandClicked = { viewModel.toggleShowSavedWifis(it) },
            onDeleteWifiClicked = { viewModel.deleteWifi(it) })
        LogList(state) { viewModel.toggleShowLogs(it) }
    }
}

@Composable
fun TimingSettings(state: MainScreenState, onTimingsChanged: (TimingThresholds) -> Unit) {
    val focusManager = LocalFocusManager.current


    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            NumberInputField(5, focusManager) { Timber.d(" New number: $it") }
        }
    }
}

