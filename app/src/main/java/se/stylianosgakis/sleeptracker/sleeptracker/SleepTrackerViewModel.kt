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
    val nightsString: LiveData<String> =
        Transformations.map(nightsLiveData) { nights ->
            nights.toString()
        }
    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
    val navigateToSleepQuality: LiveData<SleepNight> = _navigateToSleepQuality
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
    private val _showSnackbarEvent = MutableLiveData<Boolean>()
    val showSnackbarEvent: LiveData<Boolean> = _showSnackbarEvent

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        viewModelScope.launch {
            tonightLiveData.value = getTonightFromDatabase()
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(IO) {
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        }
    }

    fun onStartTracking() {
        viewModelScope.launch {
            insert(SleepNight())
            tonightLiveData.value = getTonightFromDatabase()
        }
    }

    private suspend fun insert(night: SleepNight) {
        withContext(IO) {
            database.insert(night)
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

    private suspend fun update(oldNight: SleepNight) {
        withContext(IO) {
            database.update(oldNight)
        }
    }

    fun onClearButton() {
        viewModelScope.launch {
            clearDatabase()
            tonightLiveData.value = null
            _showSnackbarEvent.value = true
        }
    }

    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

    private suspend fun clearDatabase() {
        withContext(IO) {
            database.clear()
        }
    }
}

