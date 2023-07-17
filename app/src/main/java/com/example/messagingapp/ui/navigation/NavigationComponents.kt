package com.example.messagingapp.ui.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.messagingapp.Destinations
import com.example.messagingapp.R

/*
@Composable
fun AppBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    BottomNavigation(backgroundColor = Color.White) {
        bottomBarOptions.forEach { option ->
            BottomNavigationItem(
                selected =
                currentDestination?.hierarchy?.any { it.route == option.route } == true,
                onClick = { navController.navigate(option.route) },
                icon = {
                    Icon(
                        imageVector = option.imageVector,
                        contentDescription = null
                    )
                })
        }
    }
}
 */

sealed class BottomBarOption(val route: String, val iconRes: Int, val title: String) {
    object Chats : BottomBarOption(
        route = Destinations.CHATS,
        iconRes = R.drawable.chats, title = "Chats"
    )

    object Channels :
        BottomBarOption(
            route = Destinations.CHANNELS,
            iconRes = R.drawable.posts, title = "Channels"
        )

    object Settings :
        BottomBarOption(
            route = Destinations.SETTINGS,
            iconRes = R.drawable.settings, title = "Settings"
        )
}

private val bottomBarOptions = listOf(
    BottomBarOption.Chats,
    BottomBarOption.Channels,
    BottomBarOption.Settings
)

@Composable
fun MessagingAppBottomNavigation(
    onBottomBarItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(containerColor = Color.White) {
        bottomBarOptions.forEach { option ->
            NavigationBarItem(
                selected = false,
                onClick = { onBottomBarItemClick(option.route) },
                icon = {
                    Icon(
                        painter = painterResource(option.iconRes),
                        contentDescription = null,
                        modifier = modifier.padding(bottom = 16.dp)
                    )

                },
                label = { Text(text = option.title) })
        }
    }
}