package com.nenolink.gmailcatch

import android.content.Context

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("gmail_catch", Context.MODE_PRIVATE)

    var enabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ENABLED, value).apply()

    var vipSender: String
        get() = prefs.getString(KEY_VIP_SENDER, DEFAULT_SENDER) ?: DEFAULT_SENDER
        set(value) = prefs.edit().putString(KEY_VIP_SENDER, value.trim()).apply()

    var subjectPrefix: String
        get() = prefs.getString(KEY_SUBJECT_PREFIX, DEFAULT_SUBJECT_PREFIX) ?: DEFAULT_SUBJECT_PREFIX
        set(value) = prefs.edit().putString(KEY_SUBJECT_PREFIX, value.trim()).apply()

    var subjectRequired: Boolean
        get() = prefs.getBoolean(KEY_SUBJECT_REQUIRED, true)
        set(value) = prefs.edit().putBoolean(KEY_SUBJECT_REQUIRED, value).apply()

    companion object {
        private const val KEY_ENABLED = "enabled"
        private const val KEY_VIP_SENDER = "vip_sender"
        private const val KEY_SUBJECT_PREFIX = "subject_prefix"
        private const val KEY_SUBJECT_REQUIRED = "subject_required"

        const val DEFAULT_SENDER = "noreply@em.lilt.com"
        const val DEFAULT_SUBJECT_PREFIX = "You have a new translation assignment on project"
    }
}
