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
                UserCard(user = user, onUserClick = {
                    viewModel.clearSearchTextField()
                    onChatClick(user.docId!!, null)
                })
            }
        }
    } else {
        val chatsMapEntries = uiState.value.chatsMap.entries
        LazyColumn {
            items(items = chatsMapEntries.toList()) { (chat, user) ->
                ChatCard(chat = chat, participant = user, onChatClick = {
                    if (chat.isGroup!!) onChatClick(null, chat.docId)
                    else onChatClick(user!!.docId!!, chat.docId)
                })
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
            Box(modifier = modifier.fillMaxHeight(),
                contentAlignment = Alignment.CenterStart) {
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

@Composable
fun UserCard(
    user: User,
    onUserClick: () -> Unit = {},
    trailingComponent: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(4.dp)
                .clickable { onUserClick() }
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(com.google.firebase.database.collection.R.drawable.googleg_disabled_color_18),
                contentDescription = null,
                modifier = modifier.size(36.dp)
            )
            Spacer(modifier = modifier.width(16.dp))
            Box(modifier = modifier.fillMaxHeight()) {
                Row(modifier = modifier.align(Alignment.CenterStart)) {
                    Column(modifier = modifier.fillMaxHeight()) {
                        UserCardHeader(user = user)
                        if (user.tag != null) {
                            Text(text = "@${user.tag}")
                        }
                    }
                     Spacer(modifier = modifier.weight(1f))
                    trailingComponent()
                }
             Divider(modifier = modifier.align(Alignment.BottomEnd), thickness = 0.5.dp)
            }

        }

}

@Composable
fun ChatCard(
    chat: Chat,
    participant: User?,
    onChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .size(64.dp)
            .padding(start = 8.dp)
            .clickable { onChatClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(com.google.firebase.appcheck.interop.R.drawable.googleg_disabled_color_18),
            contentDescription = null,
            modifier = modifier.size(52.dp)
        )
        Spacer(modifier = modifier.width(16.dp))
        Box(modifier = modifier.fillMaxHeight()) {
            val cardModifier = Modifier
                .align(Alignment.CenterStart)
                .padding(end = 8.dp)
            if (participant != null) {
                UserChatCardContent(
                    lastMessage = chat.lastMessage!!,
                    participant = participant,
                    modifier = cardModifier
                )
            } else {
                GroupChatCardContent(chat = chat, modifier = cardModifier)
            }
            Divider(modifier = modifier.align(Alignment.BottomEnd), thickness = 0.5.dp)
        }
    }
}

@Composable
fun UserChatCardContent(
    lastMessage: Message, participant: User,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        UserCardHeader(
            user = participant,
            dateString = lastMessage.dateString()
        )
        Text(
            text = lastMessage.content ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun GroupChatCardContent(
    chat: Chat,
    modifier: Modifier = Modifier
) {
    val dateString = chat.lastUpdated!!.asTimestampToString("HH:mm")
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = chat.groupInfo!!.name!!,
                fontWeight = FontWeight.W500,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = dateString)
        }
        val messageContent = if (chat.lastMessage != null) chat.lastMessage.content!!
        else "No messages here yet"
        Text(text = messageContent)
    }
}

@Composable
fun UserCardHeader(
    user: User, dateString: String? = null,
    modifier: Modifier = Modifier
) {
    Row {
        Text(
            text = user.headerName(),
            fontWeight = FontWeight.W500,
            style = MaterialTheme.typography.titleMedium
        )
        if (dateString != null) {
            Spacer(modifier = modifier.weight(1f))
            Text(text = dateString)
        }
    }

}