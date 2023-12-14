package com.usfzy.criminalintent.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.usfzy.criminalintent.databinding.FragmentCrimeDetailsBinding
import com.usfzy.criminalintent.model.Crime
import java.util.Date
import java.util.UUID

class CrimeDetailsFragment : Fragment() {

    private lateinit var crime: Crime
    private var _binding: FragmentCrimeDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime(
            id = UUID.randomUUID(),
            title = "",
            date = Date(),
            isSolved = false,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCrimeDetailsBinding.inflate(inflater, container, false)

        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                crime = crime.copy(title = text.toString())
            }
            crimeDate.apply {
                text = Date().toString()
                isEnabled = false
            }
            crimeSolved.setOnCheckedChangeListener { _, isChanged ->
                crime = crime.copy(isSolved = isChanged)
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}