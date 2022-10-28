package dev.ionice.snapshot.data.database

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import dev.ionice.snapshot.data.database.dao.DayDao
import dev.ionice.snapshot.data.database.dao.TagDao
import dev.ionice.snapshot.data.database.model.Date
import dev.ionice.snapshot.data.database.model.DayProperties
import dev.ionice.snapshot.data.database.model.TagEntry
import dev.ionice.snapshot.data.database.model.TagProperties
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TagDaoTest {

    private lateinit var tagDao: TagDao
    private lateinit var dayDao: DayDao
    private lateinit var db: SnapshotDatabase

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, SnapshotDatabase::class.java)
            .allowMainThreadQueries().build()
        tagDao = db.tagDao
        dayDao = db.dayDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private suspend fun insertBaseProperties(): Long {
        val tagProps = TagProperties(
            id = TestingData.Tag.initialId,
            name = TestingData.Tag.name,
            lastUsedAt = TestingData.Tag.lastUsedAt
        )
        return tagDao.insertProperties(tagProps)
    }

    private suspend fun insertDayProperties(): Long {
        val dayProps = DayProperties(
            id = TestingData.Day.date,
            summary = TestingData.Day.daySummary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(TestingData.Day.year, TestingData.Day.month, TestingData.Day.dayOfMonth)
        )
        return dayDao.insertProperties(dayProps)
    }

    /**
     * Asserts that inserting TagProperties works correctly.
     */
    @Test
    @Throws(IOException::class)
    fun insertTagProperties(): Unit = runTest {
        val tagId = insertBaseProperties()

        val query = tagDao.get(tagId)
        assertTrue("Expected Tag object after insertion, got null.", query != null)

        val (id, name, lastUsedAt) = query!!.properties
        assertTrue(id == tagId)
        assertTrue(name == TestingData.Tag.name)
        assertTrue(lastUsedAt == TestingData.Tag.lastUsedAt)
    }

    /**
     * Asserts that inserting a TagProperties with a duplicate ID throws an error.
     */
    @Test(expected = SQLiteConstraintException::class)
    @Throws(IOException::class)
    fun insertTagPropertiesWithDuplicateId(): Unit = runTest {
        val tagId = insertBaseProperties()
        tagDao.insertProperties(TagProperties(id = tagId, name = "NewName", lastUsedAt = 1))
    }

    /**
     * Asserts that updating TagProperties works correctly.
     */
    @Test
    @Throws(IOException::class)
    fun updateTagProperties(): Unit = runTest {
        val tagId = insertBaseProperties()
        val nameUpdate = "TestTagUpdate"
        val lastUsedAtUpdate = 1L
        tagDao.updateProperties(
            TagProperties(
                id = tagId, name = nameUpdate, lastUsedAt = lastUsedAtUpdate
            )
        )

        val query = tagDao.get(tagId)
        assertTrue("Expected Tag object after insertion, got null.", query != null)

        val (id, name, lastUsedAt) = query!!.properties
        assertTrue(id == tagId)
        assertTrue(name == nameUpdate)
        assertTrue(lastUsedAt == lastUsedAtUpdate)
    }

    /**
     * Asserts that querying a Tag with an existing tag returns the correct result,
     * and that inserting TagEntry works correctly.
     */
    @Test
    @Throws(IOException::class)
    fun insertTagEntryAndQueryTag(): Unit = runTest {
        val tagId = insertBaseProperties()
        val dayId = insertDayProperties()
        val entry = TagEntry(dayId = dayId, tagId = tagId, content = TestingData.Tag.content)
        tagDao.insertEntry(entry)

        val query = tagDao.get(tagId)
        assertTrue("Expected Tag object after insertion, got null.", query != null)

        assertTrue("Expected 1 TagEntry, got ${query!!.entries.size}", query.entries.size == 1)
        val queryEntry = query.entries[0]
        assertTrue(queryEntry.dayId == dayId)
        assertTrue(queryEntry.tagId == tagId)
        assertTrue(queryEntry.content == TestingData.Tag.content)
    }

    /**
     * Asserts that inserting a TagEntry with an existing dayId + tagId combination throws an error.
     */
    @Test(expected = SQLiteConstraintException::class)
    @Throws(IOException::class)
    fun insertTagEntryWithDuplicateId(): Unit = runTest {
        val tagId = insertBaseProperties()
        val dayId = insertDayProperties()
        val entry = TagEntry(dayId = dayId, tagId = tagId, content = TestingData.Tag.content)
        tagDao.insertEntry(entry)
        tagDao.insertEntry(TagEntry(dayId = dayId, tagId = tagId))
    }

    /**
     * Asserts that querying a non-existent Tag returns null.
     */
    @Test
    @Throws(IOException::class)
    fun queryNonExistentTag(): Unit = runTest {
        assertTrue(tagDao.get(3) == null)
    }
}