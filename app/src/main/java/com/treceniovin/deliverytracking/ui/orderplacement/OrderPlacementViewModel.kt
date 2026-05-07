package com.treceniovin.deliverytracking.ui.orderplacement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treceniovin.deliverytracking.data.model.Order
import com.treceniovin.deliverytracking.data.model.OrderStatus
import com.treceniovin.deliverytracking.domain.usecase.CreateOrderUseCase
import com.treceniovin.deliverytracking.domain.usecase.GetRegisteredUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

data class OrderPlacementUiState(
    val description: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null
)

sealed interface OrderPlacementEvent {
    data object Success : OrderPlacementEvent
}

class OrderPlacementViewModel(
    private val createOrderUseCase: CreateOrderUseCase,
    private val getRegisteredUserUseCase: GetRegisteredUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderPlacementUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<OrderPlacementEvent>()
    val events = _events.asSharedFlow()

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun submitOrder() {
        val currentState = _uiState.value
        
        viewModelScope.launch {
            _uiState.value = currentState.copy(isSubmitting = true, error = null)
            try {
                val user = getRegisteredUserUseCase().first() ?: throw Exception("User not registered")
                
                val newOrder = Order(
                    id = "",
                    orderId = UUID.randomUUID().toString(),
                    customerId = user.uid,
                    customerName = user.name,
                    deliveryAddress = user.address,
                    status = OrderStatus.PENDING,
                    description = currentState.description
                )
                createOrderUseCase(newOrder)
                _events.emit(OrderPlacementEvent.Success)
                _uiState.value = currentState.copy(isSubmitting = false, error = null)
            } catch (e: Exception) {
                val userFriendlyMessage = when {
                    e.message?.contains("500") == true -> "Server is currently busy. Your order was saved locally."
                    e.message?.contains("NoTransformationFound") == true -> "Network error. Please try again."
                    else -> "Failed to submit order. Please check your connection."
                }
                _uiState.value = _uiState.value.copy(isSubmitting = false, error = userFriendlyMessage)
            }
        }
    }
}
