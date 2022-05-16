package com.abhinavmarwaha.walletx.db.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abhinavmarwaha.walletx.db.ID_UNSET

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: KeyValueNote) : Long

    @Query("SELECT * FROM notes_table")
    fun getNotes() : LiveData<KeyValueNote>

    @Query("SELECT * FROM notes_table")
    suspend fun loadNotes(): List<KeyValueNote>

    @Query("SELECT * FROM notes_table WHERE id IS :id")
    suspend fun loadNote(id: Long): KeyValueNote?

    @Update
    suspend fun updateNote(note: KeyValueNote): Int

    @Query(
        """
        DELETE FROM notes_table WHERE id IN (:ids)
        """
    )
    suspend fun deleteNotes(ids: List<Long>): Int

    suspend fun upsertNote(note: KeyValueNote): Long = when (note.id > ID_UNSET) {
        true -> {
            updateNote(note)
            note.id
        }
        false -> {
            insertNote(note)
        }
    }
}