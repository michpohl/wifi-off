package com.michaelpohl.wifitool.ui.screens.mainscreen

import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelpohl.design.util.appTextStyles

@Composable
fun NumberInputField(number: Int, focusManager: FocusManager, onNumberChanged: (Int) -> Unit) {
    var currentNumberString = remember { mutableStateOf(number.toString()) }
    OutlinedTextField(
        modifier = Modifier.requiredWidth(75.dp),
        textStyle = appTextStyles.body2.copy(textAlign = TextAlign.Center),
        singleLine = true,
        value = currentNumberString.value,
        onValueChange = {
            currentNumberString.value = when {
                it.length < 4 -> it
                else -> it.subSequence(0, 2).toString()
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            if (currentNumberString.value.isBlank()) currentNumberString.value = "1"
            val newNumber = currentNumberString.value.toInt()
            if (newNumber != number) {
                onNumberChanged(newNumber)
            }
        })
    )
}