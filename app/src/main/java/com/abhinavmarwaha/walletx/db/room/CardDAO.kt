package com.abhinavmarwaha.walletx.db.room

import androidx.room.*
import com.abhinavmarwaha.walletx.db.ID_UNSET
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card) : Long

    @Query("SELECT * FROM cards_table")
    fun getCards() : Flow<List<Card>>

    @Query("SELECT * FROM cards_table WHERE id IS :id")
    fun loadCard(id: Long): Flow<Card?>

    @Update
    suspend fun updateCard(card: Card): Int

    @Query(
        """
        DELETE FROM cards_table WHERE id IN (:ids)
        """
    )
    suspend fun deleteCards(ids: List<Long>): Int

    suspend fun upsertCard(card: Card): Long = when (card.id > ID_UNSET) {
        true -> {
            updateCard(card)
            card.id
        }
        false -> {
            insertCard(card)
        }
    }
}