package com.example.seawatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val repository:AvvistamentiRepository):ViewModelProvider.Factory {
    override fun <T:ViewModel> create(modelClass:Class<T>):T{
        if(modelClass.isAssignableFrom(AvvistamentiViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return AvvistamentiViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe di ViewModel sconosciuta!")
    }
}