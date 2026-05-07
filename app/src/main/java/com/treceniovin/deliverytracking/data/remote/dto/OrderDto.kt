package com.treceniovin.deliverytracking.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: String? = null,
    val orderId: String? = null,
    val customerId: String? = null,
    val customerName: String? = null,
    val deliveryAddress: String? = null,
    val status: String? = null,
    val description: String? = null,
    val updatedAt: Long? = null
)
