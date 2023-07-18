package com.example.messagingapp.ui.chats

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.messagingapp.R
import com.example.messagingapp.ui.navigation.MessagingAppBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    onBottomBarItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ChatsScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Chats") }) },
        bottomBar = { MessagingAppBottomNavigation(onBottomBarItemClick) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    uiState.value.searchQuery?.let {
                        IconButton(onClick = viewModel::clearSearchTextField) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                value = uiState.value.searchQuery ?: "",
                onValueChange = viewModel::updateSearchQuery,
                placeholder = {
                    Column(modifier = modifier.fillMaxHeight()) {
                        Text(text = "Search")
                    }
                },
                modifier = modifier
                    .fillMaxWidth(0.95F)
                    .height(52.dp)
            )
            if (uiState.value.users.isNotEmpty()) {
                Spacer(modifier = modifier.height(8.dp))
                LazyColumn {
                    items(items = uiState.value.users) { user ->
                        OutlinedCard(
                            modifier = modifier
                                .fillMaxSize(),
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
                                    painter = painterResource(com.google.firebase.appcheck.interop.R.drawable.googleg_disabled_color_18),
                                    contentDescription = null,
                                    modifier = modifier.size(36.dp)
                                )
                                Spacer(modifier = modifier.width(16.dp))
                                Column(modifier = modifier.fillMaxHeight()) {
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
                                    if (user.tag != null) {
                                        Text(text = "@${user.tag}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}