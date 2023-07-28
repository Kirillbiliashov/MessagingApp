package com.example.messagingapp.ui.addChannel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChannelScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AddChannelScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create channel") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = modifier.height(64.dp))
            OutlinedTextField(value = uiState.value.name,
                onValueChange = viewModel::updateNameTextFieldValue,
                label = { Text(text = "Name") })
            Spacer(modifier = modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.value.description ?: "",
                onValueChange = viewModel::updateDescriptionFieldValue,
                label = { Text(text = "Description") },
                placeholder = { Text(text = "Description (optional)") })
            Spacer(modifier = modifier.height(8.dp))
            OutlinedTextField(value = uiState.value.tag,
                onValueChange = viewModel::updateTagTextFieldValue,
                label = { Text(text = "Tag") })
            Spacer(modifier = modifier.height(32.dp))
            Button(onClick = {
                onBackClick()
                viewModel.createChannel()
            }) {
                Text(text = "Create")
            }
        }
    }
}