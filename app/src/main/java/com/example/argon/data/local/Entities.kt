package com.example.argon.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for storing offline cached ebooks and study materials locally on the device.
 */
@Entity(tableName = "cached_ebooks")
data class CachedEbookEntity(
    @PrimaryKey
    @ColumnInfo(name = "content_id")
    val contentId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "slug")
    val slug: String,

    @ColumnInfo(name = "author")
    val author: String,

    @ColumnInfo(name = "summary")
    val summary: String,

    @ColumnInfo(name = "chapters_payload")
    val chaptersPayload: String, // Pipe or delimited/JSON representation of chapters

    @ColumnInfo(name = "total_read_time_minutes")
    val totalReadTimeMinutes: Int,

    @ColumnInfo(name = "cached_at_timestamp")
    val cachedAtTimestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "file_size_bytes")
    val fileSizeBytes: Long = 0L,

    @ColumnInfo(name = "is_offline_available")
    val isOfflineAvailable: Boolean = true
)

/**
 * Entity for tracking podcast and audio lesson playback positions and completion status.
 */
@Entity(tableName = "podcast_progress")
data class PodcastProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = "content_id")
    val contentId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "last_position_seconds")
    val lastPositionSeconds: Int,

    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int,

    @ColumnInfo(name = "progress_percent")
    val progressPercent: Int,

    @ColumnInfo(name = "playback_speed")
    val playbackSpeed: Float = 1.0f,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "updated_at_timestamp")
    val updatedAtTimestamp: Long = System.currentTimeMillis()
)

/**
 * Entity for storing user bookmarks locally for offline reference.
 */
@Entity(tableName = "local_bookmarks")
data class LocalBookmarkEntity(
    @PrimaryKey
    @ColumnInfo(name = "content_id")
    val contentId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content_type")
    val contentType: String,

    @ColumnInfo(name = "saved_at_timestamp")
    val savedAtTimestamp: Long = System.currentTimeMillis()
)
