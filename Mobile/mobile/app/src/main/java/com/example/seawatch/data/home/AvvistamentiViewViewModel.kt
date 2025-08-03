package com.example.seawatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AvvistamentiViewViewModel(private val repository:AvvistamentiViewRepository):ViewModel() {
    val all=repository.all
    fun insert(avvistamento:AvvistamentiDaVedere)=viewModelScope.launch {
        repository.insert(avvistamento)
    }
    fun deleteAll()=viewModelScope.launch {
        repository.deleteAll()
    }
}