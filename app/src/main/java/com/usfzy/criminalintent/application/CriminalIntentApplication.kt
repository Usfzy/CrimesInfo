package com.usfzy.criminalintent.application

import android.app.Application
import com.usfzy.criminalintent.repository.CrimeRepository

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        CrimeRepository.initialize(this)
    }
}