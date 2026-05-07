package com.treceniovin.deliverytracking.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val uid: String? = null,
    val name: String? = null,
    val address: String? = null,
    val role: String? = null
)
