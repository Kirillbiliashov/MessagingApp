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
import com.example.messagingapp.ui.chats.SearchTextField
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
            LazyColumn {
                items(items = uiState.value.userChannels) { channel ->
                    Row(
                        modifier = modifier
                            .fillMaxSize()
                            .size(64.dp)
                            .padding(start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.googleg_disabled_color_18),
                            contentDescription = null,
                            modifier = modifier.size(52.dp)
                        )
                        Spacer(modifier = modifier.width(16.dp))
                        Box(modifier = modifier.fillMaxHeight()) {
                            Column(
                                modifier = modifier
                                    .align(Alignment.CenterStart)
                                    .padding(end = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = channel.name!!,
                                        fontWeight = FontWeight.W500,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = modifier.weight(1f))
                                    val dateString =
                                        channel.lastUpdated!!.asTimestampToString("HH:mm")
                                    Text(text = dateString)
                                }
                                val lastPost = channel.lastPost
                                if (lastPost != null) {
                                    Text(text = lastPost.content!!)
                                } else {
                                    Text(text = "No posts yet")
                                }
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
    }
}