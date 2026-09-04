# Architecture

## Data flow

```text
Google/Gmail mail delivery
        |
        v
Official Gmail Android app
        |
        | Android notification
        v
GmailNotificationListener
        |
        | package == com.google.android.gm
        v
NotificationTextExtractor
        |
        v
VipMatcher ---- SettingsRepository
        |
        | match
        v
AlarmService
        |
        +--> looping alarm audio
        +--> vibration
        +--> high-importance notification
        +--> full-screen AlarmActivity when allowed
```

## Components

### `MainActivity`
Configuration and health/status screen. It does not need to remain open for matching to work.

### `GmailNotificationListener`
Android `NotificationListenerService`. Receives notification callbacks from the operating system. It rejects packages other than Gmail before doing any content matching.

### `NotificationTextExtractor`
Converts Android notification extras into a normalized collection of candidate strings. Gmail notification formats can vary, so extraction must not depend on a single `title` field.

### `VipMatcher`
Pure matching logic. v0.1 uses case-insensitive substring matching against the configured sender string. Keeping this logic independent makes it easy to unit-test and improve after observing real Gmail notifications.

### `AlarmService`
Foreground service responsible for the alarm lifecycle. Owns audio and vibration resources and releases them on STOP.

### `AlarmActivity`
Minimal lock-screen alarm UI. It is not responsible for deciding whether a notification matches.

### `SettingsRepository`
Local SharedPreferences-backed configuration for v0.1. No account credentials are stored.

### `EventLog`
Small local ring-buffer diagnostic log. It stores operational events, not email contents.

## Security/privacy boundary

The app requires notification-listener access because that is the input channel. It deliberately does not request Internet permission. This makes the v0.1 architecture local-only and prevents accidental transmission of notification content by the app itself.

## Android constraints

Modern Android restricts background activity launches. Alarm presentation therefore uses an alarm notification/full-screen intent where allowed rather than directly launching an activity from the notification listener. Full-screen intent availability must be treated as a runtime capability, not assumed.

The alarm service is a foreground service and declares its service type. Notification permission and full-screen-intent access are separate concerns.

## Gmail-specific risk

Gmail can change notification layouts and can group multiple messages. Therefore the first physical-device build must include diagnostics. A failure to match on the OnePlus should first be treated as an extraction/matching issue, not as a reason to add Gmail API access.
