package com.treceniovin.deliverytracking.domain.usecase

import com.treceniovin.deliverytracking.data.OrderRepository
import com.treceniovin.deliverytracking.data.model.OrderStatus

class UpdateOrderStatusUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(id: String, status: OrderStatus) {
        repository.updateOrderStatus(id, status)
    }
}
