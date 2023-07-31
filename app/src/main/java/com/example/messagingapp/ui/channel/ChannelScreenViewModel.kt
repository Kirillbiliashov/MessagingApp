package com.example.messagingapp.ui.channel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.model.Channel
import com.example.messagingapp.data.model.Post
import com.example.messagingapp.data.model.Reaction
import com.example.messagingapp.data.model.ReactionType
import com.example.messagingapp.data.model.User
import com.example.messagingapp.data.service.AuthenticationService
import com.example.messagingapp.data.service.ChannelService
import com.example.messagingapp.data.service.PostService
import com.example.messagingapp.data.service.ReactionService
import com.example.messagingapp.data.service.UserProfileService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val currUser: User? = null,
    val channel: Channel? = null,
    val posts: List<Post> = listOf(),
    val userReactions: List<Reaction> = listOf(),
    val currentPost: String = ""
) {
    val isUserAdmin: Boolean
        get() = currUser?.docId == channel?.ownerId

    val isUserSubscribed: Boolean
        get() = currUser?.channelTags?.contains(channel?.tag) ?: false


    fun hasReaction(type: String, post: Post) = userReactions.any { reaction ->
        reaction.postId == post.docId && reaction.type == type
    }

}

@HiltViewModel
class ChannelScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val channelService: ChannelService,
    private val postService: PostService,
    private val userProfileService: UserProfileService,
    private val reactionService: ReactionService
) : ViewModel() {

    private val channelId: String = requireNotNull(savedStateHandle["channelId"])

    private val channelFlow = MutableStateFlow<Channel?>(null)
    private val currentPostFlow = MutableStateFlow("")
    private val currUserFlow = userProfileService.currentUserFlow

    val uiState = combine(
        currUserFlow,
        channelFlow,
        postService.getChannelPostsFlow(channelId),
        currentPostFlow
    ) { currUser, channel, posts, currentPost ->
        UiState(
            currUser = currUser, channel = channel,
            posts = posts, currentPost = currentPost
        )
    }.combine(reactionService.getChannelReactionsFlow(channelId)) { uiState, reactions ->
        println("new reactions: $reactions")
        uiState.copy(userReactions = reactions)
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

    fun subscribeToChannel() {
        viewModelScope.launch {
            channelService.subscribeToChannel(
                user = uiState.value.currUser!!,
                channel = uiState.value.channel!!
            )
        }
    }

    fun reactToPost(postId: String, reactionType: ReactionType) {
        val reaction = Reaction(
            channelId = channelId,
            postId = postId,
            type = reactionType.toString()
        )
        viewModelScope.launch {
            reactionService.addReaction(reaction)
        }
    }

}