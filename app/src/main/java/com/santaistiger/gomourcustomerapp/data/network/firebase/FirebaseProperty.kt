package com.santaistiger.gomourcustomerapp.data.network.firebase

import com.santaistiger.gomourcustomerapp.data.model.Customer
import com.santaistiger.gomourcustomerapp.data.model.DeliveryMan
import com.santaistiger.gomourcustomerapp.data.model.Order

data class OrderResponse(
    var order: Order? = null,
    var exception: Exception? = null
)

data class CustomerResponse(
    var customer: Customer? = null,
    var exception: Exception? = null
)

data class DeliveryManResponse(
    var deliveryMan: DeliveryMan? = null,
    var exception: Exception? = null
)