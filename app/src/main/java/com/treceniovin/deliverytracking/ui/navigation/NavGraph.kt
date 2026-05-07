package com.treceniovin.deliverytracking.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.treceniovin.deliverytracking.domain.usecase.GetRegisteredUserUseCase
import kotlinx.coroutines.flow.first
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun NavGraph() {
    val getUserUseCase: GetRegisteredUserUseCase = koinInject()
    var startDestination by remember { mutableStateOf<Destination?>(null) }

    LaunchedEffect(Unit) {
        val user = getUserUseCase().first()
        startDestination = if (user != null) {
            Destination.Dashboard(user.role)
        } else {
            Destination.RoleSelection
        }
    }

    val currentStartDestination = startDestination
    if (currentStartDestination != null) {
        val backStack = rememberNavBackStack(currentStartDestination)
        val navigator = remember(backStack) { Navigator(backStack) }

        CompositionLocalProvider(LocalNavigator provides navigator) {
            val entryProvider = koinEntryProvider<NavKey>()

            NavDisplay(
                backStack = backStack,
                onBack = { navigator.back() },
                entryProvider = entryProvider
            )
        }
    }
}
