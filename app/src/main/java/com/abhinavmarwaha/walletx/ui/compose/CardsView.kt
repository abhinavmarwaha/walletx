package com.abhinavmarwaha.walletx.ui.compose

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhinavmarwaha.walletx.archmodel.CardsStore
import com.abhinavmarwaha.walletx.db.room.Card
import com.abhinavmarwaha.walletx.utils.MediaUtils
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.io.FileInputStream

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CardsView(group: String) {
    val context = LocalContext.current
    val di: DI by closestDI(LocalContext.current)
    val cardStore: CardsStore by di.instance()
    val cards = cardStore.getCards(group).collectAsState(listOf())

    return if (cards.value.isEmpty()) {
        Box(
            Modifier
                .border(border = BorderStroke(10.dp, Color.White))
                .fillMaxWidth()
                .height(70.dp), contentAlignment = Alignment.Center
        ) {
            Text("Add Card", color = Color.Red)
        }
    } else {
        HorizontalPager(count = cards.value.size, itemSpacing = 0.dp) { page ->
            Log.e("Image", cards.value[page].image)
            val imageFile = FileInputStream(MediaUtils.getRealPathFromURI_API19(context, Uri.parse(cards.value[page].image))).readBytes()
            Image(
                BitmapFactory.decodeByteArray(imageFile, 0, imageFile.size).asImageBitmap(),
                cards.value[page].title,
                Modifier.width(200.dp)
            )
        }
    }

}
