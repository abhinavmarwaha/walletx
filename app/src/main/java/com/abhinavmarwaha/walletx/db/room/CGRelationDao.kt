package com.abhinavmarwaha.walletx.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CGRelationDao {

    @Query("SELECT 'id' FROM cards_group_relation_table WHERE 'id' IS :cardId")
    suspend fun getGroupIdsOfCard(cardId: Long): List<Int>

    @Query("SELECT 'guid' FROM cards_group_relation_table WHERE 'guid' IS :guid")
    suspend fun getCardIdsOfGroup(guid: Long): List<Int>

    @Query("SELECT 'id' FROM cards_group_relation_table WHERE 'id' IS :cardId")
    fun loadGroupIdsOfCard(cardId: Long): Flow<List<Int>>

    @Query("SELECT 'guid' FROM cards_group_relation_table WHERE 'guid' IS :guid")
    fun loadCardIdsOfGroup(guid: Long): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelations(cgRelation: List<CardGroupRelation>) : List<Long>

    @Query(
        """
        SELECT *
        FROM cards_table 
        INNER JOIN cards_group_table 
            ON cards_group_table.guid = cards_group_relation_table.guid
        INNER JOIN cards_group_relation_table 
            ON cards_group_relation_table.id = cards_table.id
        WHERE cards_group_table.guid = :guid
        """
    )
    suspend fun getCardsOfGroup(guid: Long): List<Card>

    @Query(
        """
        SELECT *
        FROM cards_table 
        INNER JOIN cards_group_table 
            ON cards_group_table.guid = cards_group_relation_table.guid
        INNER JOIN cards_group_relation_table 
            ON cards_group_relation_table.id = cards_table.id
        WHERE cards_group_table.guid = :guid
        """
    )
    fun loadCardsOfGroup(guid: Long): Flow<List<Card>>

    @Query(
        """
        SELECT *
        FROM cards_group_table 
        INNER JOIN cards_group_relation_table 
            ON cards_group_relation_table.guid = cards_group_table.guid
        INNER JOIN cards_table 
            ON cards_group_relation_table.id = cards_table.id
        WHERE cards_group_table.guid = :guid
        """
    )
    suspend fun getGroupsofCard(guid: Long): List<CardGroup>

    @Query(
        """
        SELECT *
        FROM cards_group_table 
        INNER JOIN cards_group_relation_table 
            ON cards_group_relation_table.guid = cards_group_table.guid
        INNER JOIN cards_table 
            ON cards_group_relation_table.id = cards_table.id
        WHERE cards_group_table.guid = :guid
        """
    )
    fun loadGroupsofCard(guid: Long): Flow<List<CardGroup>>


}