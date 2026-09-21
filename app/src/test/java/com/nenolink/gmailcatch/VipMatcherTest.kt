package com.nenolink.gmailcatch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VipMatcherTest {
    @Test
    fun matchesEmailCaseInsensitively() {
        assertTrue(VipMatcher.matches("vip@example.com", listOf("VIP@EXAMPLE.COM", "Subject")))
    }

    @Test
    fun rejectsDifferentSender() {
        assertFalse(VipMatcher.matches("vip@example.com", listOf("other@example.com", "Subject")))
    }

    @Test
    fun blankVipNeverMatches() {
        assertFalse(VipMatcher.matches("   ", listOf("anything")))
    }

    @Test
    fun matchesLiltReviewAssignmentWithLeadingPaperclip() {
        val result = VipMatcher.evaluate(
            "Lilt",
            "You have",
            true,
            listOf("Lilt"),
            listOf("  📎 You have a new review assignment on project X")
        )
        assertTrue(result.senderFound)
        assertTrue(result.subjectPrefixFound)
        assertTrue(result.matched)
    }

    @Test
    fun matchesLiltTranslationAssignment() {
        assertTrue(
            VipMatcher.evaluate(
                "Lilt",
                "You have",
                true,
                listOf("lIlT"),
                listOf("You have a new translation assignment on project X")
            ).matched
        )
    }

    @Test
    fun rejectsLiltWithOtherText() {
        val result = VipMatcher.evaluate(
            "Lilt",
            "You have",
            true,
            listOf("Lilt"),
            listOf("Your weekly LILT summary")
        )
        assertTrue(result.senderFound)
        assertFalse(result.subjectPrefixFound)
        assertFalse(result.matched)
    }

    @Test
    fun rejectsOtherSenderWithYouHaveText() {
        assertFalse(
            VipMatcher.evaluate(
                "Lilt",
                "You have",
                true,
                listOf("Another sender"),
                listOf("You have a new translation assignment")
            ).matched
        )
    }
}
