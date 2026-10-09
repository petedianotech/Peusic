package com.petediano.peusic.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.petediano.peusic.data.download.DownloadRepository
import com.petediano.peusic.data.download.UrlValidator
import com.petediano.peusic.domain.model.DownloadItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DownloadsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = DownloadRepository(application)

    val downloads: StateFlow<List<DownloadItem>> = repo.observeDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun enqueue(rawUrl: String) {
        viewModelScope.launch {
            val validation = UrlValidator.validate(rawUrl)
            if (!validation.isValid) {
                _error.value = validation.reason ?: "Invalid URL"
                return@launch
            }
            _error.value = null
            val result = repo.enqueue(validation.normalizedUrl!!, validation.suggestedFileName)
            result.onFailure { e ->
                _error.value = e.message ?: "Failed to start download"
            }
        }
    }
}
