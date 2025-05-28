package com.digital.chat.presentation.ui

import SearchBar
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.digital.chat.presentation.ChatViewModel
import com.digital.chat.presentation.ui.find.FindElement
import com.digital.chat.presentation.ui.find.FindViewModel
import com.example.cardss.CardsViewModel
import com.example.profile.data.Profile
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@Composable
fun ConversationScreen(
    navController: NavController,
    cardsViewModel : CardsViewModel,
    chatViewModel: ChatViewModel = ChatViewModel(),
    findViewModel: FindViewModel = FindViewModel(),
    selectedChat : String? = null
) {
    val conversations = chatViewModel.conversations.collectAsState()
    val currentUserId = chatViewModel.currentUserId.collectAsState()
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    if (!bottomSheetState.isVisible) {
        chatViewModel.unsubscribeToChannel(chatViewModel.currentConversation.value?.id ?: "")
    }

    if (selectedChat != null) {
        chatViewModel.findConversation(currentUserId.value, selectedChat)
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

    var query      by remember { mutableStateOf("") }
    var isFieldFocused by remember { mutableStateOf(false) }
    val focusReq   = remember { FocusRequester() }
    var foundUsers = findViewModel.users.collectAsState()

    LaunchedEffect(Unit) {
        snapshotFlow { query }
            .debounce(300)
            .filter { it.isNotBlank() }
            .collect { q ->
                val self = chatViewModel.currentUserId.value
                findViewModel.findUsers(q.trim(), self)
            }
    }

    val topOffset by animateDpAsState(
        if (isFieldFocused) 0.dp else 20.dp,
        label = "searchBarOffset"
    )

    val dialogState = remember { mutableStateOf(false) }

    if (dialogState.value) {
        DeleteDialog(onConfirm = {
            scope.launch {
                bottomSheetState.hide()
            }
            chatViewModel.deleteConversation()
            dialogState.value = false
        }, onDismiss = {
            dialogState.value = false
        })
    }

    val loading by findViewModel.isSearching.collectAsState()

    LaunchedEffect(chatViewModel) {
        chatViewModel.currentConversation.collect {
            if (it != null) {
                val otherUserLocal = when (currentUserId.value) {
                    it.user1.user_id -> it.user2
                    else -> it.user1
                }
                otherUser.value = otherUserLocal
                bottomSheetState.show()
            }
        }
    }


    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        sheetBackgroundColor = Color.White,
        sheetContent = {
            ChatScreen(otherUser.value,
                navController,
                chatViewModel, onUserDelete = {
                dialogState.value = true
            })
        }
    ) {
        Container {
            Column(modifier = Modifier
                .animateContentSize()
            ) {
                AnimatedVisibility(!isFieldFocused) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (selectedChat != null) {
                            IconButton(onClick = {
                                navController.navigateUp()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBackIosNew,
                                    contentDescription = "Назад",
                                    tint = Color(0xFF7F265B),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        BaseText("Чаты", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(topOffset))
                if (selectedChat == null) {
                    SearchBar(
                        text = query,
                        onTextChange = { query = it },
                        onFocusChange = { isFieldFocused = it },
                        focusRequester = focusReq
                    )
                }
                Spacer(Modifier.padding(vertical = 20.dp))
                AnimatedContent(
                    targetState = isFieldFocused,
                    label = "contentSwitch"
                ) { searching ->
                    if (isFieldFocused) {
                        if (loading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        else {
                            LazyColumn {
                                items(foundUsers.value) { user ->
                                    FindElement(
                                        profile = user,
                                        navController = navController,
                                        onLikeClick = {
                                            cardsViewModel.acceptProfile(user)
                                            foundUsers =
                                                mutableStateOf(foundUsers.value.filter { it.user_id != user.user_id })
                                            findViewModel.findUsers(query, currentUserId.value)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    else {
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
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}