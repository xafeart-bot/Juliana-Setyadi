package com.example.argon.data

import com.example.argon.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

sealed class ContentAccessResult {
    data object Granted : ContentAccessResult()
    data class RequiresUpgrade(val message: String, val planPriceMonthlyIdr: Long) : ContentAccessResult()
    data class RequiresPurchase(val message: String, val priceIdr: Long) : ContentAccessResult()
}

data class AudioPlayerState(
    val contentItem: ContentItem? = null,
    val isPlaying: Boolean = false,
    val positionSeconds: Int = 0,
    val durationSeconds: Int = 0,
    val playbackSpeed: Float = 1.0f,
    val showTranscript: Boolean = false
)

object ArgonRepository {

    val categories = listOf(
        Category("cat-1", "Bangkit Setelah Kegagalan", "bangkit-setelah-kegagalan", "Menavigasi kehancuran mental, finansial, dan restrukturisasi arah hidup."),
        Category("cat-2", "Pelajaran dari Kebangkrutan", "pelajaran-dari-kebangkrutan", "Analisis post-mortem bisnis nyata tanpa pemanis atau pembelaan diri."),
        Category("cat-3", "Memulai Kembali dari Nol", "memulai-kembali-dari-nol", "Langkah bertahan 180 hari pertama saat reputasi dan modal sirna."),
        Category("cat-4", "Personal Branding Otentik", "personal-branding-otentik", "Membangun otoritas berbasis keahlian nyata dan integritas jangka panjang."),
        Category("cat-5", "Storytelling & Penulisan Narasi", "storytelling-dan-narasi", "Menata pengalaman pahit menjadi pembelajaran bernilai bagi orang lain."),
        Category("cat-6", "Prinsip Hidup & Pengambilan Keputusan", "prinsip-hidup-keputusan", "Kerangka berpikir tenang di tengah ketidakpastian ekstrem.")
    )

    private val initialUser = User(
        id = "usr-001",
        email = "member@argonclub.id",
        displayName = "Dimas Prasetyo",
        role = UserRole.FREE_MEMBER,
        avatarUrl = null,
        isEmailVerified = true,
        memberSince = "September 2024",
        biography = "Founder agensi digital & pembelajar yang sedang membangun kembali portofolio profesional.",
        learningGoals = listOf("Rekonstruksi narasi bisnis", "Manajemen risiko krisis", "Personal branding tanpa sensasi")
    )

    private val _currentUser = MutableStateFlow(initialUser)
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _bookmarks = MutableStateFlow<Set<String>>(setOf("cnt-001", "cnt-003"))
    val bookmarks: StateFlow<Set<String>> = _bookmarks.asStateFlow()

    private val _learningProgress = MutableStateFlow<Map<String, Int>>(mapOf("cnt-001" to 65, "cnt-002" to 20))
    val learningProgress: StateFlow<Map<String, Int>> = _learningProgress.asStateFlow()

    private val _ownedProductIds = MutableStateFlow<Set<String>>(emptySet())
    val ownedProductIds: StateFlow<Set<String>> = _ownedProductIds.asStateFlow()

    private val _activeSubscription = MutableStateFlow<Subscription?>(null)
    val activeSubscription: StateFlow<Subscription?> = _activeSubscription.asStateFlow()

    val availablePlans = listOf(
        Plan(
            id = "plan-monthly",
            name = "Keanggotaan Premium Bulanan",
            intervalLabel = "bulan",
            priceIdr = 149000L,
            originalPriceIdr = 199000L,
            benefits = listOf(
                "Akses tanpa batas seluruh Ebook & Audio Lessons",
                "Episode podcast eksklusif & transkrip mendalam",
                "Koleksi template narasi dan studi kasus founder",
                "Diskon Rp 250.000 untuk setiap sesi konsultasi 1-on-1",
                "Partisipasi dalam forum tanya-jawab tertutup bulanan"
            ),
            isPopular = false
        ),
        Plan(
            id = "plan-annual",
            name = "Keanggotaan Premium Tahunan",
            intervalLabel = "tahun",
            priceIdr = 1490000L,
            originalPriceIdr = 2388000L,
            benefits = listOf(
                "Hemat biaya 2 bulan penuh dibanding paket bulanan",
                "Seluruh benefit paket Premium bulanan",
                "1x Sesi Audit Portofolio / Konsultasi 45 Menit Gratis",
                "Akses prioritas rilis buku & template baru sebelum publik",
                "Sertifikat keanggotaan privat ARGON Club"
            ),
            isPopular = true
        )
    )

