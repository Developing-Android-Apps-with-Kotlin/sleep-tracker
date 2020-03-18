package se.stylianosgakis.sleeptracker.sleepdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.stylianosgakis.sleeptracker.database.SleepDatabaseDao
import se.stylianosgakis.sleeptracker.database.SleepNight

class SleepDetailViewModel(
        private val sleepNightKey: Long = 0L,
        val database: SleepDatabaseDao
) : ViewModel() {
    private val night = MediatorLiveData<SleepNight>()
    fun getNight() = night

    init {
        night.addSource(database.getNightWithId(sleepNightKey), night::setValue)
    }

    private val _navigateToSleepTracker = MutableLiveData<Boolean>()
    val navigateToSleepTracker: LiveData<Boolean>
            get() = _navigateToSleepTracker

    override fun onCleared() {
        super.onCleared()
        //viewModelJob.cancel()
    }

    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    fun onClose() {
        _navigateToSleepTracker.value = true
    }

}

 