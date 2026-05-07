package com.treceniovin.deliverytracking.data.model

import com.treceniovin.deliverytracking.ui.navigation.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val address: String,
    val role: UserRole
)
