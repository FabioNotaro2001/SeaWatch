package com.example.seawatch

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class AvvistamentiViewRepository(private val avvistamentiViewDAO: AvvistamentiViewDAO) {
    val all: Flow<List<AvvistamentiDaVedere>> = avvistamentiViewDAO.getAllSighting()
    @WorkerThread
    suspend fun insert(avvistamento: AvvistamentiDaVedere){
        avvistamentiViewDAO.insert(avvistamento)
    }

    @WorkerThread
    suspend fun deleteAll(){
        avvistamentiViewDAO.deleteAll()
    }
}