    val consultationServices = listOf(
        ConsultationService(
            id = "srv-001",
            title = "1-on-1 Failure Recovery & Strategic Mentorship",
            description = "Sesi privat 60 menit menganalisis krisis bisnis atau karir Anda, memetakan risiko emosional dan reputasi, serta merancang rencana pemulihan 90 hari yang realistis.",
            durationMinutes = 60,
            priceIdr = 750000L,
            memberDiscountPriceIdr = 500000L,
            preparationNotes = "Siapkan ringkasan singkat linimasa peristiwa, data finansial/operasional terkait, dan 3 keputusan tersulit yang sedang Anda hadapi."
        ),
        ConsultationService(
            id = "srv-002",
            title = "Personal Brand Narrative & Storytelling Audit",
            description = "Bedah mendalam profil profesional, cara bercerita, dan positioning Anda agar dipercaya pasar tanpa harus memamerkan pencapaian semu.",
            durationMinutes = 45,
            priceIdr = 550000L,
            memberDiscountPriceIdr = 350000L,
            preparationNotes = "Kirim tautan LinkedIn, draf bio, atau contoh konten yang pernah Anda tulis sebelum sesi berlangsung."
        )
    )

    val availableSlots = listOf(
        AvailabilitySlot("slot-1", "Senin, 21 Oktober 2024", "13:00 - 14:00 WIB", true),
        AvailabilitySlot("slot-2", "Senin, 21 Oktober 2024", "15:30 - 16:30 WIB", true),
        AvailabilitySlot("slot-3", "Rabu, 23 Oktober 2024", "10:00 - 11:00 WIB", true),
        AvailabilitySlot("slot-4", "Rabu, 23 Oktober 2024", "14:00 - 15:00 WIB", true),
        AvailabilitySlot("slot-5", "Jumat, 25 Oktober 2024", "16:00 - 17:00 WIB", true)
    )

