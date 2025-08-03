package com.example.seawatch.data

import androidx.annotation.WorkerThread
import com.example.seawatch.AvvistamentiDAO
import com.example.seawatch.AvvistamentiDaCaricare
import kotlinx.coroutines.flow.Flow

class DescriptionRepository(private val descriptionDAO: DescriptionDAO) {
    val all: Flow<List<Description>> = descriptionDAO.getAllDescriptions()

    @WorkerThread
    suspend fun insert(description: Description){
        descriptionDAO.insert(description)
    }

    @WorkerThread
    suspend fun deleteAll(){
        descriptionDAO.deleteAll()
    }

    @WorkerThread
    suspend fun getDescription(animale:String, specie:String):String{
        return descriptionDAO.getDescription(animale, specie)
    }

    @WorkerThread
    suspend fun getCount():Int{
        return descriptionDAO.getCount()
    }


}