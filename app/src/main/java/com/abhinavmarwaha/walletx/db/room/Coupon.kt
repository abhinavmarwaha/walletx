package com.abhinavmarwaha.walletx.db.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abhinavmarwaha.walletx.db.*

const val COUPONS_TABLE_NAME = "coupons_table"

@Entity(
    tableName = COUPONS_TABLE_NAME,
)
data class Coupon constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_ID) var id: Long = ID_UNSET,
    @ColumnInfo(name = COL_TITLE) var title: String = "",
    @ColumnInfo(name = COL_IMAGE) var image: String = "",
    @ColumnInfo(name = COL_COUPON) var coupon: String = "",
    @ColumnInfo(name = COL_DESC) var desc: String = ""

) {
    constructor() : this(id = ID_UNSET)
}

