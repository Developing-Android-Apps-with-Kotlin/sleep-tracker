package se.stylianosgakis.sleeptracker.sleepdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import se.stylianosgakis.sleeptracker.R
import se.stylianosgakis.sleeptracker.database.SleepDatabase
import se.stylianosgakis.sleeptracker.databinding.FragmentSleepDetailBinding

class SleepDetailFragment : Fragment() {
    private val sleepDetailViewModel by viewModels<SleepDetailViewModel> {
        val application = requireNotNull(this.activity).application
        val arguments = SleepDetailFragmentArgs.fromBundle(requireArguments())
        // Create an instance of the ViewModel Factory.
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        SleepDetailViewModelFactory(arguments.sleepNightKey, dataSource)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepDetailBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_detail, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.sleepDetailViewModel = sleepDetailViewModel
        sleepDetailViewModel.navigateToSleepTracker.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().popBackStack()
                sleepDetailViewModel.doneNavigating()
            }
        })

        return binding.root
    }
}
