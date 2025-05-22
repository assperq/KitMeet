package com.digital.chat.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digital.chat.presentation.ChatViewModel
import com.digital.chat.presentation.formatMessageDate
import com.digital.chat.presentation.groupByDate
import com.example.profile.data.Profile
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ChatScreen(otherUser : Profile, chatViewModel: ChatViewModel = ChatViewModel()) {

    var messages = chatViewModel.messages.collectAsState()
    val scrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = Int.MAX_VALUE
    )
    LaunchedEffect(chatViewModel) {
        chatViewModel.messages.collect {
            scrollState.scrollToItem(Int.MAX_VALUE)
        }
    }
    val currentUser = chatViewModel.currentUserId.collectAsState()
    val textState = remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(Color.White)
            .padding(24.dp)

    ) {
        Divider(
            thickness = 2.dp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 130.dp)
        )

        Spacer(Modifier.padding(vertical = 6.dp))

        Row {
            KamelImage(
                resource = {
                    asyncPainterResource(otherUser.main_photo)
                },
                contentDescription = "Profile photo",
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(50.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.padding(horizontal = 4.dp))
            BaseText(
                otherUser.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                overflow = TextOverflow.Visible,
                maxLines = 4
            )
        }

        Spacer(Modifier.padding(vertical = 13.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp),
                state = scrollState
            ) {
                val grouped = messages.value.groupBy {
                    it.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
                }

                grouped.forEach { (_, dailyMessages) ->
                    item {
                        DateDivider(formatMessageDate(dailyMessages.first().createdAt))
                    }

                    items(dailyMessages) { message ->
                        if (message.senderId == currentUser.value) {
                            RightMessage(message)
                        } else {
                            LeftMessage(message)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            MessageInput(
                text = textState.value,
                onTextChange = { textState.value = it },
                onSend = {
                    chatViewModel.sendMessage(textState.value)
                    textState.value = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .imePadding()
            )
        }


    }
}

//if (textState.value.isNotBlank()) {
//    val newMessage = Message(
//        id = UUID.randomUUID().toString(),
//        content = textState.value,
//        senderId = currentUserId,
//        createdAt = Instant.now()
//    )
//    messages.add(newMessage)
//    textState.value = ""
//}