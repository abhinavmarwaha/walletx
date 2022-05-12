package com.abhinavmarwaha.walletx


import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.abhinavmarwaha.walletx.archmodel.CardsStore
import com.abhinavmarwaha.walletx.db.room.AppDatabase
import com.abhinavmarwaha.walletx.db.room.Card as MyCard
import com.abhinavmarwaha.walletx.db.room.CardDAO
import com.abhinavmarwaha.walletx.db.room.CardGroupDAO
import com.abhinavmarwaha.walletx.di.archModelModule
import com.abhinavmarwaha.walletx.models.Money
import com.abhinavmarwaha.walletx.ui.theme.WalletXTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import kotlinx.coroutines.launch
import org.kodein.di.*
import org.kodein.di.android.closestDI
import java.io.FileInputStream

class MainActivity : ComponentActivity(), DIAware {
    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "money")

    override val di by DI.lazy {
        bind<AppDatabase>() with singleton { AppDatabase.getInstance(this@MainActivity) }
        bind<CardDAO>() with singleton { instance<AppDatabase>().cardDao() }
        bind<CardGroupDAO>() with singleton { instance<AppDatabase>().cardGroupDao() }
        bind<Money>() with singleton { Money(dataStore) }

        import(archModelModule)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            WalletXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home()
                }
            }
        }
    }
}

@Composable
fun Home() {
    Column(Modifier.fillMaxHeight()) {
        MoneyView()
        CardsView("main")
        Row(Modifier.fillMaxWidth()) {
            LongButton({}, "All")
            Spacer(Modifier.size(10.dp))
            SmallButton({ }, "Add")
        }
        KeyValueView("main")
        LongButton({ }, "All")
    }
}

@Composable
fun KeyValueView(group: String) {
    val keyVal: List<Pair<String, String>> = listOf(Pair("Name", "Abhinav Marwaha"))
    Box(
        Modifier
            .border(BorderStroke(2.dp, Color(android.graphics.Color.WHITE)))
            .padding(20.dp)
    ) {
        Column {
            LazyColumn {
                items(keyVal.size) {
                    Row(Modifier.padding(20.dp)) {
                        Text(keyVal[it].first)
                        Spacer(Modifier.size(10.dp))
                        Text(keyVal[it].second)
                    }

                }
            }
            SmallButton(function = { /*TODO*/ }, text = "Add")
        }
    }
}

@Composable
fun DialogDemo(showDialog: Boolean, setShowDialog: (Boolean) -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text("Title")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Change the state to close the dialog
                        setShowDialog(false)
                    },
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Change the state to close the dialog
                        setShowDialog(false)
                    },
                ) {
                    Text("Dismiss")
                }
            },
            text = {
                Text("This is a text on the dialog")
            },
        )
    }
}

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

@Composable
fun LongButton(function: () -> Unit, text: String) {
    Button(
        modifier = Modifier.size(260.dp, 40.dp),
        onClick = {
            function()
        },
        shape = RoundedCornerShape(50)
    ) {
        Text(text)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CardsView(group: String) {
    val di: DI by closestDI(LocalContext.current)
    val cardStore: CardsStore by di.instance()
    val vm = CardViewModel(cardStore, group)

    HorizontalPager(count = vm.cards.size) { page ->
        val imageFile = FileInputStream(vm.cards[page].image).readBytes()
        Image(
            BitmapFactory.decodeByteArray(imageFile, 0, imageFile.size).asImageBitmap(),
            vm.cards[page].title
        )
    }
}


class CardViewModel(private val cardStore: CardsStore, group: String) : ViewModel() {
    val cards = mutableStateListOf<MyCard>()

    init {
        viewModelScope.launch {
            cardStore.getCards(group).asFlow().collect {
                cards.addAll(it)
            }
        }
    }

}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun MoneyView() {
    val (showDialog, setShowDialog) =  remember { mutableStateOf(false) }

    val di: DI by closestDI(LocalContext.current)
    val money: Money by di.instance()
    val vm = MoneyViewModel(money)

    Row {
        Image(
            painterResource(R.drawable.cash),
            contentDescription = "Cash",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(48.dp)
        )

        Text(vm.cash.toString(), Modifier.combinedClickable(enabled = true, onLongClick = {
            setShowDialog(true)
        }, onClick = {}))
        Image(
            painterResource(R.drawable.coin),
            contentDescription = "Cash",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(48.dp)
        )
        Text(vm.change.toString(),Modifier.combinedClickable(enabled = true, onLongClick = {
            setShowDialog(true)
        }, onClick = {}))

        DialogDemo(showDialog, setShowDialog)
    }

}

class MoneyViewModel(private val money: Money) : ViewModel() {
    val cash = mutableStateOf(-1)
    val change = mutableStateOf(-1)

    init {
        viewModelScope.launch {
            money.cashflow.collect {
                cash.value = it[0]
                change.value = it[1]
            }
        }
    }
}
