package com.example.messagingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.messagingapp.ui.addGroupChat.AddGroupChatScreen
import com.example.messagingapp.ui.addProfile.AddProfileScreen
import com.example.messagingapp.ui.channels.ChannelsScreen
import com.example.messagingapp.ui.chat.ChatScreen
import com.example.messagingapp.ui.chats.ChatsScreen
import com.example.messagingapp.ui.phonenumber.PhoneNumberScreen
import com.example.messagingapp.ui.settings.SettingsScreen
import com.example.messagingapp.ui.start.StartScreen
import com.example.messagingapp.ui.theme.MessagingAppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessagingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Destinations.START
                    ) {
                        composable(route = Destinations.START) {
                            StartScreen(
                                navigateToChats = {
                                    navController.navigate(Destinations.CHATS)
                                },
                                onButtonClick = {
                                    navController.navigate(Destinations.PHONE_NUMBER)
                                })
                        }
                        composable(route = Destinations.PHONE_NUMBER) {
                            PhoneNumberScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onVerifyClick = { userExists ->
                                    val destination = if (userExists) Destinations.CHATS
                                    else Destinations.ADD_PROFILE
                                    navController.navigate(destination)
                                })
                        }
                        composable(route = Destinations.ADD_PROFILE) {
                            AddProfileScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onCompleteClick = {
                                    navController.navigate(Destinations.CHATS)
                                })
                        }
                        navigation(Destinations.CHATS, "chatsSection") {
                            composable(route = Destinations.CHATS) {
                                ChatsScreen(
                                    onAddGroupChatClick = {
                                        navController.navigate(Destinations.ADD_GROUP_CHAT)
                                    },
                                    onUserClick = { userId, chatId ->
                                        val route = if (chatId == null) "chat/$userId"
                                        else "chat/$userId?chatId=$chatId"
                                        navController.navigate(route)
                                    },
                                    onBottomBarItemClick = {
                                        navController.navigate(it) {
                                            launchSingleTop = true
                                        }
                                    })
                            }
                            composable(
                                route = Destinations.CHAT,
                                arguments = listOf(navArgument("participantId") {
                                    type = NavType.StringType
                                }, navArgument("chatId") {
                                    nullable = true
                                    defaultValue = null
                                    type = NavType.StringType
                                }),
                            ) {
                                ChatScreen(onBackClick = {
                                    navController.popBackStack()
                                })
                            }
                            composable(route = Destinations.ADD_GROUP_CHAT) {
                                AddGroupChatScreen(onBackClick = {
                                    navController.popBackStack()
                                })
                            }
                        }
                        composable(route = Destinations.CHANNELS) {
                            ChannelsScreen(onBottomBarItemClick = {
                                navController.navigate(it) {
                                    launchSingleTop = true
                                }
                            })
                        }
                        composable(route = Destinations.SETTINGS) {
                            SettingsScreen(onBottomBarItemClick = {
                                navController.navigate(it) {
                                    launchSingleTop = true
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}

object Destinations {
    val START = "start"
    val PHONE_NUMBER = "phoneNumber"
    val CHATS = "chats"
    val CHAT = "chat/{participantId}?chatId={chatId}"
    val CHANNELS = "channels"
    val SETTINGS = "settings"
    val ADD_PROFILE = "addProfile"
    val ADD_GROUP_CHAT = "addGroupChat"
}