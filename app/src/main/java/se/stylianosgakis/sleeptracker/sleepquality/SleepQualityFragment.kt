package se.stylianosgakis.sleeptracker.sleepquality

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import se.stylianosgakis.sleeptracker.R
import se.stylianosgakis.sleeptracker.database.SleepDatabase
import se.stylianosgakis.sleeptracker.databinding.FragmentSleepQualityBinding

class SleepQualityFragment : Fragment() {
    private val viewModel by viewModels<SleepQualityViewModel> {
        val arguments = SleepQualityFragmentArgs.fromBundle(requireArguments())
        val database =
            SleepDatabase.getInstance(requireNotNull(this.activity).application).sleepDatabaseDao
        SleepQualityViewModelFactory(database, arguments.sleepNightKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSleepQualityBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_quality, container, false
        )
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            sleepQualityViewModel = viewModel
        }
        viewModel.navigateToSleepTracker.observe(viewLifecycleOwner, Observer { doNavigate ->
            if (doNavigate) {
                this.findNavController().navigate(
                    SleepQualityFragmentDirections
                        .actionSleepQualityFragmentToSleepTrackerFragment()
                )
                viewModel.doneNavigating()
            }
        })
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.cancelSleepQuality()
        }
        return binding.root
    }
}
