package com.abhinavmarwaha.walletx.archmodel

import com.abhinavmarwaha.walletx.db.ID_UNSET
import com.abhinavmarwaha.walletx.db.room.KeyValueNote
import com.abhinavmarwaha.walletx.db.room.NotesDao
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance


class NotesStore(override val di: DI) : DIAware {
    private val notesDao: NotesDao by instance()

    suspend fun getNote(id: Long): KeyValueNote? = notesDao.loadNote(id)
    suspend fun getNotes(): List<KeyValueNote> = notesDao.loadNotes()

    suspend fun saveNote(note: KeyValueNote): Long {
        return if (note.id > ID_UNSET) {
            notesDao.updateNote(note)
            note.id
        } else {
            notesDao.insertNote(note)
        }
    }

    suspend fun deleteNotes(ids: List<Long>) {
        notesDao.deleteNotes(ids)
    }

    suspend fun upsertNote(note: KeyValueNote) =
        notesDao.upsertNote(note)

    suspend fun deleteNote(id: Long) {
        notesDao.deleteNotes(List(1) { id })
    }
}
