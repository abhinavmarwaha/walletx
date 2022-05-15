package com.abhinavmarwaha.walletx.ui.compose

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
    val (showNoteEdit, setNoteEdit) = remember { mutableStateOf(false) }
    val editing = remember { mutableStateOf(false) }
    var oldPair = Pair("", "")
    val context = LocalContext.current
    val di: DI by closestDI(LocalContext.current)
    val notesStore: NotesStore by di.instance()
    val vm = NotesViewModel(notesStore, id)

    val addKey = remember { mutableStateOf("") }
    val addValue = remember { mutableStateOf("") }
    val editTitle = remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Box(
        Modifier
            .border(BorderStroke(2.dp, Color(android.graphics.Color.GRAY)))
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                vm.note.value.title,
                color = Color.White,
                modifier = Modifier.clickable {
                    editTitle.value = vm.note.value.title
                    setNoteEdit(true)
                })
            vm.note.value.keyValues.map {
                Row(
                    Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .clickable {
                            addKey.value = it.first
                            addValue.value = it.second
                            editing.value = true
                            oldPair = it
                            setShowDialog(true)
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(it.first)
                    Text(it.second)
                }
            }
            SmallButton(function = {
                addKey.value = ""
                addValue.value = ""
                editing.value = false
                setShowDialog(true)
            }, text = "Add", color = DarkRed, modifier = Modifier)
            EditDialog2(
                showDialog,
                setShowDialog,
                addKey,
                addValue,
                "Add Info",
                "Name",
                "Value",
                onNegativeClick = {
                    if (editing.value) {
                        coroutineScope.launch {
                            val result = withContext(Dispatchers.IO) {
                                vm.note.value.keyValues.drop(
                                    vm.note.value.keyValues.indexOf(
                                        oldPair
                                    )
                                )
                                val result = withContext(Dispatchers.IO) {
                                    notesStore.upsertNote(vm.note.value)
                                }
                            }
                        }
                    }
                },
                negativeText = if (editing.value) "Delete" else "Dismiss"
            ) {
                if (addKey.value.isNotEmpty()) {
                    coroutineScope.launch {
                        val keyVal = Pair(addKey.value, addValue.value)
                        val newKeyValues = mutableListOf<Pair<String, String>>()

                        if (editing.value) vm.note.value.keyValues.drop(
                            vm.note.value.keyValues.indexOf(
                                oldPair
                            )
                        )

                        newKeyValues.addAll(vm.note.value.keyValues)
                        newKeyValues.add(keyVal)
                        vm.note.value.keyValues = newKeyValues
                        val result = withContext(Dispatchers.IO) {
                            notesStore.upsertNote(vm.note.value)
                        }
                    }
                } else {
                    Toast.makeText(context, "Name can't be empty", Toast.LENGTH_SHORT).show()
                }
            }
            EditDialog(
                showDialog = showNoteEdit,
                setShowDialog = setNoteEdit,
                text = editTitle,
                title = "Group",
                negativeText = "Delete",
                onNegativeClick = {
                    coroutineScope.launch {
                        val result = withContext(Dispatchers.IO) {
                            notesStore.deleteNote(vm.note.value.id)
                        }
                    }
                }) {
                vm.note.value.title = editTitle.value
                coroutineScope.launch {
                    val result = withContext(Dispatchers.IO) {
                        notesStore.upsertNote(vm.note.value)
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