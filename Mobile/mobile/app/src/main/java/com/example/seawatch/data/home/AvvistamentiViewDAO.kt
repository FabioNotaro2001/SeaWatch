package com.example.seawatch

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AvvistamentiViewDAO {
    @Query("SELECT * FROM avvistamentiDaVedere")
    fun getAllSighting(): Flow<List<AvvistamentiDaVedere>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(avvistamento:AvvistamentiDaVedere)

    @Query("DELETE FROM avvistamentiDaVedere")
    suspend fun deleteAll()
}