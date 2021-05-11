package com.santaistiger.gomourdeliveryapp.data.model


/**
 * @param deliveryTime
 * 배달 완료 전 - 예상 도착 시간 / 배달 완료 후 - 도착시간
 */

data class Order(
    val orderId: String = "",
    val customerUid: String = "",
    val deliveryMan: String = "",
    var stores: List<Store> = ArrayList(),
    val deliveryCharge: Int = 0,
    val destination: Place = Place(),
    val message: String = "",
    val orderDate: Long = 0L,
    var deliveryTime: Long = 0L
)
