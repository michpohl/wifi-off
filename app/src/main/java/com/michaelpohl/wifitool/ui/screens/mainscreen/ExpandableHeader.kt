package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.michaelpohl.design.util.appColors
import com.michaelpohl.design.util.appTextStyles
import com.michaelpohl.wifitool.R

@Composable
fun ExpandableHeader(
    shouldShowContent: Boolean,
    headerText: String,
    onExpandClicked: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .background(appColors.background)
            .clickable { onExpandClicked(!shouldShowContent) },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val iconId =
            if (shouldShowContent) R.drawable.baseline_expand_less_24 else R.drawable.baseline_expand_more_24
        Text(
            text = headerText,
            modifier = Modifier.padding(all = 16.dp),
            style = appTextStyles.h2

        )
        Image(
            painter = painterResource(id = iconId),
            contentDescription = "expand icon",
            modifier = Modifier.padding(all = 16.dp)
        )
    }
}
