package com.example.seawatch.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class PlacesRepository(private val placesDAO:PlacesDAO) {

    val places: Flow<List<Place>> = placesDAO.getPlaces()

    //@WorkerThread Denotes that the annotated method should only be called on a worker thread.
    //By default Room runs suspend queries off the main thread
    @WorkerThread
    suspend fun insertNewPlace(place: Place) {
        placesDAO.insert(place)
    }
}