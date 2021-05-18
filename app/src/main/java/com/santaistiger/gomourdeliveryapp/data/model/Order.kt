package com.santaistiger.gomourdeliveryapp.data.model

import android.os.Parcelable
import androidx.databinding.BaseObservable
import kotlinx.android.parcel.Parcelize


/**
 * @param deliveryTime
 * 배달 완료 전 - 예상 도착 시간 / 배달 완료 후 - 도착시간
 */
data class Order(
        val orderId: String? = null,
        val customerUid: String? = null,
        val deliveryManUid: String? = null,
        var stores: ArrayList<Store>? = null,
        val deliveryCharge: Int? = null,
        val destination: Place? = null,
        val message: String? = null,
        val orderDate: Long? = null,
        var deliveryTime: Long? = null,
        var status: Status = Status.PREPARING
)

