package com.treceniovin.deliverytracking.domain.usecase

import com.treceniovin.deliverytracking.data.OrderRepository
import com.treceniovin.deliverytracking.data.model.Order
import kotlinx.coroutines.flow.Flow

class GetOrdersUseCase(private val repository: OrderRepository) {
    operator fun invoke(): Flow<List<Order>> {
        return repository.getAllOrders()
    }
}
