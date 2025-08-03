package com.example.seawatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AvvistamentiViewModelFactory(private val repository:AvvistamentiViewRepository):ViewModelProvider.Factory {
    override fun <T:ViewModel> create(modelClass:Class<T>):T{
        if(modelClass.isAssignableFrom(AvvistamentiViewViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return AvvistamentiViewViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe di ViewModel sconosciuta!")
    }
}