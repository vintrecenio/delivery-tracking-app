package com.treceniovin.deliverytracking.data.remote

import com.treceniovin.deliverytracking.data.remote.dto.OrderDto
import com.treceniovin.deliverytracking.data.remote.dto.UserDto

interface DeliveryApi {
    suspend fun getOrders(): List<OrderDto>
    suspend fun getOrderById(id: String): OrderDto
    suspend fun updateOrderStatus(id: String, status: String): OrderDto
    suspend fun createOrder(order: OrderDto): OrderDto
    suspend fun registerUser(user: UserDto): UserDto
}
