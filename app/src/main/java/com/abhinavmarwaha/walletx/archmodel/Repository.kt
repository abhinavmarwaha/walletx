package com.abhinavmarwaha.walletx.archmodel

import com.abhinavmarwaha.walletx.db.room.Card
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class Repository(override val di: DI) : DIAware {

    private val cardsStore: CardsStore by instance()
    private val cardGroupsStore: CardGroupsStore by instance()
    private val couponsStore: CouponsStore by instance()

    suspend fun upsertCard(card: Card) =
        cardsStore.upsertCard(card)

}