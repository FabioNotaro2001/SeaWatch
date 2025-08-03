package com.example.seawatch.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seawatch.LocationDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val repository: PlacesRepository
) : ViewModel() {

    val places = repository.places

    fun addNewPlace(place: Place) = viewModelScope.launch {
        repository.insertNewPlace(place)
        resetGPSPlace()
    }

    private var _placeSelected: Place? = null
    val placeSelected
        get() = _placeSelected

    fun selectPlace(place: Place) {
        _placeSelected = place
    }


    private var _placeFromGPS = mutableStateOf<String>("")
    val placeFromGPS
        get() = _placeFromGPS

    fun setGPSPlace(place: LocationDetails) {
        try {
            _placeFromGPS.value = (place.latitude.toString().substring(0..8)+" "+place.longitude.toString().substring(0..8))
        } catch (e: Exception){
            _placeFromGPS.value = (place.latitude.toString()+" "+place.longitude.toString())
        }
    }

    private fun resetGPSPlace() {
        _placeFromGPS.value = ""
    }
}