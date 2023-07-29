package com.example.messagingapp.ui.channel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.model.Channel
import com.example.messagingapp.data.model.Post
import com.example.messagingapp.data.service.ChannelService
import com.example.messagingapp.data.service.PostService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val channel: Channel? = null,
    val posts: List<Post> = listOf(),
    val currentPost: String = ""
)

@HiltViewModel
class ChannelScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val channelService: ChannelService,
    private val postService: PostService
) : ViewModel() {

    private val channelId: String = requireNotNull(savedStateHandle["channelId"])

    private val channelFlow = MutableStateFlow<Channel?>(null)
    private val currentPostFlow = MutableStateFlow("")

    val uiState = combine(
        channelFlow,
        postService.getChannelPostsFlow(channelId),
        currentPostFlow
    ) { channel, posts, currentPost ->
        UiState(channel = channel, posts = posts, currentPost = currentPost)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState()
    )

    init {
        viewModelScope.launch {
            channelFlow.value = channelService.getChannelByDocId(channelId)
        }
    }

    fun updatePostTextField(newValue: String) {
        currentPostFlow.value = newValue
    }

    fun publishPost() {
        val post = Post(
            content = uiState.value.currentPost
        )
        currentPostFlow.value = ""
        viewModelScope.launch {
            postService.savePost(channelId, post)
        }
    }

}