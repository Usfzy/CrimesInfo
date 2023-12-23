package com.usfzy.criminalintent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usfzy.criminalintent.model.Crime
import com.usfzy.criminalintent.repository.CrimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CrimeListViewModel : ViewModel() {

    private val crimeRepository: CrimeRepository = CrimeRepository.get()

    companion object {
        private const val TAG = "CrimeListViewModel"
    }

    private val _crimes: MutableStateFlow<List<Crime>> = MutableStateFlow(emptyList())
    val crimes: StateFlow<List<Crime>>
        get() = _crimes.asStateFlow()

    init {
        viewModelScope.launch {
            crimeRepository.getCrimes().collect {
                _crimes.value = it
            }
        }
    }
    private suspend fun insertCrimes() {
        for (i in 0 until 100) {
            val crime = Crime(
                id = UUID.randomUUID(),
                title = "Crime #$i",
                date = Date(),
                isSolved = i % 2 == 0,
                requiresPolice = i % 2 == 0,
            )
            crimeRepository.insertCrime(crime)
        }
    }
}


