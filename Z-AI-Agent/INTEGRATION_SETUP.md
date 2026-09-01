# Z Agent integrations

Z Agent is designed as an owner-authorized Android agent. The project intentionally does not contain real passwords, OTPs, API secrets, OAuth refresh tokens, or bypasses.

## Real integrations to connect
- Gmail: Google OAuth + Gmail API
- Calendar: Google OAuth + Calendar API
- Drive: Google OAuth + Drive API
- Contacts/Phone/SMS: Android runtime permissions and native APIs
- WhatsApp/Telegram: official APIs where available; otherwise Android intents/accessibility only where the target app exposes compatible UI
- Voice: Android SpeechRecognizer / a configured speech-to-text provider
- Brain: an LLM provider implementing AIProvider and tool-calling
- Proactive behavior: WorkManager/AlarmManager + foreground service where Android permits

## Rule
Permissions expand what Z Agent can do, but Android and individual apps still enforce platform/security restrictions. The app should never attempt to bypass those restrictions.
