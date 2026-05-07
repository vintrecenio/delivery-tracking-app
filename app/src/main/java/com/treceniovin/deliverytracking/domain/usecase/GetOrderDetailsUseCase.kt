package com.treceniovin.deliverytracking.domain.usecase

import com.treceniovin.deliverytracking.data.OrderRepository
import com.treceniovin.deliverytracking.data.model.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class GetOrderDetailsUseCase(private val repository: OrderRepository) {
    operator fun invoke(id: String): Flow<Order?> {
        return repository.observeOrderById(id)
            .onStart {
                repository.getOrderById(id)
            }
    }
}
