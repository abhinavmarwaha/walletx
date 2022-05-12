package com.abhinavmarwaha.walletx.db.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abhinavmarwaha.walletx.db.ID_UNSET

@Dao
interface CardGroupDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: CardGroup): Long

    @Query("SELECT * FROM cards_group_table")
    fun getGroups() : LiveData<CardGroup>

    @Query("SELECT * FROM cards_group_table")
    suspend fun loadGroups(): List<CardGroup>

    @Query("SELECT * FROM cards_group_table WHERE 'group' IS :group")
    suspend fun loadGroup(group: String): List<CardGroup>

    @Query("SELECT * FROM cards_group_table WHERE 'guid' IS :guid")
    suspend fun loadGroup(guid: Long): CardGroup?

    @Update
    suspend fun updateGroup(group: CardGroup): Int

    @Query(
        """
        DELETE FROM cards_group_table WHERE guid IN (:guids)
        """
    )
    suspend fun deleteGroups(guids: List<Long>): Int

    suspend fun upsertGroup(group: CardGroup): Long = when (group.guid > ID_UNSET) {
        true -> {
            updateGroup(group)
            group.guid
        }
        false -> {
            insertGroup(group)
        }
    }


}