package com.treceniovin.deliverytracking.data.remote

import com.treceniovin.deliverytracking.data.remote.dto.OrderDto
import com.treceniovin.deliverytracking.data.remote.dto.UserDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorDeliveryApi(private val client: HttpClient) : DeliveryApi {
    
    private val baseUrl = "https://69fbe78afce564e25916fd6f.mockapi.io/api/v1"

    override suspend fun getOrders(): List<OrderDto> {
        return try {
            val response = client.get("$baseUrl/orders")
            if (response.status.isSuccess()) response.body() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getOrderById(id: String): OrderDto {
        val response = client.get("$baseUrl/orders/$id")
        if (response.status.isSuccess()) return response.body() else throw Exception("Order not found")
    }

    override suspend fun updateOrderStatus(id: String, status: String): OrderDto {
        val response = client.put("$baseUrl/orders/$id") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("status" to status))
        }
        if (response.status.isSuccess()) return response.body() else throw Exception("Failed to update status")
    }

    override suspend fun createOrder(order: OrderDto): OrderDto {
        val response = client.post("$baseUrl/orders") {
            contentType(ContentType.Application.Json)
            setBody(order)
        }
        return if (response.status.isSuccess()) {
            response.body()
        } else {
            throw Exception("Failed to create order on server (Status: ${response.status})")
        }
    }

    override suspend fun registerUser(user: UserDto): UserDto {
        val response = client.post("$baseUrl/users") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        return if (response.status.isSuccess()) response.body() else user
    }
}
