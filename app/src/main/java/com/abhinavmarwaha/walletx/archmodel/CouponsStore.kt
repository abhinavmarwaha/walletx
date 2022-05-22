package com.abhinavmarwaha.walletx.archmodel

import com.abhinavmarwaha.walletx.db.ID_UNSET
import com.abhinavmarwaha.walletx.db.room.CGRelationDao
import com.abhinavmarwaha.walletx.db.room.Card
import com.abhinavmarwaha.walletx.db.room.Coupon
import com.abhinavmarwaha.walletx.db.room.CouponDAO
import kotlinx.coroutines.flow.Flow
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class CouponsStore(override val di: DI) : DIAware {
    private val couponDAO: CouponDAO by instance()

    fun getCoupons(): Flow<List<Coupon>> = couponDAO.getCoupons()

    fun getCoupon(id: Long): Flow<Coupon?> = couponDAO.loadCoupon(id)

    suspend fun saveCoupon(coupon: Coupon): Long {
        return if (coupon.id > ID_UNSET) {
            couponDAO.updateCoupon(coupon)
            coupon.id
        } else {
            couponDAO.insertCoupon(coupon)
        }
    }

    suspend fun deleteCoupons(ids: List<Long>) {
        couponDAO.deleteCoupons(ids)
    }

    suspend fun upsertCoupon(coupon: Coupon) =
        couponDAO.upsertCoupon(coupon)

    suspend fun deleteCoupon(id: Long) {
        couponDAO.deleteCoupons(List(1){id})
    }
}