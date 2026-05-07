package com.treceniovin.deliverytracking.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treceniovin.deliverytracking.data.model.Order
import com.treceniovin.deliverytracking.data.model.OrderStatus
import com.treceniovin.deliverytracking.domain.usecase.GetOrdersUseCase
import com.treceniovin.deliverytracking.domain.usecase.RefreshOrdersUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val selectedStatus: OrderStatus? = null,
    val error: String? = null
)

class DashboardViewModel(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val refreshOrdersUseCase: RefreshOrdersUseCase
) : ViewModel() {

    private val _selectedStatus = MutableStateFlow<OrderStatus?>(null)
    
    val uiState: StateFlow<DashboardUiState> = combine(
        getOrdersUseCase(),
        _selectedStatus
    ) { orders, status ->
        val filteredOrders = if (status == null) {
            orders
        } else {
            orders.filter { it.status == status }
        }
        DashboardUiState(orders = filteredOrders, selectedStatus = status)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    init {
        refreshOrders()
    }

    fun refreshOrders() {
        viewModelScope.launch {
            try {
                refreshOrdersUseCase()
            } catch (e: Exception) {
                // Silently fail or log, but don't show raw error to user
            }
        }
    }

    fun filterByStatus(status: OrderStatus?) {
        _selectedStatus.value = status
    }
}
