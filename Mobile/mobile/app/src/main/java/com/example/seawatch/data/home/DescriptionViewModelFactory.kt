package com.example.seawatch.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DescriptionViewModelFactory(private val repository: DescriptionRepository): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass:Class<T>):T{
        if(modelClass.isAssignableFrom(DescriptionViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return DescriptionViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe di ViewModel sconosciuta!")
    }
}