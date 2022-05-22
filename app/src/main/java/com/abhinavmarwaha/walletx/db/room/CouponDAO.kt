package com.abhinavmarwaha.walletx.db.room

import androidx.room.*
import com.abhinavmarwaha.walletx.db.ID_UNSET
import kotlinx.coroutines.flow.Flow

@Dao
interface CouponDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: Coupon) : Long

    @Query("SELECT * FROM coupons_table")
    fun getCoupons() : Flow<List<Coupon>>

    @Query("SELECT * FROM coupons_table WHERE id IS :id")
    fun loadCoupon(id: Long): Flow<Coupon?>

    @Update
    suspend fun updateCoupon(coupons: Coupon): Int

    @Query(
        """
        DELETE FROM coupons_table WHERE id IN (:ids)
        """
    )
    suspend fun deleteCoupons(ids: List<Long>): Int

    suspend fun upsertCoupon(coupon: Coupon): Long = when (coupon.id > ID_UNSET) {
        true -> {
            upsertCoupon(coupon)
            coupon.id
        }
        false -> {
            insertCoupon(coupon)
        }
    }
}