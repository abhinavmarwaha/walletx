package com.abhinavmarwaha.walletx.archmodel

import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance


class Repository(override val di: DI) : DIAware {

    suspend fun upsertFeed(feedSql: Feed) =
        feedStore.upsertFeed(feedSql)

    suspend fun insertFeed(feedSql: Feed) =
        feedDAO.insertFeed(feedSql)

    suspend fun upsertFeedItems(
        itemsWithText: List<Pair<FeedItem, String>>,
        block: suspend (FeedItem, String) -> Unit
    ) {
        feedItemStore.upsertFeedItems(itemsWithText, block)
    }

    suspend fun loadFeedItem(guid: String, feedId: Long): FeedItem? =
        feedItemStore.loadFeedItem(guid = guid, feedId = feedId)

    suspend fun getItemsToBeCleanedFromFeed(feedId: Long, keepCount: Int) =
        feedItemStore.getItemsToBeCleanedFromFeed(feedId = feedId, keepCount = keepCount)


    suspend fun deleteFeedItems(ids: List<Long>) {
        feedItemStore.deleteFeedItems(ids)
    }
}