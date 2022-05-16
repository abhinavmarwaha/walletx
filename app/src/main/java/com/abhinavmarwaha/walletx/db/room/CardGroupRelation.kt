package com.abhinavmarwaha.walletx.db.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import com.abhinavmarwaha.walletx.db.COL_GUID
import com.abhinavmarwaha.walletx.db.COL_ID
import com.abhinavmarwaha.walletx.db.ID_UNSET

const val CARDS_GROUP_RELATION_TABLE_NAME = "cards_group_relation_table"

@Entity(
    tableName = CARDS_GROUP_RELATION_TABLE_NAME,
    foreignKeys = [
        ForeignKey( onDelete = CASCADE, parentColumns = [COL_ID], childColumns = [COL_ID], entity = Card::class,),
        ForeignKey( onDelete = CASCADE, parentColumns = [COL_GUID], childColumns = [COL_GUID], entity = CardGroup::class,),
    ],
    primaryKeys = [COL_GUID, COL_ID],
)
data class CardGroupRelation constructor(
    @ColumnInfo(name = COL_GUID) var guid: Long = ID_UNSET,
    @ColumnInfo(name = COL_ID) var id: Long = ID_UNSET,
)