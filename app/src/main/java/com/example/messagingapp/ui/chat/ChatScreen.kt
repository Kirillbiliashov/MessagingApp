package com.example.messagingapp.ui.chat

import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.messagingapp.data.model.Message
import com.example.messagingapp.data.model.User
import com.example.messagingapp.data.model.timestampToString
import com.example.messagingapp.ui.components.MessageTextField
import com.example.messagingapp.utils.Helpers
import com.example.messagingapp.utils.Helpers.asTimestampToString
import com.google.firebase.appcheck.interop.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ChatScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(topBar = {
        TopAppBar(
            title = {
                ChatScreenTopBarContent(participant = uiState.value.participant)
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
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
                Badge(
                    containerColor = MaterialTheme.colorScheme.outlineVariant,
                    modifier = modifier.padding(vertical = 8.dp)
                ) {
                    val displayDate = if (date == Helpers.currDate
                            .asTimestampToString("MM.dd.yyyy")
                    )
                        "Today" else date
                    Text(
                        text = displayDate,
                        modifier = modifier.padding(4.dp), fontSize = 12.sp
                    )
                }
                LazyColumn {
                    items(items = messages) { message ->
                        MessageRow(
                            message = message,
                            userId = viewModel.userId,
                            participantId = viewModel.participantId
                        )
                    }
                }
            }
            Spacer(modifier = modifier.weight(1f))
            MessageTextField(
                value = uiState.value.currentMessage,
                onValueChange = viewModel::updateMessageTextField,
                onSendIconClick = viewModel::sendMessage
            )
        }
    }
}

@Composable
fun ChatScreenTopBarContent(participant: User?, modifier: Modifier = Modifier) {
    participant?.let {
        val text = if (it.firstName != null)
            "${it.firstName} ${it.lastName}" else it.phoneNumber!!
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(text = text)
                Spacer(modifier = modifier.weight(1f))
                Image(
                    painter = painterResource(R.drawable.googleg_disabled_color_18),
                    contentDescription = null,
                    modifier = modifier.size(36.dp)
                )
            }
        }
    }
}

@Composable
fun MessageRow(
    message: Message,
    userId: String,
    participantId: String,
    modifier: Modifier = Modifier
) {
    val userRowModifier = if (message.senderId == userId)
        modifier.padding(start = 24.dp, end = 8.dp)
    else modifier.padding(start = 8.dp, end = 24.dp)
    Row(modifier = userRowModifier.fillMaxWidth()) {
        if (message.senderId == userId) {
            Spacer(modifier = modifier.weight(1f))
        }
        Card(modifier = modifier.padding(4.dp)) {
            Box {
                Column(
                    modifier = modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 4.dp)
                ) {
                    Text(text = message.timestampToString("HH:mm"), fontSize = 12.sp)
                }
                Column(
                    modifier = modifier
                        .padding(8.dp)
                        .padding(end = 36.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(text = message.content!!, fontSize = 16.sp)
                }
            }
        }
        if (message.senderId == participantId) {
            Spacer(modifier = modifier.weight(1f))
        }
    }
}
