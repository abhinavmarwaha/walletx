package com.abhinavmarwaha.walletx.ui.compose

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.abhinavmarwaha.walletx.archmodel.CardsStore
import com.abhinavmarwaha.walletx.db.room.Card
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
    val di: DI by closestDI(LocalContext.current)
    val cardStore: CardsStore by di.instance()
    val vm = CardViewModel(cardStore, group)

    return if (group.compareTo("main") == 0 && vm.cards.size == 0) {
        Box(
            Modifier
                .border(border = BorderStroke(10.dp, Color.White))
                .fillMaxWidth()
                .height(70.dp), contentAlignment = Alignment.Center
        ) {
            Text("Add Card", color = Color.Red)
        }
    } else {
        HorizontalPager(count = vm.cards.size) { page ->
            val imageFile = FileInputStream(vm.cards[page].image).readBytes()
            Image(
                BitmapFactory.decodeByteArray(imageFile, 0, imageFile.size).asImageBitmap(),
                vm.cards[page].title
            )
        }
    }

}


class CardViewModel(private val cardStore: CardsStore, group: String) : ViewModel() {
    val cards = mutableStateListOf<Card>()

    init {
        viewModelScope.launch {
            cardStore.getCards(group).asFlow().collect {
                cards.addAll(it)
            }
        }
    }

}
