package dev.ionice.snapshot.core.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import dev.ionice.snapshot.core.database.dao.DayDao
import dev.ionice.snapshot.core.database.dao.LocationDao
import dev.ionice.snapshot.core.database.dao.TagDao
import dev.ionice.snapshot.core.database.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
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
    fun insertDayEntity(): Unit = runTest {
        val props = DayEntity(
            id = date,
            summary = summary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertEntity(props)

        val query = dayDao.get(date)
        assertThat(query).isNotNull()

        val (id, entrySummary, createdAt, lastUpdatedAt, isFavorite, entryDate) = query!!.properties
        assertThat(id).isEqualTo(date)
        assertThat(entrySummary).isEqualTo(summary)
        assertThat(createdAt).isEqualTo(0L)
        assertThat(lastUpdatedAt).isEqualTo(0L)
        assertThat(isFavorite).isFalse()
        assertThat(entryDate).isEqualTo(Date(year, month, dayOfMonth))
    }

    @Test
    @Throws(IOException::class)
    fun searchSummary(): Unit = runTest {
        val props = DayEntity(
            id = date,
            summary = summary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertEntity(props)

        val query = dayDao.searchBySummary("summary").first()
        assertThat(query.size).isEqualTo(1)
    }

    @Test
    @Throws(IOException::class)
    fun searchSummaryNoMatch(): Unit = runTest {
        val props = DayEntity(
            id = date,
            summary = summary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertEntity(props)

        val query = dayDao.searchBySummary(queryString = "summary", startDayId = date + 1).first()
        assertThat(query.size).isEqualTo(0)
    }

    @Test
    @Throws(IOException::class)
    fun searchTagEntry(): Unit = runTest {
        val props = DayEntity(
            id = date,
            summary = summary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertEntity(props)

        val tagProps = TagEntity(
            id = TestingData.Tag.initialId,
            name = TestingData.Tag.name,
            lastUsedAt = TestingData.Tag.lastUsedAt
        )
        val tagId = tagDao.insertEntity(tagProps)
        tagDao.insertCrossRef(DayTagCrossRef(date, tagId, TestingData.Tag.content))

        val query = dayDao.searchByTagEntry(queryString = "TestTagContent").first()
        assertThat(query.size).isEqualTo(1)
    }

    @Test
    @Throws(IOException::class)
    fun searchTagEntryNoMatch(): Unit = runTest {
        val props = DayEntity(
            id = date,
            summary = summary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertEntity(props)

        val tagProps = TagEntity(
            id = TestingData.Tag.initialId,
            name = TestingData.Tag.name,
            lastUsedAt = TestingData.Tag.lastUsedAt
        )
        val tagId = tagDao.insertEntity(tagProps)
        tagDao.insertCrossRef(DayTagCrossRef(date, tagId, TestingData.Tag.content))

        val query = dayDao.searchByTagEntry(queryString = "???").first()
        assertThat(query.size).isEqualTo(0)
    }

    /**
     * Asserts that a duplicate insert is ignored.
     */
    @Test
    @Throws(IOException::class)
    fun insertDuplicateDayProperties(): Unit = runTest {
        val props = DayEntity(
            id = date,
            summary = summary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertEntity(props)
        dayDao.insertEntity(props.copy(summary = "Replacement!"))

        val query = dayDao.get(date)
        assertThat(query).isNotNull()
        assertThat(query!!.properties.summary).isEqualTo(summary)
    }

    /**
     * Asserts that updating DayProperties works correctly.
     */
    @Test
    @Throws(IOException::class)
    fun updateDayProperties(): Unit = runTest {
        val props = DayEntity(
            id = date,
            summary = summary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertEntity(props)
        val summaryUpdate = "TestSummaryUpdate"
        dayDao.updateEntity(
            props.copy(
                summary = summaryUpdate, createdAt = 1, lastModifiedAt = 1, isFavorite = true
            )
        )

        val query = dayDao.get(date)
        assertThat(query).isNotNull()

        val (id, entrySummary, createdAt, lastUpdatedAt, isFavorite) = query!!.properties
        assertThat(id).isEqualTo(date)
        assertThat(entrySummary).isEqualTo(summaryUpdate)
        assertThat(createdAt).isEqualTo(1L)
        assertThat(lastUpdatedAt).isEqualTo(1L)
        assertThat(isFavorite).isTrue()
    }

    /**
     * Asserts that querying a Day with a Location and TagEntries returns the correct results.
     */
    @Test
    @Throws(IOException::class)
    fun queryDay(): Unit = runTest {

        // insert day data
        val dayProps = DayEntity(
            id = date,
            summary = summary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertEntity(dayProps)

        // insert location data
        val locationProps = LocationEntity(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = 0
        )
        val locationId = locationDao.insertEntity(locationProps)
        locationDao.insertCrossRef(DayLocationCrossRef(date, locationId))

        // insert tag data
        val tagProps = TagEntity(
            id = TestingData.Tag.initialId,
            name = TestingData.Tag.name,
            lastUsedAt = TestingData.Tag.lastUsedAt
        )
        val tagId = tagDao.insertEntity(tagProps)
        tagDao.insertCrossRef(DayTagCrossRef(date, tagId, TestingData.Tag.content))

        val query = dayDao.get(date)
        assertThat(query).isNotNull()

        assertThat(query!!.location).isNotNull()
        val (locLocationId) = query.location!!
        assertThat(locLocationId).isEqualTo(locationId)

        assertThat(query.tags.size).isEqualTo(1)
        val (tagTagId) = query.tags[0]
        assertThat(tagTagId).isEqualTo(tagId)

        val tagContent = query.tagContents.find { it.tagId == tagTagId }
        assertThat(tagContent).isNotNull()
        assertThat(tagContent!!.content).isEqualTo(TestingData.Tag.content)
    }

    /**
     * Asserts that querying a non-existent Day returns null.
     */
    @Test
    @Throws(IOException::class)
    fun queryNonExistentDay(): Unit = runTest {
        assertThat(dayDao.get(LocalDate.now().toEpochDay())).isNull()
    }
}