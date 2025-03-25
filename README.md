# Lifebalance To-Do App

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/umidsafarov/lifebalance/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.4-blue.svg)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Room-2.6.1-blue.svg)](https://developer.android.com/training/data-storage/room)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

## Overview

This project is a simple, offline-first to-do application built as a demonstration of modern Android development practices. It's designed to showcase a clean, well-structured codebase and serves as a portfolio piece highlighting my skills in Android development. The app allows users to create, manage, and track their to-do items, all while functioning seamlessly offline.

## Architecture

This project follows the **Clean Architecture** principles, promoting separation of concerns and testability. The architecture is structured into distinct layers:

*   **Presentation Layer:** Handles UI logic and user interactions (Jetpack Compose, MVI).
*   **Domain Layer:** Contains business logic and use cases.
*   **Data Layer:** Manages data access and persistence (Room).

**Architectural Patterns:**

*   **Model-View-Intent (MVI):** The presentation layer utilizes the MVI pattern for unidirectional data flow and state management, leading to a more predictable and testable UI.

## Technologies

*   **Jetpack Compose:** Modern declarative UI toolkit for building native Android UIs.
*   **Room:** Persistence library for local data storage and SQLite database management.
*   **Kotlin:** The primary programming language, leveraging its modern features and conciseness.
*   **Coroutines:** For asynchronous programming and managing background tasks.
*   **Dependency Injection (Hilt):** For managing dependencies and improving testability.
*   **Navigation Compose:** For navigation between screens.

## Testing

This project emphasizes the importance of testing and includes a comprehensive suite of tests:

*   **Unit Tests:** Verify the correctness of individual components (e.g., use cases, repositories).
*   **Integration Tests:** Ensure that different parts of the application work together correctly (e.g., data layer interactions).
*   **UI Tests:** Test the user interface and user interactions (Jetpack Compose UI testing).

## Getting Started

1.  **Clone the repository:** bash git clone https://github.com/umidsafarov/lifebalance.git
2.  **Open in Android Studio:** Import the project into Android Studio.
3.  **Build and Run:** Build the project and run it on an emulator or physical device.

## Run the project
1. **Download APK** from github.com/umidsafarov/lifebalance/blob/main/bin/lifebalance.apk
2. **Install APK** file to your android device

## License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

## Author

[Umid Safarov](https://www.linkedin.com/in/umidsafarov/)

## Disclaimer

This project is intended as a portfolio piece and a demonstration of Android development skills. It is not intended for production use without further development and testing.