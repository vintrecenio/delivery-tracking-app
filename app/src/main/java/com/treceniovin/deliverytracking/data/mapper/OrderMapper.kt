package com.treceniovin.deliverytracking.data.mapper

import com.treceniovin.deliverytracking.data.model.Order
import com.treceniovin.deliverytracking.data.model.OrderStatus
import com.treceniovin.deliverytracking.data.remote.dto.OrderDto

fun OrderDto.toDomain(): Order {
    return Order(
        id = id.orEmpty(),
        orderId = orderId.orEmpty(),
        customerId = customerId.orEmpty(),
        customerName = customerName.orEmpty(),
        deliveryAddress = deliveryAddress.orEmpty(),
        status = try {
            OrderStatus.valueOf(status?.uppercase() ?: OrderStatus.PENDING.name)
        } catch (_: Exception) {
            OrderStatus.PENDING
        },
        description = description.orEmpty(),
        updatedAt = updatedAt ?: System.currentTimeMillis()
    )
}

fun Order.toDto(): OrderDto {
    return OrderDto(
        id = id,
        orderId = orderId,
        customerId = customerId,
        customerName = customerName,
        deliveryAddress = deliveryAddress,
        status = status.name,
        description = description,
        updatedAt = updatedAt
    )
}
