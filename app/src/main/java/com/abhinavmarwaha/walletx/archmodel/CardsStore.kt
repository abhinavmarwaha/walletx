package com.abhinavmarwaha.walletx.archmodel

import androidx.lifecycle.LiveData
import com.abhinavmarwaha.walletx.db.room.Card
import com.abhinavmarwaha.walletx.db.room.CardDAO
import com.abhinavmarwaha.walletx.db.ID_UNSET
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class CardsStore(override val di: DI) : DIAware {
    private val cardDAO: CardDAO by instance()

    private var nextTagUiId: Long = -1000

    private val tagUiIds = mutableMapOf<String, Long>()

    private fun getTagUiId(tag: String): Long {
        return tagUiIds.getOrPut(tag) {
            --nextTagUiId
        }
    }

    fun getCards(group: String): LiveData<List<Card>> = cardDAO.getCards(group)

    suspend fun getCard(id: Long): Card? = cardDAO.loadCard(id)

    suspend fun saveCard(card: Card): Long {
        return if (card.id > ID_UNSET) {
            cardDAO.updateCard(card)
            card.id
        } else {
            cardDAO.insertCard(card)
        }
    }

    suspend fun deleteFeeds(ids: List<Long>) {
        cardDAO.deleteCards(ids)
    }

    suspend fun upsertCard(card: Card) =
        cardDAO.upsertCard(card)

    suspend fun deleteCard(id: Long) {
        cardDAO.deleteCards(List(1){id})
    }


}
