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

The v0.1 log records only the relevant notification-extra fields, truncates each field to 160 characters, and shows the sender/subject match decisions. Real Gmail field placement remains **requires device test**.

## OnePlus / OxygenOS setup (Android 14)

Menu names can vary slightly by OxygenOS build:

1. In Gmail Catch, open **GIV NOTIFIKATIONSADGANG** and enable Gmail Catch.
2. Allow Gmail Catch notifications and leave its alarm notification category enabled.
3. Open **KONTROLLÉR FULL-SCREEN ALARM** and allow full-screen notifications/alarms if the setting is offered.
4. In **Settings > Apps > App management > Gmail Catch > Battery usage**, allow background activity and select **Unrestricted / Don't optimize** where available.
5. In **Settings > Battery > More settings > Optimize battery use**, exclude Gmail Catch from optimization. Also disable Sleep standby optimization for the overnight reliability test if OxygenOS exposes it.
6. Keep Gmail notifications enabled for the account/label receiving LILT assignments. DND must allow alarms if the phone is expected to sound during DND.
7. Do not Force stop Gmail Catch. Removing it from recents should be tested separately.

## Physical LILT test

Keep the default sender `noreply@em.lilt.com` and subject prefix `You have a new translation assignment on project` enabled. Verify TEST ALARM first, then lock the phone and deliver one real assignment. Afterward inspect Diagnostics for `senderFound=true`, `subjectPrefixFound=true`, and `alarmTriggered=true`. Repeat after several idle hours, with DND configured to allow alarms, and with grouped Gmail notifications. These scenarios are **requires device test**.

## Release gate

Do not call v0.1 reliable for overnight use until it has passed at least these real-device scenarios:

- locked screen;
- DND/night configuration;
- several hours idle;
- Gmail grouped notifications;
- at least one VIP and one non-VIP sender.
