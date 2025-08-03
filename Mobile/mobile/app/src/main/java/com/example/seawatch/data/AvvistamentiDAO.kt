package com.example.seawatch

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AvvistamentiDAO {
    @Query("SELECT * FROM avvistamentiDaCaricare")
    fun getAllSighting(): Flow<List<AvvistamentiDaCaricare>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(avvistamento:AvvistamentiDaCaricare)

    @Query("DELETE FROM avvistamentiDaCaricare")
    suspend fun deleteAll()
}