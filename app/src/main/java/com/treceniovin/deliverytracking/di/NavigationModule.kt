package com.treceniovin.deliverytracking.di

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.treceniovin.deliverytracking.ui.dashboard.DashboardScreen
import com.treceniovin.deliverytracking.ui.dashboard.DashboardViewModel
import com.treceniovin.deliverytracking.ui.details.OrderDetailsScreen
import com.treceniovin.deliverytracking.ui.details.OrderDetailsViewModel
import com.treceniovin.deliverytracking.ui.navigation.Destination
import com.treceniovin.deliverytracking.ui.navigation.LocalNavigator
import com.treceniovin.deliverytracking.ui.navigation.UserRole
import com.treceniovin.deliverytracking.ui.orderplacement.OrderPlacementEvent
import com.treceniovin.deliverytracking.ui.orderplacement.OrderPlacementScreen
import com.treceniovin.deliverytracking.ui.orderplacement.OrderPlacementViewModel
import com.treceniovin.deliverytracking.ui.registration.RegistrationEvent
import com.treceniovin.deliverytracking.ui.registration.RegistrationScreen
import com.treceniovin.deliverytracking.ui.registration.RegistrationViewModel
import com.treceniovin.deliverytracking.ui.roleselection.RoleSelectionScreen
import com.treceniovin.deliverytracking.worker.PollOrdersWorker
import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {
    // Navigation
    navigation<Destination.RoleSelection> {
        val navigator = LocalNavigator.current
        RoleSelectionScreen(
            onRoleSelected = { role ->
                navigator.navigate(Destination.Registration(role))
            }
        )
    }

    navigation<Destination.Registration> { destination ->
        val navigator = LocalNavigator.current
        val viewModel: RegistrationViewModel = koinViewModel { parametersOf(destination.role) }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                if (event is RegistrationEvent.Success) {
                    navigator.navigate(Destination.Dashboard(destination.role))
                }
            }
        }

        RegistrationScreen(
            uiState = uiState,
            role = destination.role,
            onNameChange = viewModel::onNameChange,
            onAddressChange = viewModel::onAddressChange,
            onRegisterClick = viewModel::register,
            onBackClick = { navigator.back() }
        )
    }

    navigation<Destination.Dashboard> { destination ->
        val navigator = LocalNavigator.current
        val context = LocalContext.current
        val viewModel: DashboardViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        if (destination.role == UserRole.DRIVER) {
            DisposableEffect(Unit) {
                PollOrdersWorker.enqueueNext(context)
                onDispose {
                    PollOrdersWorker.cancel(context)
                }
            }
        }

        DashboardScreen(
            uiState = uiState,
            role = destination.role,
            onFilterSelected = viewModel::filterByStatus,
            onOrderClick = { orderId ->
                navigator.navigate(
                    Destination.OrderDetails(
                        orderId,
                        destination.role
                    )
                )
            },
            onAddOrderClick = { navigator.navigate(Destination.OrderPlacement) }
        )
    }

    navigation<Destination.OrderPlacement> {
        val navigator = LocalNavigator.current
        val viewModel: OrderPlacementViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                if (event is OrderPlacementEvent.Success) {
                    navigator.back()
                }
            }
        }

        OrderPlacementScreen(
            uiState = uiState,
            onDescriptionChange = viewModel::onDescriptionChange,
            onSubmitClick = viewModel::submitOrder,
            onBackClick = { navigator.back() }
        )
    }

    navigation<Destination.OrderDetails> { destination ->
        val navigator = LocalNavigator.current
        val viewModel: OrderDetailsViewModel = koinViewModel(
            key = "order-${destination.orderId}"
        ) {
            parametersOf(destination.orderId)
        }

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        OrderDetailsScreen(
            uiState = uiState,
            role = destination.role,
            onStatusUpdate = viewModel::updateStatus,
            onBackClick = { navigator.back() }
        )
    }
}