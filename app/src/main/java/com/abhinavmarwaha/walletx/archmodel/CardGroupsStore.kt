package com.abhinavmarwaha.walletx.archmodel

import com.abhinavmarwaha.walletx.db.room.CardGroup
import com.abhinavmarwaha.walletx.db.room.CardGroupDAO
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance


class CardGroupsStore(override val di: DI) : DIAware {
    private val cardGroupDAO: CardGroupDAO by instance()

    private var nextTagUiId: Long = -1000

    private val tagUiIds = mutableMapOf<String, Long>()

    private fun getTagUiId(tag: String): Long {
        return tagUiIds.getOrPut(tag) {
            --nextTagUiId
        }
    }

    suspend fun deleteCardGroups(ids: List<Long>) {
        cardGroupDAO.deleteGroups(ids)
    }

    suspend fun upsertCard(card: CardGroup) =
        cardGroupDAO.upsertGroup(card)

    suspend fun deleteCardGroup(id: Long) {
        cardGroupDAO.deleteGroups(List(1){id})
    }


}
