package com.example.argon.data.local

import android.content.Context
import com.example.argon.model.ContentItem
import kotlinx.coroutines.flow.Flow

class ArgonLocalDataSource(context: Context) {

    private val database = ArgonLocalDatabase.getInstance(context)
    val cachedEbookDao = database.cachedEbookDao()
    val podcastProgressDao = database.podcastProgressDao()
    val localBookmarkDao = database.localBookmarkDao()

    // 1. Cached Ebooks
    fun getAllCachedEbooks(): Flow<List<CachedEbookEntity>> = cachedEbookDao.getAllCachedEbooks()

    fun getCachedEbook(contentId: String): Flow<CachedEbookEntity?> = cachedEbookDao.getCachedEbookById(contentId)

    suspend fun cacheEbook(content: ContentItem) {
        val chaptersSerialized = content.chapters.joinToString(separator = "|||") { ch ->
            "${ch.title}:::${ch.readTimeMinutes}:::${ch.content}"
        }
        val entity = CachedEbookEntity(
            contentId = content.id,
            title = content.title,
            slug = content.slug,
            author = content.author,
            summary = content.summary,
            chaptersPayload = chaptersSerialized,
            totalReadTimeMinutes = content.readTimeMinutes,
            fileSizeBytes = (content.body.length * 2L) + 1024L,
            isOfflineAvailable = true
        )
        cachedEbookDao.insertOrUpdateEbook(entity)
    }

    suspend fun removeCachedEbook(contentId: String) {
        cachedEbookDao.deleteEbookById(contentId)
    }

    // 2. Podcast & Audio Progress
    fun getAllPodcastProgress(): Flow<List<PodcastProgressEntity>> = podcastProgressDao.getAllPodcastProgress()

    fun getInProgressPodcasts(): Flow<List<PodcastProgressEntity>> = podcastProgressDao.getInProgressPodcasts()

    fun getPodcastProgress(contentId: String): Flow<PodcastProgressEntity?> =
        podcastProgressDao.getProgressByContentId(contentId)

    suspend fun savePodcastProgress(
        contentId: String,
        title: String,
        positionSeconds: Int,
        durationSeconds: Int,
        playbackSpeed: Float = 1.0f
    ) {
        val total = if (durationSeconds > 0) durationSeconds else 1
        val percent = ((positionSeconds.toLong() * 100) / total).toInt().coerceIn(0, 100)
        val completed = percent >= 95

        val entity = PodcastProgressEntity(
            contentId = contentId,
            title = title,
            lastPositionSeconds = positionSeconds,
            durationSeconds = durationSeconds,
            progressPercent = percent,
            playbackSpeed = playbackSpeed,
            isCompleted = completed,
            updatedAtTimestamp = System.currentTimeMillis()
        )
        podcastProgressDao.insertOrUpdateProgress(entity)
    }

    suspend fun deletePodcastProgress(contentId: String) {
        podcastProgressDao.deleteProgressById(contentId)
    }

    // 3. Local Bookmarks
    fun getAllBookmarks(): Flow<List<LocalBookmarkEntity>> = localBookmarkDao.getAllBookmarks()

    fun isBookmarked(contentId: String): Flow<Boolean> = localBookmarkDao.isBookmarked(contentId)

    suspend fun addBookmark(contentId: String, title: String, contentType: String) {
        localBookmarkDao.insertBookmark(
            LocalBookmarkEntity(
                contentId = contentId,
                title = title,
                contentType = contentType
            )
        )
    }

    suspend fun removeBookmark(contentId: String) {
        localBookmarkDao.deleteBookmarkById(contentId)
    }
}
