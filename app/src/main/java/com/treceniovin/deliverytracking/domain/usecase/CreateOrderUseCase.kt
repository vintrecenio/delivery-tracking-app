package com.treceniovin.deliverytracking.domain.usecase

import com.treceniovin.deliverytracking.data.OrderRepository
import com.treceniovin.deliverytracking.data.model.Order

class CreateOrderUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(order: Order) {
        repository.createOrder(order)
    }
}
