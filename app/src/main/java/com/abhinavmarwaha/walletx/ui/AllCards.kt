package com.abhinavmarwaha.walletx.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abhinavmarwaha.walletx.archmodel.CardGroupsStore
import com.abhinavmarwaha.walletx.db.room.CardGroup
import com.abhinavmarwaha.walletx.db.room.CardGroupDAO
import com.abhinavmarwaha.walletx.ui.compose.CardsView
import com.abhinavmarwaha.walletx.ui.compose.EditDialog
import com.abhinavmarwaha.walletx.ui.widgets.LongButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.android.closestDI
import org.kodein.di.instance

@Composable
fun AllCards(navController: NavController) {
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    val di: DI by closestDI(LocalContext.current)
    val cardGroupsStore: CardGroupsStore by di.instance()
    val cardGroupDAO: CardGroupDAO by di.instance()
    val groups = cardGroupsStore.getCardGroups().collectAsState(listOf())

    val addGroupTitle = remember { mutableStateOf("") }
    val editGroupTitle = remember { mutableStateOf("") }
    val editGroupId = remember { mutableStateOf(0L) }
    val editing = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.padding(30.dp)) {
        LongButton(function = {
            editing.value = false
            editGroupId.value = 0
            setShowDialog(true)
        }, text = "Add", Modifier.padding(30.dp).align(Alignment.CenterHorizontally))
        LazyColumn() {
            items(items = groups.value) { item ->
                Column() {
                    Text(item.group, modifier = Modifier.clickable {
                        editGroupTitle.value = item.group
                        editGroupId.value = item.guid
                        editing.value = true
                    })
                    CardsView(group = item.group, navController)
                }
            }
        }
        EditDialog(
            showDialog,
            setShowDialog,
            addGroupTitle,
            "Group Name",
            onNegativeClick = {
                if (editing.value) {
                    coroutineScope.launch {
                        CoroutineScope(Dispatchers.IO).launch {
                            cardGroupDAO.deleteGroups(listOf(editGroupId.value))
                        }
                    }
                }
            },
            negativeText = if(editing.value) "Delete" else "Dismiss"
        ) {
            if (addGroupTitle.value.isNotEmpty()) {
                coroutineScope.launch {
                    val group = CardGroup()
                    group.group = addGroupTitle.value
                    CoroutineScope(Dispatchers.IO).launch {
                        cardGroupDAO.insertGroup(group)
                    }
                }
            }
        }
    }
}