package com.example.argon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun LibraryScreen(
    user: User,
    contents: List<ContentItem>,
    bookmarkedIds: Set<String>,
    progressMap: Map<String, Int>,
    ownedProductIds: Set<String>,
    onSelectContent: (ContentItem) -> Unit,
    onExploreClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Tersimpan", "Dalam Proses", "Dimiliki", "Akses Langganan")

    val bookmarkedItems = contents.filter { bookmarkedIds.contains(it.id) }
    val inProgressItems = contents.filter { (progressMap[it.id] ?: 0) in 1..99 }
    val ownedItems = contents.filter { ownedProductIds.contains(it.id) }
    val subscriptionItems = if (user.role == UserRole.PREMIUM_MEMBER) {
        contents.filter { it.accessType == AccessType.PREMIUM }
    } else emptyList()

    val currentItems = when (selectedTab) {
        0 -> bookmarkedItems
        1 -> inProgressItems
        2 -> ownedItems
        else -> subscriptionItems
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("library_screen")
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text(
                text = "Koleksi Saya",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pantau perkembangan belajar, simpanan esai, dan produk yang Anda miliki.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 20.dp,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outline) }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) MutedBronze else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Distinction Banner for Tab 2 vs Tab 3
        if (selectedTab == 2) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hak Milik Seumur Hidup: Produk di tab ini tetap dapat Anda akses selamanya tanpa terikat masa aktif langganan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (selectedTab == 3) {
            Surface(
                color = BronzeContainerLight,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = BronzeDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Akses Langganan: Materi premium aktif selama keanggotaan Anda berjalan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = BronzeDark
                    )
                }
            }
        }

        // List
        if (currentItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = when (selectedTab) {
                            0 -> Icons.Outlined.BookmarkBorder
                            1 -> Icons.Outlined.TrendingUp
                            2 -> Icons.Outlined.ShoppingBag
                            else -> Icons.Outlined.WorkspacePremium
                        },
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when (selectedTab) {
                            0 -> "Belum Ada Materi Tersimpan"
                            1 -> "Belum Ada Pembelajaran Aktif"
                            2 -> "Belum Ada Pembelian Mandiri"
                            else -> "Keanggotaan Premium Belum Aktif"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (selectedTab) {
                            0 -> "Klik ikon bookmark di materi mana saja untuk menyimpannya di sini."
                            1 -> "Mulai membaca atau mendengarkan konten untuk melacak progres."
                            2 -> "Ebook mandiri yang Anda beli akan muncul di sini seumur hidup."
                            else -> "Tingkatkan keanggotaan ke Premium untuk membuka seluruh katalog eksklusif."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onExploreClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Charcoal800, contentColor = WarmIvory),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Jelajahi Katalog")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(currentItems) { item ->
                    val progress = progressMap[item.id] ?: 0

                    Surface(
                        onClick = { onSelectContent(item) },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("library_item_${item.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.contentType.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedBronze
                                )
                                if (progress > 0) {
                                    Text(
                                        text = "$progress% Selesai",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (progress > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
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
}
