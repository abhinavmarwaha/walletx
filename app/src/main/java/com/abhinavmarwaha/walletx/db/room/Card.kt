package com.abhinavmarwaha.walletx.db.room

import androidx.room.*
import com.abhinavmarwaha.walletx.db.COL_ID
import com.abhinavmarwaha.walletx.db.COL_IMAGE
import com.abhinavmarwaha.walletx.db.COL_TITLE
import com.abhinavmarwaha.walletx.db.ID_UNSET

const val CARDS_TABLE_NAME = "cards_table"

@Entity(
    tableName = CARDS_TABLE_NAME,
    )
data class Card constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_ID) var id: Long = ID_UNSET,
    @ColumnInfo(name = COL_TITLE) var title: String = "",
    @ColumnInfo(name = COL_IMAGE) var image: String = ""
) {
    constructor() : this(id = ID_UNSET)
}

