package com.nenolink.gmailcatch

import android.app.Notification
import android.os.Bundle

object NotificationTextExtractor {
    fun extract(notification: Notification): List<String> {
        val extras = notification.extras ?: Bundle.EMPTY
        val result = linkedSetOf<String>()

        fun add(value: CharSequence?) {
            value?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let(result::add)
        }

        add(extras.getCharSequence(Notification.EXTRA_TITLE))
        add(extras.getCharSequence(Notification.EXTRA_TITLE_BIG))
        add(extras.getCharSequence(Notification.EXTRA_TEXT))
        add(extras.getCharSequence(Notification.EXTRA_BIG_TEXT))
        add(extras.getCharSequence(Notification.EXTRA_SUB_TEXT))
        add(extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT))
        extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.forEach(::add)

        return result.toList()
    }
}
