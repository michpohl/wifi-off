package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(color = appColors.background)
            ) {
                state.wifis.wifis.forEach {
                    SavedWifiEntry(it, onDeleteWifiClicked)
                }
            }
        }
    }
}
