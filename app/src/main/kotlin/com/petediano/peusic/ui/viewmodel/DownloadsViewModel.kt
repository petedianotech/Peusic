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
            val result = UrlValidator.validate(rawUrl)
            if (!result.isValid) {
                _error.value = result.reason ?: "Invalid URL"
                return@launch
            }
            _error.value = null
            try {
                repo.enqueue(result.normalizedUrl!!, result.suggestedFileName ?: "audio")
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to start download"
            }
        }
    }
}
