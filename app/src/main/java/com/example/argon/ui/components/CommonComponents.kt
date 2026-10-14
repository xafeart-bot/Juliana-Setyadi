package com.example.argon.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.argon.data.ArgonRepository
import com.example.argon.data.AudioPlayerState
import com.example.argon.model.*
import com.example.ui.theme.*

@Composable
fun TopEditorialBar(
    user: User,
    unreadNotifCount: Int,
    onRoleSwitcherClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onAdminClick: (() -> Unit)? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ARGON Logo and Monogram
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onRoleSwitcherClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Charcoal800)
                        .border(1.dp, MutedBronze, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "A",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MutedBronze
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "ARGON",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "PERSONAL BRAND CLUB",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 9.sp,
                        letterSpacing = 1.2.sp,
                        color = MutedBronze
                    )
                }
            }

            // Right Actions: Role Badge Button & Notifications
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Role Switcher Pill (Critical for multi-role testing)
                Surface(
                    onClick = onRoleSwitcherClick,
                    shape = RoundedCornerShape(16.dp),
                    color = Color(user.role.badgeColorHex).copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(user.role.badgeColorHex).copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("role_badge_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(user.role.badgeColorHex))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = user.role.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Ganti peran",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                if (user.role == UserRole.ADMIN || user.role == UserRole.CONTENT_EDITOR) {
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { onAdminClick?.invoke() },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("admin_dashboard_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Panel Admin",
                            tint = MutedBronze
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("notifications_icon")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifCount > 0) {
                                Badge(
                                    containerColor = MutedBronze,
                                    contentColor = Charcoal900
                                ) {
                                    Text(unreadNotifCount.toString(), fontSize = 10.sp)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifikasi",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiniAudioPlayerBar(
    audioState: AudioPlayerState,
    onExpandPlayer: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onClosePlayer: () -> Unit
) {
    AnimatedVisibility(
        visible = audioState.contentItem != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        val item = audioState.contentItem ?: return@AnimatedVisibility

        Surface(
            color = Charcoal800,
            contentColor = WarmIvory,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandPlayer() }
                .testTag("mini_audio_player")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Progress line
                LinearProgressIndicator(
                    progress = {
                        if (audioState.durationSeconds > 0) {
                            audioState.positionSeconds.toFloat() / audioState.durationSeconds.toFloat()
                        } else 0f
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = MutedBronze,
                    trackColor = Charcoal600
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Charcoal700)
                                .border(1.dp, MutedBronze.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item.contentType == ContentType.PODCAST) Icons.Default.Podcasts else Icons.Default.Headphones,
                                contentDescription = null,
                                tint = MutedBronze,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = WarmIvory
                            )
                            Text(
                                text = "${Formatters.formatSecondsToTime(audioState.positionSeconds)} / ${Formatters.formatSecondsToTime(audioState.durationSeconds)} • ${audioState.playbackSpeed}x",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedDark
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier.testTag("mini_player_play_pause")
                        ) {
                            Icon(
                                imageVector = if (audioState.isPlaying) Icons.Filled.PauseCircleFilled else Icons.Filled.PlayCircleFilled,
                                contentDescription = if (audioState.isPlaying) "Jeda" else "Putar",
                                tint = MutedBronze,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        IconButton(
                            onClick = onClosePlayer,
                            modifier = Modifier.testTag("mini_player_close")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup Pemutar",
                                tint = TextMutedDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaywallModal(
    contentTitle: String,
    accessMessage: String,
    onDismiss: () -> Unit,
    onSelectPlan: (Plan) -> Unit,
    onPurchaseStandalone: (() -> Unit)? = null,
    standalonePriceIdr: Long = 0L
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(16.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(BronzeContainerLight)
                        .border(1.dp, MutedBronze, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Terkunci",
                        tint = BronzeDark,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Akses Khusus Anggota",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = accessMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Monthly Option Card
                Surface(
                    onClick = { onSelectPlan(ArgonRepository.availablePlans[0]) },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("paywall_monthly_plan")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Keanggotaan Bulanan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Bebas batalkan kapan saja",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Rp 149.000 / bln",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MutedBronze
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Annual Option Card (Best Value)
                Surface(
                    onClick = { onSelectPlan(ArgonRepository.availablePlans[1]) },
                    shape = RoundedCornerShape(12.dp),
                    color = BronzeContainerLight,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MutedBronze),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("paywall_annual_plan")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Keanggotaan Tahunan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Charcoal800
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MutedBronze
                            ) {
                                Text(
                                    text = "HEMAT 2 BULAN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Charcoal900,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp 1.490.000 / tahun (termasuk 1x audit 1-on-1 gratis)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = BronzeDark
                        )
                    }
                }

                if (onPurchaseStandalone != null && standalonePriceIdr > 0L) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onPurchaseStandalone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("paywall_standalone_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Beli Ebook Ini Saja: ${Formatters.formatRupiah(standalonePriceIdr)}")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onDismiss) {
                    Text("Nanti Saja", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    )
}
