package se.stylianosgakis.sleeptracker.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.stylianosgakis.sleeptracker.database.SleepDatabaseDao
import se.stylianosgakis.sleeptracker.database.SleepNight

class SleepTrackerViewModel(
    val database: SleepDatabaseDao, application: Application
) : AndroidViewModel(application) {
    private val tonightLiveData = MutableLiveData<SleepNight?>()
    val nightsLiveData = database.getAllNights()

    // Navigation
    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality
    private val _navigateToSleepDataQuality = MutableLiveData<Long?>()
    val navigateToSleepDataQuality: LiveData<Long?>
        get() = _navigateToSleepDataQuality

    fun doneNavigating() {
        _navigateToSleepQuality.value = null
        _navigateToSleepDataQuality.value = null
    }

    fun onSleepNightClicked(nightId: Long) {
        _navigateToSleepDataQuality.value = nightId
    }

    // Snackbar
    private val _showSnackbarEvent = MutableLiveData<Boolean>()
    val showSnackbarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

    // Button visibility LiveData
    val startButtonVisible: LiveData<Boolean> =
        Transformations.map(tonightLiveData) { sleepNight ->
            null == sleepNight
        }
    val stopButtonVisible: LiveData<Boolean> =
        Transformations.map(tonightLiveData) { sleepNight ->
            null != sleepNight
        }
    val clearButtonVisible: LiveData<Boolean> =
        Transformations.map(nightsLiveData) { listOfNights ->
            listOfNights.isNotEmpty()
        }

    // Initialization
    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        viewModelScope.launch {
            tonightLiveData.value = getTonightFromDatabase()
        }
    }

    // Button handlers
    fun onStartTracking() {
        viewModelScope.launch {
            insert(SleepNight())
            tonightLiveData.value = getTonightFromDatabase()
        }
    }

    fun onStopTracking() {
        viewModelScope.launch {
            val oldNight = tonightLiveData.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            _navigateToSleepQuality.value = oldNight
        }
    }

    fun onClearButton() {
        viewModelScope.launch {
            clearDatabase()
            tonightLiveData.value = null
            _showSnackbarEvent.value = true
        }
    }

    // Database
    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(IO) {
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        }
    }

    private suspend fun insert(night: SleepNight) {
        withContext(IO) {
            database.insert(night)
        }
    }

    private suspend fun update(oldNight: SleepNight) {
        withContext(IO) {
            database.update(oldNight)
        }
    }

    private suspend fun clearDatabase() {
        withContext(IO) {
            database.clear()
        }
    }
}

