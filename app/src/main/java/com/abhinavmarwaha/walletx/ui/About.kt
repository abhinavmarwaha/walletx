package com.abhinavmarwaha.walletx.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.abhinavmarwaha.walletx.FeedbackConstants
import com.abhinavmarwaha.walletx.ui.theme.DarkRed
import com.abhinavmarwaha.walletx.utils.UrlUtils

@Composable
fun About() {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        AboutBtn(
            title = "Feedback / Bug",
            url = FeedbackConstants.FEATUREFORMURL,
            subTitle = "Microsoft forms"
        )
        AboutBtn(title = "Rate App", url = FeedbackConstants.RATEAPPURL, subTitle = "play store")
        AboutBtn(title = "Github", url = FeedbackConstants.GITHUBREPO)
        AboutBtn(title = "Discord", url = FeedbackConstants.DISCORD)
        AboutBtn(title = "Patreon", url = FeedbackConstants.PATREON)
        AboutBtn(title = "Buy Me a Coffee", url = FeedbackConstants.COFFEE, subTitle = "ko-fi")
    }

}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun AboutBtn(title: String, url: String, subTitle: String = "") {
    val ctx = LocalContext.current
    Button(
        modifier = Modifier,
        colors = ButtonDefaults.buttonColors(containerColor = DarkRed),
        onClick = {
            UrlUtils.openUrl(ctx, url)
        },
        shape = RoundedCornerShape(50)
    ) {
        Column() {
            androidx.compose.material3.Text(
                title,
                color = Color.White,
                fontSize = TextUnit(2f, TextUnitType.Em)
            )
            androidx.compose.material3.Text(
                subTitle,
                color = Color.Gray,
                fontSize = TextUnit(1f, TextUnitType.Em)
            )
        }
    }
}