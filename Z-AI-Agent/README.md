# Z-AI-Agent-PRO

A personal Android AI-agent foundation for Zohaib Mughal. The agent addresses the developer as **Sir**, supports Urdu/English interaction, encrypted local memory foundation, native Android actions, reminders, background service foundation, accessibility integration, and a GitHub Actions APK build.

## Native actions
- Open supported installed apps
- Open browser/web pages
- Open email compose
- Open SMS compose
- Open phone dialer
- Schedule reminders and notifications

## AI integration
`AIClient.kt` is deliberately an adapter. Connect a real LLM through a secure backend or an appropriate local model. Never hard-code provider secrets in the APK.

## Account integrations
Gmail/Google/Drive/GitHub and other services require their official authorization flows/APIs. Third-party app automation is subject to Android and app restrictions.

## APK build
GitHub Actions workflow: `.github/workflows/android-apk.yml`
Run **Actions → Build Android APK → Run workflow**, then download artifact `Z-AI-Agent-debug-apk`.
