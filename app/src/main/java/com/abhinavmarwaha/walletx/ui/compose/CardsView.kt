package com.abhinavmarwaha.walletx.ui.compose

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abhinavmarwaha.walletx.archmodel.CardsStore
import com.abhinavmarwaha.walletx.crypto.ImageCryptor
import com.abhinavmarwaha.walletx.models.globalState
import com.abhinavmarwaha.walletx.ui.theme.DarkRed
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.android.closestDI
import org.kodein.di.instance

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CardsView(guid: Long, navController: NavController) {
    val context = LocalContext.current
    val di: DI by closestDI(LocalContext.current)
    val cardStore: CardsStore by di.instance()
    val cards = cardStore.getCards(guid).collectAsState(listOf())

    return if (cards.value.isEmpty()) {
        Box(
            Modifier
                .border(border = BorderStroke(2.dp, Color.White))
                .fillMaxWidth()
                .height(200.dp)
                .clickable { navController.navigate("addCard") },
            contentAlignment = Alignment.Center
        ) {
            Text("Add Card", color = Color.Red)
        }
    } else {
        Column(Modifier.fillMaxSize()) {
            val pagerState = rememberPagerState()
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.wrapContentWidth(),
                count = cards.value.size,
                itemSpacing = 11.dp,
                contentPadding = PaddingValues(11.dp)
            ) { page ->
                val fileBytes = ImageCryptor(globalState.pattern!!).decryptBitmap(
                    cards.value[page].image,
                    context
                )
                val bitmap = BitmapFactory.decodeByteArray(fileBytes!!, 0, fileBytes.size)
                Image(
                    bitmap.asImageBitmap(),
                    cards.value[page].title,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .height(200.dp)
                        .clickable {
                            navController.navigate("addCard/${cards.value[page].id}")
                        }
                )
            }
//            HorizontalPagerIndicator(
//                pagerState = pagerState,
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .padding(16.dp),
//            )

            ActionsRow(
                pagerState = pagerState,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun ActionsRow(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    infiniteLoop: Boolean = false
) {
    Row(modifier) {
        val scope = rememberCoroutineScope()

        IconButton(
            enabled = infiniteLoop || pagerState.currentPage > 0,
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            }
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = DarkRed)
        }

        IconButton(
            enabled = infiniteLoop || pagerState.currentPage < pagerState.pageCount - 1,
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        ) {
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = DarkRed)
        }
    }
}