    private val _consultationBookings = MutableStateFlow<List<ConsultationBooking>>(
        listOf(
            ConsultationBooking(
                id = "bok-101",
                serviceId = "srv-002",
                serviceTitle = "Personal Brand Narrative & Storytelling Audit",
                date = "Rabu, 23 Oktober 2024",
                time = "10:00 - 11:00 WIB",
                userName = "Dimas Prasetyo",
                userEmail = "member@argonclub.id",
                intakeGoal = "Menata ulang reputasi pasca penutupan startup logistik.",
                intakeChallenges = "Sulit menjelaskan kegagalan tanpa terkesan defensif atau kehilangan kredibilitas klien baru.",
                status = "Terkonfirmasi",
                meetingUrl = "https://meet.google.com/arg-on24-ses"
            )
        )
    )
    val consultationBookings: StateFlow<List<ConsultationBooking>> = _consultationBookings.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(
        listOf(
            Order(
                id = "ord-901",
                orderNumber = "ARG-202410-0012",
                date = "10 Oktober 2024",
                itemTitle = "Sesi 1-on-1 Personal Brand Audit",
                amountIdr = 350000L,
                status = "Berhasil"
            )
        )
    )
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(
        listOf(
            NotificationItem(
                id = "notif-1",
                title = "Sesi Konsultasi Terkonfirmasi",
                message = "Jadwal sesi 'Personal Brand Audit' Anda telah disetujui untuk Rabu, 23 Oktober 2024 pukul 10:00 WIB.",
                date = "10 Okt 2024",
                isRead = false
            ),
            NotificationItem(
                id = "notif-2",
                title = "Esai Baru Diterbitkan",
                message = "Raden Aria menerbitkan esai reflektif: 'Mengapa Kegagalan Pertama Menyelamatkan Saya'.",
                date = "08 Okt 2024",
                isRead = true
            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(
        listOf(
            AuditLog("aud-01", "system", "AUTH_VERIFY_EMAIL", "Email verified for user member@argonclub.id", "10 Okt 2024 09:12 WIB"),
            AuditLog("aud-02", "admin", "CONTENT_PUBLISHED", "Published ebook 'Arsitektur Narasi & Reputasi Profesional'", "12 Okt 2024 14:30 WIB")
        )
    )
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private val _audioPlayerState = MutableStateFlow(AudioPlayerState())
    val audioPlayerState: StateFlow<AudioPlayerState> = _audioPlayerState.asStateFlow()

    private val initialContents = listOf(
        ContentItem(
            id = "cnt-001",
            title = "Pelajaran Nyata dari Kegagalan Rp 2 Miliar: Mengurai Malu & Menemukan Suara Baru",
            slug = "pelajaran-nyata-dari-kegagalan-rp-2-miliar",
            summary = "Refleksi terbuka dan terukur tentang kebangkrutan retail pertama saya. Tanpa dramatisasi, hanya dokumentasi keputusan yang salah, beban psikologis, dan cara bangkit tanpa kepalsuan.",
            body = """
                Ketika saldo rekening operasional bisnis saya menyentuh angka nol di akhir tahun 2021 dengan kewajiban utang tersisa Rp 2,1 Miliar, hal pertama yang melumpuhkan saya bukanlah angka utangnya, melainkan rasa malu yang mencekik.
                
                Selama bertahun-tahun, identitas saya melekat pada status 'entrepreneur muda sukses'. Diundang berbicara, dipuji kerabat, dan dihormati karyawan. Ketika roda itu berhenti seketika, saya menyadari kebenaran pahit: saya telah membangun bisnis di atas asumsi pertumbuhan pasar tanpa manajemen arus kas cadangan.
                
                1. Membedakan Kegagalan Keputusan dengan Kegagalan Karakter
                Kesalahan fatal pertama yang dilakukan founder yang jatuh adalah mengidentifikasikan dirinya sebagai kegagalan itu sendiri. Bisnis Anda bisa gagal karena asumsi unit economics yang keliru. Namun integritas, kejujuran, dan kemauan Anda menatap mata kreditur adalah ukuran karakter sejati.
                
                2. Menghapus Kepalsuan Sosial
                Langkah pertama rekonsiliasi adalah berhenti berpura-pura baik-baik saja di media sosial. Saya menutup diri selama 90 hari bukan untuk bersembunyi, melainkan untuk menyusun neraca kebenaran: siapa saja yang dirugikan, berapa nilai riil kewajiban, dan apa keterampilan fundamental yang masih saya miliki untuk menghasilkan uang kembali.
                
                3. Nilai yang Tumbuh dari Kehancuran
                Kredibilitas hari ini bukan lahir dari klaim bahwa saya orang suci yang tak pernah salah. Kredibilitas lahir karena saya telah berjalan melewati puing-puing kehancuran itu, membayarnya dengan keringat dan waktu, serta menolak menjual resep instan kepada orang lain.
            """.trimIndent(),
            contentType = ContentType.FOUNDER_STORY,
            accessType = AccessType.FREE,
            categoryId = "cat-1",
            readTimeMinutes = 8,
            publicationDate = "14 Oktober 2024",
            isFeatured = true
        ),
        ContentItem(
            id = "cnt-002",
            title = "Buku Panduan: Arsitektur Narasi & Reputasi Profesional",
            slug = "arsitektur-narasi-dan-reputasi-profesional",
            summary = "Buku kerja komprehensif 4 bab untuk founder, manajer, dan konsultan dalam menyusun cerita profesional berbasis fakta teruji.",
            body = "Buku panduan lengkap dengan latihan mandiri untuk merestrukturisasi narasi profesional Anda.",
            contentType = ContentType.EBOOK,
            accessType = AccessType.PREMIUM,
            categoryId = "cat-5",
            readTimeMinutes = 45,
            publicationDate = "11 Oktober 2024",
            chapters = listOf(
                EbookChapter(
                    "Bab 1: Anatomi Kepercayaan Publik",
                    12,
                    "Mengapa pasar modern semakin kebal terhadap klaim bombastis. Data menunjukkan audiens di Indonesia memiliki tingkat skeptisisme tertinggi terhadap influencer yang hanya memamerkan gaya hidup mewah. Kepercayaan otentik terbangun saat Anda bersedia menunjukkan proses di balik layar, termasuk keraguan dan ketidakpastian yang Anda kelola."
                ),
                EbookChapter(
                    "Bab 2: Mengubah Titik Nadir Menjadi Aset Pembelajaran",
                    15,
                    "Cara membingkai kegagalan masa lalu tanpa terkesan mengeluh (whining) atau mencari simpati murahan. Kerangka kerja 3 langkah: Dokumentasi Fakta, Ekstraksi Prinsip, dan Implementasi ke Keputusan Saat Ini."
                ),
                EbookChapter(
                    "Bab 3: Format Penulisan Narasi Tanpa Jargon",
                    10,
                    "Gunakan Bahasa Indonesia yang jernih, lugas, dan bersahaja. Hindari istilah asing yang hanya berfungsi untuk mengintimidasi pembaca. Tulislah seolah Anda sedang berbicara dengan kawan lama di sebuah kedai kopi yang sunyi."
                ),
                EbookChapter(
                    "Bab 4: Memelihara Batasan Privasi vs Transparansi Publik",
                    8,
                    "Otentisitas tidak berarti membuka seluruh isi kamar tidur Anda ke publik. Pelajari garis batas tegas antara apa yang bermanfaat untuk dibagikan dan apa yang harus tetap menjadi ruang privat Anda."
                )
            )
        ),
        ContentItem(
            id = "cnt-003",
            title = "Episode 01: Mengapa Kegagalan Pertama Menyelamatkan Saya",
            slug = "podcast-eps-01-mengapa-kegagalan-pertama-menyelamatkan-saya",
            summary = "Percakapan intim berdurasi 24 menit mengupas titik terendah saat pabrik pertama harus ditutup, dan bagaimana rasa malu berubah menjadi kejelasan tujuan.",
            body = "Dengarkan audio episode penuh di pemutar bawah bersama transkrip lengkap.",
            contentType = ContentType.PODCAST,
            accessType = AccessType.FREE,
            categoryId = "cat-2",
            readTimeMinutes = 24,
            audioDurationSeconds = 1458,
            publicationDate = "05 Oktober 2024",
            transcript = """
                [00:00] Selamat datang di podcast ARGON. Saya Raden Aria.
                [01:15] Hari ini saya ingin menceritakan hari di mana saya harus menandatangani berita acara penutupan pabrik kami di Cikarang.
                [03:40] Banyak orang bertanya, apa yang paling menyakitkan saat usaha bangkrut? Uang yang hilang? Tidak. Uang bisa dicari lagi dengan tenaga dan kesehatan yang ada.
                [08:20] Yang paling menusuk adalah saat Anda harus melihat mata 24 staf Anda, yang sebagian besar sudah bersama Anda sejak awal, dan mengatakan bahwa perusahaan tidak lagi sanggup memperpanjang kontrak mereka bulan depan.
                [14:10] Saat itu saya sadar, ego saya yang terlalu besar ingin terlihat ekspansif telah mengorbankan stabilitas orang-orang yang mempercayai saya.
                [19:45] Hari itu menjadi hari kematian kesombongan saya. Dan hari itu pulalah proses belajar yang sesungguhnya baru dimulai.
                [23:30] Terima kasih telah mendengarkan. Sampai jumpa di episode berikutnya.
            """.trimIndent()
        ),
        ContentItem(
            id = "cnt-004",
            title = "Pelajaran Audio: 4 Kuadran Keputusan Tenang di Tengah Tekanan",
            slug = "audio-4-kuadran-keputusan-tenang",
            summary = "Audio lesson berdurasi 18 menit berisi formula mental untuk mengambil keputusan rasional saat menghadapi ancaman finansial atau hukum.",
            body = "Khusus Member Premium. Panduan taktis menangani krisis secara bertahap.",
            contentType = ContentType.AUDIO_LESSON,
            accessType = AccessType.PREMIUM,
            categoryId = "cat-6",
            readTimeMinutes = 18,
            audioDurationSeconds = 1120,
            publicationDate = "01 Oktober 2024",
            transcript = """
                [00:00] Audio Lesson ARGON: 4 Kuadran Keputusan Tenang.
                [02:10] Kuadran 1: Yang Berada Dalam Kendali Penuh (Tindakan, Respon, Integritas Anda).
                [06:30] Kuadran 2: Yang Berada Dalam Pengaruh (Komunikasi dengan Kreditur, Negosiasi).
                [11:00] Kuadran 3: Yang Di Luar Kendali (Opini Publik, Reaksi Pasar, Keputusan Pihak Ketiga).
                [15:20] Kunci ketenangan adalah mencurahkan 90% energi Anda hanya pada Kuadran 1 dan 2.
            """.trimIndent()
        ),
        ContentItem(
            id = "cnt-005",
            title = "Template Narasi: Mengurai Kegagalan Menjadi Pertumbuhan",
            slug = "template-narasi-mengurai-kegagalan",
            summary = "Template struktur cerita interaktif 7 elemen (Hook, Konteks, Konflik, Titik Balik, Pelajaran, Aksi Nyata, Refleksi) siap pakai.",
            body = "Gunakan template terstruktur ini untuk menyusun postingan, esai, atau materi presentasi tentang pengalaman bisnis Anda.",
            contentType = ContentType.STORYTELLING_TEMPLATE,
            accessType = AccessType.FREE,
            categoryId = "cat-5",
            readTimeMinutes = 12,
            publicationDate = "28 September 2024",
            templatePrompt = StorytellingTemplate(
                id = "tpl-01",
                title = "Struktur Narasi: Krisis Menuju Kejelasan",
                objective = "Membagikan kegagalan profesional dengan kredibel dan penuh empati tanpa terkesan mencari validasi murahan.",
                hookGuide = "Tuliskan 1-2 kalimat pembuka yang menyebut fakta yang mengejutkan atau paradoks, bukan pamer hasil.",
                contextGuide = "Jelaskan situasi awal dengan jujur: apa tujuan Anda saat itu dan apa asumsi yang mendasarinya?",
                conflictGuide = "Ceritakan momen di mana asumsi tersebut bertabrakan dengan kenyataan keras.",
                turningPointGuide = "Apa keputusan sulit yang terpaksa Anda ambil? Tindakan apa yang mengubah arah Anda?",
                lessonLearnedGuide = "Ekstraksi 1 prinsip universal yang Anda pelajari langsung dari rasa sakit tersebut.",
                practicalTakeawayGuide = "Apa saran praktis yang dapat langsung diterapkan oleh pembaca hari ini agar tidak mengulangi kesalahan serupa?",
                closingReflectionGuide = "Tutup dengan refleksi bersahaja tentang perjalanan Anda yang masih terus berproses.",
                exampleText = "Contoh: Di tahun 2022 saya kehilangan 60% klien dalam tempo 2 bulan. Bukan karena krisis moneter, melainkan karena saya lupa menjaga hubungan personal dengan mereka..."
            )
        ),
        ContentItem(
            id = "cnt-006",
            title = "Masterclass Standalone: Menetapkan Tarif Otentik Konsultan & Kreator",
            slug = "masterclass-menetapkan-tarif-otentik",
            summary = "Ebook mendalam & lembar kalkulasi harga profesional untuk mematok fee jasa tanpa rasa bersalah dan tanpa overpromising.",
            body = "Materi premium mandiri yang dapat dibeli terpisah seumur hidup atau diakses langsung oleh Member Premium.",
            contentType = ContentType.EBOOK,
            accessType = AccessType.PURCHASE_ONLY,
            categoryId = "cat-4",
            readTimeMinutes = 60,
            standalonePriceIdr = 149000L,
            publicationDate = "15 September 2024",
            chapters = listOf(
                EbookChapter("Bab 1: Menghitung Biaya Riil Keahlian Anda", 15, "Perhitungan matematis jam kerja, biaya riset, dan nilai risiko yang Anda tanggung."),
                EbookChapter("Bab 2: Menolak Klien yang Menuntut Hasil Garansi Semu", 20, "Cara diplomatis menolak proyek yang menjanjikan hasil tidak masuk akal."),
                EbookChapter("Bab 3: Surat Penawaran & Kontrak Jasa Transparan", 25, "Klausul penting perlindungan profesional di Indonesia.")
            )
        )
    )

    private val _contents = MutableStateFlow<List<ContentItem>>(initialContents)
    val contents: StateFlow<List<ContentItem>> = _contents.asStateFlow()

    fun switchRole(newRole: UserRole) {
        _currentUser.update { it.copy(role = newRole) }
        logAudit("ROLE_SWITCHED", "Peran pengguna diubah menjadi ${newRole.label}")
    }

    fun checkContentAccess(content: ContentItem): ContentAccessResult {
        val user = _currentUser.value
        // Admins have universal access
        if (user.role == UserRole.ADMIN || user.role == UserRole.CONTENT_EDITOR) {
            return ContentAccessResult.Granted
        }
        // Free content is open to all
        if (content.accessType == AccessType.FREE) {
            return ContentAccessResult.Granted
        }
        // Premium subscribers access both PREMIUM and included items
        if (user.role == UserRole.PREMIUM_MEMBER) {
            return ContentAccessResult.Granted
        }
        // Check standalone purchase
        if (_ownedProductIds.value.contains(content.id)) {
            return ContentAccessResult.Granted
        }
        // Otherwise, locked
        return if (content.accessType == AccessType.PURCHASE_ONLY) {
            ContentAccessResult.RequiresPurchase(
                message = "Konten ini dapat dibeli terpisah atau diakses gratis dengan paket Premium.",
                priceIdr = content.standalonePriceIdr
            )
        } else {
            ContentAccessResult.RequiresUpgrade(
                message = "Konten eksklusif untuk Member Premium ARGON Club.",
                planPriceMonthlyIdr = 149000L
            )
        }
    }

    fun toggleBookmark(contentId: String) {
        _bookmarks.update { current ->
            if (current.contains(contentId)) current - contentId else current + contentId
        }
    }

    fun updateProgress(contentId: String, percent: Int) {
        _learningProgress.update { current ->
            current + (contentId to percent.coerceIn(0, 100))
        }
    }

    fun playAudio(content: ContentItem) {
        _audioPlayerState.update {
            it.copy(
                contentItem = content,
                isPlaying = true,
                positionSeconds = 0,
                durationSeconds = content.audioDurationSeconds.takeIf { s -> s > 0 } ?: 900
            )
        }
    }

    fun toggleAudioPlayPause() {
        _audioPlayerState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun seekAudio(newPositionSeconds: Int) {
        _audioPlayerState.update {
            it.copy(positionSeconds = newPositionSeconds.coerceIn(0, it.durationSeconds))
        }
    }

    fun cyclePlaybackSpeed() {
        val speeds = listOf(1.0f, 1.25f, 1.5f, 0.75f)
        _audioPlayerState.update {
            val nextIdx = (speeds.indexOf(it.playbackSpeed) + 1) % speeds.size
            it.copy(playbackSpeed = speeds[nextIdx])
        }
    }

    fun toggleTranscript() {
        _audioPlayerState.update { it.copy(showTranscript = !it.showTranscript) }
    }

    fun stopAudio() {
        _audioPlayerState.update { AudioPlayerState() }
    }

    fun subscribePlan(plan: Plan) {
        _currentUser.update { it.copy(role = UserRole.PREMIUM_MEMBER) }
        val subscription = Subscription(
            id = "sub-${UUID.randomUUID().toString().take(8)}",
            planId = plan.id,
            planName = plan.name,
            status = "Aktif",
            renewalDate = "14 November 2024",
            priceIdr = plan.priceIdr
        )
        _activeSubscription.value = subscription
        val order = Order(
            id = "ord-${UUID.randomUUID().toString().take(6)}",
            orderNumber = "ARG-${System.currentTimeMillis().toString().takeLast(6)}",
            date = "Hari ini",
            itemTitle = plan.name,
            amountIdr = plan.priceIdr,
            status = "Berhasil"
        )
        _orders.update { listOf(order) + it }
        logAudit("SUBSCRIPTION_CREATED", "Berlangganan ${plan.name} senilai ${Formatters.formatRupiah(plan.priceIdr)}")
        addNotification("Selamat Datang di Premium", "Keanggotaan Anda kini aktif. Nikmati akses penuh seluruh materi dan diskon konsultasi.")
    }

    fun cancelSubscription() {
        _activeSubscription.update { it?.copy(status = "Dibatalkan (Aktif hingga akhir periode)") }
        logAudit("SUBSCRIPTION_CANCELLED", "Pengguna membatalkan perpanjangan otomatis langganan.")
        addNotification("Perpanjangan Dibatalkan", "Langganan Anda tidak akan diperpanjang setelah tanggal jatuh tempo.")
    }

    fun purchaseStandaloneContent(content: ContentItem) {
        _ownedProductIds.update { it + content.id }
        val order = Order(
            id = "ord-${UUID.randomUUID().toString().take(6)}",
            orderNumber = "ARG-ITEM-${System.currentTimeMillis().toString().takeLast(6)}",
            date = "Hari ini",
            itemTitle = content.title,
            amountIdr = content.standalonePriceIdr,
            status = "Berhasil"
        )
        _orders.update { listOf(order) + it }
        logAudit("ONE_TIME_PURCHASE", "Pembelian mandiri: ${content.title} senilai ${Formatters.formatRupiah(content.standalonePriceIdr)}")
        addNotification("Pembelian Sukses", "Materi '${content.title}' kini tersedia seumur hidup di Koleksi Anda.")
    }

    fun bookConsultation(
        service: ConsultationService,
        slot: AvailabilitySlot,
        goal: String,
        challenges: String
    ): ConsultationBooking {
        val user = _currentUser.value
        val booking = ConsultationBooking(
            id = "bok-${UUID.randomUUID().toString().take(6)}",
            serviceId = service.id,
            serviceTitle = service.title,
            date = slot.date,
            time = slot.time,
            userName = user.displayName,
            userEmail = user.email,
            intakeGoal = goal,
            intakeChallenges = challenges,
            status = "Terkonfirmasi",
            meetingUrl = "https://meet.google.com/arg-${UUID.randomUUID().toString().take(6)}"
        )
        _consultationBookings.update { listOf(booking) + it }
        val isDiscount = user.role == UserRole.PREMIUM_MEMBER
        val finalPrice = if (isDiscount) service.memberDiscountPriceIdr else service.priceIdr
        val order = Order(
            id = "ord-${UUID.randomUUID().toString().take(6)}",
            orderNumber = "ARG-BOK-${System.currentTimeMillis().toString().takeLast(6)}",
            date = "Hari ini",
            itemTitle = "Sesi 1-on-1: ${service.title}",
            amountIdr = finalPrice,
            status = "Berhasil"
        )
        _orders.update { listOf(order) + it }
        logAudit("CONSULTATION_BOOKED", "Booking sesi ${service.title} pada ${slot.date} (${slot.time})")
        addNotification("Booking Dikonfirmasi", "Sesi konsultasi Anda pada ${slot.date} telah tercatat. Tautan Google Meet telah diterbitkan.")
        return booking
    }

    fun addContent(content: ContentItem) {
        _contents.update { listOf(content) + it }
        logAudit("CONTENT_CREATED", "Konten baru '${content.title}' dibuat oleh admin.")
        addNotification("Materi Baru Dirilis", content.title)
    }

    fun deleteContent(contentId: String) {
        val item = _contents.value.find { it.id == contentId }
        _contents.update { it.filterNot { c -> c.id == contentId } }
        logAudit("CONTENT_DELETED", "Konten '${item?.title ?: contentId}' dihapus.")
    }

    private fun logAudit(action: String, details: String) {
        val entry = AuditLog(
            id = "aud-${UUID.randomUUID().toString().take(6)}",
            actor = _currentUser.value.displayName,
            action = action,
            details = details,
            timestamp = "Hari ini, 14:02 WIB"
        )
        _auditLogs.update { listOf(entry) + it }
    }

    private fun addNotification(title: String, message: String) {
        val notif = NotificationItem(
            id = "notif-${UUID.randomUUID().toString().take(6)}",
            title = title,
            message = message,
            date = "Hari ini",
            isRead = false
        )
        _notifications.update { listOf(notif) + it }
    }
}
