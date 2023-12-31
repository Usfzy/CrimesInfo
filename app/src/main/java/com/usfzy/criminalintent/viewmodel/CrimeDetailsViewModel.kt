package com.usfzy.criminalintent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.usfzy.criminalintent.model.Crime
import com.usfzy.criminalintent.repository.CrimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class CrimeDetailsViewModel(crimeId: UUID) : ViewModel() {

    private val crimeRepository = CrimeRepository.get()

    private var _crime: MutableStateFlow<Crime?> = MutableStateFlow(null)
    val crime: StateFlow<Crime?> = _crime.asStateFlow()

    init {
        viewModelScope.launch {
            _crime.value = crimeRepository.getCrime(crimeId)
        }
    }

    fun updateCrime(onUpdate: (Crime) -> Crime) {
        _crime.update { oldCrime ->
            oldCrime?.let { onUpdate(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()

        crime.value?.let {
            crimeRepository.updateCrime(it)
        }
    }
}

class CrimeDetailsViewModelFactory(private val uuid: UUID) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CrimeDetailsViewModel(uuid) as T
    }

}