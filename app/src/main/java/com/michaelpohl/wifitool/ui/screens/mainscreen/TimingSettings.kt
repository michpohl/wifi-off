package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.michaelpohl.design.util.appColors
import com.michaelpohl.wifiservice.looper.TimingThresholds
import com.michaelpohl.wifitool.R

@Suppress("MagicNumber")
@Composable
fun TimingSettings(
    timings: TimingThresholds,
    modifier: Modifier? = null,
    onTimingsChanged: (TimingThresholds) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val maxTextWidth = (LocalConfiguration.current.screenWidthDp * 0.7).dp

    Column(
        modifier = (modifier ?: Modifier).fillMaxWidth().background(color = appColors.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.number_input_description_scan_interval),
                modifier = Modifier.widthIn(max = maxTextWidth)
            )
            NumberInputField(
                timings.scanInterval, focusManager
            ) { onTimingsChanged(timings.copy(scanInterval = it)) }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.number_input_description_turn_off_threshold),
                modifier = Modifier.widthIn(max = maxTextWidth)
            )
            NumberInputField(
                timings.turnOffThreshold, focusManager
            ) { onTimingsChanged(timings.copy(turnOffThreshold = it)) }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.number_input_description_turn_on_threshold),
                modifier = Modifier.widthIn(max = maxTextWidth)
            )
            NumberInputField(
                timings.turnOnThreshold, focusManager
            ) { onTimingsChanged(timings.copy(turnOnThreshold = it)) }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.number_input_description_turned_off_min_threshold),
                modifier = Modifier.widthIn(max = maxTextWidth)
            )
            NumberInputField(
                timings.turnedOffMinThreshold, focusManager
            ) { onTimingsChanged(timings.copy(turnedOffMinThreshold = it)) }
        }
    }
}
