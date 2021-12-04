package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen() {
    val viewModel: MainScreenViewModel = viewModel()

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

    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {
        Row() {
            Text(text = "Current connected Wifi:")
            Text(text = state.currentConnectedWifi?.ssid ?: "unknown")
        }
           }
}

