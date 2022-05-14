package com.abhinavmarwaha.walletx.ui.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LongButton(function: () -> Unit, text: String, modifier: Modifier) {
    Button(
        modifier = modifier.size(260.dp, 40.dp),
        onClick = {
            function()
        },
        shape = RoundedCornerShape(50)
    ) {
        Text(text, color = Color.White)
    }
}