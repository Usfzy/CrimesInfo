package com.usfzy.criminalintent.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.usfzy.criminalintent.model.Crime
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CrimeDao {

    @Query("SELECT * FROM Crime")
    fun getCrimes(): Flow<List<Crime>>

    @Query("SELECT * FROM Crime WHERE id =(:id)")
    suspend fun getCrime(id: UUID): Crime

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrime(crime: Crime)

    @Delete
    suspend fun deleteCrime(crime: Crime)

    @Update()
    suspend fun updateCrime(crime: Crime)
}