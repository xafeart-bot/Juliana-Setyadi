package com.example.argon.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedEbookDao {

    @Query("SELECT * FROM cached_ebooks ORDER BY cached_at_timestamp DESC")
    fun getAllCachedEbooks(): Flow<List<CachedEbookEntity>>

    @Query("SELECT * FROM cached_ebooks WHERE content_id = :contentId LIMIT 1")
    fun getCachedEbookById(contentId: String): Flow<CachedEbookEntity?>

    @Query("SELECT * FROM cached_ebooks WHERE content_id = :contentId LIMIT 1")
    suspend fun getCachedEbookDirect(contentId: String): CachedEbookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateEbook(ebook: CachedEbookEntity)

    @Query("DELETE FROM cached_ebooks WHERE content_id = :contentId")
    suspend fun deleteEbookById(contentId: String)

    @Query("DELETE FROM cached_ebooks")
    suspend fun clearAllCachedEbooks()

    @Query("SELECT COUNT(*) FROM cached_ebooks")
    fun getCachedEbooksCount(): Flow<Int>
}

@Dao
interface PodcastProgressDao {

    @Query("SELECT * FROM podcast_progress ORDER BY updated_at_timestamp DESC")
    fun getAllPodcastProgress(): Flow<List<PodcastProgressEntity>>

    @Query("SELECT * FROM podcast_progress WHERE content_id = :contentId LIMIT 1")
    fun getProgressByContentId(contentId: String): Flow<PodcastProgressEntity?>

    @Query("SELECT * FROM podcast_progress WHERE content_id = :contentId LIMIT 1")
    suspend fun getProgressDirect(contentId: String): PodcastProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: PodcastProgressEntity)

    @Query("DELETE FROM podcast_progress WHERE content_id = :contentId")
    suspend fun deleteProgressById(contentId: String)

    @Query("DELETE FROM podcast_progress")
    suspend fun clearAllProgress()

    @Query("SELECT * FROM podcast_progress WHERE is_completed = 0 ORDER BY updated_at_timestamp DESC")
    fun getInProgressPodcasts(): Flow<List<PodcastProgressEntity>>
}

@Dao
interface LocalBookmarkDao {

    @Query("SELECT * FROM local_bookmarks ORDER BY saved_at_timestamp DESC")
    fun getAllBookmarks(): Flow<List<LocalBookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM local_bookmarks WHERE content_id = :contentId)")
    fun isBookmarked(contentId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: LocalBookmarkEntity)

    @Query("DELETE FROM local_bookmarks WHERE content_id = :contentId")
    suspend fun deleteBookmarkById(contentId: String)

    @Query("DELETE FROM local_bookmarks")
    suspend fun clearAllBookmarks()
}
