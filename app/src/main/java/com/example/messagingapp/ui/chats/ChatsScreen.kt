package com.example.messagingapp.ui.chats

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.messagingapp.data.model.Chat
import com.example.messagingapp.data.model.Message
import com.example.messagingapp.data.model.User
import com.example.messagingapp.data.model.dateString
import com.example.messagingapp.data.model.headerName
import com.example.messagingapp.data.model.timestampToString
import com.example.messagingapp.ui.components.FeedListItem
import com.example.messagingapp.ui.components.SearchResultListItem
import com.example.messagingapp.ui.navigation.MessagingAppBottomNavigation
import com.example.messagingapp.utils.Helpers.asTimestampToString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    onAddGroupChatClick: () -> Unit,
    onChatClick: (String?, String?) -> Unit,
    onBottomBarItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Chats") }) },
        bottomBar = { MessagingAppBottomNavigation(onBottomBarItemClick) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGroupChatClick) {
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
            ChatsScreenContent(onChatClick)
        }
    }
}

@Composable
fun ChatsScreenContent(
    onChatClick: (String?, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ChatsScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    SearchTextField(
        value = uiState.value.searchQuery,
        onValueChange = viewModel::updateSearchQuery,
        onClearIconClick = viewModel::clearSearchTextField
    )
    Spacer(modifier = modifier.height(8.dp))
    val users = uiState.value.users
    Divider(thickness = 0.5.dp)
    if (users.isNotEmpty()) {
        LazyColumn {
            items(items = users) { user ->
                SearchResultListItem(title = user.headerName(), content = user.tag,
                    modifier = modifier.clickable {
                        viewModel.clearSearchTextField()
                        onChatClick(user.docId!!, null)
                    })
            }
        }
    } else {
        val chatsMapEntries = uiState.value.chatsMap.entries
        LazyColumn {
            items(items = chatsMapEntries.toList()) { (chat, user) ->
                FeedListItem(
                    title = user?.headerName() ?: chat.groupInfo!!.name!!,
                    content = chat.lastMessage?.content ?: "No messages here yet",
                    date = chat.lastMessage?.dateString() ?:
                    chat.lastUpdated!!.asTimestampToString("HH:mm"),
                    modifier = modifier.clickable {
                        if (chat.isGroup!!) onChatClick(null, chat.docId)
                        else onChatClick(user!!.docId!!, chat.docId)
                    }
                )
            }
        }
    }
}

@Composable
fun SearchTextField(
    value: String?,
    onValueChange: (String) -> Unit,
    onClearIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(value = value ?: "", onValueChange = onValueChange) { innerTextField ->
        Row(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp)
                .height(36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
            Spacer(modifier = modifier.width(8.dp))
            Box(
                modifier = modifier.fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                innerTextField()
                if (value == null) {
                    Text(text = "Search", color = Color.DarkGray)
                }
            }
            value?.let {
                Spacer(modifier = modifier.weight(1f))
                IconButton(
                    onClick = onClearIconClick,
                    modifier = modifier.size(22.dp)
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                }
            }
        }
    }
}
