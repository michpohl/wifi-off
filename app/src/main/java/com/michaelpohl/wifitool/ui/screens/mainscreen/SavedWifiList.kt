package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.michaelpohl.design.util.appColors
import com.michaelpohl.wifiservice.model.WifiData
import com.michaelpohl.wifitool.R

@Composable
fun SavedWifiList(
    state: MainScreenState,
    onExpandClicked: (Boolean) -> Unit,
    onDeleteWifiClicked: (WifiData) -> Unit
) {
    val isExpanded = state.showSavedWifis

    Column() {
        ExpandableHeader(
            shouldShowContent = state.showSavedWifis,
            headerText = stringResource(id = R.string.saved_wifis_header),
            onExpandClicked = onExpandClicked
        )
        if (isExpanded) {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .background(color = appColors.background)
            ) {
                itemsIndexed(state.wifis.wifis) { _, wifi ->
                    SavedWifiEntry(wifi, onDeleteWifiClicked)
                }
            }
        }
    }
}

