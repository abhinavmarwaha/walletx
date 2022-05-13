package com.abhinavmarwaha.walletx.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.abhinavmarwaha.walletx.archmodel.CardGroupsStore
import com.abhinavmarwaha.walletx.db.room.CardGroup
import com.abhinavmarwaha.walletx.db.room.CardGroupDAO
import com.abhinavmarwaha.walletx.models.Money
import com.abhinavmarwaha.walletx.ui.compose.CardsView
import com.abhinavmarwaha.walletx.ui.compose.EditDialog
import com.abhinavmarwaha.walletx.ui.compose.MoneyViewModel
import com.abhinavmarwaha.walletx.ui.widgets.LongButton
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.android.closestDI
import org.kodein.di.instance

@Composable
fun AllCards(){
    val (showDialog, setShowDialog) =  remember { mutableStateOf(false) }

    val di: DI by closestDI(LocalContext.current)
    val cardGroupsStore: CardGroupsStore by di.instance()
    val cardGroupDAO: CardGroupDAO by di.instance()
    val vm = AllCardsViewModel(cardGroupsStore)

    val addGroupTitle = remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()


    Column() {
        LongButton(function = {setShowDialog(true)}, text = "Add")
        LazyColumn(){
            items(items = vm.groups) {
                    item ->
                Column() {
                    Text(item.group)
                    CardsView(group = item.group)
                }

            }
        }
        EditDialog(
            showDialog,
            setShowDialog,
            addGroupTitle,
            "Group Name",
        ) {
            if(addGroupTitle.value.isNotEmpty()) {
                coroutineScope.launch {
                    val group = CardGroup()
                    group.group = addGroupTitle.value
                    cardGroupDAO.insertGroup(group)
                }
            }
        }
    }


}

class AllCardsViewModel(private val groupsStore: CardGroupsStore): ViewModel(){
    val groups = mutableStateListOf<CardGroup>()

    init {
        viewModelScope.launch {
            groupsStore.getCardGroups().asFlow().collect{
                groups.addAll(it)
            }
        }
    }
}