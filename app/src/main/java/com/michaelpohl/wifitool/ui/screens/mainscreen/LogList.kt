package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.michaelpohl.design.util.appColors
import com.michaelpohl.wifitool.R
import kotlinx.coroutines.launch

@Composable
fun LogList(
    state: MainScreenState,
    onExpandClicked: (Boolean) -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val isExpanded = state.showLogs
    Column() {

        ExpandableHeader(
            shouldShowContent = state.showLogs,
            headerText = stringResource(R.string.logs_header),
            onExpandClicked = onExpandClicked
        )
        if (isExpanded) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = appColors.background)
            ) {
               state.timberMessages.get().forEach {

                        Text(
                            text = it,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                        )
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.6.dp)
                    }
                }

        }
    }
}
