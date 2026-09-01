# Z-AI Agent FINAL24 — کیا update کیا گیا

## بنیادی fixes
- App/service architecture کو persistent foreground agent کے لیے update کیا گیا۔
- VoiceController کو continuous recognition + TTS completion loop کے ساتھ update کیا گیا۔
- Main screen اب local-only voice کے بجائے persistent `ZohaibService` start/stop کرتی ہے۔
- Floating visible Z-AI status bubble شامل کیا گیا۔
- API key encrypted local storage میں ہی رہتی ہے؛ source میں secret hard-code نہیں۔
- ہر external/device action سے پہلے confirmation gate شامل ہے۔

## Voice
- Wake phrases: Z Agent / Zohaib Agent / Zohaib / اردو variants
- Wake-only اور All-speech modes
- Voice answer کے بعد دوبارہ listening
- Foreground service notification

## Apps & media
- Known apps: YouTube, Chrome, Gmail, WhatsApp, Telegram, Drive, Maps, Camera, Calculator, MX Player, Settings
- Unknown launchable apps کے لیے installed launcher labels سے fallback matching
- YouTube search
- YouTube Downloads first + Accessibility visible-title click + search fallback
- MX Player open + visible-title click attempt

## Calls/notifications
- Notification Access کے ذریعے call-like notification پر voice alert
- Phone permission کے بعد answer/end call best-effort
- ہر call action پہلے confirmation مانگتا ہے

## Accessibility
- User-enabled Accessibility service visible UI text click اور focused text set/submit کر سکتی ہے۔
- یہ third-party app UI changes کے باعث best-effort ہے، universal guarantee نہیں۔

## Android limits
- Lock screen/screen-off listening صرف Android/OEM اجازت اور service survival کی حدود کے اندر ہے۔
- App Android security, permission, account یا payment controls bypass نہیں کرتی۔
