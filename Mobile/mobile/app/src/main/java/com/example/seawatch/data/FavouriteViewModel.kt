package com.example.seawatch.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repository: FavouriteRepository): ViewModel() {
    val all=repository.all

    fun insert(favourite: Favourite)=viewModelScope.launch {
        repository.insert(favourite)
    }

    fun deleteAll()=viewModelScope.launch {
        repository.deleteAll()
    }

    fun deletePref(favourite: String, utente: String)=viewModelScope.launch {
        repository.deletePref(favourite, utente)
    }

}