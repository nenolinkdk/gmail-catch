package com.nenolink.gmailcatch

object VipMatcher {
    data class Result(
        val senderFound: Boolean,
        val subjectPrefixFound: Boolean,
        val matched: Boolean
    )

    fun evaluate(
        sender: String,
        subjectPrefix: String,
        subjectRequired: Boolean,
        candidates: Collection<String>
    ): Result {
        val senderNeedle = sender.trim().lowercase()
        val prefixNeedle = subjectPrefix.trim().lowercase()
        val normalized = candidates.flatMap { candidate ->
            candidate.lineSequence().map(String::trim).filter(String::isNotEmpty).toList()
        }.map(String::lowercase)

        val senderFound = senderNeedle.isNotEmpty() && normalized.any { it.contains(senderNeedle) }
        val subjectFound = prefixNeedle.isNotEmpty() && normalized.any { value ->
            val prefixIndex = value.indexOf(prefixNeedle)
            prefixIndex == 0 || (prefixIndex > 0 && value.substring(0, prefixIndex).contains(senderNeedle))
        }
        val validSubjectRule = !subjectRequired || (prefixNeedle.isNotEmpty() && subjectFound)
        return Result(senderFound, subjectFound, senderFound && validSubjectRule)
    }

    fun matches(vipSender: String, candidates: Collection<String>): Boolean {
        return evaluate(vipSender, "", false, candidates).matched
    }
}
