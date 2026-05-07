package com.treceniovin.deliverytracking.domain.usecase

import com.treceniovin.deliverytracking.data.OrderRepository

class RefreshOrdersUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke() {
        repository.refreshOrders()
    }
}
