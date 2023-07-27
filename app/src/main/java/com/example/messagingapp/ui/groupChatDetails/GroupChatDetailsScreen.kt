package com.example.messagingapp.ui.groupChatDetails

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.messagingapp.data.model.headerName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatDetailsScreen(modifier: Modifier = Modifier) {
    val viewModel: GroupChatMembersViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Details")
            }, navigationIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            })
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(com.google.firebase.appcheck.interop.R.drawable.googleg_disabled_color_18),
                contentDescription = null,
                modifier = modifier.size(80.dp)
            )
            val chat = uiState.value.chat
            if (chat != null) {
                Text(
                    text = chat.groupInfo!!.name!!,
                    style = MaterialTheme.typography.displaySmall, fontSize = 30.sp
                )
                Spacer(modifier = modifier.height(8.dp))
                Text(
                    text = "@${chat.groupInfo.tag!!}",
                    style = MaterialTheme.typography.bodyLarge, fontSize = 16.sp
                )
            }
            Spacer(modifier = modifier.height(8.dp))
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Members", modifier = modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(modifier = modifier.height(8.dp))
            Divider()
            LazyColumn {
                items(items = uiState.value.members) { user ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Spacer(modifier = modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = com.google.firebase.firestore.ktx.R.drawable.googleg_disabled_color_18),
                            contentDescription = null,
                            modifier = modifier
                                .size(40.dp)
                        )
                        Spacer(modifier = modifier.width(8.dp))
                        Box(modifier = modifier.fillMaxHeight()) {
                            Text(
                                text = user.headerName(), fontSize = 16.sp,
                                modifier = modifier.align(Alignment.CenterStart)
                            )
                            Divider(modifier = modifier.align(Alignment.BottomEnd))
                        }
                    }
                }
            }
        }
    }
}