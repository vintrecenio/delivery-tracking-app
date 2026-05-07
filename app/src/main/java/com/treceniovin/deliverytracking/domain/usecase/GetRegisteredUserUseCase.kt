package com.treceniovin.deliverytracking.domain.usecase

import com.treceniovin.deliverytracking.data.OrderRepository
import com.treceniovin.deliverytracking.data.model.User
import kotlinx.coroutines.flow.Flow

class GetRegisteredUserUseCase(private val repository: OrderRepository) {
    operator fun invoke(): Flow<User?> {
        return repository.getRegisteredUser()
    }
}
