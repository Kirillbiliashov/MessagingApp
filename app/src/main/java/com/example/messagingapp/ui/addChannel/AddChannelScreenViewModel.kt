package com.example.messagingapp.ui.addChannel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.model.Channel
import com.example.messagingapp.data.service.ChannelService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val name: String = "",
    val description: String? = null,
    val tag: String = ""
)

@HiltViewModel
class AddChannelScreenViewModel @Inject constructor(
    private val channelService: ChannelService
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState

    fun updateNameTextFieldValue(newValue: String) {
        _uiState.update { it.copy(name = newValue) }
    }

    fun updateDescriptionFieldValue(newValue: String) {
        _uiState.update { it.copy(description = newValue) }
    }

    fun updateTagTextFieldValue(newValue: String) {
        _uiState.update { it.copy(tag = newValue) }
    }

    fun createChannel() {
        val channel = Channel(
            name = _uiState.value.name,
            description = _uiState.value.description,
            tag = _uiState.value.tag,
            subscribersCount = 1
        )
        viewModelScope.launch {
            channelService.saveChannel(channel)
        }
    }

}