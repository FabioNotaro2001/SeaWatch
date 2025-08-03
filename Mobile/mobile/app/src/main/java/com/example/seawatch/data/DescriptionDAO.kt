package com.example.seawatch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.seawatch.AvvistamentiDaCaricare
import kotlinx.coroutines.flow.Flow

@Dao
interface DescriptionDAO {
    @Query("SELECT * FROM descriptions")
    fun getAllDescriptions(): Flow<List<Description>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(descrizione: Description)

    @Query("DELETE FROM descriptions")
    suspend fun deleteAll()

    @Query("SELECT descrizione FROM descriptions WHERE animale=:animale AND specie=:specie")
    fun getDescription(animale:String, specie:String): String

    @Query("SELECT COUNT(*) FROM descriptions")
    suspend fun getCount(): Int

}