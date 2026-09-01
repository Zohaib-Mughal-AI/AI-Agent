# Security & Privacy Notes

- No API secret is hard-coded in source code.
- Gemini API key is stored using AndroidX Security EncryptedSharedPreferences.
- No analytics, ads, or custom telemetry are included in this project.
- Network traffic is sent to the configured AI provider only when the owner uses the AI feature.
- Microphone listening is user-controlled and visibly starts/stops from the app.
- Accessibility and notification access are optional and controlled by Android settings.
- The app does not attempt to bypass Android, payment, account, privacy, or security controls.
- No app can honestly guarantee that a device is impossible to hack. Keep Android, Google Play system components, and the device firmware updated, use a strong screen lock, and never share your API key.
