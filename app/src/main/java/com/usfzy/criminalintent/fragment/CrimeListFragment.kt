package com.usfzy.criminalintent.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.usfzy.criminalintent.adapter.CrimeListAdapter
import com.usfzy.criminalintent.databinding.FragmentCrimeListBinding
import com.usfzy.criminalintent.viewmodel.CrimeListViewModel

class CrimeListFragment : Fragment() {

    private var _binding: FragmentCrimeListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Binding should not be null"
        }

    private val crimeListViewModel: CrimeListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCrimeListBinding.inflate(inflater, container, false)

        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = CrimeListAdapter(crimes = crimeListViewModel.crimes)
        binding.crimeRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}