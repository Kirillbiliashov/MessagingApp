package com.example.messagingapp.ui.channels

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.messagingapp.data.model.Channel
import com.example.messagingapp.ui.chats.SearchTextField
import com.example.messagingapp.ui.components.FeedListItem
import com.example.messagingapp.ui.navigation.MessagingAppBottomNavigation
import com.example.messagingapp.utils.Helpers.asTimestampToString
import com.google.firebase.database.collection.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelsScreen(
    onAddChannelClick: () -> Unit,
    onBottomBarItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ChannelsScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Channels") }) },
        bottomBar = {
            MessagingAppBottomNavigation(onBottomBarItemClick)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddChannelClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchTextField(
                value = uiState.value.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                onClearIconClick = viewModel::clearSearchTextField
            )
            Spacer(modifier = modifier.height(8.dp))
            Divider(thickness = 0.5.dp)
            val queryChannels = uiState.value.queryChannels
            if (queryChannels.isNotEmpty()) {
                QueryChannelsList(queryChannels = queryChannels)
            } else {
                UserChannelsList(userChannels = uiState.value.userChannels)
            }
        }
    }
}

@Composable
fun QueryChannelsList(queryChannels: List<Channel>, modifier: Modifier = Modifier) {
    LazyColumn {
        items(items = queryChannels) { channel ->
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.googleg_disabled_color_18),
                    contentDescription = null,
                    modifier = modifier.size(36.dp)
                )
                Spacer(modifier = modifier.width(16.dp))
                Box(modifier = modifier.fillMaxHeight()) {
                    Row(modifier = modifier.align(Alignment.CenterStart)) {
                        Column(modifier = modifier.fillMaxHeight()) {
                            Text(
                                text = channel.name!!, fontWeight = FontWeight.W500,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "@${channel.tag!!}, " +
                                        "${channel.subscribersCount} subscribers"
                            )
                        }
                        Spacer(modifier = modifier.weight(1f))
                    }
                    Divider(
                        modifier = modifier.align(Alignment.BottomEnd),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}


@Composable
fun UserChannelsList(
    userChannels: List<Channel>,
    modifier: Modifier = Modifier
) {
    LazyColumn {
        items(items = userChannels) { channel ->
            val lastPost = channel.lastPost
            FeedListItem(
                title = channel.name!!,
                content = if (lastPost != null) lastPost.content!! else "No posts yet",
                date = channel.lastUpdated!!.asTimestampToString("HH:mm")
            )
        }
    }
}
