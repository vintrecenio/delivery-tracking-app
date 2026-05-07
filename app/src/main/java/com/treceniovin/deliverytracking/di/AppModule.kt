package com.treceniovin.deliverytracking.di

import com.treceniovin.deliverytracking.data.OrderRepository
import com.treceniovin.deliverytracking.domain.usecase.*
import com.treceniovin.deliverytracking.ui.dashboard.DashboardViewModel
import com.treceniovin.deliverytracking.ui.details.OrderDetailsViewModel
import com.treceniovin.deliverytracking.ui.orderplacement.OrderPlacementViewModel
import com.treceniovin.deliverytracking.ui.registration.RegistrationViewModel
import com.treceniovin.deliverytracking.ui.navigation.UserRole
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext

val appModule = module {
    // Data Layer
    single { OrderRepository(get(), androidContext()) }

    // Domain Layer (Use Cases)
    factory { GetOrdersUseCase(get()) }
    factory { GetOrderDetailsUseCase(get()) }
    factory { CreateOrderUseCase(get()) }
    factory { UpdateOrderStatusUseCase(get()) }
    factory { RefreshOrdersUseCase(get()) }
    factory { RegisterUserUseCase(get()) }
    factory { GetRegisteredUserUseCase(get()) }
    factory { ObserveOrderUseCase(get()) }

    // UI Layer (ViewModels)
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { OrderPlacementViewModel(get(), get()) }
    viewModel { (orderId: String) -> OrderDetailsViewModel(orderId, get(), get(), get()) }
    viewModel { (role: UserRole) -> RegistrationViewModel(role, get()) }
}
