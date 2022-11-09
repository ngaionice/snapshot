package dev.ionice.snapshot.core.database

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import dev.ionice.snapshot.core.database.TestingData.Day.date
import dev.ionice.snapshot.core.database.TestingData.Day.dayOfMonth
import dev.ionice.snapshot.core.database.TestingData.Day.daySummary
import dev.ionice.snapshot.core.database.TestingData.Day.month
import dev.ionice.snapshot.core.database.TestingData.Day.year
import dev.ionice.snapshot.core.database.dao.DayDao
import dev.ionice.snapshot.core.database.dao.LocationDao
import dev.ionice.snapshot.core.database.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
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
        val locationProps = LocationEntity(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = TestingData.Location.lastUsedAt
        )
        val locationId = locationDao.insertEntity(locationProps)
        val query = locationDao.get(locationId)

        assertThat(query).isNotNull()

        val (id, coordinates, name) = query!!.properties
        assertThat(id).isEqualTo(locationId)
        assertThat(coordinates).isEqualTo(TestingData.Location.coordinates)
        assertThat(name).isEqualTo(TestingData.Location.name)
    }

    /**
     * Asserts that inserting a LocationProperties with a duplicate ID throws an error.
     */
    @Test(expected = SQLiteConstraintException::class)
    @Throws(IOException::class)
    fun insertLocationPropertiesWithDuplicateId(): Unit = runTest {
        val locationProps = LocationEntity(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = TestingData.Location.lastUsedAt
        )
        val locationId = locationDao.insertEntity(locationProps)
        locationDao.insertEntity(
            LocationEntity(
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
        val locationProps = LocationEntity(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = TestingData.Location.lastUsedAt
        )
        locationDao.insertEntity(locationProps)
        locationDao.insertEntity(
            LocationEntity(
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
        val locationProps = LocationEntity(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = TestingData.Location.lastUsedAt
        )
        val locationId = locationDao.insertEntity(locationProps)
        val nameUpdate = "TestLocationUpdate"
        val coordsUpdate = CoordinatesEntity(4.0, 4.0)
        locationDao.updateEntity(
            LocationEntity(
                id = locationId,
                coordinates = coordsUpdate,
                name = nameUpdate,
                lastUsedAt = TestingData.Location.lastUsedAt
            )
        )

        val query = locationDao.get(locationId)
        assertThat(query).isNotNull()

        val (_, coordinates, name) = query!!.properties
        assertThat(coordinates).isEqualTo(coordsUpdate)
        assertThat(name).isEqualTo(nameUpdate)
    }

    /**
     * Asserts that inserting a LocationEntry with an existing dayId + locationId combination
     * does not throw an error.
     */
    @Test
    @Throws(IOException::class)
    fun insertLocationEntryWithDuplicateIds(): Unit = runTest {
        val locationProps = LocationEntity(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = TestingData.Location.lastUsedAt
        )
        val locationId = locationDao.insertEntity(locationProps)

        val dayProps = DayEntity(
            id = date,
            summary = daySummary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertEntity(dayProps)
        locationDao.insertCrossRef(DayLocationCrossRef(date, locationId))
        locationDao.insertCrossRef(DayLocationCrossRef(date, locationId))
    }

    /**
     * Asserts that querying a Location with an existing entry returns the correct result,
     * and that inserting LocationEntry works correctly.
     */
    @Test
    @Throws(IOException::class)
    fun insertLocationEntryAndQueryLocation(): Unit = runTest {
        val locationProps = LocationEntity(
            id = TestingData.Location.initialId,
            coordinates = TestingData.Location.coordinates,
            name = TestingData.Location.name,
            lastUsedAt = TestingData.Location.lastUsedAt
        )
        val locationId = locationDao.insertEntity(locationProps)

        val dayProps = DayEntity(
            id = date,
            summary = daySummary,
            createdAt = 0,
            lastModifiedAt = 0,
            date = Date(year, month, dayOfMonth)
        )
        dayDao.insertEntity(dayProps)
        locationDao.insertCrossRef(DayLocationCrossRef(date, locationId))

        val query = locationDao.get(locationId)
        assertThat(query).isNotNull()

        val (id, coordinates, name) = query!!.properties
        assertThat(id).isEqualTo(locationId)
        assertThat(coordinates).isEqualTo(TestingData.Location.coordinates)
        assertThat(name).isEqualTo(TestingData.Location.name)

        assertThat(query.entries.size).isEqualTo(1)
        assertThat(query.entries[0].id).isEqualTo(date)
    }

    /**
     * Asserts that querying a non-existent Location returns null.
     */
    @Test
    @Throws(IOException::class)
    fun queryNonExistentLocation(): Unit = runTest {
        assertThat(locationDao.get(3)).isNull()
    }
}