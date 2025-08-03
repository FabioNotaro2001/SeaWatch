package com.example.seawatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val theme = settingsRepository.preferenceFlow

    fun saveTheme(theme:String) {
        viewModelScope.launch {
            settingsRepository.saveToDataStore(theme)
        }
    }
}