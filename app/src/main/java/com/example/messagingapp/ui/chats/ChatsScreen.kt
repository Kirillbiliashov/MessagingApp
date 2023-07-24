package com.example.messagingapp.ui.chats

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.messagingapp.data.model.firebase.Chat
import com.example.messagingapp.data.model.firebase.User
import com.example.messagingapp.data.model.firebase.timestampToString
import com.example.messagingapp.ui.navigation.MessagingAppBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    onAddGroupChatClick: () -> Unit,
    onUserClick: (String, String?) -> Unit,
    onBottomBarItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ChatsScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
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
            SearchTextField(
                value = uiState.value.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                onClearIconClick = viewModel::clearSearchTextField
            )
            Spacer(modifier = modifier.height(8.dp))
            if (uiState.value.users.isNotEmpty()) {
                LazyColumn {
                    items(items = uiState.value.users) { user ->
                        UserCard(user = user, onUserClick = {
                            viewModel.clearSearchTextField()
                            onUserClick(user.docId!!, null)
                        })
                    }
                }
            } else {
                LazyColumn {
                    items(items = uiState.value.chatsMap.entries.toList()) { (user, chat) ->
                        ChatCard(chat = chat, participant = user, onChatClick = {
                            onUserClick(user.docId!!, chat.docId)
                        })
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
    value: String?,
    onValueChange: (String) -> Unit,
    onClearIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            value?.let {
                IconButton(onClick = onClearIconClick) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                }
            }
        },
        value = value ?: "",
        onValueChange = onValueChange,
        placeholder = {
            Column(modifier = modifier.fillMaxHeight()) {
                Text(text = "Search")
            }
        },
        modifier = modifier
            .fillMaxWidth(0.95F)
            .height(52.dp)
    )
}

@Composable
fun UserCard(
    user: User,
    onUserClick: () -> Unit = {},
    showCheckbox: Boolean = false,
    onCheckboxClick: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                onUserClick()
            },
        shape = RoundedCornerShape(0.dp),
        border = CardDefaults.outlinedCardBorder(enabled = false)
    ) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(4.dp)
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(com.google.firebase.database.collection.R.drawable.googleg_disabled_color_18),
                contentDescription = null,
                modifier = modifier.size(36.dp)
            )
            Spacer(modifier = modifier.width(16.dp))
            Column(modifier = modifier.fillMaxHeight()) {
                UserCardHeader(user = user)
                if (user.tag != null) {
                    Text(text = "@${user.tag}")
                }
            }
            if (showCheckbox) {
                val checked = remember { mutableStateOf(false) }
                Spacer(modifier = modifier.weight(1f))
                Checkbox(checked = checked.value,
                    onCheckedChange = {
                        val newValue = !checked.value
                        checked.value = newValue
                        onCheckboxClick(newValue)
                    })
            }
        }
    }
}

@Composable
fun ChatCard(
    chat: Chat, participant: User,
    onChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        shape = RoundedCornerShape(0.dp),
        border = CardDefaults.outlinedCardBorder(enabled = false),
        modifier = modifier.clickable { onChatClick() }
    ) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(4.dp)
                .padding(horizontal = 12.dp),
        ) {
            Image(
                painter = painterResource(com.google.firebase.appcheck.interop.R.drawable.googleg_disabled_color_18),
                contentDescription = null,
                modifier = modifier.size(52.dp)
            )
            Spacer(modifier = modifier.width(16.dp))
            Column(modifier = modifier.fillMaxHeight()) {
                UserCardHeader(
                    user = participant,
                    dateString = chat.lastMessage!!.timestampToString("HH:mm")
                )
                Text(
                    text = chat.lastMessage.content ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun UserCardHeader(
    user: User, dateString: String? = null,
    modifier: Modifier = Modifier
) {
    Row {
        if (user.firstName != null) {
            Text(
                text = "${user.firstName} ${user.lastName}",
                fontWeight = FontWeight.W500,
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            Text(
                text = user.phoneNumber!!,
                fontWeight = FontWeight.W600,
                style = MaterialTheme.typography.titleMedium
            )
        }
        if (dateString != null) {
            Spacer(modifier = modifier.weight(1f))
            Text(text = dateString)
        }
    }

}