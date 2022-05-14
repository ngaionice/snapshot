package me.ionice.snapshot

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import me.ionice.snapshot.database.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
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

    private fun <T> LiveData<T>.blockingObserve(): T? {
        var value: T? = null
        val latch = CountDownLatch(1)

        val observer = Observer<T> { t ->
            value = t
            latch.countDown()
        }

        observeForever(observer)

        latch.await(2, TimeUnit.SECONDS)
        return value
    }

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
    fun testDayInsertion() {
        val day = Day()
        dayDao.insert(day)
        val today = dayDao.getLatest()
        println("today's id is ${today!!.id}")
    }

    @Test(expected = SQLiteConstraintException::class)
    @Throws(IOException::class)
    fun testDuplicateDayInsertion() {
        val day = Day()
        dayDao.insert(day)
        dayDao.insert(day)
    }

    @Test
    @Throws(IOException::class)
    fun testDayAndMetricInsertion() {
        val day = Day()
        dayDao.insert(day)

        val key = MetricKey(name = "test metric key")

        metricDao.insertKey(key)

        val keys = metricDao.getAllKeys().blockingObserve()

        val insertedKeyId = keys!![0].id
        val entry = MetricEntry(insertedKeyId, day.id, "test metric entry")
        metricDao.insertEntry(entry)

        val insertedDay = dayDao.getWithMetrics(day.id)
        assert(insertedDay!!.metrics.isNotEmpty())

        val insertedMetric = metricDao.getMetric(insertedKeyId)
        assert(insertedMetric!!.entries.isNotEmpty())
    }
}