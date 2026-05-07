package com.treceniovin.deliverytracking

import com.treceniovin.deliverytracking.data.model.Order
import com.treceniovin.deliverytracking.data.model.OrderStatus
import com.treceniovin.deliverytracking.data.model.User
import com.treceniovin.deliverytracking.data.remote.DeliveryApi
import com.treceniovin.deliverytracking.ui.navigation.UserRole
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DeliveryApiTest {

    private lateinit var deliveryApi: DeliveryApi

    @Before
    fun setup() {
        deliveryApi = mockk()
    }

    @Test
    fun `getOrders should return list of orders`() = runTest {
        // Given
        val orders = listOf(
            Order("1", orderId = "11111", customerId = "1234", "Customer 1", "123 Main St", OrderStatus.PENDING, "Fragile item"),
            Order("2", orderId = "22222", customerId = "2345", "Customer 2", "456 Oak Ave", OrderStatus.IN_TRANSIT, ""),
            Order("3", orderId = "33333", customerId = "3456", "Customer 3", "789 Pine Rd", OrderStatus.DELIVERED, "Leave at door")
        )

        coEvery { deliveryApi.getOrders() } returns orders

        // When
        val result = deliveryApi.getOrders()

        // Then
        assertEquals(3, result.size)
        assertEquals(OrderStatus.PENDING, result[0].status)

        coVerify(exactly = 1) {
            deliveryApi.getOrders()
        }
    }

    @Test
    fun `getOrderById should return correct order`() = runTest {
        // Given
        val order = Order("123", orderId = "11111", customerId = "1234", "Customer 1", "123 Main St", OrderStatus.PENDING, "Fragile item")

        coEvery { deliveryApi.getOrderById("123") } returns order

        // When
        val result = deliveryApi.getOrderById("123")

        // Then
        assertEquals("123", result.id)

        coVerify {
            deliveryApi.getOrderById("123")
        }
    }

    @Test
    fun `updateOrderStatus should update status`() = runTest {
        // Given
        val updatedOrder = Order("123", orderId = "11111", customerId = "1234", "Customer 1", "123 Main St", OrderStatus.DELIVERED, "Fragile item")

        coEvery {
            deliveryApi.updateOrderStatus(
                "123",
                OrderStatus.DELIVERED
            )
        } returns updatedOrder

        // When
        val result = deliveryApi.updateOrderStatus(
            "123",
            OrderStatus.DELIVERED
        )

        // Then
        assertEquals(OrderStatus.DELIVERED, result.status)

        coVerify {
            deliveryApi.updateOrderStatus(
                "123",
                OrderStatus.DELIVERED
            )
        }
    }

    @Test
    fun `createOrder should return created order`() = runTest {
        // Given
        val order = Order("123", orderId = "11111", customerId = "1234", "Customer 1", "123 Main St", OrderStatus.PENDING, "Fragile item")

        coEvery { deliveryApi.createOrder(order) } returns order

        // When
        val result = deliveryApi.createOrder(order)

        // Then
        assertEquals("123", result.id)

        coVerify {
            deliveryApi.createOrder(order)
        }
    }

    @Test
    fun `registerUser should return registered user`() = runTest {
        // Given
        val user = User("123", "Marvin", "Alaminos, PH", UserRole.CUSTOMER)

        coEvery { deliveryApi.registerUser(user) } returns user

        // When
        val result = deliveryApi.registerUser(user)

        // Then
        assertEquals("Marvin", result.name)

        coVerify {
            deliveryApi.registerUser(user)
        }
    }
}