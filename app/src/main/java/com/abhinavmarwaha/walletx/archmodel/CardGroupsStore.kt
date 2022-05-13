package com.abhinavmarwaha.walletx.archmodel

import androidx.lifecycle.LiveData
import com.abhinavmarwaha.walletx.db.room.CGRelationDao
import com.abhinavmarwaha.walletx.db.room.CardGroup
import com.abhinavmarwaha.walletx.db.room.CardGroupDAO
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance


class CardGroupsStore(override val di: DI) : DIAware {
    private val cardGroupDAO: CardGroupDAO by instance()
    private val cgRelationDao: CGRelationDao by instance()

    private var nextTagUiId: Long = -1000

    private val tagUiIds = mutableMapOf<String, Long>()

    private fun getTagUiId(tag: String): Long {
        return tagUiIds.getOrPut(tag) {
            --nextTagUiId
        }
    }

    fun getCardGroups() : LiveData<List<CardGroup>> {
        return cardGroupDAO.getGroups()
    }

    suspend fun loadCardGroups() {
        cardGroupDAO.loadGroups()
    }

    suspend fun loadGroup(group: String){
        cardGroupDAO.loadGroup(group)
    }

    suspend fun loadGroup(id: Long){
        cardGroupDAO.loadGroup(id)
    }

    suspend fun deleteCardGroups(ids: List<Long>) {
        cardGroupDAO.deleteGroups(ids)
    }

    suspend fun upsertCard(card: CardGroup) =
        cardGroupDAO.upsertGroup(card)

    suspend fun deleteCardGroup(id: Long) {
        cardGroupDAO.deleteGroups(List(1){id})
    }

    suspend fun getGroupsOfCard(id: Long){
        cgRelationDao.getGroupsofCard(id)
    }


}
