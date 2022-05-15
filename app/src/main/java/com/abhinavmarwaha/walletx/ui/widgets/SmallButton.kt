package com.abhinavmarwaha.walletx.ui.widgets

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun SmallButton(function: () -> Unit, text: String, color: Color, modifier: Modifier) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        onClick = {
            function()
        },
        shape = RoundedCornerShape(50)
    ) {
        Text(text, color=Color.White)
    }
}