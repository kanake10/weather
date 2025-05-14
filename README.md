## Objective
This project is an Android application that fetches and displays weather data from the OpenWeather API. The app is built using **Jetpack Compose** for Android, and follows **MVVM architecture** for clean code practices. It includes key features such as offline storage when the user isn't connected to the Internet, error handling and a responsive UI/UX design.

## Features
- **Current Weather & 5-Day Forecast**: Displays current weather and a 5-day forecast for a selected city.
- **City Search**: Allows users to search for cities and view the respective weather data.
- **Offline Storage**: Caches weather data for offline viewing and displays last updated timestamp.
- **Error Handling**: Graceful error handling for network issues or API errors.
- **Modern UI**: Uses **Material3** components and a clean, responsive design.

## Technologies Used
- **[Native Android](https://developer.android.com/)** (Kotlin)
- **[Retrofit](https://square.github.io/retrofit/)** for API integration
- **[Gson](https://github.com/google/gson)** for JSON parsing
- **[Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** for asynchronous operations
- **[Room](https://developer.android.com/topic/libraries/architecture/room)** for offline storage
- **[Hilt](https://developer.android.com/training/dependency-injection/hilt-android)** for dependency injection
- **[KSP](https://github.com/google/ksp)** for code generation
- **MVVM Architecture** for clean code separation
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** for UI components
- **[Material3](https://m3.material.io/)** for modern UI design
- 
## Installation Instructions

### 1. Clone the Repository
Clone this repository to your local machine using the following command:
```
git clone https://github.com/kanake10/weather.git
```
- Sync the project to download dependencies.
- Run the project on an emulator or a physical device.



## App Preview

<div style="display: flex; gap: 10px;">
  <img src="https://github.com/user-attachments/assets/32e15ffe-b8f1-4353-8056-b289bf261ac6" height="600" width="300" />
  <img src="https://github.com/user-attachments/assets/aaa95aea-6f05-45f7-87c0-a94eec471ef7" height="600" width="300" />
</div>

