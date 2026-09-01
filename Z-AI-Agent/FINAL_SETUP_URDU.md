# Z-AI Agent FINAL24 — Setup Guide (Urdu)

## مقصد
یہ build ایک personal Android AI agent ہے۔ اس میں:
- Google Gemini runtime API key
- Urdu/English chat
- Text-to-Speech + Speech-to-Text
- foreground continuous voice loop
- visible listening notification
- optional floating status bubble
- local encrypted conversation memory
- owner profile
- confirmation-before-action policy
- app launcher fallback for installed launchable apps
- YouTube search/download-page workflow
- optional Accessibility UI automation
- notification/call announcement
- call answer/end best-effort controls

## پہلی بار setup

1. APK install کریں۔
2. Z-AI Agent کھولیں۔
3. Microphone permission Allow کریں۔
4. Notifications Allow کریں۔
5. Google AI Studio سے اپنی API key بنائیں اور app میں paste کریں۔
6. `Save` دبائیں، پھر `Test AI` چلائیں۔
7. اگر floating icon چاہیے تو `Floating icon permission` Allow کریں۔
8. اگر دوسرے apps کی screen automation چاہیے تو Accessibility میں Z-AI Agent enable کریں۔
9. اگر notifications/call announcements چاہیے تو Notification Access enable کریں۔
10. اگر call answer/end چاہیے تو Phone control permission دیں۔
11. `START AGENT` دبائیں۔
12. اب app کی main screen بند کی جا سکتی ہے؛ foreground service notification باقی رہے گی۔

## Wake phrase
Default:
- Z Agent
- Zohaib Agent
- Zohaib
- زیڈ ایجنٹ
- زوہیب ایجنٹ
- زوہیب

Wake phrase mode میں wake phrase کے بغیر عام گفتگو process نہیں ہوگی۔ `All speech` mode صرف owner کی طرف سے نہیں، بلکہ microphone پر آنے والی دوسری recognized speech بھی process کر سکتا ہے؛ اسے صرف اپنی privacy preference کے مطابق استعمال کریں۔

## Action confirmation
Device/app actions کے لیے agent پہلے پوچھتا ہے:
> Sir، کیا میں ... کروں؟

پھر:
> ہاں، کر دو

کہنے پر action چلتا ہے۔ `نہیں` / `cancel` سے pending action ختم ہو جاتا ہے۔

## YouTube
مثال:
> "Z Agent، YouTube پر یہ گانا offline چلاؤ"

Agent:
1. confirmation مانگے گا۔
2. اجازت ملنے پر YouTube Downloads کھولے گا۔
3. Accessibility enabled ہو تو visible matching item پر click کی کوشش کرے گا۔
4. visible match نہ ملنے پر YouTube search کھولے گا۔

یہ third-party app کے UI پر depend کرتا ہے؛ ہر YouTube/MX Player version میں automation کی 100% guarantee نہیں۔

## Lock screen / screen off
Foreground microphone service screen off کے دوران چل سکتی ہے، لیکن Android version، battery optimization، OEM restrictions اور microphone policies اسے روک سکتے ہیں۔ کوئی app Android security controls کو bypass نہیں کر سکتی۔ اسی لیے notification/status indicator موجود رکھا گیا ہے۔

## Privacy
- API key DeveloperProfile میں hard-code نہیں ہے۔
- API key encrypted local storage میں رکھی جاتی ہے۔
- Conversation memory encrypted local storage میں ہے۔
- Notifications صرف user-enabled Notification Access کے بعد پڑھی جاتی ہیں۔
- Accessibility صرف user کے manually enabling کے بعد استعمال ہوتی ہے۔
- Agent private owner information کسی دوسرے شخص کو خود سے reveal نہ کرنے کی policy رکھتا ہے۔

## APK
GitHub Actions کے successful run کے بعد:
Actions → successful workflow → Summary → Artifacts → `z-agent-final24-debug-apk`

Personal sideload کے لیے Debug APK کافی ہے۔ Play Store کے لیے بعد میں signed Release/AAB الگ بنانا ہوگا۔
