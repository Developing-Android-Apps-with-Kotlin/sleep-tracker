package se.stylianosgakis.sleeptracker.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import se.stylianosgakis.sleeptracker.database.SleepDatabaseDao

class SleepTrackerViewModel(
    val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {
}

