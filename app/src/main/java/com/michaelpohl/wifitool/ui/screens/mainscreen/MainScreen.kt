package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
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
    val scrollState = rememberScrollState()
    Column(Modifier.fillMaxSize().verticalScroll(scrollState)) {
        MainToggle(state) { viewModel.toggleServiceEnabled(it) }
        StatusInfo(state) { viewModel.saveWifi(it) }
        TimingSettings(state.timings, modifier = Modifier.padding(top = 16.dp)) { viewModel.onTimingsChanged(it) }
        SavedWifiList(state = state,
            onExpandClicked = { viewModel.toggleShowSavedWifis(it) },
            onDeleteWifiClicked = { viewModel.deleteWifi(it) })
        LogList(state) { viewModel.toggleShowLogs(it) }
    }
}
