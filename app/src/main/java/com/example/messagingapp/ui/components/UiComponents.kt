package com.example.messagingapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSendIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = "Type your message...") },
        trailingIcon = {
            val iconColor = if (value.isEmpty())
                LocalContentColor.current
            else MaterialTheme.colorScheme.primary
            IconButton(onClick = onSendIconClick) {
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