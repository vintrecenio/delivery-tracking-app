package com.treceniovin.deliverytracking.data.remote

import com.treceniovin.deliverytracking.data.model.Order
import com.treceniovin.deliverytracking.data.model.OrderStatus
import com.treceniovin.deliverytracking.data.model.User

interface DeliveryApi {
    suspend fun getOrders(): List<Order>
    suspend fun getOrderById(id: String): Order
    suspend fun updateOrderStatus(id: String, status: OrderStatus): Order
    suspend fun createOrder(order: Order): Order
    suspend fun registerUser(user: User): User
}
