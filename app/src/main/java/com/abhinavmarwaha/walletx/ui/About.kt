package com.abhinavmarwaha.walletx.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.abhinavmarwaha.walletx.FeedbackConstants
import com.abhinavmarwaha.walletx.utils.UrlUtils

@Composable
fun About() {
    AboutBtn(title = "Feedback / Bug", url = FeedbackConstants.FEATUREFORMURL)
    AboutBtn(title = "Rate App", url = FeedbackConstants.RATEAPPURL)
    AboutBtn(title = "Github", url = FeedbackConstants.GITHUBREPO)
    AboutBtn(title = "Discord", url = FeedbackConstants.DISCORD)
    AboutBtn(title = "Patreon", url = FeedbackConstants.PATREON)
    AboutBtn(title = "Buy Me a Coffee", url = FeedbackConstants.COFFEE)
}

@Composable
fun AboutBtn(title: String, url: String){
    val ctx = LocalContext.current
    Text(title, Modifier.clickable { UrlUtils.openUrl( ctx,url) })
}