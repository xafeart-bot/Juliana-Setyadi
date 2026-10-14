package com.example.argon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.argon.model.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    user: User,
    contents: List<ContentItem>,
    progressMap: Map<String, Int>,
    activeSubscription: Subscription?,
    upcomingBookings: List<ConsultationBooking>,
    onSelectContent: (ContentItem) -> Unit,
    onBookConsultationClick: () -> Unit,
    onExploreClick: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    val featuredItem = contents.firstOrNull { it.isFeatured } ?: contents.firstOrNull()
    val continueLearningItems = contents.filter { progressMap.containsKey(it.id) }
    val recentItems = contents.filter { !it.isFeatured }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Personalized Editorial Greeting & Membership Status
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Refleksi Hari Ini",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedBronze,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Selamat datang, ${user.displayName.split(" ").firstOrNull() ?: "Sahabat"}",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ruang belajar otentik untuk menata kembali narasi, kepemimpinan, dan ketahanan bisnis Anda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Membership Status Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (user.role == UserRole.PREMIUM_MEMBER) Charcoal800 else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (user.role == UserRole.PREMIUM_MEMBER) MutedBronze else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (user.role == UserRole.PREMIUM_MEMBER) MutedBronze else Color(user.role.badgeColorHex).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (user.role == UserRole.PREMIUM_MEMBER) Icons.Default.WorkspacePremium else Icons.Default.PersonOutline,
                                    contentDescription = null,
                                    tint = if (user.role == UserRole.PREMIUM_MEMBER) Charcoal900 else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (user.role == UserRole.PREMIUM_MEMBER) "Status: Keanggotaan Premium Aktif" else "Status: Member Gratis",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (user.role == UserRole.PREMIUM_MEMBER) WarmIvory else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (user.role == UserRole.PREMIUM_MEMBER)
                                        "Perpanjangan berikutnya: ${activeSubscription?.renewalDate ?: "14 Nov 2024"}"
                                    else
                                        "Buka akses ke semua ebook & materi eksklusif",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (user.role == UserRole.PREMIUM_MEMBER) TextMutedDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (user.role != UserRole.PREMIUM_MEMBER && user.role != UserRole.ADMIN) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onUpgradeClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MutedBronze,
                                    contentColor = Charcoal900
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("home_upgrade_button")
                            ) {
                                Text("Upgrade", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. Upcoming Consultation Reminder (If any)
        if (upcomingBookings.isNotEmpty()) {
            item {
                val booking = upcomingBookings.first()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BronzeContainerLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MutedBronze.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MutedBronze.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = BronzeDark
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Konsultasi Mendatang",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BronzeDark,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = booking.serviceTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${booking.date} • ${booking.time}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Charcoal600
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Featured Founder Story Hero Card
        if (featuredItem != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kisah Founder Terpilih",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Serif
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MutedBronze.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "REFLEKSI UTAMA",
                                style = MaterialTheme.typography.labelSmall,
                                color = BronzeDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        onClick = { onSelectContent(featuredItem) },
                        shape = RoundedCornerShape(16.dp),
                        color = Charcoal800,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SandBorderDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("featured_story_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = featuredItem.author,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedBronze
                                )
                                Text(
                                    text = "${featuredItem.readTimeMinutes} menit baca",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedDark
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = featuredItem.title,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                lineHeight = 24.sp,
                                color = WarmIvory
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = featuredItem.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMutedDark,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoStories,
                                        contentDescription = null,
                                        tint = MutedBronze,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Akses Terbuka",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MutedBronze
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Baca Esai Penuh",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = WarmIvory
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = WarmIvory,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Continue Learning (Lanjutkan Belajar)
        if (continueLearningItems.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    Text(
                        text = "Lanjutkan Belajar",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(continueLearningItems) { item ->
                            val progress = progressMap[item.id] ?: 0
                            Surface(
                                onClick = { onSelectContent(item) },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier
                                    .width(260.dp)
                                    .testTag("continue_learning_${item.id}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = item.contentType.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MutedBronze
                                        )
                                        Text(
                                            text = "$progress%",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    LinearProgressIndicator(
                                        progress = { progress / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = MutedBronze,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. 1-on-1 Consultation CTA Banner
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Surface(
                    onClick = onBookConsultationClick,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MutedBronze.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_consultation_cta")
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "BIMBINGAN STRATEGIS 1-ON-1",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedBronze,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Hadapi Krisis Karir & Bisnis dengan Jernih",
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sesi privat 60 menit langsung bersama founder. Pemetaan masalah tanpa penghakiman.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Pilih Jadwal Sesi",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MutedBronze
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = MutedBronze,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Recently Published Resources & Podcasts
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Publikasi Terbaru",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif
                    )
                    TextButton(onClick = onExploreClick) {
                        Text("Lihat Semua", color = MutedBronze, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                recentItems.take(4).forEach { item ->
                    Surface(
                        onClick = { onSelectContent(item) },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .testTag("recent_content_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (item.contentType) {
                                        ContentType.PODCAST -> Icons.Default.Podcasts
                                        ContentType.AUDIO_LESSON -> Icons.Default.Headphones
                                        ContentType.EBOOK -> Icons.Default.MenuBook
                                        ContentType.STORYTELLING_TEMPLATE -> Icons.Default.Draw
                                        else -> Icons.Default.Article
                                    },
                                    contentDescription = null,
                                    tint = MutedBronze,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.contentType.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MutedBronze
                                    )
                                    Text(
                                        text = " • ${item.readTimeMinutes} mnt",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = item.summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            if (item.accessType == AccessType.PREMIUM) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Premium",
                                    tint = BronzeDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
