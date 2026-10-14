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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.argon.data.AudioPlayerState
import com.example.argon.data.ContentAccessResult
import com.example.argon.model.*
import com.example.ui.theme.*

@Composable
fun ContentDetailScreen(
    content: ContentItem,
    accessResult: ContentAccessResult,
    isBookmarked: Boolean,
    audioState: AudioPlayerState,
    onBack: () -> Unit,
    onToggleBookmark: () -> Unit,
    onPlayAudio: (ContentItem) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeekAudio: (Int) -> Unit,
    onCycleSpeed: () -> Unit,
    onUpdateProgress: (Int) -> Unit,
    onUpgradeClick: () -> Unit,
    onPurchaseStandaloneClick: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedChapterIndex by remember { mutableIntStateOf(0) }
    var readingFontSize by remember { mutableIntStateOf(16) }

    // Template worksheet fields
    var hookText by remember { mutableStateOf("") }
    var contextText by remember { mutableStateOf("") }
    var conflictText by remember { mutableStateOf("") }
    var turningPointText by remember { mutableStateOf("") }
    var lessonText by remember { mutableStateOf("") }
    var actionText by remember { mutableStateOf("") }
    var reflectionText by remember { mutableStateOf("") }

    val isEntitled = accessResult is ContentAccessResult.Granted

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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onToggleBookmark) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Simpan",
                                tint = if (isBookmarked) MutedBronze else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(onClick = {
                            Toast.makeText(context, "Tautan materi disalin ke papan klip", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(imageVector = Icons.Outlined.Share, contentDescription = "Bagikan")
                        }
                    }
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
                .testTag("content_detail_screen")
        ) {
            // Category & Content Type Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (content.accessType == AccessType.PREMIUM) BronzeContainerLight else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = content.accessType.label.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (content.accessType == AccessType.PREMIUM) BronzeDark else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = content.contentType.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedBronze,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = " • ${content.readTimeMinutes} menit",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = content.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Author & Date
            Text(
                text = "Oleh ${content.author} • Diterbitkan ${content.publicationDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Summary Callout
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(
                    text = content.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // PAYWALL GATE (If locked)
            if (!isEntitled) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Charcoal800,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MutedBronze),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .testTag("paywall_locked_card")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Charcoal700)
                                .border(1.dp, MutedBronze, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MutedBronze,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Materi Terkunci Khusus Member",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = WarmIvory
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = when (accessResult) {
                                is ContentAccessResult.RequiresPurchase -> accessResult.message
                                is ContentAccessResult.RequiresUpgrade -> accessResult.message
                                else -> "Konten ini membutuhkan hak akses khusus."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMutedDark,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onUpgradeClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MutedBronze,
                                contentColor = Charcoal900
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("paywall_unlock_button")
                        ) {
                            Text("Upgrade ke Premium (Mulai Rp 149.000)", fontWeight = FontWeight.Bold)
                        }

                        if (content.accessType == AccessType.PURCHASE_ONLY) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onPurchaseStandaloneClick,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Beli Terpisah: ${Formatters.formatRupiah(content.standalonePriceIdr)}", color = WarmIvory)
                            }
                        }
                    }
                }
            } else {
                // UNLOCKED CONTENT BODY: Render according to ContentType

                // 1. AUDIO / PODCAST PLAYER VIEW
                if (content.contentType == ContentType.PODCAST || content.contentType == ContentType.AUDIO_LESSON) {
                    val isThisPlaying = audioState.contentItem?.id == content.id && audioState.isPlaying

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Charcoal800,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SandBorderDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("full_audio_player_view")
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (content.contentType == ContentType.PODCAST) Icons.Default.Podcasts else Icons.Default.Headphones,
                                contentDescription = null,
                                tint = MutedBronze,
                                modifier = Modifier.size(54.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = content.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WarmIvory
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Slider
                            val curPos = if (audioState.contentItem?.id == content.id) audioState.positionSeconds else 0
                            val totalDur = if (content.audioDurationSeconds > 0) content.audioDurationSeconds else 900

                            Slider(
                                value = curPos.toFloat(),
                                onValueChange = { onSeekAudio(it.toInt()) },
                                valueRange = 0f..totalDur.toFloat(),
                                colors = SliderDefaults.colors(
                                    thumbColor = MutedBronze,
                                    activeTrackColor = MutedBronze,
                                    inactiveTrackColor = Charcoal600
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = Formatters.formatSecondsToTime(curPos),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedDark
                                )
                                Text(
                                    text = Formatters.formatSecondsToTime(totalDur),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedDark
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Playback Controls
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // 15s Rewind
                                IconButton(onClick = { onSeekAudio((curPos - 15).coerceAtLeast(0)) }) {
                                    Icon(
                                        imageVector = Icons.Default.Replay10,
                                        contentDescription = "Mundur 15 dtk",
                                        tint = WarmIvory
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Main Play / Pause
                                FloatingActionButton(
                                    onClick = {
                                        if (audioState.contentItem?.id == content.id) {
                                            onTogglePlayPause()
                                        } else {
                                            onPlayAudio(content)
                                        }
                                    },
                                    containerColor = MutedBronze,
                                    contentColor = Charcoal900,
                                    shape = CircleShape,
                                    modifier = Modifier.testTag("audio_detail_play_pause")
                                ) {
                                    Icon(
                                        imageVector = if (isThisPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                        contentDescription = if (isThisPlaying) "Jeda" else "Putar",
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // 15s Forward
                                IconButton(onClick = { onSeekAudio((curPos + 15).coerceAtMost(totalDur)) }) {
                                    Icon(
                                        imageVector = Icons.Default.Forward10,
                                        contentDescription = "Maju 15 dtk",
                                        tint = WarmIvory
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Speed Cycle Button
                                OutlinedButton(
                                    onClick = onCycleSpeed,
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Charcoal600)
                                ) {
                                    Text(
                                        text = "${audioState.playbackSpeed}x",
                                        color = WarmIvory,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Transcript Section
                    if (!content.transcript.isNullOrBlank()) {
                        Text(
                            text = "Catatan & Transkrip Lengkap",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = content.transcript,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 24.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                // 2. EBOOK MULTI-CHAPTER READER
                else if (content.contentType == ContentType.EBOOK && content.chapters.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daftar Bab Pembahasan",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Serif
                        )

                        // Font sizing control
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (readingFontSize > 13) readingFontSize -= 2 }) {
                                Text("A-", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text("$readingFontSize sp", style = MaterialTheme.typography.labelSmall)
                            IconButton(onClick = { if (readingFontSize < 22) readingFontSize += 2 }) {
                                Text("A+", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Chapter Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        content.chapters.forEachIndexed { index, chapter ->
                            Surface(
                                onClick = {
                                    selectedChapterIndex = index
                                    onUpdateProgress(((index + 1) * 100) / content.chapters.size)
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedChapterIndex == index) Charcoal800 else MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (selectedChapterIndex == index) MutedBronze else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier.testTag("chapter_tab_$index")
                            ) {
                                Text(
                                    text = "Bab ${index + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedChapterIndex == index) WarmIvory else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val activeChapter = content.chapters[selectedChapterIndex]
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = activeChapter.title,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = activeChapter.content,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = readingFontSize.sp,
                                lineHeight = (readingFontSize + 9).sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // 3. STORYTELLING INTERACTIVE TEMPLATE
                else if (content.contentType == ContentType.STORYTELLING_TEMPLATE && content.templatePrompt != null) {
                    val template = content.templatePrompt

                    Text(
                        text = "Lembar Kerja Interaktif 7 Elemen",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = template.objective,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Hook
                    TemplateField(
                        title = "1. Hook (Kalimat Pembuka Menarik)",
                        guide = template.hookGuide,
                        value = hookText,
                        onValueChange = { hookText = it }
                    )

                    // 2. Konteks
                    TemplateField(
                        title = "2. Konteks (Latar Belakang & Asumsi Awal)",
                        guide = template.contextGuide,
                        value = contextText,
                        onValueChange = { contextText = it }
                    )

                    // 3. Konflik
                    TemplateField(
                        title = "3. Konflik (Benturan dengan Kenyataan)",
                        guide = template.conflictGuide,
                        value = conflictText,
                        onValueChange = { conflictText = it }
                    )

                    // 4. Titik Balik
                    TemplateField(
                        title = "4. Titik Balik (Keputusan Krusial)",
                        guide = template.turningPointGuide,
                        value = turningPointText,
                        onValueChange = { turningPointText = it }
                    )

                    // 5. Pelajaran Hidup
                    TemplateField(
                        title = "5. Pelajaran Berharga (Prinsip Universal)",
                        guide = template.lessonLearnedGuide,
                        value = lessonText,
                        onValueChange = { lessonText = it }
                    )

                    // 6. Aksi Nyata
                    TemplateField(
                        title = "6. Aksi Nyata (Saran Praktis bagi Pembaca)",
                        guide = template.practicalTakeawayGuide,
                        value = actionText,
                        onValueChange = { actionText = it }
                    )

                    // 7. Refleksi
                    TemplateField(
                        title = "7. Refleksi Penutup (Kerendahan Hati)",
                        guide = template.closingReflectionGuide,
                        value = reflectionText,
                        onValueChange = { reflectionText = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val combined = """
                                [DRAF CERITA NARASI ARGON]
                                
                                $hookText
                                
                                $contextText
                                
                                $conflictText
                                
                                $turningPointText
                                
                                Pelajaran:
                                $lessonText
                                
                                Saran Praktis:
                                $actionText
                                
                                Refleksi:
                                $reflectionText
                            """.trimIndent()
                            clipboardManager.setText(AnnotatedString(combined))
                            Toast.makeText(context, "Draf cerita lengkap berhasil disalin!", Toast.LENGTH_SHORT).show()
                            onUpdateProgress(100)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MutedBronze,
                            contentColor = Charcoal900
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("copy_story_draft_button")
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Salin Seluruh Draf Cerita", fontWeight = FontWeight.Bold)
                    }
                }

                // 4. DEFAULT ARTICLE / FOUNDER STORY ESSAY
                else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = content.body,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 26.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            onUpdateProgress(100)
                            Toast.makeText(context, "Materi ditandai selesai!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Charcoal800,
                            contentColor = WarmIvory
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tandai Materi Selesai Dibaca")
                    }
                }
            }
        }
    }
}

@Composable
fun TemplateField(
    title: String,
    guide: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = guide,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Tuliskan narasi Anda di sini...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            minLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MutedBronze,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}
