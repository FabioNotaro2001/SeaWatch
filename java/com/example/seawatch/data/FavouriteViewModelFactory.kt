package com.example.seawatch.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FavouriteViewModelFactory(private val repository: FavouriteRepository): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass:Class<T>):T{
        if(modelClass.isAssignableFrom(FavouriteViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return FavouriteViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe di ViewModel sconosciuta!")
    }
}