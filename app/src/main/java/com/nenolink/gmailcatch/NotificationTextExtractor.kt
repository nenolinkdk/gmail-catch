package com.nenolink.gmailcatch

import android.app.Notification
import android.os.Bundle

object NotificationTextExtractor {
    data class Extracted(val fields: Map<String, List<String>>) {
        val candidates: List<String> = fields.values.flatten().distinct()

        fun diagnosticSummary(maxValueLength: Int = 160): String = fields.entries.joinToString("; ") { (name, values) ->
            val safe = values.joinToString(" | ").replace(Regex("\\s+"), " ").take(maxValueLength)
            "$name=${if (safe.isBlank()) "<empty>" else safe}"
        }
    }

    fun extract(notification: Notification): Extracted {
        val extras = notification.extras ?: Bundle.EMPTY
        val result = linkedMapOf<String, MutableList<String>>()

        fun add(name: String, value: CharSequence?) {
            value?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let {
                result.getOrPut(name) { mutableListOf() }.add(it)
            }
        }

        add("title", extras.getCharSequence(Notification.EXTRA_TITLE))
        add("titleBig", extras.getCharSequence(Notification.EXTRA_TITLE_BIG))
        add("text", extras.getCharSequence(Notification.EXTRA_TEXT))
        add("bigText", extras.getCharSequence(Notification.EXTRA_BIG_TEXT))
        add("subText", extras.getCharSequence(Notification.EXTRA_SUB_TEXT))
        add("summaryText", extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT))
        extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.forEach { add("textLines", it) }

        return Extracted(result)
    }
}
