package com.michaelpohl.design.atoms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.michaelpohl.design.R
import com.michaelpohl.design.util.appColors
import com.michaelpohl.design.util.appTextStyles

/**
 * A very simple error message, showing a header and a message (if available)
 */
@Composable
fun SimpleErrorMessage(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SafeSingleLineText(
            text = stringResource(R.string.simple_error_message_header),
            style = appTextStyles.h2,
            color = appColors.foregroundPrimary

        )
        Text(
            text = message,
            style = appTextStyles.body1,
            color = appColors.foregroundPrimary,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 32.dp, top = 16.dp, end = 32.dp)

        )
    }
}
