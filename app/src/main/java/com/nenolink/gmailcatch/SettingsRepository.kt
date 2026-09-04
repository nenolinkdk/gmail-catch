package com.nenolink.gmailcatch

import android.content.Context

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("gmail_catch", Context.MODE_PRIVATE)

    var enabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ENABLED, value).apply()

    var vipSender: String
        get() = prefs.getString(KEY_VIP_SENDER, "") ?: ""
        set(value) = prefs.edit().putString(KEY_VIP_SENDER, value.trim()).apply()

    companion object {
        private const val KEY_ENABLED = "enabled"
        private const val KEY_VIP_SENDER = "vip_sender"
    }
}
