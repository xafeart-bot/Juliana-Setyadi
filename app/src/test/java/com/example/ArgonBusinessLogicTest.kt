package com.example

import com.example.argon.data.ArgonRepository
import com.example.argon.data.ContentAccessResult
import com.example.argon.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ArgonBusinessLogicTest {

    @Before
    fun setUp() {
        ArgonRepository.switchRole(UserRole.FREE_MEMBER)
    }

    @Test
    fun testRupiahFormatting() {
        val formatted = Formatters.formatRupiah(149000L)
        assertTrue("Formatted Rupiah should contain 149", formatted.contains("149"))
        assertTrue("Formatted Rupiah should start with Rp", formatted.startsWith("Rp"))
    }

    @Test
    fun testSecondsToTimeFormatting() {
        assertEquals("00:00", Formatters.formatSecondsToTime(0))
        assertEquals("01:30", Formatters.formatSecondsToTime(90))
        assertEquals("24:18", Formatters.formatSecondsToTime(1458))
    }

    @Test
    fun testFreeContentAccessibleToFreeMember() {
        val freeContent = ArgonRepository.contents.value.first { it.accessType == AccessType.FREE }
        val access = ArgonRepository.checkContentAccess(freeContent)
        assertTrue("Free content must be granted to Free Member", access is ContentAccessResult.Granted)
    }

    @Test
    fun testPremiumContentRestrictedForFreeMember() {
        ArgonRepository.switchRole(UserRole.FREE_MEMBER)
        val premiumContent = ArgonRepository.contents.value.first { it.accessType == AccessType.PREMIUM }
        val access = ArgonRepository.checkContentAccess(premiumContent)
        assertTrue("Premium content should require upgrade for free member", access is ContentAccessResult.RequiresUpgrade)
    }

    @Test
    fun testPremiumContentGrantedForPremiumMember() {
        ArgonRepository.switchRole(UserRole.PREMIUM_MEMBER)
        val premiumContent = ArgonRepository.contents.value.first { it.accessType == AccessType.PREMIUM }
        val access = ArgonRepository.checkContentAccess(premiumContent)
        assertTrue("Premium content must be granted to Premium Member", access is ContentAccessResult.Granted)
    }

    @Test
    fun testAdminBypassesAllAccessRestrictions() {
        ArgonRepository.switchRole(UserRole.ADMIN)
        ArgonRepository.contents.value.forEach { content ->
            val access = ArgonRepository.checkContentAccess(content)
            assertTrue("Admin must have access to ${content.title}", access is ContentAccessResult.Granted)
        }
    }

    @Test
    fun testStandaloneContentPurchasedGranted() {
        ArgonRepository.switchRole(UserRole.FREE_MEMBER)
        val standaloneItem = ArgonRepository.contents.value.first { it.accessType == AccessType.PURCHASE_ONLY }
        
        // Before purchase
        val beforeAccess = ArgonRepository.checkContentAccess(standaloneItem)
        assertTrue(beforeAccess is ContentAccessResult.RequiresPurchase)
        
        // After purchase
        ArgonRepository.purchaseStandaloneContent(standaloneItem)
        val afterAccess = ArgonRepository.checkContentAccess(standaloneItem)
        assertTrue("Standalone purchase must grant access", afterAccess is ContentAccessResult.Granted)
    }

    @Test
    fun testConsultationMemberDiscountCalculation() {
        val service = ArgonRepository.consultationServices.first()
        assertEquals(750000L, service.priceIdr)
        assertEquals(500000L, service.memberDiscountPriceIdr)
        assertTrue(service.memberDiscountPriceIdr < service.priceIdr)
    }
}
