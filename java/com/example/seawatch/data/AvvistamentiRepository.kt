package com.example.seawatch

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class AvvistamentiRepository(private val avvistamentiDAO: AvvistamentiDAO) {
    val all: Flow<List<AvvistamentiDaCaricare>> = avvistamentiDAO.getAllSighting()
    @WorkerThread
    suspend fun insert(avvistamento: AvvistamentiDaCaricare){
        avvistamentiDAO.insert(avvistamento)
    }

    @WorkerThread
    suspend fun deleteAll(){
        avvistamentiDAO.deleteAll()
    }
}