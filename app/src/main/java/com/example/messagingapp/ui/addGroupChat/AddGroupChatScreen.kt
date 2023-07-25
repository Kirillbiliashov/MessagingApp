package com.example.messagingapp.ui.addGroupChat

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
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.messagingapp.R
import com.example.messagingapp.data.model.firebase.User
import com.example.messagingapp.ui.chats.UserCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupChatScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "New group chat") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                })
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AddGroupChatScreenContent(onBackClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupChatScreenContent(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AddGroupChatScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    OutlinedTextField(
        value = uiState.value.name,
        onValueChange = viewModel::updateNameTextField,
        label = { Text(text = "Group name") })
    Spacer(modifier = modifier.height(8.dp))
    OutlinedTextField(value = uiState.value.tag,
        onValueChange = viewModel::updateTagTextField,
        label = { Text(text = "Group tag") }
    )
    Spacer(modifier = modifier.height(8.dp))
    PrivateSwitch(
        isPrivate = uiState.value.isPrivate,
        onCheckedChange = viewModel::updateIsPrivateValue
    )
    Row(
        modifier = modifier.fillMaxWidth(0.7f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Members", style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = modifier.weight(1f))
        OutlinedButton(onClick = viewModel::displayFindMemberDialog) {
            Text(text = "Add")
        }
    }
    GroupChatMembers(
        groupChatMembers = uiState.value.groupChatMembers,
        onPersonRemoveClick = viewModel::removeSelectedMember
    )
    if (uiState.value.dialogShown) {
        FindMembersDialog(
            onDismiss = viewModel::closeDialogWindow,
            onAddClick = viewModel::addSelectedMembers,
            query = uiState.value.membersQuery,
            onQueryChange = viewModel::updateMembersQueryTextField,
            queryUsers = uiState.value.queryUsers,
            selectedMembers = uiState.value.selectedMembers,
            onCheckboxClick = viewModel::updateSelectedMembers
        )
    }
    Spacer(modifier = modifier.height(8.dp))
    Button(onClick = {
        onBackClick()
        viewModel.createNewChatGroup()
    }) {
        Text(text = "Create")
    }
}

@Composable
fun PrivateSwitch(
    isPrivate: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(0.7f),
    ) {
        Text(text = "Private", fontSize = 16.sp)
        Spacer(modifier = modifier.width(16.dp))
        Switch(
            checked = isPrivate,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun GroupChatMembers(
    groupChatMembers: List<User>,
    onPersonRemoveClick: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    if (groupChatMembers.isNotEmpty()) {
        Spacer(modifier = modifier.height(8.dp))
        Box(modifier = modifier.requiredHeightIn(max = 300.dp)) {
            LazyColumn {
                items(items = groupChatMembers) { user ->
                    UserCard(user = user, trailingComponent = {
                        IconButton(onClick = { onPersonRemoveClick(user) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.remove_person),
                                contentDescription = null
                            )
                        }
                    })
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindMembersDialog(
    onDismiss: () -> Unit,
    onAddClick: () -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    queryUsers: List<User>,
    selectedMembers: List<User>,
    onCheckboxClick: (User, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Find members",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = modifier.height(8.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                )
                Spacer(modifier = modifier.height(4.dp))
                QueryUsersSection(
                    queryUsers = queryUsers,
                    onCheckboxClick = onCheckboxClick,
                    selectedMembers = selectedMembers
                )
                Spacer(modifier = modifier.weight(1f))
                FindMembersDialogButtons(
                    onAddClick = onAddClick,
                    onDismissClick = onDismiss
                )
            }
        }
    }
}

@Composable
fun QueryUsersSection(
    queryUsers: List<User>,
    selectedMembers: List<User>,
    onCheckboxClick: (User, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (queryUsers.isNotEmpty()) {
        LazyColumn(modifier = modifier.fillMaxWidth()) {
            items(items = queryUsers) { user ->
                UserCard(
                    user = user,
                    trailingComponent = {
                        val checked = remember {
                            mutableStateOf(selectedMembers.contains(user))
                        }
                        Checkbox(checked = checked.value,
                            onCheckedChange = {
                                val newValue = !checked.value
                                checked.value = newValue
                                onCheckboxClick(user, newValue)
                            })
                    })
            }
        }
    } else {
        Text(text = "No users selected yet.")
    }
}

@Composable
fun FindMembersDialogButtons(
    onAddClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Close",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            modifier = modifier.clickable { onDismissClick() }
        )
        Text(
            text = "Add",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            modifier = modifier.clickable { onAddClick() }
        )
    }
}