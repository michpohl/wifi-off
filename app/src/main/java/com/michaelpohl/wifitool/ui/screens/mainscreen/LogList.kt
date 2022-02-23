package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.michaelpohl.design.util.appColors
import kotlinx.coroutines.launch

@Composable
fun LogList(
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