package com.abhinavmarwaha.walletx.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun EditDialog(
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
    text: MutableState<String>,
    title: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
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
                    TextField(
                        text.value,
                        onValueChange = { text.value = it },
                        keyboardOptions = keyboardOptions
                    )
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