package com.abhinavmarwaha.walletx.ui.widgets

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun SmallButton(function: () -> Unit, text: String) {
    Button(
        onClick = {
            function()
        },
        shape = RoundedCornerShape(50)
    ) {
        Text(text)
    }
}