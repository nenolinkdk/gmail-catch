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
}
