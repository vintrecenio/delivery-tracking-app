package com.treceniovin.deliverytracking.ui.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.treceniovin.deliverytracking.data.model.Order
import com.treceniovin.deliverytracking.data.model.OrderStatus
import com.treceniovin.deliverytracking.ui.navigation.UserRole
import com.treceniovin.deliverytracking.ui.theme.DeliveryTrackingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    uiState: OrderDetailsUiState,
    role: UserRole,
    onStatusUpdate: (OrderStatus) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null && uiState.order == null) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (uiState.order != null) {
                val order = uiState.order
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Progress UI - Only visible for CUSTOMER role
                    if (role == UserRole.CUSTOMER) {
                        StatusProgressIndicator(currentStatus = order.status)
                    }

                    // Order Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order #${order.orderId.take(8).uppercase()}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (role == UserRole.DRIVER) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = MaterialTheme.shapes.extraSmall
                                    ) {
                                        Text(
                                            text = order.status.name.replace("_", " "),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = order.customerName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = order.deliveryAddress,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (order.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Notes",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = order.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Driver Controls - Only visible for DRIVER role
                    if (role == UserRole.DRIVER) {
                        Text(
                            text = "Update Delivery Status",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        DriverStatusController(
                            currentStatus = order.status,
                            onStatusUpdate = onStatusUpdate,
                            isUpdating = uiState.isUpdating
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusProgressIndicator(currentStatus: OrderStatus) {
    val stages = OrderStatus.entries
    val currentStageIndex = stages.indexOf(currentStatus)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        stages.forEachIndexed { index, status ->
            val isCompleted = index < currentStageIndex
            val isActive = index == currentStageIndex
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted || isActive -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = (index + 1).toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = status.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = if (isActive || isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            if (index < stages.size - 1) {
                val lineColor = if (index < currentStageIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .weight(0.5f)
                        // Align the line with the center of the circles (approx 16dp from top of the Box)
                        .offset(y = (-10).dp) 
                        .background(lineColor)
                )
            }
        }
    }
}

@Composable
fun DriverStatusController(
    currentStatus: OrderStatus,
    onStatusUpdate: (OrderStatus) -> Unit,
    isUpdating: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatusControlButton(
            status = OrderStatus.PENDING,
            isSelected = currentStatus == OrderStatus.PENDING,
            onClick = { onStatusUpdate(OrderStatus.PENDING) },
            icon = Icons.Default.PendingActions,
            enabled = !isUpdating
        )
        StatusControlButton(
            status = OrderStatus.IN_TRANSIT,
            isSelected = currentStatus == OrderStatus.IN_TRANSIT,
            onClick = { onStatusUpdate(OrderStatus.IN_TRANSIT) },
            icon = Icons.Default.LocalShipping,
            enabled = !isUpdating
        )
        StatusControlButton(
            status = OrderStatus.DELIVERED,
            isSelected = currentStatus == OrderStatus.DELIVERED,
            onClick = { onStatusUpdate(OrderStatus.DELIVERED) },
            icon = Icons.Default.CheckCircle,
            enabled = !isUpdating
        )
    }
}

@Composable
fun StatusControlButton(
    status: OrderStatus,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    enabled: Boolean
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            when (status) {
                OrderStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
                OrderStatus.IN_TRANSIT -> MaterialTheme.colorScheme.tertiaryContainer
                OrderStatus.DELIVERED -> MaterialTheme.colorScheme.primaryContainer
            }
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "containerColor"
    )

    val contentColor = if (isSelected) {
        when (status) {
            OrderStatus.PENDING -> MaterialTheme.colorScheme.onSecondaryContainer
            OrderStatus.IN_TRANSIT -> MaterialTheme.colorScheme.onTertiaryContainer
            OrderStatus.DELIVERED -> MaterialTheme.colorScheme.onPrimaryContainer
        }
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = if (isSelected) BorderStroke(0.dp, Color.Transparent) else ButtonDefaults.outlinedButtonBorder(enabled),
        enabled = enabled
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, contentDescription = null)
            Text(
                text = status.name.replace("_", " "),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Current Status", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderDetailsPreview() {
    DeliveryTrackingTheme {
        OrderDetailsScreen(
            uiState = OrderDetailsUiState(
                order = Order(
                    id = "1",
                    orderId = "12345678",
                    customerId = "1234",
                    customerName = "Customer 1",
                    deliveryAddress = "123 Main St",
                    status = OrderStatus.IN_TRANSIT,
                    description = "Ring doorbell twice please."
                )
            ),
            role = UserRole.CUSTOMER,
            onStatusUpdate = {},
            onBackClick = {}
        )
    }
}
