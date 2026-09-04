package com.nenolink.gmailcatch

import android.content.Context
import java.text.DateFormat
import java.util.Date

object EventLog {
    private const val PREFS = "gmail_catch_log"
    private const val KEY = "events"
    private const val MAX_EVENTS = 30

    fun add(context: Context, message: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = prefs.getString(KEY, "").orEmpty()
            .lineSequence().filter { it.isNotBlank() }.toMutableList()
        val stamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date())
        current.add(0, "$stamp — $message")
        prefs.edit().putString(KEY, current.take(MAX_EVENTS).joinToString("\n")).apply()
    }

    fun read(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, "Ingen hændelser endnu.") ?: "Ingen hændelser endnu."
}
