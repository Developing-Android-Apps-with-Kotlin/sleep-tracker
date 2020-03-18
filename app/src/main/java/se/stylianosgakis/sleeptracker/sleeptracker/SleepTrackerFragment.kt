package se.stylianosgakis.sleeptracker.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import se.stylianosgakis.sleeptracker.R
import se.stylianosgakis.sleeptracker.database.SleepDatabase
import se.stylianosgakis.sleeptracker.databinding.FragmentSleepTrackerBinding

class SleepTrackerFragment : Fragment() {
    private val viewModel by viewModels<SleepTrackerViewModel> {
        val application = requireNotNull(this.activity).application
        val sleepDatabaseDao = SleepDatabase.getInstance(application).sleepDatabaseDao
        SleepTrackerViewModelFactory(sleepDatabaseDao, application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_tracker, container, false
        )
        val sleepNightAdapter = SleepNightAdapter()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            sleepTrackerViewModel = viewModel
            sleepRecyclerView.apply {
                adapter = sleepNightAdapter
            }
        }
        viewModel.nightsLiveData.observe(viewLifecycleOwner, Observer { listOfSleepNights ->
            listOfSleepNights?.let {
                sleepNightAdapter.submitList(it)
            }
        })
        viewModel.navigateToSleepQuality.observe(viewLifecycleOwner, Observer { night ->
            night?.let {
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId)
                )
                viewModel.doneNavigating()
            }
        })
        viewModel.showSnackbarEvent.observe(viewLifecycleOwner, Observer { showSnackbar ->
            if (showSnackbar) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT
                ).show()
                viewModel.doneShowingSnackbar()
            }
        })
        return binding.root
    }
}
