package com.example.messagingapp.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.appcheck.interop.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ChatScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    val participant = uiState.value.participant
    Scaffold(topBar = {
        TopAppBar(
            title = {
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
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            })
    }) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn {
                items(items = uiState.value.messages) { message ->
                    val userRowModifier = if (message.senderId == viewModel.userId)
                        modifier.padding(start = 24.dp, end = 8.dp)
                    else modifier.padding(start = 8.dp, end = 24.dp)
                    Row(modifier = userRowModifier.fillMaxWidth()) {
                        if (message.senderId == viewModel.userId) {
                            Spacer(modifier = modifier.weight(1f))
                        }
                        Card(
                            modifier = modifier
                                .padding(4.dp)
                        ) {
                            Column(
                                modifier = modifier
                                    .padding(8.dp)
                            ) {
                                Text(text = message.content!!)
                            }
                        }
                        if (message.senderId == viewModel.participantId) {
                            Spacer(modifier = modifier.weight(1f))
                        }
                    }
                }
            }
            Spacer(modifier = modifier.weight(1f))
            OutlinedTextField(
                value = uiState.value.currentMessage,
                onValueChange = viewModel::updateMessageTextField,
                placeholder = { Text(text = "Type your message...") },
                trailingIcon = {
                    val iconColor = if (uiState.value.currentMessage.isEmpty())
                        LocalContentColor.current
                    else MaterialTheme.colorScheme.primary
                    IconButton(onClick = viewModel::sendMessage) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null, tint = iconColor
                        )
                    }
                },
                modifier = modifier
                    .fillMaxWidth(0.95f)
                    .padding(bottom = 8.dp)
            )
        }
    }
}