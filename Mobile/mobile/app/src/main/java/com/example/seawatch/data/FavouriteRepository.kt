package com.example.seawatch.data

import androidx.annotation.WorkerThread
import com.example.seawatch.AvvistamentiDAO
import com.example.seawatch.AvvistamentiDaCaricare
import kotlinx.coroutines.flow.Flow

class FavouriteRepository(private val favouriteDAO: FavouriteDAO) {
    val all: Flow<List<Favourite>> = favouriteDAO.getAllFavourite()

    @WorkerThread
    suspend fun insert(favourite: Favourite){
        favouriteDAO.insert(favourite)
    }

    @WorkerThread
    suspend fun deleteAll(){
        favouriteDAO.deleteAll()
    }

    @WorkerThread
    suspend fun deletePref(favourite: String, utente:String){
        favouriteDAO.deletePref(favourite, utente)
    }
}