package com.example.messagingapp.ui.channel

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.example.messagingapp.data.model.timestampToString
import com.example.messagingapp.ui.chat.DateBadge
import com.example.messagingapp.ui.chat.MessageRow
import com.example.messagingapp.ui.components.BackNavigationIcon
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    uiState.value.channel?.let { channel ->
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
                        Row(
                            modifier = modifier
                                .padding(start = 8.dp, end = 24.dp)
                                .fillMaxWidth()
                        ) {
                            Card(modifier = modifier.padding(4.dp)) {
                                Box {
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
                                    Column(
                                        modifier = modifier
                                            .padding(
                                                top = 16.dp,
                                                bottom = 4.dp, end = 44.dp, start = 8.dp
                                            )
                                    ) {
                                        Text(text = post.content!!, fontSize = 16.sp)
                                    }
                                    Column(
                                        modifier = modifier
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = uiState.value.channel!!.name!!)
                                    }
                                }
                            }
                            Spacer(modifier = modifier.weight(1f))
                        }
                    }
                }
            }
            Spacer(modifier = modifier.weight(1f))
            MessageTextField(
                value = uiState.value.currentPost,
                onValueChange = viewModel::updatePostTextField,
                onSendIconClick = viewModel::publishPost
            )
        }
    }
}