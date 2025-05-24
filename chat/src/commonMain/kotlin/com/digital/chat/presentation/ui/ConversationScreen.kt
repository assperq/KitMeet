package com.digital.chat.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digital.chat.presentation.ChatViewModel
import com.example.profile.data.Profile
import kotlinx.coroutines.launch

@Composable
fun ConversationScreen(chatViewModel: ChatViewModel = ChatViewModel()) {
    val conversations = chatViewModel.conversations.collectAsState()
    val currentUserId = chatViewModel.currentUserId.collectAsState()
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    if (!bottomSheetState.isVisible) {
        chatViewModel.unsubscribeToChannel(chatViewModel.currentConversation.value?.id ?: "")
    }
    val otherUser = remember {
        mutableStateOf(
            Profile(
                user_id = "",
                name = "",
                profession = "",
                group = "",
                main_photo = "",
                gallery_photos = emptyList(),
                looking_for = "",
                about_me = "",
                gender = "",
                age = 0,
                status = "",
                specialty = "",
                friends = emptyList(),
                acceptedProfiles = emptyList(),
                rejectedProfiles = emptyList()
            )
        )
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        sheetBackgroundColor = Color.White,
        sheetContent = {
            ChatScreen(otherUser.value, chatViewModel)
        }
    ) {
        Container {
            Column {
                BaseText("Чаты", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.padding(vertical = 20.dp))

                LazyColumn {
                    items(conversations.value) { conversation ->
                        val otherUserLocal = when (currentUserId.value) {
                            conversation.user1.user_id -> conversation.user2
                            else -> conversation.user1
                        }

                        ConversationItem(
                            user = otherUserLocal,
                            lastMessage = conversation.lastMessage,
                            isUnread = conversation.lastMessage?.let {
                                !it.isRead && it.senderId != currentUserId.value
                            } == true,
                            isCurrentUserFirst = (conversation.lastMessage?.senderId
                                ?: "") == currentUserId.value,
                            onClick = {
                                chatViewModel.selectConversation(conversation)
                                otherUser.value = otherUserLocal
                                scope.launch {
                                    bottomSheetState.show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}