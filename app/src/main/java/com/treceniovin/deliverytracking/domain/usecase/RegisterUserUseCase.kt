package com.treceniovin.deliverytracking.domain.usecase

import com.treceniovin.deliverytracking.data.OrderRepository
import com.treceniovin.deliverytracking.data.model.User

class RegisterUserUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(user: User) {
        repository.registerUser(user)
    }
}
