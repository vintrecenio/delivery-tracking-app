package com.treceniovin.deliverytracking.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treceniovin.deliverytracking.data.model.User
import com.treceniovin.deliverytracking.domain.usecase.RegisterUserUseCase
import com.treceniovin.deliverytracking.ui.navigation.UserRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class RegistrationUiState(
    val name: String = "",
    val address: String = "",
    val isRegistering: Boolean = false,
    val error: String? = null
)

sealed interface RegistrationEvent {
    data object Success : RegistrationEvent
}

class RegistrationViewModel(
    private val role: UserRole,
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RegistrationEvent>()
    val events = _events.asSharedFlow()

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onAddressChange(address: String) {
        _uiState.value = _uiState.value.copy(address = address)
    }

    fun register() {
        val currentState = _uiState.value
        if (currentState.name.isBlank() || (role == UserRole.CUSTOMER && currentState.address.isBlank())) {
            _uiState.value = currentState.copy(error = "Please fill in all required fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isRegistering = true, error = null)
            try {
                val user = User(
                    id = UUID.randomUUID().toString(),
                    name = currentState.name,
                    address = currentState.address,
                    role = role
                )
                registerUserUseCase(user)
                _events.emit(RegistrationEvent.Success)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isRegistering = false, error = "Registration failed. Please try again.")
            }
        }
    }
}
