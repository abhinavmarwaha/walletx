package com.abhinavmarwaha.walletx.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhinavmarwaha.walletx.R
import com.abhinavmarwaha.walletx.models.Money
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.android.closestDI
import org.kodein.di.instance


@Composable
@OptIn(ExperimentalFoundationApi::class)
fun MoneyView() {
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    val di: DI by closestDI(LocalContext.current)
    val money: Money by di.instance()
    val vm = MoneyViewModel(money)

    val editMoney = remember { mutableStateOf("") }
    var editMoneyTitle = remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 30.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.combinedClickable(enabled = true, onClick = {
                editMoneyTitle.value = "Cash"
                editMoney.value = vm.cash.value.toString()
                setShowDialog(true)
            })
        ) {
            Image(
                painterResource(R.drawable.cash),
                contentDescription = "Cash",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.size(20.dp))
            Text(vm.cash.value.toString())
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.combinedClickable(enabled = true, onClick = {
                editMoneyTitle.value = "Change"
                editMoney.value = vm.change.value.toString()
                setShowDialog(true)
            })
        ) {
            Image(
                painterResource(R.drawable.coin),
                contentDescription = "Cash",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.size(20.dp))
            Text(vm.change.value.toString())
        }
        EditDialog(
            showDialog,
            setShowDialog,
            editMoney,
            editMoneyTitle.value,
            KeyboardOptions(keyboardType = KeyboardType.Number),
            onNegativeClick = {},
            negativeText = "Dismiss"
        ) {
            if (editMoney.value.isEmpty()) editMoney.value = "0"
            if (editMoneyTitle.value.compareTo("Cash") == 0) {
                coroutineScope.launch {
                    money.setCash(editMoney.value.toInt())
                }
            } else {
                coroutineScope.launch {
                    money.setChange(editMoney.value.toInt())
                }

            }
        }
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
