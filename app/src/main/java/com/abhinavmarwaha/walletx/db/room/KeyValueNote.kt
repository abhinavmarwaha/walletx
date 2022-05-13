package com.abhinavmarwaha.walletx.db.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abhinavmarwaha.walletx.db.*

const val Note_TABLE_NAME = "notes_table"

@Entity(
    tableName = Note_TABLE_NAME,
)
data class KeyValueNote constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_ID) var id: Long = ID_UNSET,
    @ColumnInfo(name = COL_TITLE) var title: String = "",
    @ColumnInfo(name = COL_KEY_VALUE) var keyValues: List<Pair<String,String>> = listOf()
) {
    constructor() : this(id = ID_UNSET)
}
