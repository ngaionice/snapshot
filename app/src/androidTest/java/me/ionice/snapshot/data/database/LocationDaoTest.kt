package me.ionice.snapshot.data.database

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import me.ionice.snapshot.data.database.TestingData.Day.date
import me.ionice.snapshot.data.database.TestingData.Day.dayOfMonth
import me.ionice.snapshot.data.database.TestingData.Day.daySummary
import me.ionice.snapshot.data.database.TestingData.Day.month
import me.ionice.snapshot.data.database.TestingData.Day.year
import me.ionice.snapshot.data.database.dao.DayDao
import me.ionice.snapshot.data.database.dao.LocationDao
import me.ionice.snapshot.data.database.model.*
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
class LocationDaoTest {

    private lateinit var locationDao: LocationDao
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
        locationDao = db.locationDao
        dayDao = db.dayDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**
     * Asserts that inserting LocationProperties works correctly.
     */
    @Test
    @Throws(IOException::class)
    fun insertLocationProperties(): Unit = runTest {
        val locationProps = LocationProperties(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = TestingData.Location.lastUsedAt
        )
        val locationId = locationDao.insertProperties(locationProps)
        val query = locationDao.get(locationId)
        assertTrue("Expected Location object after insertion, got null.", query != null)

        val (id, coordinates, name) = query!!.properties
        assertTrue(id == locationId)
        assertTrue(coordinates == TestingData.Location.coordinates)
        assertTrue(name == TestingData.Location.name)
    }

    /**
     * Asserts that inserting a LocationProperties with a duplicate ID throws an error.
     */
    @Test(expected = SQLiteConstraintException::class)
    @Throws(IOException::class)
    fun insertLocationPropertiesWithDuplicateId(): Unit = runTest {
        val locationProps = LocationProperties(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = TestingData.Location.lastUsedAt
        )
        val locationId = locationDao.insertProperties(locationProps)
        locationDao.insertProperties(
            LocationProperties(
                id = locationId,
                coordinates = TestingData.Location.coordinates,
                name = "AltName",
                lastUsedAt = TestingData.Location.lastUsedAt
            )
        )
    }

    /**
     * Asserts that inserting a LocationProperties with duplicate coordinates throws an error.
     */
    @Test(expected = SQLiteConstraintException::class)
    @Throws(IOException::class)
    fun insertLocationPropertiesWithDuplicateCoordinates(): Unit = runTest {
        val locationProps = LocationProperties(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = TestingData.Location.lastUsedAt
        )
        locationDao.insertProperties(locationProps)
        locationDao.insertProperties(
            LocationProperties(
                id = TestingData.Location.initialId,
                coordinates = TestingData.Location.coordinates,
                name = "AltName",
                lastUsedAt = TestingData.Location.lastUsedAt
            )
        )
    }

    /**
     * Asserts that updating LocationProperties works correctly.
     */
    @Test
    @Throws(IOException::class)
    fun updateLocationProperties(): Unit = runTest {
        val locationProps = LocationProperties(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = TestingData.Location.lastUsedAt
        )
        val locationId = locationDao.insertProperties(locationProps)
        val nameUpdate = "TestLocationUpdate"
        val coordsUpdate = Coordinates(4.0, 4.0)
        locationDao.updateProperties(
            LocationProperties(
                id = locationId,
                coordinates = coordsUpdate,
                name = nameUpdate,
                lastUsedAt = TestingData.Location.lastUsedAt
            )
        )

        val query = locationDao.get(locationId)
        assertTrue("Expected Location object after insertion, got null.", query != null)

        val (_, coordinates, name) = query!!.properties
        assertTrue(coordinates == coordsUpdate)
        assertTrue(name == nameUpdate)
    }

    /**
     * Asserts that inserting a LocationEntry with an existing dayId + locationId combination
     * does not throw an error.
     */
    @Test
    @Throws(IOException::class)
    fun insertLocationEntryWithDuplicateIds(): Unit = runTest {
        val locationProps = LocationProperties(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = TestingData.Location.lastUsedAt
        )
        val locationId = locationDao.insertProperties(locationProps)

        val dayProps = DayProperties(
            id = date,
            summary = daySummary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertProperties(dayProps)
        locationDao.insertEntry(LocationEntry(date, locationId))
        locationDao.insertEntry(LocationEntry(date, locationId))
    }

    /**
     * Asserts that querying a Location with an existing entry returns the correct result,
     * and that inserting LocationEntry works correctly.
     */
    @Test
    @Throws(IOException::class)
    fun insertLocationEntryAndQueryLocation(): Unit = runTest {
        val locationProps = LocationProperties(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = TestingData.Location.lastUsedAt
        )
        val locationId = locationDao.insertProperties(locationProps)

        val dayProps = DayProperties(
            id = date,
            summary = daySummary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertProperties(dayProps)
        locationDao.insertEntry(LocationEntry(date, locationId))

        val query = locationDao.get(locationId)
        assertTrue("Expected Location object after insertion, got null.", query != null)

        val (id, coordinates, name) = query!!.properties
        assertTrue(id == locationId)
        assertTrue(coordinates == TestingData.Location.coordinates)
        assertTrue(name == TestingData.Location.name)

        assertTrue(
            "Expected entries size to be 1, got ${query.entries.size}.",
            query.entries.size == 1
        )
        assertTrue(query.entries[0].dayId == date)
        assertTrue(query.entries[0].locationId == locationId)
    }

    /**
     * Asserts that querying a non-existent Location returns null.
     */
    @Test
    @Throws(IOException::class)
    fun queryNonExistentLocation(): Unit = runTest {
        assertTrue(locationDao.get(3) == null)
    }
}