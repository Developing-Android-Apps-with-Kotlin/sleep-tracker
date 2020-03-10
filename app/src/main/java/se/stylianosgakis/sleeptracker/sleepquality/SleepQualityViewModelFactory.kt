package se.stylianosgakis.sleeptracker.sleepquality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import se.stylianosgakis.sleeptracker.database.SleepDatabaseDao

class SleepQualityViewModelFactory(
    private val dataSource: SleepDatabaseDao,
    private val sleepNightKey: Long
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepQualityViewModel::class.java)) {
            return SleepQualityViewModel(dataSource, sleepNightKey) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}