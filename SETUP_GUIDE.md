# Setup Guide for DevPilot AI

Follow these steps to set up the project on your local machine and get it running.

## Prerequisites

- **Android Studio**: Ladybug (2024.2.1) or newer.
- **JDK**: Version 17 or 11 (Project is configured for Java 11).
- **Android SDK**: API 35 (Android 15) installed.
- **Firebase Account**: Required for Authentication and Firestore.

## Getting Started

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/devpilot-ai.git
   cd devpilot-ai
   ```

2. **Firebase Configuration**
   - Create a new project in the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app with the package name `com.devpilotai`.
   - Download the `google-services.json` file and place it in the `app/` directory.
   - Enable **Email/Password** and **Google** sign-in providers in the Firebase Authentication section.

3. ### AI Configuration

DevPilot AI uses Firebase AI Logic with the Gemini Developer API.

No Gemini API key is hardcoded in the Android application.

The AI model is configured through Firebase AI Logic in `GeminiAIService.java`.

4. **GitHub Integration (Optional)**
   - Create a GitHub OAuth App or Personal Access Token if you plan to test the GitHub features extensively.

## Building and Running

1. **Open the project** in Android Studio.
2. **Sync Gradle**: Let the IDE download all dependencies.
3. **Select a device**: Use a Physical Device or an Emulator (API 26+).
4. **Run**: Click the 'Run' button (Green play icon).

## Troubleshooting

- **Google Services Error**: Ensure `google-services.json` is in the `app` folder and the package name matches.
- **Gemini API 403/401**: Verify your API key and ensure the Gemini API is enabled for your Google Cloud project.
- **Room Migration**: If you encounter database errors after pulling changes, perform a 'Clean Project' or uninstall the app from your device to trigger a fresh database creation.

## Production Checklist

- [ ] Change `applicationId` to your own unique package name.
- [ ] Set up Proguard/R8 rules for release builds.
- [ ] Move API keys to a secure location (Secrets Gradle Plugin).
- [ ] Update the `google-services.json` for the Production Firebase project.
