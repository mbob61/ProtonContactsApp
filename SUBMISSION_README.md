# QA Automation Submission - Technical Overview

This document provides a technical overview of the automated testing framework implemented for the ToDo App.

---

## 1. Testing Framework Architecture

The framework is built on a Page Object Model (POM) design pattern, which enhances test maintenance and reduces code duplication. This is achieved through the use of **Screen Classes** (`ListScreen`, `DetailsScreen`) that encapsulate the UI elements and interactions for each screen of the app.

### Key Components:

- **`BaseTest`**: A base class for all test files. It handles the setup and teardown of the Compose test rule, Hilt for dependency injection, and provides helper methods for creating test data (e.g., `insertTestNote`).

- **`BaseScreen`**: A superclass for all screen classes. It holds the `ComposeContentTestRule` and provides common, shared functionality, such as waiting for the UI to be idle.

- **Screen Classes (`ListScreen`, `DetailsScreen`)**: These classes represent a single screen in the app. They are responsible for:
    - **Encapsulating UI selectors**: All `onNodeWithText`, `onNodeWithContentDescription`, etc., calls are centralized here.
    - **Providing action methods**: High-level methods like `clickSaveNote()` or `enterNoteTitle("...")` make the tests highly readable and abstract away the low-level Compose testing API calls.
    - **Fluent Interface**: Methods return an instance of the screen class, allowing for clear and readable chained method calls in the tests.

This architecture ensures that if the UI changes, updates only need to be made in the relevant screen class, not in every test that interacts with that UI element.

## 2. Technology Stack

The testing suite is built with a modern Android testing stack:

- **Kotlin**: The primary programming language.
- **JUnit 4**: The core testing framework for running tests.
- **Espresso & Compose Testing**: Google's native frameworks for UI testing Android apps. All tests use the Compose-specific APIs (`createAndroidComposeRule`).
- **Hilt**: Used for dependency injection. In our tests, Hilt provides an in-memory database (`TestAppModule`) to ensure tests run in an isolated environment.

## 3. Test Categorization and Filtering

To allow for flexible test execution, a robust categorization and filtering system has been implemented.

### Test Annotations:

Custom annotations were created to categorize tests based on their purpose:
- **`@SmokeTest`**: For critical, high-level user flows.
- **`@RegressionTest`**: For more comprehensive checks that cover a wider range of functionality.
- **`@NegativeTest`**: For tests that verify the app's behavior under error conditions or with invalid input.

### Build-Level Filtering:

The `app/build.gradle.kts` file has been configured to filter tests based on these annotations. This is accomplished by passing an `annotation` property to the Android instrumentation runner. This setup allows for running specific suites from both Android Studio and the command line.

Additionally, the configuration was updated so that when the **Regression** suite is run, **all tests are executed**, providing a full validation of the application.

## 4. How to Run Tests

Tests can be executed in two primary ways:

### From Android Studio:

1.  Navigate to the `app/src/androidTest/java` directory.
2.  To run a specific test, right-click on a test class or method and select "Run".
3.  To run a full suite (e.g., Smoke), you would typically use the created run configurations that pass the appropriate annotation to the test runner.

### From the Command Line:

Open a terminal in the project root and use the following Gradle commands:

- **Run All Tests:**
  ```bash
  ./gradlew app:connectedCheck 
  ```

- **Run Smoke Tests:**
  ```bash
  ./gradlew app:connectedCheck -Pandroid.test.runner.annotation=com.example.todoapp.annotations.SmokeTest
  ```

- **Run Regression Tests (all tests):**
  ```bash
  ./gradlew app:connectedCheck -Pandroid.test.runner.annotation=com.example.todoapp.annotations.RegressionTest
  ```

- **Run Negative Tests:**
  ```bash
  ./gradlew app:connectedCheck -Pandroid.test.runner.annotation=com.example.todoapp.annotations.NegativeTest
  ```
