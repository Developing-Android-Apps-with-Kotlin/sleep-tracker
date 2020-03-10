package se.stylianosgakis.sleeptracker.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.stylianosgakis.sleeptracker.database.SleepDatabaseDao

class SleepQualityViewModel(
    val database: SleepDatabaseDao, private val sleepNightKey: Long = 0L
) : ViewModel() {
    private val _navigateToSleepTracker = MutableLiveData<Boolean>()
    val navigateToSleepTracker: LiveData<Boolean> = _navigateToSleepTracker

    fun doneNavigating() {
        _navigateToSleepTracker.value = false
    }

    fun onSetSleepQuality(quality: Int) {
        viewModelScope.launch {
            withContext(IO) {
                val tonight = database.get(sleepNightKey) ?: return@withContext
                tonight.sleepQuality = quality
                database.update(tonight)
            }
            _navigateToSleepTracker.value = true
        }
    }

    fun cancelSleepQuality() {
        viewModelScope.launch {
            withContext(IO) {
                val tonight = database.get(sleepNightKey) ?: return@withContext
                tonight.endTimeMilli = tonight.startTimeMilli
                database.update(tonight)
            }
            _navigateToSleepTracker.value = true
        }
    }
}