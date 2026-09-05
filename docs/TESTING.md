# Testing Gmail Catch

## Why physical-device testing is required

The critical integration is between the real Gmail app, Android's notification listener, lock-screen behavior, Do Not Disturb and OnePlus/OxygenOS power management. An emulator can test UI and matching logic but cannot prove overnight reliability.

## v0.1 test matrix

| Test | Expected result |
|---|---|
| TEST ALARM button | Alarm starts and loops until STOP |
| Gmail, VIP sender, screen on | Alarm starts |
| Gmail, VIP sender, screen locked | Alarm notification/full-screen UI appears as permitted and sound starts |
| Gmail, non-VIP sender | No alarm |
| Gmail, VIP sender, DND on | Alarm behavior is tested and documented; required system access/settings are corrected if blocked |
| Two callbacks for same Gmail notification | One alarm event |
| App removed from recents | Notification listener remains system-managed and can detect later Gmail notification |
| Phone left idle overnight | Morning test message still triggers alarm |

## Diagnostic procedure

If a VIP mail does not trigger:

1. Confirm Gmail itself produced an Android notification.
2. Confirm Gmail Catch has Notification Access.
3. Open Gmail Catch log.
4. Check whether a Gmail notification event was observed.
5. If observed but unmatched, compare the configured sender with the diagnostic candidate fields; do not log the full mail body.
6. If no event was observed, inspect OnePlus background/battery settings and Android notification-listener status.

## Release gate

Do not call v0.1 reliable for overnight use until it has passed at least these real-device scenarios:

- locked screen;
- DND/night configuration;
- several hours idle;
- Gmail grouped notifications;
- at least one VIP and one non-VIP sender.
