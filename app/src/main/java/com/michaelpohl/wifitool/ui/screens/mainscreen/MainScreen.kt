package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
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
//        viewModel.getSavedWifis()
    }

    Column {
        MainToggle(state) { viewModel.toggleServiceEnabled(it) }
        StatusInfo(state) { viewModel.saveWifi(it) }
        SavedWifiList(state) { viewModel.deleteWifi(it) }
        LogList(state)
    }
}


