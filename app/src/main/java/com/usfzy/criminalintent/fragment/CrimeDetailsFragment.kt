package com.usfzy.criminalintent.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.usfzy.criminalintent.databinding.FragmentCrimeDetailsBinding
import com.usfzy.criminalintent.model.Crime
import com.usfzy.criminalintent.viewmodel.CrimeDetailsViewModel
import com.usfzy.criminalintent.viewmodel.CrimeDetailsViewModelFactory
import kotlinx.coroutines.launch
import java.sql.Time

class CrimeDetailsFragment : Fragment() {

    private var _binding: FragmentCrimeDetailsBinding? = null
    private val args: CrimeDetailsFragmentArgs by navArgs()

    private val crimeDetailsViewModel: CrimeDetailsViewModel by viewModels {
        CrimeDetailsViewModelFactory(args.crimeId)
    }
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCrimeDetailsBinding.inflate(inflater, container, false)

        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                crimeDetailsViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }

            crimeSolved.setOnCheckedChangeListener { _, isChanged ->
                crimeDetailsViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChanged)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailsViewModel.crime.collect { crime ->
                    crime?.let {
                        updateUi(it)
                    }
                }
            }
        }

        setFragmentResultListener(DatePickerFragment.REQUEST_KEY_DATE) { _, bundle ->
            val date = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Time
            crimeDetailsViewModel.updateCrime { it.copy(date = date) }
        }

        return binding.root
    }

    private fun updateUi(crime: Crime) {
        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.toString()
            crimeDate.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailsFragmentDirections.selectDate(crime.date)
                )
            }
            crimeSolved.isChecked = crime.isSolved
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    companion object {
        private const val TAG: String = "CrimeDetailsFragment"
    }
}