package com.example.messagingapp.ui.addProfile

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
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
import com.example.messagingapp.ui.components.BackNavigationIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileScreen(
    onBackClick: () -> Unit,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AddProfileViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Authentication") }, navigationIcon = {
            IconButton(onClick = onBackClick) {
                BackNavigationIcon(onBackClick = onBackClick)
            }
        })
    }) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create your profile",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = modifier.height(8.dp))
            OutlinedTextField(value = uiState.value.firstName,
                onValueChange = viewModel::updateFirstNameTextFieldValue,
                label = { Text(text = "First Name") })
            Spacer(modifier = modifier.height(8.dp))
            OutlinedTextField(value = uiState.value.lastName,
                onValueChange = viewModel::updateLastNameTextFieldValue,
                label = { Text(text = "Last Name") })
            Spacer(modifier = modifier.height(8.dp))
            OutlinedTextField(value = uiState.value.description,
                onValueChange = viewModel::updateDescriptionTextFieldValue,
                label = { Text(text = "Description") })
            Spacer(modifier = modifier.height(8.dp))
            OutlinedTextField(value = uiState.value.tag,
                onValueChange = viewModel::updateTagTextFieldValue,
                label = { Text(text = "Tag") })
            Spacer(modifier = modifier.height(8.dp))
            Button(onClick = {
                viewModel.saveUserProfile()
                onCompleteClick()
            }) {
                Text(text = "Complete")
            }
        }
    }


}