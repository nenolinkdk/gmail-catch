# Gmail Catch — implementation plan

## Product definition

Gmail Catch is a dedicated Android alarm layer on top of Gmail notifications. Gmail remains responsible for receiving mail through Google's infrastructure. Gmail Catch is responsible only for detecting selected Gmail notifications and escalating a match into a persistent alarm.

## v0.1 — minimum viable alarm

### User functions

- Enable/disable Gmail Catch.
- Configure one VIP sender string (normally an email address).
- Open Android notification-listener settings.
- Test the alarm without receiving an email.
- Stop an active alarm.
- See a small local diagnostic log.

### Detection

- Accept notifications only from `com.google.android.gm`.
- Extract title, text, subtext, summary text, text lines and messaging-style sender/person fields where Android exposes them.
- Normalize to lowercase for matching.
- Match the configured sender against the combined notification metadata.
- Ignore Gmail notifications with no match.
- Deduplicate repeated callbacks for the same notification key/content for a short interval.

### Alarm

- Start a foreground alarm service after a VIP match.
- Loop an alarm sound and vibration until STOP.
- Post a high-importance alarm notification.
- Request full-screen alarm presentation where Android permits it.
- Show an alarm activity over the lock screen.
- Provide STOP from both alarm screen and notification.

### Privacy

The persistent log must store only timestamp, event type and a short diagnostic reason. Do not persist email bodies or full notification payloads.

## v0.2 — after real-device validation

- Multiple VIP senders.
- Per-sender enable/disable.
- Optional active hours, including windows crossing midnight such as 22:00–07:00.
- Sender display names plus email-address aliases.
- Configurable alarm sound.
- Snooze.
- Exportable diagnostic report.

## v0.3 — reliability

- OnePlus-specific setup guidance for battery/background restrictions.
- Boot/restart status diagnostics.
- Better Gmail grouped-notification handling based on observations from the test phone.
- Automated unit tests for notification-text extraction, normalization, matching and time windows.

## Acceptance criteria for v0.1

1. A normal Gmail notification from a non-VIP sender never starts the alarm.
2. A Gmail notification containing the configured VIP email address or sender string starts the alarm.
3. Alarm is clearly different from Gmail's normal notification sound.
4. Alarm keeps sounding until STOP is pressed.
5. Alarm can appear while the device is locked, subject to Android full-screen-intent permission/policy.
6. TEST ALARM works without Gmail.
7. App survives being closed from the recent-apps UI because detection is provided by the system-bound NotificationListenerService.
8. No Gmail credentials or network permissions are required.

## Explicit non-goals for v0.1

- Gmail API / OAuth.
- Reading inbox history.
- Sending or replying to mail.
- Server-side push infrastructure.
- Play Store publication work.
- Supporting arbitrary mail apps.

## Test sequence

1. Install debug APK on the OnePlus.
2. Grant notification access to Gmail Catch.
3. Grant notification permission.
4. Check full-screen-intent access if Android exposes the control.
5. Disable battery/background restrictions for Gmail Catch on the OnePlus.
6. Configure a test sender.
7. Press TEST ALARM and verify STOP.
8. Lock the phone and send a Gmail message from the VIP account.
9. Repeat with Do Not Disturb enabled.
10. Send from a non-VIP account and confirm no alarm.
11. Test Gmail grouped notifications and record any extraction mismatch in the diagnostic log.
