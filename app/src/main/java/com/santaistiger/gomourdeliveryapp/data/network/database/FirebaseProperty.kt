package com.santaistiger.gomourdeliveryapp.data.network.database

import com.santaistiger.gomourdeliveryapp.data.model.Customer
import com.santaistiger.gomourdeliveryapp.data.model.DeliveryMan
import com.santaistiger.gomourdeliveryapp.data.model.Order

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