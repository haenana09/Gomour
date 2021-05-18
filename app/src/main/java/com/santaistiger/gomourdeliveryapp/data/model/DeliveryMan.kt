package com.santaistiger.gomourdeliveryapp.data.model

data class DeliveryMan(
    val email: String? = null,
    val password: String? = null,
    val name: String? = null,
    val phone: String?= null,
    var uid: String? = null,
    val accountInfo: AccountInfo? = null,
    var certified: Boolean = false

)
