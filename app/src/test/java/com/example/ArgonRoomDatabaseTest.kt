package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.argon.data.local.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ArgonRoomDatabaseTest {

    private lateinit var database: ArgonLocalDatabase
    private lateinit var cachedEbookDao: CachedEbookDao
    private lateinit var podcastProgressDao: PodcastProgressDao
    private lateinit var bookmarkDao: LocalBookmarkDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ArgonLocalDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        cachedEbookDao = database.cachedEbookDao()
        podcastProgressDao = database.podcastProgressDao()
        bookmarkDao = database.localBookmarkDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testInsertAndRetrieveCachedEbook() = runBlocking {
        val ebook = CachedEbookEntity(
            contentId = "ebk-001",
            title = "Arsitektur Narasi & Reputasi Profesional",
            slug = "arsitektur-narasi",
            author = "Raden Aria",
            summary = "Panduan pemulihan reputasi.",
            chaptersPayload = "Bab 1:::10:::Isi Bab 1|||Bab 2:::15:::Isi Bab 2",
            totalReadTimeMinutes = 25,
            fileSizeBytes = 2048L,
            isOfflineAvailable = true
        )

        cachedEbookDao.insertOrUpdateEbook(ebook)

        val retrieved = cachedEbookDao.getCachedEbookDirect("ebk-001")
        assertNotNull(retrieved)
        assertEquals("Arsitektur Narasi & Reputasi Profesional", retrieved?.title)
        assertEquals(25, retrieved?.totalReadTimeMinutes)
        assertTrue(retrieved?.isOfflineAvailable == true)

        val list = cachedEbookDao.getAllCachedEbooks().first()
        assertEquals(1, list.size)
        assertEquals("ebk-001", list.first().contentId)
    }

    @Test
    fun testDeleteCachedEbook() = runBlocking {
        val ebook = CachedEbookEntity(
            contentId = "ebk-002",
            title = "Menetapkan Tarif Otentik",
            slug = "menetapkan-tarif",
            author = "Raden Aria",
            summary = "Buku panduan tarif.",
            chaptersPayload = "Bab 1:::10:::Isi",
            totalReadTimeMinutes = 10
        )
        cachedEbookDao.insertOrUpdateEbook(ebook)
        assertEquals(1, cachedEbookDao.getAllCachedEbooks().first().size)

        cachedEbookDao.deleteEbookById("ebk-002")
        val emptyList = cachedEbookDao.getAllCachedEbooks().first()
        assertTrue(emptyList.isEmpty())
    }

    @Test
    fun testPodcastProgressInsertAndUpdate() = runBlocking {
        val progress1 = PodcastProgressEntity(
            contentId = "pod-101",
            title = "Episode 01: Mengapa Kegagalan Pertama Menyelamatkan Saya",
            lastPositionSeconds = 120,
            durationSeconds = 1458,
            progressPercent = 8,
            playbackSpeed = 1.0f,
            isCompleted = false
        )
        podcastProgressDao.insertOrUpdateProgress(progress1)

        val retrieved1 = podcastProgressDao.getProgressDirect("pod-101")
        assertNotNull(retrieved1)
        assertEquals(120, retrieved1?.lastPositionSeconds)
        assertFalse(retrieved1?.isCompleted ?: true)

        // Update progress to completion
        val progress2 = progress1.copy(
            lastPositionSeconds = 1450,
            progressPercent = 99,
            isCompleted = true
        )
        podcastProgressDao.insertOrUpdateProgress(progress2)

        val retrieved2 = podcastProgressDao.getProgressDirect("pod-101")
        assertEquals(1450, retrieved2?.lastPositionSeconds)
        assertTrue(retrieved2?.isCompleted == true)
    }

    @Test
    fun testLocalBookmarksDao() = runBlocking {
        val bookmark = LocalBookmarkEntity(
            contentId = "cnt-999",
            title = "Pelajaran Nyata dari Kegagalan",
            contentType = "FOUNDER_STORY"
        )
        bookmarkDao.insertBookmark(bookmark)

        val isBookmarked = bookmarkDao.isBookmarked("cnt-999").first()
        assertTrue(isBookmarked)

        val all = bookmarkDao.getAllBookmarks().first()
        assertEquals(1, all.size)
        assertEquals("cnt-999", all.first().contentId)

        bookmarkDao.deleteBookmarkById("cnt-999")
        val isBookmarkedAfterDelete = bookmarkDao.isBookmarked("cnt-999").first()
        assertFalse(isBookmarkedAfterDelete)
    }
}
