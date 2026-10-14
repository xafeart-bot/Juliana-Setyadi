package com.example.argon.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.argon.model.*
import com.example.ui.theme.*

@Composable
fun ConsultationBookingScreen(
    services: List<ConsultationService>,
    availableSlots: List<AvailabilitySlot>,
    user: User,
    onBack: () -> Unit,
    onConfirmBooking: (ConsultationService, AvailabilitySlot, String, String) -> ConsultationBooking
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var selectedService by remember { mutableStateOf(services.first()) }
    var selectedSlot by remember { mutableStateOf(availableSlots.first()) }
    var intakeGoal by remember { mutableStateOf("") }
    var intakeChallenges by remember { mutableStateOf("") }
    var confirmedBooking by remember { mutableStateOf<ConsultationBooking?>(null) }

    val isPremiumMember = user.role == UserRole.PREMIUM_MEMBER
    val effectivePrice = if (isPremiumMember) selectedService.memberDiscountPriceIdr else selectedService.priceIdr

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Jadwalkan Konsultasi 1-on-1",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("consultation_booking_screen")
        ) {
            if (confirmedBooking != null) {
                val booking = confirmedBooking!!
                // Success Confirmation Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SuccessGreen),
                    modifier = Modifier.fillMaxWidth().testTag("booking_success_card")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Sesi Berhasil Dikonfirmasi!",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Konfirmasi jadwal dan instruksi persiapan telah dikirimkan ke email Anda (${user.email}).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(text = "Layanan: ${booking.serviceTitle}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Waktu: ${booking.date} (${booking.time})", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Tautan Rapat: ${booking.meetingUrl}", style = MaterialTheme.typography.bodySmall, color = MutedBronze)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = Charcoal800, contentColor = WarmIvory),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Kembali ke Beranda")
                        }
                    }
                }
            } else {
                // STEP 1: Pilih Layanan Konsultasi
                Text(
                    text = "1. Pilih Layanan Konsultasi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                services.forEach { service ->
                    val isSelected = selectedService.id == service.id
                    Surface(
                        onClick = { selectedService = service },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) BronzeContainerLight else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MutedBronze else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("service_option_${service.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = service.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${service.durationMinutes} menit",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedBronze
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = service.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = Formatters.formatRupiah(service.priceIdr),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (isPremiumMember) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Harga Member: ${Formatters.formatRupiah(service.memberDiscountPriceIdr)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // STEP 2: Pilih Tanggal & Slot Waktu (Asia/Jakarta)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2. Pilih Jadwal Sesi (WIB)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Zona Waktu: Asia/Jakarta",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                availableSlots.forEach { slot ->
                    val isSelected = selectedSlot.id == slot.id
                    Surface(
                        onClick = { selectedSlot = slot },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Charcoal800 else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MutedBronze else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("slot_option_${slot.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${slot.date} • ${slot.time}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) WarmIvory else MaterialTheme.colorScheme.onSurface
                            )
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

                Spacer(modifier = Modifier.height(20.dp))

                // STEP 3: Kuisioner Awal (Intake Questionnaire)
                Text(
                    text = "3. Kuisioner & Konteks Sesi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Membantu kami memahami situasi Anda agar waktu sesi 100% fokus pada pemecahan masalah.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = intakeGoal,
                    onValueChange = { intakeGoal = it },
                    label = { Text("Tujuan Utama Konsultasi") },
                    placeholder = { Text("Misal: Ingin menata ulang reputasi pasca penutupan unit usaha.") },
                    modifier = Modifier.fillMaxWidth().testTag("intake_goal_input"),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = intakeChallenges,
                    onValueChange = { intakeChallenges = it },
                    label = { Text("Tantangan & Pertanyaan Terbesar") },
                    placeholder = { Text("Misal: Bingung bagaimana menjelaskan kegagalan kepada calon klien.") },
                    modifier = Modifier.fillMaxWidth().testTag("intake_challenges_input"),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(20.dp))

                // STEP 4: Ringkasan Biaya & Kebijakan
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Investasi:", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = Formatters.formatRupiah(effectivePrice),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MutedBronze
                            )
                        }

                        if (isPremiumMember) {
                            Text(
                                text = "Termasuk diskon Member Premium (hemat Rp 200.000)",
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = selectedService.cancellationPolicy,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (intakeGoal.isBlank()) {
                            Toast.makeText(context, "Harap isi tujuan konsultasi Anda.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val booking = onConfirmBooking(selectedService, selectedSlot, intakeGoal, intakeChallenges)
                        confirmedBooking = booking
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MutedBronze,
                        contentColor = Charcoal900
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_booking_button")
                ) {
                    Icon(imageVector = Icons.Default.EventAvailable, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Konfirmasi & Amankan Jadwal",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
