package com.treceniovin.deliverytracking.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.work.WorkManager
import com.treceniovin.deliverytracking.ui.dashboard.DashboardScreen
import com.treceniovin.deliverytracking.ui.dashboard.DashboardViewModel
import com.treceniovin.deliverytracking.ui.details.OrderDetailsScreen
import com.treceniovin.deliverytracking.ui.details.OrderDetailsViewModel
import com.treceniovin.deliverytracking.ui.orderplacement.OrderPlacementEvent
import com.treceniovin.deliverytracking.ui.orderplacement.OrderPlacementScreen
import com.treceniovin.deliverytracking.ui.orderplacement.OrderPlacementViewModel
import com.treceniovin.deliverytracking.ui.registration.RegistrationEvent
import com.treceniovin.deliverytracking.ui.registration.RegistrationScreen
import com.treceniovin.deliverytracking.ui.registration.RegistrationViewModel
import com.treceniovin.deliverytracking.ui.roleselection.RoleSelectionScreen
import com.treceniovin.deliverytracking.worker.PollOrdersWorker
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.navigation3.runtime.NavKey

@Composable
fun NavGraph() {
    val backStack = rememberNavBackStack(Destination.RoleSelection)
    val context = LocalContext.current

    NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
        entryProvider = { key ->
            @Suppress("UNCHECKED_CAST")
            when (key) {
                is Destination.RoleSelection -> {
                    NavEntry(key as NavKey) {
                        RoleSelectionScreen(
                            onRoleSelected = { role ->
                                backStack.add(Destination.Registration(role))
                            }
                        )
                    }
                }
                is Destination.Registration -> {
                    NavEntry(key as NavKey) {
                        val viewModel: RegistrationViewModel = koinViewModel { parametersOf(key.role) }
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        LaunchedEffect(Unit) {
                            viewModel.events.collect { event ->
                                if (event is RegistrationEvent.Success) {
                                    backStack.add(Destination.Dashboard(key.role))
                                }
                            }
                        }

                        RegistrationScreen(
                            uiState = uiState,
                            role = key.role,
                            onNameChange = viewModel::onNameChange,
                            onAddressChange = viewModel::onAddressChange,
                            onRegisterClick = viewModel::register,
                            onBackClick = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                        )
                    }
                }
                is Destination.Dashboard -> {
                    NavEntry(key as NavKey) {
                        val viewModel: DashboardViewModel = koinViewModel()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        if (key.role == UserRole.DRIVER) {
                            DisposableEffect(Unit) {
                                PollOrdersWorker.enqueueNext(context)
                                onDispose {
                                    PollOrdersWorker.cancel(context)
                                }
                            }
                        }

                        DashboardScreen(
                            uiState = uiState,
                            role = key.role,
                            onFilterSelected = viewModel::filterByStatus,
                            onOrderClick = { orderId -> backStack.add(Destination.OrderDetails(orderId, key.role)) },
                            onAddOrderClick = { backStack.add(Destination.OrderPlacement) }
                        )
                    }
                }
                is Destination.OrderPlacement -> {
                    NavEntry(key as NavKey) {
                        val viewModel: OrderPlacementViewModel = koinViewModel()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        LaunchedEffect(Unit) {
                            viewModel.events.collect { event ->
                                if (event is OrderPlacementEvent.Success) {
                                    if (backStack.size > 1) backStack.removeAt(backStack.size - 1)
                                }
                            }
                        }

                        OrderPlacementScreen(
                            uiState = uiState,
                            onDescriptionChange = viewModel::onDescriptionChange,
                            onSubmitClick = viewModel::submitOrder,
                            onBackClick = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                        )
                    }
                }
                is Destination.OrderDetails -> {
                    NavEntry(key as NavKey) {
                        val viewModel: OrderDetailsViewModel = koinViewModel { parametersOf(key.orderId) }
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        OrderDetailsScreen(
                            uiState = uiState,
                            role = key.role,
                            onStatusUpdate = viewModel::updateStatus,
                            onBackClick = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                        )
                    }
                }
                else -> NavEntry(key) {}
            } as NavEntry<NavKey>
        }
    )
}
