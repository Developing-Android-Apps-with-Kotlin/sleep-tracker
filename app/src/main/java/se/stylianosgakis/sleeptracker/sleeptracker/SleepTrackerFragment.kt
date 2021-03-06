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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import se.stylianosgakis.sleeptracker.R
import se.stylianosgakis.sleeptracker.database.SleepDatabase
import se.stylianosgakis.sleeptracker.databinding.FragmentSleepTrackerBinding

private const val COLUMN_COUNT = 4

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
        val sleepNightAdapter = SleepNightAdapter(SleepNightListener(itemClickedListener))
        val gridLayoutManager = GridLayoutManager(activity, COLUMN_COUNT)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = when (position) {
                0 -> COLUMN_COUNT
                else -> 1
            }
        }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            sleepTrackerViewModel = viewModel
            sleepRecyclerView.apply {
                layoutManager = gridLayoutManager
                adapter = sleepNightAdapter
            }
        }
        viewModel.nightsLiveData.observe(viewLifecycleOwner, Observer { listOfSleepNights ->
            listOfSleepNights?.let {
                sleepNightAdapter.addHeaderAndSubmitList(it)
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
        viewModel.navigateToSleepDataQuality.observe(viewLifecycleOwner, Observer { nightId ->
            nightId?.let {
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepDetailFragment(nightId)
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

    private val itemClickedListener: (nightId: Long) -> Unit = { nightId ->
        viewModel.onSleepNightClicked(nightId)
    }
}
