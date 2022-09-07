package me.ionice.snapshot.data.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import me.ionice.snapshot.data.database.dao.DayDao
import me.ionice.snapshot.data.database.dao.LocationDao
import me.ionice.snapshot.data.database.dao.TagDao
import me.ionice.snapshot.data.database.model.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class DayDaoTest {

    private lateinit var dayDao: DayDao
    private lateinit var locationDao: LocationDao
    private lateinit var tagDao: TagDao
    private lateinit var db: SnapshotDatabase

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, SnapshotDatabase::class.java)
            .allowMainThreadQueries().build()
        dayDao = db.dayDao
        locationDao = db.locationDao
        tagDao = db.tagDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private val year = TestingData.Day.year
    private val month = TestingData.Day.month
    private val dayOfMonth = TestingData.Day.dayOfMonth

    private val date = TestingData.Day.date
    private val summary = TestingData.Day.daySummary

    /**
     * Asserts that inserting DayProperties works correctly.
     */
    @Test
    @Throws(IOException::class)
    fun insertDayProperties(): Unit = runTest {
        val props = DayProperties(
            id = date,
            summary = summary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertProperties(props)

        val query = dayDao.get(date)
        assertTrue("Expected a day entry from query, got null.", query != null)

        val (id, entrySummary, createdAt, lastUpdatedAt, isFavorite, entryDate) = query!!.properties
        assertTrue(id == date)
        assertTrue(entrySummary == summary)
        assertTrue(createdAt == 0L)
        assertTrue(lastUpdatedAt == 0L)
        assertTrue(!isFavorite)
        assertTrue(entryDate == Date(year, month, dayOfMonth))
    }

    /**
     * Asserts that a duplicate insert is ignored.
     */
    @Test
    @Throws(IOException::class)
    fun insertDuplicateDayProperties(): Unit = runTest {
        val props = DayProperties(
            id = date,
            summary = summary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertProperties(props)
        dayDao.insertProperties(props.copy(summary = "Replacement!"))

        val query = dayDao.get(date)
        assertTrue("Expected a day entry from query, got null.", query != null)
        assertTrue(query!!.properties.summary == summary)
    }

    /**
     * Asserts that updating DayProperties works correctly.
     */
    @Test
    @Throws(IOException::class)
    fun updateDayProperties(): Unit = runTest {
        val props = DayProperties(
            id = date,
            summary = summary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertProperties(props)
        val summaryUpdate = "TestSummaryUpdate"
        dayDao.updateProperties(
            props.copy(
                summary = summaryUpdate, createdAt = 1, lastModifiedAt = 1, isFavorite = true
            )
        )

        val query = dayDao.get(date)
        assertTrue("Expected a day entry from query, got null.", query != null)

        val (id, entrySummary, createdAt, lastUpdatedAt, isFavorite) = query!!.properties
        assertTrue(id == date)
        assertTrue(entrySummary == summaryUpdate)
        assertTrue(createdAt == 1L)
        assertTrue(lastUpdatedAt == 1L)
        assertTrue(isFavorite)
    }

    /**
     * Asserts that querying a Day with a Location and TagEntries returns the correct results.
     */
    @Test
    @Throws(IOException::class)
    fun queryDay(): Unit = runTest {

        // insert day data
        val dayProps = DayProperties(
            id = date,
            summary = summary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertProperties(dayProps)

        // insert location data
        val locationProps = LocationProperties(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = 0
        )
        val locationId = locationDao.insertProperties(locationProps)
        locationDao.insertEntry(LocationEntry(date, locationId))

        // insert tag data
        val tagProps = TagProperties(
            id = TestingData.Tag.initialId,
            name = TestingData.Tag.name,
            lastUsedAt = TestingData.Tag.lastUsedAt
        )
        val tagId = tagDao.insertProperties(tagProps)
        tagDao.insertEntry(TagEntry(date, tagId, TestingData.Tag.content))

        val query = dayDao.get(date)
        assertTrue("Expected a day entry from query, got null.", query != null)

        assertTrue("Expected a location, got null.", query!!.location != null)
        val (locDayId, locLocationId) = query.location!!
        assertTrue(locDayId == date)
        assertTrue(locLocationId == locationId)

        assertTrue("Expected 1 tag entry, found ${query.tags.size}.", query.tags.size == 1)
        val (tagDayId, tagTagId) = query.tags[0]
        assertTrue(tagDayId == date)
        assertTrue(tagTagId == tagId)
    }

    /**
     * Asserts that querying a non-existent Day returns null.
     */
    @Test
    @Throws(IOException::class)
    fun queryNonExistentDay(): Unit = runTest {
        assertTrue(dayDao.get(LocalDate.now().toEpochDay()) == null)
    }
}