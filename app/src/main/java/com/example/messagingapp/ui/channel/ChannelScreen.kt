package com.example.messagingapp.ui.channel

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.messagingapp.data.model.Channel
import com.example.messagingapp.data.model.Post
import com.example.messagingapp.data.model.ReactionType
import com.example.messagingapp.data.model.timestampToString
import com.example.messagingapp.ui.chat.MessageRow
import com.example.messagingapp.ui.components.BackNavigationIcon
import com.example.messagingapp.ui.components.DateBadge
import com.example.messagingapp.ui.components.MessageCard
import com.example.messagingapp.ui.components.MessageTextField
import com.example.messagingapp.utils.Helpers.asTimestampToString
import com.google.firebase.appcheck.interop.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ChannelScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    val channel = uiState.value.channel
    Scaffold(
        topBar = {
            ChannelScreenTopBar(onBackClick = onBackClick, channel = channel)
        },
        bottomBar = {
            ChannelScreenBottomBar(
                showMessageTextField = uiState.value.isUserAdmin,
                showJoinButton = !uiState.value.isUserSubscribed,
                onJoinClick = viewModel::subscribeToChannel
            ) {
                MessageTextField(
                    value = uiState.value.currentPost,
                    onValueChange = viewModel::updatePostTextField,
                    onSendIconClick = viewModel::publishPost,
                )
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val messagesByDateMap = uiState.value.posts.groupBy {
                it.postedAt!!.asTimestampToString("MM.dd.yyyy")
            }
            messagesByDateMap.forEach { (date, posts) ->
                DateBadge(date = date)
                LazyColumn {
                    items(items = posts) { post ->
                        ChannelPostRow(
                            post = post, channel = channel,
                            liked = uiState.value.hasReaction(ReactionType.LIKE.toString(), post),
                            disliked = uiState.value.hasReaction(
                                ReactionType.DISLIKE.toString(),
                                post
                            ),
                            onReactionClick = { type ->
                                viewModel.reactToPost(post.docId!!, type)
                            }
                        )
                    }
                }
            }
            Spacer(modifier = modifier.weight(1f))
        }
    }
}

@Composable
fun ChannelScreenBottomBar(
    showMessageTextField: Boolean,
    showJoinButton: Boolean,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier,
    textField: @Composable () -> Unit = {}
) {
    if (showMessageTextField) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxWidth()
        ) {
            textField()
        }
    } else if (showJoinButton) {
        Box(modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable {
                onJoinClick()
            }
        ) {
            Divider(modifier = modifier.align(Alignment.TopCenter), thickness = 0.5.dp)
            Text(
                text = "JOIN", modifier = modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelScreenTopBar(
    onBackClick: () -> Unit,
    channel: Channel?,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            channel?.let { channel ->
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = channel.name!!)
                        Text(
                            text = "${channel.subscribersCount} subscribers",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.W300
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.googleg_disabled_color_18),
                        contentDescription = null,
                        modifier = modifier.size(36.dp)
                    )
                }
            }
        },
        navigationIcon = {
            BackNavigationIcon(onBackClick = onBackClick)
        })
}

@Composable
fun ChannelPostRow(
    post: Post,
    channel: Channel?,
    liked: Boolean,
    disliked: Boolean,
    onReactionClick: (ReactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(start = 8.dp, end = 24.dp)
            .fillMaxWidth()
    ) {
        PostCard(
            channelName = channel?.name,
            post = post,
            liked = liked,
            disliked = disliked,
            onReactionClick = onReactionClick
        )
        Spacer(modifier = modifier.weight(1f))
    }
}

@Composable
fun PostCard(
    channelName: String?,
    modifier: Modifier = Modifier,
    post: Post,
    liked: Boolean, disliked: Boolean,
    onReactionClick: (ReactionType) -> Unit
) {
    Card(modifier = modifier.padding(4.dp)) {
        Box {
            Column(
                modifier = modifier
                    .align(Alignment.TopStart)
                    .padding(end = 48.dp, start = 8.dp, bottom = 4.dp, top = 4.dp)
            ) {
                if (channelName != null) {
                    Text(text = channelName)
                }
                Text(text = post.content!!, fontSize = 16.sp)
                Row {
                    val likeColor = if (liked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant
                    val dislikeColor = if (disliked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = likeColor
                        ),
                        modifier = modifier.clickable {
                            onReactionClick(ReactionType.LIKE)
                        }
                    ) {
                        Text(
                            text = "\uD83D\uDC4D ${post.likesCount}",
                            modifier = modifier.padding(4.dp),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = modifier.width(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = dislikeColor
                        ),
                        modifier = modifier.clickable {
                            onReactionClick(ReactionType.DISLIKE)
                        }
                    ) {
                        Text(
                            text = "\uD83D\uDC4E ${post.dislikesCount}",
                            modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                            fontSize = 12.sp
                        )
                    }

                }
            }
            Column(
                modifier = modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 4.dp, start = 4.dp)
            ) {
                Text(
                    text = post.postedAt!!.asTimestampToString("HH:mm"),
                    fontSize = 12.sp
                )
            }
        }
    }
}
