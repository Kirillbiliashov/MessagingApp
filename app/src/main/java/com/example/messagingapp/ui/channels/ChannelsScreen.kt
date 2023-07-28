package com.example.messagingapp.ui.channels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.messagingapp.ui.navigation.MessagingAppBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelsScreen(
    onAddChannelClick: () -> Unit,
    onBottomBarItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Channels")
        }
    }
}