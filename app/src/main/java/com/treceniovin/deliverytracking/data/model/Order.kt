package com.treceniovin.deliverytracking.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus {
    PENDING,
    IN_TRANSIT,
    DELIVERED
}

@Serializable
data class Order(
    val id: String,
    val orderId: String,
    val customerId: String,
    val customerName: String,
    val deliveryAddress: String,
    val status: OrderStatus,
    val description: String,
    val updatedAt: Long = System.currentTimeMillis()
)
