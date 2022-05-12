package com.abhinavmarwaha.walletx.db.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abhinavmarwaha.walletx.db.COL_GROUP
import com.abhinavmarwaha.walletx.db.COL_GUID
import com.abhinavmarwaha.walletx.db.ID_UNSET

const val CARDS_GROUP_TABLE_NAME = "cards_group_table"

@Entity(
    tableName = CARDS_GROUP_TABLE_NAME,
)
data class CardGroup constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_GUID) var guid: Long = ID_UNSET,
    @ColumnInfo(name = COL_GROUP) var group: String = "",
) {
    constructor() : this(guid = ID_UNSET)
}
