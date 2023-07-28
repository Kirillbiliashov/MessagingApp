package com.example.messagingapp.ui.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.model.Channel
import com.example.messagingapp.data.service.ChannelService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val userChannels: List<Channel> = listOf(),
    val queryChannels: List<Channel> = listOf(),
    val searchQuery: String? = null
)

@HiltViewModel
class ChannelsScreenViewModel @Inject constructor(
    private val channelService: ChannelService
) : ViewModel() {

    private val searchQueryFlow = MutableStateFlow<String?>(null)
    private val queryChannelsFlow = MutableStateFlow(listOf<Channel>())

    val uiState = combine(
        channelService.userChannelsFlow,
        queryChannelsFlow,
        searchQueryFlow
    ) { channels, queryChannels, searchQuery ->
        UiState(
            userChannels = channels,
            searchQuery = searchQuery,
            queryChannels = queryChannels
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState()
        )

    fun updateSearchQuery(newValue: String) {
        searchQueryFlow.value = newValue
        viewModelScope.launch {
            queryChannelsFlow.value = channelService.getChannelsByQuery(newValue)
        }
    }

    fun clearSearchTextField() {
        searchQueryFlow.value = null
        queryChannelsFlow.value = listOf()
    }

}