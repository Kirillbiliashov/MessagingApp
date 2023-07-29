package com.example.messagingapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.messagingapp.utils.Helpers.asTimestampToString
import com.google.firebase.database.collection.R

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

@Composable
fun FeedListItem(
    title: String,
    content: String,
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .size(64.dp)
            .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.googleg_disabled_color_18),
            contentDescription = null,
            modifier = modifier.size(52.dp)
        )
        Spacer(modifier = modifier.width(16.dp))
        Box(modifier = modifier.fillMaxHeight()) {
            Column(
                modifier = modifier
                    .align(Alignment.CenterStart)
                    .padding(end = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.W500,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = modifier.weight(1f))
                    Text(text = date)
                }
                Text(text = content)
            }
            Divider(
                modifier = modifier.align(Alignment.BottomEnd),
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
fun SearchResultListItem(
    title: String,
    content: String?,
    trailingComponent: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .size(52.dp)
            .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.googleg_disabled_color_18),
            contentDescription = null,
            modifier = modifier.size(36.dp)
        )
        Spacer(modifier = modifier.width(16.dp))
        Box(modifier = modifier.fillMaxHeight()) {
            Row(modifier = modifier.align(Alignment.CenterStart)) {
                Column(
                    modifier = modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title, fontWeight = FontWeight.W500,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (content != null) {
                        Text(text = content)
                    }
                }
                Spacer(modifier = modifier.weight(1f))
                trailingComponent()
            }
            Divider(modifier = modifier.align(Alignment.BottomEnd), thickness = 0.5.dp)
        }

    }
}

@Composable
fun BackNavigationIcon(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onBackClick) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null
        )
    }
}

@Composable
fun SearchTextField(
    value: String?,
    onValueChange: (String) -> Unit,
    onClearIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(value = value ?: "", onValueChange = onValueChange) { innerTextField ->
        Row(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .border(
                    1.dp, MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 8.dp)
                .height(36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
            Spacer(modifier = modifier.width(8.dp))
            Box(
                modifier = modifier.fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                innerTextField()
                if (value == null) {
                    Text(text = "Search", color = Color.DarkGray)
                }
            }
            value?.let {
                Spacer(modifier = modifier.weight(1f))
                IconButton(
                    onClick = onClearIconClick,
                    modifier = modifier.size(22.dp)
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                }
            }
        }
    }
}
