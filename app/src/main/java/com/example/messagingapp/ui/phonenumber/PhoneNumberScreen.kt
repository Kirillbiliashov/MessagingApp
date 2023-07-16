package com.example.messagingapp.ui.phonenumber

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberScreen(modifier: Modifier = Modifier) {
    val snackbarHostState = remember { SnackbarHostState() }
    val viewModel: PhoneNumberViewModel = hiltViewModel()
    val ctxt = LocalContext.current
    val uiState = viewModel.uiState.collectAsState()
    uiState.value.userMessage?.let {
        LaunchedEffect(key1 = uiState.value.userMessage) {
            snackbarHostState.showSnackbar(message = it)
        }
    }
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter your phone number",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.value.phoneNumber,
                onValueChange = viewModel::changePhoneNumberValue,
                label = { Text(text = "Phone number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            AnimatedVisibility(visible = uiState.value.codeSent) {
                Spacer(modifier = modifier.height(8.dp))
                Text(
                    text = "Enter verification code",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.value.verificationCode,
                    onValueChange = viewModel::changeVerificationCodeValue,
                    label = { Text(text = "Verification code") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Spacer(modifier = modifier.height(8.dp))
            if (uiState.value.codeSent) {
                Button(onClick = viewModel::verifyCode) {
                    Text(text = "Verify")
                }
            } else {
                Button(onClick = { viewModel.sendVerificationCode(ctxt as Activity) }) {
                    Text(text = "Send code")
                }
            }
        }
    }

}