package com.nenolink.gmailcatch

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class GmailNotificationListener : NotificationListenerService() {
    private val recent = mutableMapOf<String, Long>()

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != GMAIL_PACKAGE) return

        val settings = SettingsRepository(this)
        if (!settings.enabled || settings.vipSender.isBlank()) return

        val candidates = NotificationTextExtractor.extract(sbn.notification)
        val matched = VipMatcher.matches(settings.vipSender, candidates)
        EventLog.add(this, if (matched) "Gmail-notifikation matchede VIP" else "Gmail-notifikation uden VIP-match")
        if (!matched) return

        val now = System.currentTimeMillis()
        val fingerprint = sbn.key + "|" + candidates.joinToString("|").hashCode()
        val previous = recent[fingerprint]
        if (previous != null && now - previous < DEDUPE_MS) return
        recent[fingerprint] = now
        recent.entries.removeAll { now - it.value > DEDUPE_MS * 4 }

        startForegroundService(Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_START
            putExtra(AlarmService.EXTRA_REASON, "VIP-mail modtaget")
        })
    }

    companion object {
        private const val GMAIL_PACKAGE = "com.google.android.gm"
        private const val DEDUPE_MS = 15_000L
    }
}
