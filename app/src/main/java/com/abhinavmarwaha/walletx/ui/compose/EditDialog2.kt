package com.abhinavmarwaha.walletx.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties

@Composable
fun EditDialog2(
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
    text: MutableState<String>,
    text2: MutableState<String>,
    title: String,
    lbl: String,
    lbl2: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardOptions2: KeyboardOptions = KeyboardOptions(),
    negativeText: String,
    onNegativeClick: () -> Unit,
    onPositiveClick: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
            onDismissRequest = {
                setShowDialog(false)
            },
            title = {
                Text(title)
            },
            text = {
                Column() {
                    TextField(
                        text.value,
                        onValueChange = { text.value = it },
                        keyboardOptions = keyboardOptions,
                        label = { Text(lbl) },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White,
                        )
                    )
                    TextField(
                        text2.value,
                        onValueChange = { text2.value = it },
                        keyboardOptions = keyboardOptions2,
                        label = { Text(lbl2) },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White,
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onPositiveClick()
                        setShowDialog(false)
                    },
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onNegativeClick()
                        setShowDialog(false)
                    },
                ) {
                    Text(negativeText)
                }
            },
        )
    }
}