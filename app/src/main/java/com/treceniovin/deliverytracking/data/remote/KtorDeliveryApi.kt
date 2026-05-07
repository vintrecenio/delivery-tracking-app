package com.treceniovin.deliverytracking.data.remote

import com.treceniovin.deliverytracking.data.model.Order
import com.treceniovin.deliverytracking.data.model.OrderStatus
import com.treceniovin.deliverytracking.data.model.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorDeliveryApi(private val client: HttpClient) : DeliveryApi {
    
    private val baseUrl = "https://69fbe78afce564e25916fd6f.mockapi.io/api/v1"

    override suspend fun getOrders(): List<Order> {
        return try {
            val response = client.get("$baseUrl/orders")
            if (response.status.isSuccess()) response.body() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getOrderById(id: String): Order {
        return try {
            val response = client.get("$baseUrl/orders/$id")
            if (response.status.isSuccess()) response.body() else throw Exception("Order not found")
        } catch (e: Exception) {
            throw Exception("Unable to retrieve order details")
        }
    }

    override suspend fun updateOrderStatus(id: String, status: OrderStatus): Order {
        return try {
            val response = client.put("$baseUrl/orders/$id") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("status" to status.name))
            }
            if (response.status.isSuccess()) response.body() else throw Exception("Failed to update status")
        } catch (e: Exception) {
            // Fallback for demo purposes: return a mock updated order if server fails
            throw Exception("Failed to update order status. Please check your connection.")
        }
    }

    override suspend fun createOrder(order: Order): Order {
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

    override suspend fun registerUser(user: User): User {
        return try {
            val response = client.post("$baseUrl/users") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
            if (response.status.isSuccess()) response.body() else user
        } catch (e: Exception) {
            user
        }
    }
}
