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
    fun matchesLiltSenderAndSubjectPrefixAcrossDifferentFields() {
        val result = VipMatcher.evaluate(
            "noreply@em.lilt.com",
            "You have a new translation assignment on project",
            true,
            listOf(
                "noreply@em.lilt.com",
                "You have a new translation assignment on project snap.com - Values 2026-09-18"
            )
        )
        assertTrue(result.senderFound)
        assertTrue(result.subjectPrefixFound)
        assertTrue(result.matched)
    }

    @Test
    fun matchesCombinedGmailFieldWithDisplayNameSenderAndSubject() {
        assertTrue(
            VipMatcher.evaluate(
                "noreply@em.lilt.com",
                "You have a new translation assignment on project",
                true,
                listOf("LILT <noreply@em.lilt.com> · You have a new translation assignment on project X")
            ).matched
        )
    }

    @Test
    fun rejectsLiltSenderWithWrongSubjectWhenRequired() {
        val result = VipMatcher.evaluate(
            "noreply@em.lilt.com",
            "You have a new translation assignment on project",
            true,
            listOf("noreply@em.lilt.com", "Your weekly LILT summary")
        )
        assertTrue(result.senderFound)
        assertFalse(result.subjectPrefixFound)
        assertFalse(result.matched)
    }

    @Test
    fun canDisableSubjectRequirement() {
        assertTrue(
            VipMatcher.evaluate(
                "noreply@em.lilt.com",
                "You have a new translation assignment on project",
                false,
                listOf("Message from noreply@em.lilt.com", "Any subject")
            ).matched
        )
    }

    @Test
    fun rejectsSubjectWithoutSender() {
        assertFalse(
            VipMatcher.evaluate(
                "noreply@em.lilt.com",
                "You have a new translation assignment on project",
                true,
                listOf("other@example.com", "You have a new translation assignment on project X")
            ).matched
        )
    }
}
