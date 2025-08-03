package com.example.seawatch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.seawatch.AvvistamentiDaCaricare
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDAO {
    @Query("SELECT * FROM favourite")
    fun getAllFavourite(): Flow<List<Favourite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preferito: Favourite)

    @Query("DELETE FROM favourite")
    suspend fun deleteAll()

    @Query("DELETE FROM favourite WHERE avvistamento=:preferito AND utente=:utente")
    suspend fun deletePref(preferito: String, utente: String)
}