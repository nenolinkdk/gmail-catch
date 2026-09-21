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

        val extracted = NotificationTextExtractor.extract(sbn.notification)
        val result = VipMatcher.evaluate(
            settings.vipSender,
            settings.subjectPrefix,
            settings.subjectRequired,
            extracted.fields.getOrDefault("title", emptyList()) +
                extracted.fields.getOrDefault("titleBig", emptyList()),
            extracted.fields.getOrDefault("text", emptyList()) +
                extracted.fields.getOrDefault("bigText", emptyList()) +
                extracted.fields.getOrDefault("textLines", emptyList())
        )
        EventLog.add(
            this,
            "package=${sbn.packageName}; ${extracted.diagnosticSummary()}; " +
                "senderFound=${result.senderFound}; subjectPrefixFound=${result.subjectPrefixFound}; " +
                "alarmTriggered=${result.matched}"
        )
        if (!result.matched) return

        val now = System.currentTimeMillis()
        val fingerprint = extracted.candidates.joinToString("|") { it.trim().lowercase() }.hashCode().toString()
        synchronized(recent) {
            val previous = recent[fingerprint]
            if (previous != null && now - previous < DEDUPE_MS) {
                EventLog.add(this, "Dublet/grupperet Gmail-notifikation ignoreret")
                return
            }
            recent[fingerprint] = now
            recent.entries.removeAll { now - it.value > DEDUPE_MS * 4 }
        }

        startForegroundService(Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_START
            putExtra(AlarmService.EXTRA_REASON, "LILT assignment modtaget")
        })
    }

    companion object {
        private const val GMAIL_PACKAGE = "com.google.android.gm"
        private const val DEDUPE_MS = 60_000L
    }
}
