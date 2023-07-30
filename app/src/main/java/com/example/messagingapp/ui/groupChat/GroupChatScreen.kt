package com.example.messagingapp.ui.groupChat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.messagingapp.data.model.Chat
import com.example.messagingapp.data.model.Message
import com.example.messagingapp.data.model.headerName
import com.example.messagingapp.data.model.timestampToString
import com.example.messagingapp.ui.components.BackNavigationIcon
import com.example.messagingapp.ui.components.DateBadge
import com.example.messagingapp.ui.components.MessageCard
import com.example.messagingapp.ui.components.MessageTextField
import com.example.messagingapp.utils.Helpers
import com.example.messagingapp.utils.Helpers.asTimestampToString
import com.google.firebase.appcheck.interop.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatScreen(
    onNavBarClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: GroupChatScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    val members = uiState.value.members
    Scaffold(topBar = {
        TopAppBar(title = {
            if (members.isNotEmpty()) {
                GroupChatScreenTopBar(
                    chat = uiState.value.chat,
                    membersCount = members.count(),
                    onTopBarClick = onNavBarClick
                )
            }
        }, navigationIcon = {
            BackNavigationIcon(onBackClick = onBackClick)
        })
    }) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val messagesByDateMap = uiState.value.messages.groupBy {
                it.timestampToString("MM.dd.yyyy")
            }
            messagesByDateMap.forEach { (date, messages) ->
                DateBadge(date = date)
                LazyColumn {
                    items(items = messages) { message ->
                        if (members.isNotEmpty()) {
                            val sender = members.first { it.docId == message.senderId }
                            GroupMessageRow(
                                message = message,
                                userId = viewModel.userId,
                                username = sender.headerName()
                            )
                        }
                    }
                }
            }
            Spacer(modifier = modifier.weight(1f))
            MessageTextField(
                value = uiState.value.currentMessage,
                onValueChange = viewModel::updateCurrentMessageTextField,
                onSendIconClick = viewModel::sendMessage
            )
        }
    }
}

@Composable
fun GroupMessageRow(
    message: Message,
    userId: String,
    username: String,
    modifier: Modifier = Modifier
) {
    val isCurrUserSender = message.senderId == userId
    val userRowModifier = if (isCurrUserSender)
        modifier.padding(start = 24.dp, end = 8.dp)
    else modifier.padding(start = 8.dp, end = 24.dp)
    Row(modifier = userRowModifier.fillMaxWidth()) {
        if (isCurrUserSender) {
            Spacer(modifier = modifier.weight(1f))
        }
        MessageCard(
            title = if (isCurrUserSender) null else username,
            content = message.content!!,
            timestamp = message.timestampToString("HH:mm")
        )
        if (!isCurrUserSender) {
            Spacer(modifier = modifier.weight(1f))
        }
    }
}

@Composable
fun GroupChatScreenTopBar(
    chat: Chat?,
    membersCount: Int,
    onTopBarClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTopBarClick(chat!!.docId!!) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (chat != null) {
            Column {
                Text(text = chat.groupInfo!!.name!!)
                Text(
                    text = "$membersCount members",
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
}