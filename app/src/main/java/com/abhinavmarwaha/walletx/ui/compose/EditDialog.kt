package com.abhinavmarwaha.walletx.ui.compose

import android.util.Log
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color

@Composable
fun EditDialog(
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
    text: MutableState<String>,
    title: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    negativeText: String,
    onNegativeClick: () -> Unit,
    onPositiveClick: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                Log.e("Dismiss", "hello")
            },
            title = {
                Text(title)
            },
            text = {
                    TextField(
                        text.value,
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White,
                        ),
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