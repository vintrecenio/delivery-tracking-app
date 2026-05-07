package com.treceniovin.deliverytracking.data.mapper

import com.treceniovin.deliverytracking.data.model.User
import com.treceniovin.deliverytracking.data.remote.dto.UserDto
import com.treceniovin.deliverytracking.ui.navigation.UserRole

fun UserDto.toDomain(): User {
    return User(
        uid = uid.orEmpty(),
        name = name.orEmpty(),
        address = address.orEmpty(),
        role = try {
            UserRole.valueOf(role?.uppercase() ?: UserRole.CUSTOMER.name)
        } catch (_: Exception) {
            UserRole.CUSTOMER
        }
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        uid = uid,
        name = name,
        address = address,
        role = role.name
    )
}
