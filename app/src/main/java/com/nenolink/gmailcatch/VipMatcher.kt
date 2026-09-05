package com.nenolink.gmailcatch

object VipMatcher {
    fun matches(vipSender: String, candidates: Collection<String>): Boolean {
        val needle = vipSender.trim().lowercase()
        if (needle.isBlank()) return false
        return candidates.any { it.lowercase().contains(needle) }
    }
}
