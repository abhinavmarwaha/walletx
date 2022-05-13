package com.abhinavmarwaha.walletx.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

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
    onPositiveClick: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {

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
                        label = { Text(lbl) }
                    )
                    TextField(
                        text2.value,
                        onValueChange = { text2.value = it },
                        keyboardOptions = keyboardOptions2,
                        label = { Text(lbl2) }
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
                        setShowDialog(false)
                    },
                ) {
                    Text("Dismiss")
                }
            },
        )
    }
}