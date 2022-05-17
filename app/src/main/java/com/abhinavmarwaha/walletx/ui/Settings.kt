package com.abhinavmarwaha.walletx.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abhinavmarwaha.walletx.lock.LockCallback
import com.abhinavmarwaha.walletx.lock.PatternLock
import com.abhinavmarwaha.walletx.models.globalState
import com.abhinavmarwaha.walletx.onBoarding.AddLock

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Settings() {
    var correct = remember { mutableStateOf(false) }

    if (!correct.value) {
        Box(modifier = Modifier.padding(paddingValues = PaddingValues(top = 50.dp))) {
            PatternLock(
                size = 400.dp,
                key = ArrayList(globalState.pattern!!.toCharArray().map { it.digitToInt() }),
                dotColor = Color.White,
                dotRadius = 18f,
                lineColor = Color.White,
                lineStroke = 12f,
                callback = object : LockCallback {
                    override fun onStart() {
                    }

                    override fun onProgress(index: Int) {
                    }

                    override fun onEnd(result: ArrayList<Int>, isCorrect: Boolean) {
                        correct.value = isCorrect
                    }
                }
            )
        }
    } else
        AddLock()
}