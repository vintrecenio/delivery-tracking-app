# Delivery Tracking App

A streamlined Android application designed for seamless communication between customers and drivers. This app provides a transparent tracking experience, allowing customers to monitor their deliveries while drivers update statuses in real-time.

## Features

- **Order Management Dashboard**: View order history with filtering by status (Pending, In Transit, Delivered).
- **Order Placement**: Simple interface for customers to create and submit new delivery requests.
- **Driver Status Controller**: Intuitive interface for delivery personnel to transition orders through their lifecycle.
- **Real-Time Updates**: Near real-time UI updates reflecting status changes across the system.
- **Edge-to-Edge Design**: Full immersive experience following Android 15+ guidelines.
- **Material 3**: Modern, energetic design with dynamic color support.

## Architecture Decisions

- **MVVM (Model-View-ViewModel)**: Used for clean separation of concerns and reactive UI state management.
- **Jetpack Navigation 3**: Employed the latest state-driven navigation model for better control over the backstack and type-safe routing.
- **Ktor Client**: Used for asynchronous networking with a mock backend, providing a lightweight and modern alternative to Retrofit.
- **Koin**: Selected for Dependency Injection due to its simplicity and first-class support for Jetpack Compose and ViewModels.
- **Clean Architecture Principles**: Data is managed in a Repository layer, abstracting local and remote sources from the ViewModels.

## Trade-offs

- **Mock API**: Used a mock service (MockAPI.io) for backend interactions. In a production environment, this would be replaced by a robust gRPC or REST backend with authentication.
- **Manual Backstack Management**: Navigation 3 requires more manual handling of the backstack compared to Navigation 2, but offers significantly more flexibility and predictability in state-driven UIs.
- **Local-First vs Remote-First**: Currently, the app utilized remote data. An "offline-first" approach with background sync could be implemented.

## Future Improvements

- **User Authentication**: Implement Firebase or Auth0 for secure customer and driver profiles.
- **Push Notifications**: Use FCM to notify customers instantly when their order status changes.
- **Detailed Tracking Logs**: Store and display a full timestamped history for each order.
- **Enhanced Driver Tools**: Add route optimization and signature capture on delivery.

## Extension: Real-Time Tracking

To implement real-time geographic tracking:
1.  **Google Maps Integration**: Add a map view to the Order Details screen.
2.  **Location Services**: Use Fused Location Provider on the driver's device to stream coordinates.
3.  **WebSockets**: Replace polling/refreshing with a persistent connection (e.g., Ktor WebSockets) to stream live location updates from the driver to the customer.
4.  **Geofencing**: Automatically update status to "Arriving" when the driver enters a predefined radius around the delivery address.

## How to Run

1.  Clone the repository.
2.  Open in Android Studio (Ladybug or newer recommended).
3.  Sync Gradle files.
4.  Run the `:app` module on an emulator or physical device (API 29+).
5.  In the role selection screen, select a user type.
6.  To create an order in the next screen, tap on the  "Add Order" button.
