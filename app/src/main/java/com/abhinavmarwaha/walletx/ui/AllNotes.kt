package com.abhinavmarwaha.walletx.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhinavmarwaha.walletx.archmodel.NotesStore
import com.abhinavmarwaha.walletx.db.room.KeyValueNote
import com.abhinavmarwaha.walletx.db.room.NotesDao
import com.abhinavmarwaha.walletx.ui.compose.EditDialog
import com.abhinavmarwaha.walletx.ui.compose.NoteView
import com.abhinavmarwaha.walletx.ui.widgets.LongButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.DI
import org.kodein.di.android.closestDI
import org.kodein.di.instance

@Composable
fun AllNotes() {
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    val di: DI by closestDI(LocalContext.current)
    val notesStore: NotesStore by di.instance()
    val notesDao: NotesDao by di.instance()
    val vm = AllNotesViewModel(notesStore)

    val addNoteTitle = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(Modifier.fillMaxHeight().padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        item{LongButton(function = { setShowDialog(true) }, text = "Add", Modifier.padding(30.dp))}
        items(items = vm.notes) { item ->
            NoteView(item.id)
        }
        item {
            EditDialog(
                showDialog,
                setShowDialog,
                addNoteTitle,
                "Note Title",
                negativeText = "Dismiss",
                onNegativeClick = {}
            ) {
                if (addNoteTitle.value.isNotEmpty()) {
                    coroutineScope.launch {
                        val note = KeyValueNote()
                        note.title = addNoteTitle.value
                        val result = withContext(Dispatchers.IO) {
                            notesDao.insertNote(note)
                        }
                    }
                }
            }
        }
    }

}

class AllNotesViewModel(private val notesStore: NotesStore) : ViewModel() {
    val notes = mutableStateListOf<KeyValueNote>()

    init {
        viewModelScope.launch {
            notes.addAll(notesStore.getNotes())
        }
    }
}