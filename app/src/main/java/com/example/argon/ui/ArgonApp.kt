package com.example.argon.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.argon.data.ArgonRepository
import com.example.argon.model.*
import com.example.argon.ui.components.*
import com.example.argon.ui.screens.*
import com.example.ui.theme.*

enum class AppScreen {
    HOME,
    EXPLORE,
    LIBRARY,
    PROFILE,
    CONTENT_DETAIL,
    CONSULTATION_BOOKING,
    ADMIN_DASHBOARD
}

@Composable
fun ArgonApp() {
    val context = LocalContext.current

    // Repository reactive states
    val user by ArgonRepository.currentUser.collectAsStateWithLifecycle()
    val contents by ArgonRepository.contents.collectAsStateWithLifecycle()
    val bookmarks by ArgonRepository.bookmarks.collectAsStateWithLifecycle()
    val progressMap by ArgonRepository.learningProgress.collectAsStateWithLifecycle()
    val ownedProductIds by ArgonRepository.ownedProductIds.collectAsStateWithLifecycle()
    val activeSubscription by ArgonRepository.activeSubscription.collectAsStateWithLifecycle()
    val orders by ArgonRepository.orders.collectAsStateWithLifecycle()
    val bookings by ArgonRepository.consultationBookings.collectAsStateWithLifecycle()
    val notifications by ArgonRepository.notifications.collectAsStateWithLifecycle()
    val auditLogs by ArgonRepository.auditLogs.collectAsStateWithLifecycle()
    val audioState by ArgonRepository.audioPlayerState.collectAsStateWithLifecycle()

    // Navigation state
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedContent by remember { mutableStateOf<ContentItem?>(null) }

    // Dialog & Sheet states
    var showRoleSwitcher by remember { mutableStateOf(false) }
    var showOnboarding by remember { mutableStateOf(false) }
    var showPaywall by remember { mutableStateOf(false) }
    var paywallTargetContent by remember { mutableStateOf<ContentItem?>(null) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    // Back handling for sub-screens
    BackHandler(enabled = currentScreen != AppScreen.HOME && currentScreen != AppScreen.EXPLORE && currentScreen != AppScreen.LIBRARY && currentScreen != AppScreen.PROFILE) {
        currentScreen = AppScreen.HOME
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (currentScreen in listOf(AppScreen.HOME, AppScreen.EXPLORE, AppScreen.LIBRARY, AppScreen.PROFILE)) {
                TopEditorialBar(
                    user = user,
                    unreadNotifCount = notifications.count { !it.isRead },
                    onRoleSwitcherClick = { showRoleSwitcher = true },
                    onNotificationClick = { showNotificationsDialog = true },
                    onAdminClick = { currentScreen = AppScreen.ADMIN_DASHBOARD }
                )
            }
        },
        bottomBar = {
            if (currentScreen in listOf(AppScreen.HOME, AppScreen.EXPLORE, AppScreen.LIBRARY, AppScreen.PROFILE)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Floating Mini Player when audio is active
                    MiniAudioPlayerBar(
                        audioState = audioState,
                        onExpandPlayer = {
                            audioState.contentItem?.let {
                                selectedContent = it
                                currentScreen = AppScreen.CONTENT_DETAIL
                            }
                        },
                        onTogglePlayPause = { ArgonRepository.toggleAudioPlayPause() },
                        onClosePlayer = { ArgonRepository.stopAudio() }
                    )

                    // Navigation Bar respecting safe navigation bar insets
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("argon_bottom_navigation")
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.HOME,
                            onClick = { currentScreen = AppScreen.HOME },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Beranda"
                                )
                            },
                            label = { Text("Beranda", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Charcoal900,
                                selectedTextColor = MutedBronze,
                                indicatorColor = MutedBronze
                            ),
                            modifier = Modifier.testTag("nav_item_home")
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.EXPLORE,
                            onClick = { currentScreen = AppScreen.EXPLORE },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == AppScreen.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore,
                                    contentDescription = "Eksplorasi"
                                )
                            },
                            label = { Text("Eksplorasi", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Charcoal900,
                                selectedTextColor = MutedBronze,
                                indicatorColor = MutedBronze
                            ),
                            modifier = Modifier.testTag("nav_item_explore")
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.LIBRARY,
                            onClick = { currentScreen = AppScreen.LIBRARY },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == AppScreen.LIBRARY) Icons.Filled.CollectionsBookmark else Icons.Outlined.CollectionsBookmark,
                                    contentDescription = "Koleksi"
                                )
                            },
                            label = { Text("Koleksi", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Charcoal900,
                                selectedTextColor = MutedBronze,
                                indicatorColor = MutedBronze
                            ),
                            modifier = Modifier.testTag("nav_item_library")
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.PROFILE,
                            onClick = { currentScreen = AppScreen.PROFILE },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == AppScreen.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Akun"
                                )
                            },
                            label = { Text("Akun", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Charcoal900,
                                selectedTextColor = MutedBronze,
                                indicatorColor = MutedBronze
                            ),
                            modifier = Modifier.testTag("nav_item_profile")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(
                    user = user,
                    contents = contents,
                    progressMap = progressMap,
                    activeSubscription = activeSubscription,
                    upcomingBookings = bookings,
                    onSelectContent = { item ->
                        selectedContent = item
                        currentScreen = AppScreen.CONTENT_DETAIL
                    },
                    onBookConsultationClick = { currentScreen = AppScreen.CONSULTATION_BOOKING },
                    onExploreClick = { currentScreen = AppScreen.EXPLORE },
                    onUpgradeClick = {
                        paywallTargetContent = null
                        showPaywall = true
                    }
                )

                AppScreen.EXPLORE -> ExploreScreen(
                    contents = contents,
                    categories = ArgonRepository.categories,
                    bookmarkedIds = bookmarks,
                    onSelectContent = { item ->
                        selectedContent = item
                        currentScreen = AppScreen.CONTENT_DETAIL
                    },
                    onToggleBookmark = { id -> ArgonRepository.toggleBookmark(id) }
                )

                AppScreen.LIBRARY -> LibraryScreen(
                    user = user,
                    contents = contents,
                    bookmarkedIds = bookmarks,
                    progressMap = progressMap,
                    ownedProductIds = ownedProductIds,
                    onSelectContent = { item ->
                        selectedContent = item
                        currentScreen = AppScreen.CONTENT_DETAIL
                    },
                    onExploreClick = { currentScreen = AppScreen.EXPLORE }
                )

                AppScreen.PROFILE -> ProfileScreen(
                    user = user,
                    activeSubscription = activeSubscription,
                    orders = orders,
                    onRoleSelect = { role ->
                        ArgonRepository.switchRole(role)
                        Toast.makeText(context, "Peran diubah ke: ${role.label}", Toast.LENGTH_SHORT).show()
                    },
                    onUpgradeClick = {
                        paywallTargetContent = null
                        showPaywall = true
                    },
                    onCancelSubscription = {
                        ArgonRepository.cancelSubscription()
                        Toast.makeText(context, "Perpanjangan otomatis langganan dibatalkan", Toast.LENGTH_LONG).show()
                    }
                )

                AppScreen.CONTENT_DETAIL -> {
                    selectedContent?.let { content ->
                        val access = ArgonRepository.checkContentAccess(content)
                        ContentDetailScreen(
                            content = content,
                            accessResult = access,
                            isBookmarked = bookmarks.contains(content.id),
                            audioState = audioState,
                            onBack = { currentScreen = AppScreen.HOME },
                            onToggleBookmark = { ArgonRepository.toggleBookmark(content.id) },
                            onPlayAudio = { item -> ArgonRepository.playAudio(item) },
                            onTogglePlayPause = { ArgonRepository.toggleAudioPlayPause() },
                            onSeekAudio = { pos -> ArgonRepository.seekAudio(pos) },
                            onCycleSpeed = { ArgonRepository.cyclePlaybackSpeed() },
                            onUpdateProgress = { p -> ArgonRepository.updateProgress(content.id, p) },
                            onUpgradeClick = {
                                paywallTargetContent = content
                                showPaywall = true
                            },
                            onPurchaseStandaloneClick = {
                                ArgonRepository.purchaseStandaloneContent(content)
                                Toast.makeText(context, "Pembelian berhasil! Materi kini terbuka.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                AppScreen.CONSULTATION_BOOKING -> ConsultationBookingScreen(
                    services = ArgonRepository.consultationServices,
                    availableSlots = ArgonRepository.availableSlots,
                    user = user,
                    onBack = { currentScreen = AppScreen.HOME },
                    onConfirmBooking = { srv, slot, goal, challenges ->
                        ArgonRepository.bookConsultation(srv, slot, goal, challenges)
                    }
                )

                AppScreen.ADMIN_DASHBOARD -> AdminDashboardScreen(
                    contents = contents,
                    bookings = bookings,
                    auditLogs = auditLogs,
                    onBack = { currentScreen = AppScreen.HOME },
                    onAddContent = { item -> ArgonRepository.addContent(item) },
                    onDeleteContent = { id -> ArgonRepository.deleteContent(id) }
                )
            }
        }
    }

    // Role Switcher Modal
    if (showRoleSwitcher) {
        RoleSwitcherBottomSheet(
            currentRole = user.role,
            onRoleSelected = { role ->
                ArgonRepository.switchRole(role)
                Toast.makeText(context, "Peran beralih ke ${role.label}", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showRoleSwitcher = false }
        )
    }

    // Onboarding Modal
    if (showOnboarding) {
        OnboardingModal(
            onDismiss = { showOnboarding = false },
            onStartMembership = {
                paywallTargetContent = null
                showPaywall = true
            }
        )
    }

    // Paywall Dialog
    if (showPaywall) {
        PaywallModal(
            contentTitle = paywallTargetContent?.title ?: "Paket Keanggotaan ARGON",
            accessMessage = "Dapatkan akses tanpa batas ke seluruh kurikulum, audio lesson, ebook, dan diskon bimbingan 1-on-1.",
            onDismiss = { showPaywall = false },
            onSelectPlan = { plan ->
                ArgonRepository.subscribePlan(plan)
                showPaywall = false
                Toast.makeText(context, "Pembayaran terverifikasi! Keanggotaan ${plan.name} kini aktif.", Toast.LENGTH_LONG).show()
            },
            onPurchaseStandalone = if (paywallTargetContent?.accessType == AccessType.PURCHASE_ONLY) {
                {
                    paywallTargetContent?.let {
                        ArgonRepository.purchaseStandaloneContent(it)
                        showPaywall = false
                        Toast.makeText(context, "Pembelian materi sukses!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else null,
            standalonePriceIdr = paywallTargetContent?.standalonePriceIdr ?: 0L
        )
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Text(
                    text = "Notifikasi & Pengumuman",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (notifications.isEmpty()) {
                        Text("Belum ada notifikasi baru.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        notifications.forEach { notif ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(text = notif.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = notif.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = notif.date, style = MaterialTheme.typography.labelSmall, color = MutedBronze)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Charcoal800, contentColor = WarmIvory)
                ) {
                    Text("Tutup")
                }
            }
        )
    }
}
