package com.example.seawatch.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacesDAO {
    @Query("SELECT * FROM places")
    fun getPlaces(): Flow<List<Place>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(place: Place)
}