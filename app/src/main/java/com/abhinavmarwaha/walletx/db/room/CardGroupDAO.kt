package com.abhinavmarwaha.walletx.db.room

import androidx.room.*
import com.abhinavmarwaha.walletx.db.ID_UNSET
import kotlinx.coroutines.flow.Flow

@Dao
interface CardGroupDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroup(group: CardGroup): Long

    @Query("SELECT * FROM cards_group_table")
    fun getGroups() : Flow<List<CardGroup>>

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