package com.abhinavmarwaha.walletx.ui.compose

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhinavmarwaha.walletx.archmodel.NotesStore
import com.abhinavmarwaha.walletx.db.room.KeyValueNote
import com.abhinavmarwaha.walletx.ui.theme.DarkRed
import com.abhinavmarwaha.walletx.ui.widgets.SmallButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.DI
import org.kodein.di.android.closestDI
import org.kodein.di.instance

@Composable
fun NoteView(id: Long) {
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    val di: DI by closestDI(LocalContext.current)
    val notesStore: NotesStore by di.instance()
    val vm = NotesViewModel(notesStore, id)

    val addKey = remember { mutableStateOf("") }
    val addValue = remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Box(
        Modifier
            .border(BorderStroke(2.dp, Color(android.graphics.Color.GRAY)))
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Text(vm.note.value.title)
            vm.note.value.keyValues.map {
                Row(
                    Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(it.first)
                    Text(it.second)
                }
            }
            SmallButton(function = {
                setShowDialog(true)
            }, text = "Add", color = DarkRed)
            EditDialog2(
                showDialog,
                setShowDialog,
                addKey,
                addValue,
                "Add Info",
                "Name",
                "Value",
            ) {
                if (addKey.value.isNotEmpty()) {
                    coroutineScope.launch {
                        val keyVal = Pair(addKey.value, addValue.value)
                        val newKeyValues = mutableListOf<Pair<String, String>>()
                        newKeyValues.addAll(vm.note.value.keyValues)
                        newKeyValues.add(keyVal)
                        vm.note.value.keyValues = newKeyValues
                        val result = withContext(Dispatchers.IO) {
                            notesStore.upsertNote(vm.note.value)
                        }
                    }
                }
            }
        }
    }
}


class NotesViewModel(private val notesStore: NotesStore, private val id: Long) : ViewModel() {
    val note = mutableStateOf(KeyValueNote())

    init {
        viewModelScope.launch {
            note.value = notesStore.getNote(id)
        }
    }

}