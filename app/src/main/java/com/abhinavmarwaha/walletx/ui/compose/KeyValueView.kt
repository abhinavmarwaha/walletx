package com.abhinavmarwaha.walletx.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abhinavmarwaha.walletx.ui.widgets.SmallButton

@Composable
fun KeyValueView(group: String) {
    val keyVal: List<Pair<String, String>> = listOf(Pair("Name", "Abhinav Marwaha"))
    Box(
        Modifier
            .border(BorderStroke(2.dp, Color(android.graphics.Color.WHITE)))
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column (horizontalAlignment = Alignment.End){
            LazyColumn {
                items(keyVal.size) {
                    Row(Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(keyVal[it].first)
                        Text(keyVal[it].second)
                    }

                }
            }
                SmallButton(function = { /*TODO*/ }, text = "Add",)

        }
    }
}
