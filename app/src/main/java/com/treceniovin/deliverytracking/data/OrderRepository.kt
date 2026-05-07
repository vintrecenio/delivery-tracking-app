package com.treceniovin.deliverytracking.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.treceniovin.deliverytracking.data.model.Order
import com.treceniovin.deliverytracking.data.model.OrderStatus
import com.treceniovin.deliverytracking.data.model.User
import com.treceniovin.deliverytracking.data.remote.DeliveryApi
import com.treceniovin.deliverytracking.ui.navigation.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class OrderRepository(
    private val api: DeliveryApi,
    private val context: Context
) {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    
    private val USER_KEY = stringPreferencesKey("user_profile")

    fun getAllOrders(): Flow<List<Order>> {
        return _orders.asStateFlow()
    }

    fun observeOrderById(id: String): Flow<Order?> {
        return _orders.map { orders -> orders.find { it.id == id } }
    }

    suspend fun refreshOrders() {
        val remoteOrders = api.getOrders()
        _orders.value = remoteOrders
    }

    suspend fun getOrderById(id: String): Order? {
        val remoteOrder = api.getOrderById(id)
        // Update in-memory cache to keep observers in sync
        val currentList = _orders.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = remoteOrder
        } else {
            currentList.add(remoteOrder)
        }
        _orders.value = currentList
        return remoteOrder
    }

    suspend fun updateOrderStatus(id: String, status: OrderStatus) {
        val updatedOrder = api.updateOrderStatus(id, status)
        // Update in-memory cache
        val currentList = _orders.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = updatedOrder
            _orders.value = currentList
        }
    }

    suspend fun createOrder(order: Order) {
        val createdOrder = api.createOrder(order)
        // Update in-memory cache
        _orders.value += createdOrder
    }

    suspend fun registerUser(user: User) {
        val remoteUser = api.registerUser(user)
        context.dataStore.edit { prefs ->
            prefs[USER_KEY] = Json.encodeToString(User.serializer(), remoteUser)
        }
    }

    fun getRegisteredUser(): Flow<User?> {
        return context.dataStore.data.map { prefs ->
            prefs[USER_KEY]?.let { Json.decodeFromString(User.serializer(), it) }
        }
    }
}
