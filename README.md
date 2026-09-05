# Gmail Catch

Gmail Catch is a small Android alarm app for one purpose: wake the user when Gmail posts a notification from a configured VIP sender.

The app does **not** connect to Gmail, does not use Gmail OAuth, and does not read the mailbox. It listens locally to Android notifications from the official Gmail package (`com.google.android.gm`).

## v0.1 goal

1. User enters a VIP sender email address or sender text.
2. User grants notification-listener access.
3. Gmail Catch observes new Gmail notifications.
4. If notification content matches the VIP sender, Gmail Catch starts a looping alarm.
5. Alarm continues until the user presses STOP.
6. A local event log records what happened without storing message bodies.

## Target device

Primary test device: OnePlus Nord CE 2 Lite 5G, Android 14.

## Privacy

- No Gmail password.
- No Gmail API.
- No cloud backend.
- No analytics.
- No message content is uploaded.
- Matching is performed on-device.

## Project status

Phase 1 / v0.1 foundation.

See:

- `docs/PLAN.md`
- `docs/ARCHITECTURE.md`
- `docs/TESTING.md`

## Build

The project is Kotlin/Android and intentionally uses the platform UI APIs rather than a third-party UI framework for the first version.

Requirements:

- JDK 17
- Android Studio compatible with Android Gradle Plugin 9.4
- Android SDK 37

Open the repository in Android Studio, sync Gradle, install on the phone, and complete the permission/setup checklist shown in the app.
