package com.abhinavmarwaha.walletx.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhinavmarwaha.walletx.archmodel.NotesStore
import com.abhinavmarwaha.walletx.db.room.KeyValueNote
import com.abhinavmarwaha.walletx.db.room.NotesDao
import com.abhinavmarwaha.walletx.ui.compose.EditDialog
import com.abhinavmarwaha.walletx.ui.compose.NoteView
import com.abhinavmarwaha.walletx.ui.widgets.LongButton
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.android.closestDI
import org.kodein.di.instance

@Composable
fun AllNotes(){
    val (showDialog, setShowDialog) =  remember { mutableStateOf(false) }

    val di: DI by closestDI(LocalContext.current)
    val notesStore: NotesStore by di.instance()
    val notesDao: NotesDao by di.instance()
    val vm = AllNotesViewModel(notesStore)

    val addNoteTitle = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column() {
        LazyColumn(){
            items(items = vm.notes) {
                    item ->
                Column() {
                    LongButton(function = { /*TODO*/ }, text = "Add")
                    Text(item.title)
                    NoteView(item.id)
                }

            }
        }
        EditDialog(
            showDialog,
            setShowDialog,
            addNoteTitle,
            "Note Title"
        ) {
            if(addNoteTitle.value.isNotEmpty()){
                coroutineScope.launch {
                    val note = KeyValueNote()
                    note.title = addNoteTitle.value
                    notesDao.insertNote(note)
                }
            }
        }
    }


}

class AllNotesViewModel(private val notesStore: NotesStore): ViewModel(){
    val notes = mutableStateListOf<KeyValueNote>()

    init {
        viewModelScope.launch {
            notes.addAll(notesStore.getNotes())
        }
    }
}