package com.treceniovin.deliverytracking.ui.navigation

import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey

@Serializable
enum class UserRole {
    CUSTOMER, DRIVER
}

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object RoleSelection : Destination

    @Serializable
    data class Registration(val role: UserRole) : Destination

    @Serializable
    data class Dashboard(val role: UserRole) : Destination

    @Serializable
    data object OrderPlacement : Destination

    @Serializable
    data class OrderDetails(val orderId: String, val role: UserRole) : Destination
}
