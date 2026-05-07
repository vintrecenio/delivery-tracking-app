package com.treceniovin.deliverytracking.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treceniovin.deliverytracking.data.model.Order
import com.treceniovin.deliverytracking.data.model.OrderStatus
import com.treceniovin.deliverytracking.domain.usecase.GetOrdersUseCase
import com.treceniovin.deliverytracking.domain.usecase.GetRegisteredUserUseCase
import com.treceniovin.deliverytracking.domain.usecase.RefreshOrdersUseCase
import com.treceniovin.deliverytracking.ui.navigation.UserRole
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectedStatus: OrderStatus? = null,
    val error: String? = null
)

class DashboardViewModel(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val refreshOrdersUseCase: RefreshOrdersUseCase,
    private val getRegisteredUserUseCase: GetRegisteredUserUseCase
) : ViewModel() {

    private val _selectedStatus = MutableStateFlow<OrderStatus?>(null)
    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> = combine(
        getOrdersUseCase(),
        _selectedStatus,
        getRegisteredUserUseCase(),
        _isRefreshing
    ) { orders, status, user, isRefreshing ->
        val filteredByRole = if (user?.role == UserRole.CUSTOMER) {
            orders.filter { it.customerId == user.uid }
        } else {
            orders
        }

        val filteredByStatus = if (status == null) {
            filteredByRole
        } else {
            filteredByRole.filter { it.status == status }
        }
        DashboardUiState(
            orders = filteredByStatus,
            selectedStatus = status,
            isRefreshing = isRefreshing,
            isLoading = orders.isEmpty() && isRefreshing
        )
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
            _isRefreshing.value = true
            try {
                refreshOrdersUseCase()
            } catch (e: Exception) {
                // Silently fail or log, but don't show raw error to user
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun filterByStatus(status: OrderStatus?) {
        _selectedStatus.value = status
    }
}
