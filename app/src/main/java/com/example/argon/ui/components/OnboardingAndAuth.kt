package com.example.argon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.argon.model.UserRole
import com.example.ui.theme.*

@Composable
fun OnboardingModal(
    onDismiss: () -> Unit,
    onStartMembership: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val slides = listOf(
        Triple(
            "Belajar dari Kegagalan Nyata",
            "Bukan teori bisnis di menara gading. ARGON didirikan atas dasar kehancuran finansial nyata, rasa malu sosial, dan proses panjang membangun kembali reputasi dari nol.",
            Icons.Default.AutoStories
        ),
        Triple(
            "Kurikulum & Media Terkurasi",
            "Akses ebook panduan, podcast mendalam, pelajaran audio singkat, dan template narasi 7 elemen untuk menata kembali cerita kepemimpinan Anda.",
            Icons.Default.MenuBook
        ),
        Triple(
            "Konsultasi Privat 1-on-1",
            "Bimbingan strategis personal bersama founder untuk membedah krisis karir, keputusan sulit, dan positioning profesional Anda tanpa penghakiman.",
            Icons.Default.EventAvailable
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.padding(16.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val slide = slides[currentStep]

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Charcoal800)
                        .border(1.dp, MutedBronze, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = slide.third,
                        contentDescription = null,
                        tint = MutedBronze,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = slide.first,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = slide.second,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Dots indicator
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    slides.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .size(if (currentStep == index) 18.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (currentStep == index) MutedBronze else MaterialTheme.colorScheme.outline)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (currentStep < slides.size - 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Lewati", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { currentStep++ },
                            colors = ButtonDefaults.buttonColors(containerColor = Charcoal800, contentColor = WarmIvory),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Lanjut")
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            onDismiss()
                            onStartMembership()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MutedBronze, contentColor = Charcoal900),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("onboarding_start_button")
                    ) {
                        Text("Mulai Masuk Klub", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSwitcherBottomSheet(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .navigationBarsPadding()
                .testTag("role_switcher_sheet")
        ) {
            Text(
                text = "Pilih Peran Pengguna (Sandbox)",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Beralih peran secara dinamis untuk menguji batasan akses, katalog materi, dan hak istimewa administrator:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            UserRole.entries.forEach { role ->
                val isSelected = currentRole == role
                Surface(
                    onClick = {
                        onRoleSelected(role)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) BronzeContainerLight else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) MutedBronze else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("select_role_${role.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = role.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) BronzeDark else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when (role) {
                                    UserRole.GUEST -> "Hanya melihat landing page, preview publik, dan katalog."
                                    UserRole.FREE_MEMBER -> "Akses materi gratis, bookmark, dan pembelian mandiri."
                                    UserRole.PREMIUM_MEMBER -> "Akses tanpa batas seluruh konten, ebook, dan diskon konsultasi."
                                    UserRole.CONTENT_EDITOR -> "Membuat draf konten, mengorganisir kategori, dan preview."
                                    UserRole.ADMIN -> "Akses penuh manajemen anggota, CMS, laporan revenue, dan audit log."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MutedBronze,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
