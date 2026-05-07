package com.treceniovin.deliverytracking.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treceniovin.deliverytracking.data.model.Order
import com.treceniovin.deliverytracking.data.model.OrderStatus
import com.treceniovin.deliverytracking.domain.usecase.GetOrderDetailsUseCase
import com.treceniovin.deliverytracking.domain.usecase.ObserveOrderUseCase
import com.treceniovin.deliverytracking.domain.usecase.UpdateOrderStatusUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class OrderDetailsUiState(
    val order: Order? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUpdating: Boolean = false
)

class OrderDetailsViewModel(
    private val orderId: String,
    private val getOrderDetailsUseCase: GetOrderDetailsUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val observeOrderUseCase: ObserveOrderUseCase
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    private val _isUpdating = MutableStateFlow(false)

    val uiState: StateFlow<OrderDetailsUiState> = combine(
        observeOrderUseCase(orderId),
        _error,
        _isUpdating
    ) { order, error, isUpdating ->
        OrderDetailsUiState(
            order = order,
            isLoading = order == null && error == null,
            error = error,
            isUpdating = isUpdating
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OrderDetailsUiState(isLoading = true)
    )

    init {
        fetchOrder()
    }

    private fun fetchOrder() {
        viewModelScope.launch {
            try {
                // Ensure we have the initial data from the server
                getOrderDetailsUseCase(orderId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load order"
            }
        }
    }

    fun updateStatus(newStatus: OrderStatus) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                updateOrderStatusUseCase(orderId, newStatus)
            } catch (e: Exception) {
                _error.value = "Unable to update status. Please try again later."
            } finally {
                _isUpdating.value = false
            }
        }
    }
}
