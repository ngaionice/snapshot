package me.ionice.snapshot

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import me.ionice.snapshot.data.*
import me.ionice.snapshot.data.day.Day
import me.ionice.snapshot.data.day.DayDao
import me.ionice.snapshot.data.metric.MetricDao
import me.ionice.snapshot.data.metric.MetricEntry
import me.ionice.snapshot.data.metric.MetricKey
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var dayDao: DayDao
    private lateinit var metricDao: MetricDao

    private lateinit var db: SnapshotDatabase

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, SnapshotDatabase::class.java).allowMainThreadQueries().build()
        dayDao = db.dayDao
        metricDao = db.metricDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun testDayInsertion() = runBlocking {
        val date = LocalDate.now().toEpochDay()
        val day = Day(id = date)
        dayDao.insert(day)
        val today = dayDao.get(date)
        println("today's id is ${today!!.id}")
    }

    @Test(expected = SQLiteConstraintException::class)
    @Throws(IOException::class)
    fun testDuplicateDayInsertion() = runBlocking {
        val day = Day()
        dayDao.insert(day)
        dayDao.insert(day)
    }

    @Test
    @Throws(IOException::class)
    fun testDayAndMetricInsertion() = runBlocking {
        val day = Day()
        dayDao.insert(day)

        val key = MetricKey(name = "test metric key")

        metricDao.insertKey(key)

//        val keys = metricDao.getAllKeys()
//
//        val insertedKeyId = keys!![0].id
//        val entry = MetricEntry(insertedKeyId, day.id, "test metric entry")
//        metricDao.insertEntry(entry)
//
//        val insertedDay = dayDao.getWithMetrics(day.id)
//        assert(insertedDay!!.metrics.isNotEmpty())
//
//        val insertedMetric = metricDao.getMetric(insertedKeyId)
//        assert(insertedMetric!!.entries.isNotEmpty())
    }
}