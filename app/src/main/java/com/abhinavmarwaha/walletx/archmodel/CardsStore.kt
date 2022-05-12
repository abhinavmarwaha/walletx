package com.abhinavmarwaha.walletx.archmodel

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

    suspend fun getFeed(feedId: Long): Feed? = feedDao.loadFeed(feedId)

    suspend fun getFeed(url: URL): Feed? = feedDao.loadFeedWithUrl(url)

    suspend fun saveFeed(feed: Feed): Long {
        return if (feed.id > ID_UNSET) {
            feedDao.updateFeed(feed)
            feed.id
        } else {
            feedDao.insertFeed(feed)
        }
    }

    suspend fun deleteFeeds(feedIds: List<Long>) {
        feedDao.deleteFeeds(feedIds)
    }

    val allTags: Flow<List<String>> = feedDao.loadAllTags()

    @OptIn(ExperimentalCoroutinesApi::class)
    val drawerItemsWithUnreadCounts: Flow<List<DrawerItemWithUnreadCount>> =
        feedDao.loadFlowOfFeedsWithUnreadCounts()
            .mapLatest { feeds ->
                mapFeedsToSortedDrawerItems(feeds)
            }

    private fun mapFeedsToSortedDrawerItems(
        feeds: List<FeedUnreadCount>,
    ): List<DrawerItemWithUnreadCount> {
        var topTag = DrawerTop(unreadCount = 0, syncingChildren = 0, totalChildren = 0)
        val tags: MutableMap<String, DrawerTag> = mutableMapOf()
        val data: MutableList<DrawerItemWithUnreadCount> = mutableListOf()

        for (feedDbo in feeds) {
            val feed = DrawerFeed(
                unreadCount = feedDbo.unreadCount,
                tag = feedDbo.tag,
                id = feedDbo.id,
                displayTitle = feedDbo.displayTitle,
                currentlySyncing = feedDbo.currentlySyncing,
            )

            data.add(feed)
            topTag = topTag.copy(
                unreadCount = topTag.unreadCount + feed.unreadCount,
                totalChildren = topTag.totalChildren + 1,
                syncingChildren = if (feedDbo.currentlySyncing) {
                    topTag.syncingChildren + 1
                } else {
                    topTag.syncingChildren
                }
            )

            if (feed.tag.isNotEmpty()) {
                val tag = tags[feed.tag] ?: DrawerTag(
                    tag = feed.tag,
                    unreadCount = 0,
                    uiId = getTagUiId(feed.tag),
                    syncingChildren = 0,
                    totalChildren = 0,
                )
                tags[feed.tag] = tag.copy(
                    unreadCount = tag.unreadCount + feed.unreadCount,
                    totalChildren = tag.totalChildren + 1,
                    syncingChildren = if (feedDbo.currentlySyncing) {
                        tag.syncingChildren + 1
                    } else {
                        tag.syncingChildren
                    },
                )
            }
        }

        data.add(topTag)
        data.addAll(tags.values)

        return data.sorted()
    }

    suspend fun upsertCard(card: Card) =
        cardDAO.upsertCard(card)

    suspend fun deleteCard(id: Long) {
        cardDAO.deleteCards(List(1){id})
    }


}
