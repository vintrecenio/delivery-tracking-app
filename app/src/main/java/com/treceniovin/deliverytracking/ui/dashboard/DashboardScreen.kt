package com.treceniovin.deliverytracking.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.treceniovin.deliverytracking.data.model.Order
import com.treceniovin.deliverytracking.data.model.OrderStatus
import com.treceniovin.deliverytracking.ui.navigation.UserRole
import com.treceniovin.deliverytracking.ui.theme.DeliveryTrackingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    role: UserRole,
    onFilterSelected: (OrderStatus?) -> Unit,
    onRefresh: () -> Unit,
    onOrderClick: (String) -> Unit,
    onAddOrderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (role == UserRole.CUSTOMER) "My Orders" else "Deliveries", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            if (role == UserRole.CUSTOMER) {
                FloatingActionButton(
                    onClick = onAddOrderClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Order")
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            FilterChips(
                selectedStatus = uiState.selectedStatus,
                onStatusSelected = onFilterSelected,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.orders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No orders found", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.orders, key = { it.id }) { order ->
                            OrderCard(
                                order = order,
                                onClick = { onOrderClick(order.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    selectedStatus: OrderStatus?,
    onStatusSelected: (OrderStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedStatus == null,
                onClick = { onStatusSelected(null) },
                label = { Text("All") }
            )
        }
        OrderStatus.entries.forEach { status ->
            item {
                FilterChip(
                    selected = selectedStatus == status,
                    onClick = { onStatusSelected(status) },
                    label = { Text(status.name.replace("_", " ").lowercase().capitalize()) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.customerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = order.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = order.deliveryAddress,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (order.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = order.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: OrderStatus) {
    val containerColor = when (status) {
        OrderStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
        OrderStatus.IN_TRANSIT -> MaterialTheme.colorScheme.tertiaryContainer
        OrderStatus.DELIVERED -> Color(0xFFC8E6C9) // Energetic Green
    }
    val contentColor = when (status) {
        OrderStatus.PENDING -> MaterialTheme.colorScheme.onSecondaryContainer
        OrderStatus.IN_TRANSIT -> MaterialTheme.colorScheme.onTertiaryContainer
        OrderStatus.DELIVERED -> Color(0xFF2E7D32)
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name.replace("_", " "),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    DeliveryTrackingTheme {
        DashboardScreen(
            uiState = DashboardUiState(
                orders = listOf(
                    Order("1", orderId = "11111", customerId = "1234", "Customer 1", "123 Main St", OrderStatus.PENDING, "Fragile item"),
                    Order("2", orderId = "22222", customerId = "2345", "Customer 2", "456 Oak Ave", OrderStatus.IN_TRANSIT, ""),
                    Order("3", orderId = "33333", customerId = "3456", "Customer 3", "789 Pine Rd", OrderStatus.DELIVERED, "Leave at door")
                )
            ),
            role = UserRole.CUSTOMER,
            onFilterSelected = {},
            onRefresh = {},
            onOrderClick = {},
            onAddOrderClick = {}
        )
    }
}
