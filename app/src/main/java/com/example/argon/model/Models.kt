package com.example.argon.model

import java.text.NumberFormat
import java.util.Locale

enum class UserRole(val label: String, val badgeColorHex: Long) {
    GUEST("Tamu", 0xFF756D65),
    FREE_MEMBER("Member Gratis", 0xFF4C758E),
    PREMIUM_MEMBER("Member Premium", 0xFFC5A059),
    CONTENT_EDITOR("Editor Konten", 0xFF5D6670),
    ADMIN("Administrator", 0xFF1E1A17)
}

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val role: UserRole,
    val avatarUrl: String? = null,
    val isEmailVerified: Boolean = true,
    val memberSince: String = "Oktober 2024",
    val biography: String = "Praktisi bisnis & pembelajar seumur hidup yang fokus pada ketahanan narasi dan kepemimpinan otentik.",
    val learningGoals: List<String> = listOf("Rekonstruksi narasi bisnis", "Manajemen risiko krisis", "Personal branding tanpa sensasi")
)

enum class ContentType(val label: String, val iconName: String) {
    FOUNDER_STORY("Cerita Founder", "auto_stories"),
    EBOOK("Ebook Panduan", "menu_book"),
    PODCAST("Podcast Episode", "podcasts"),
    AUDIO_LESSON("Pelajaran Audio", "headphones"),
    ARTICLE("Esai & Artikel", "article"),
    STORYTELLING_TEMPLATE("Template Narasi", "draw"),
    COURSE("Modul Terstruktur", "school")
}

enum class AccessType(val label: String) {
    FREE("Akses Terbuka"),
    PREMIUM("Khusus Premium"),
    PURCHASE_ONLY("Beli Terpisah")
}

data class Category(
    val id: String,
    val name: String,
    val slug: String,
    val description: String
)

data class EbookChapter(
    val title: String,
    val readTimeMinutes: Int,
    val content: String
)

data class ContentItem(
    val id: String,
    val title: String,
    val slug: String,
    val summary: String,
    val body: String,
    val contentType: ContentType,
    val accessType: AccessType,
    val categoryId: String,
    val author: String = "Raden Aria (Founder ARGON)",
    val readTimeMinutes: Int = 10,
    val coverDrawableName: String? = null,
    val audioDurationSeconds: Int = 0,
    val chapters: List<EbookChapter> = emptyList(),
    val transcript: String? = null,
    val standalonePriceIdr: Long = 0L,
    val publicationDate: String = "12 Oktober 2024",
    val isFeatured: Boolean = false,
    val templatePrompt: StorytellingTemplate? = null
)

data class StorytellingTemplate(
    val id: String,
    val title: String,
    val objective: String,
    val hookGuide: String,
    val contextGuide: String,
    val conflictGuide: String,
    val turningPointGuide: String,
    val lessonLearnedGuide: String,
    val practicalTakeawayGuide: String,
    val closingReflectionGuide: String,
    val exampleText: String
)

data class Plan(
    val id: String,
    val name: String,
    val intervalLabel: String,
    val priceIdr: Long,
    val originalPriceIdr: Long,
    val benefits: List<String>,
    val isPopular: Boolean = false
)

data class Subscription(
    val id: String,
    val planId: String,
    val planName: String,
    val status: String, // "Aktif", "Dibatalkan", "Kadaluarsa"
    val renewalDate: String,
    val priceIdr: Long,
    val billingProvider: String = "Google Play / IDR Payment"
)

data class Order(
    val id: String,
    val orderNumber: String,
    val date: String,
    val itemTitle: String,
    val amountIdr: Long,
    val status: String, // "Berhasil", "Menunggu"
    val paymentMethod: String = "GoPay / QRIS / Store Billing"
)

data class ConsultationService(
    val id: String,
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val priceIdr: Long,
    val memberDiscountPriceIdr: Long,
    val preparationNotes: String,
    val cancellationPolicy: String = "Pembatalan atau penjadwalan ulang bebas biaya hingga 24 jam sebelum sesi dimulai."
)

data class AvailabilitySlot(
    val id: String,
    val date: String, // e.g. "Senin, 21 Oktober 2024"
    val time: String, // e.g. "14:00 - 15:00 WIB"
    val isAvailable: Boolean = true
)

data class ConsultationBooking(
    val id: String,
    val serviceId: String,
    val serviceTitle: String,
    val date: String,
    val time: String,
    val userName: String,
    val userEmail: String,
    val intakeGoal: String,
    val intakeChallenges: String,
    val status: String, // "Terkonfirmasi", "Menunggu Pembayaran", "Selesai"
    val meetingUrl: String = "https://meet.google.com/arg-on24-ses"
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val date: String,
    val isRead: Boolean = false,
    val actionType: String = "CONTENT"
)

data class AuditLog(
    val id: String,
    val actor: String,
    val action: String,
    val details: String,
    val timestamp: String
)

object Formatters {
    fun formatRupiah(amount: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount).replace("Rp", "Rp ")
    }

    fun formatSecondsToTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", m, s)
    }
}
