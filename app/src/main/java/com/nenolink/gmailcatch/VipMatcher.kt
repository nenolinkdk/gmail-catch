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
        senderCandidates: Collection<String>,
        subjectCandidates: Collection<String> = senderCandidates
    ): Result {
        val senderNeedle = sender.trim().lowercase()
        val prefixNeedle = subjectPrefix.trim().lowercase()
        val senderFound = senderNeedle.isNotEmpty() && senderCandidates.any {
            it.trim().lowercase() == senderNeedle
        }
        val subjectFound = prefixNeedle.isNotEmpty() && subjectCandidates.any { value ->
            value.trimStart().dropWhile { !it.isLetterOrDigit() }.trimStart()
                .lowercase().startsWith(prefixNeedle)
        }
        val validSubjectRule = !subjectRequired || (prefixNeedle.isNotEmpty() && subjectFound)
        return Result(senderFound, subjectFound, senderFound && validSubjectRule)
    }

    fun matches(vipSender: String, candidates: Collection<String>): Boolean {
        return evaluate(vipSender, "", false, candidates).matched
    }
}
