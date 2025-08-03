package com.example.seawatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AvvistamentiViewModel(private val repository:AvvistamentiRepository):ViewModel() {
    val all=repository.all
    fun insert(avvistamento:AvvistamentiDaCaricare)=viewModelScope.launch {
        repository.insert(avvistamento)
    }
    fun deleteAll()=viewModelScope.launch {
        repository.deleteAll()
    }
}