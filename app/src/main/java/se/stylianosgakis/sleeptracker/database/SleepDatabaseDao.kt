package se.stylianosgakis.sleeptracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SleepDatabaseDao {
    @Insert
    fun insert(night: SleepNight)

    @Update
    fun update(night: SleepNight)

    @Query(
        """SELECT *
         FROM daily_sleep_quality_table
         WHERE nightId = :nightId"""
    )
    fun get(nightId: Long): SleepNight?

    @Query("""DELETE FROM daily_sleep_quality_table""")
    fun clear()

    @Query(
    """SELECT *
        FROM daily_sleep_quality_table
        WHERE nightId = :nightId"""
    )
    fun getNightWithId(nightId: Long): LiveData<SleepNight?>

    @Query(
        """SELECT *
        FROM daily_sleep_quality_table
        ORDER BY nightId DESC"""
    )
    fun getAllNights(): LiveData<List<SleepNight>>

    @Query(
        """SELECT *
        FROM daily_sleep_quality_table
        ORDER BY nightId DESC
        LIMIT 1"""
    )
    fun getTonight(): SleepNight?
}
