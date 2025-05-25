package com.digital.chat.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
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
import kitmeet.chat.generated.resources.Res
import kitmeet.chat.generated.resources.ic_delete
import kitmeet.chat.generated.resources.send_ic
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

@Composable
fun ChatScreen(
    otherUser : Profile,
    chatViewModel: ChatViewModel = ChatViewModel(),
    onUserDelete : () -> Unit = {},
) {
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            KamelImage(
                resource = { asyncPainterResource(otherUser.main_photo) },
                contentDescription = "Profile photo",
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(50.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(8.dp))

            BaseText(
                otherUser.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(13.dp)
                    )
                    .background(Color.White)
                    .padding(8.dp)
                    .clickable(true) {
                        onUserDelete()
                    }
            ) {
                Icon(
                    painterResource(Res.drawable.ic_delete),
                    contentDescription = null,
                    tint = Color(127, 38, 91),
                    modifier = Modifier.size(21.dp)
                )
            }
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
                    .padding(bottom = 7.dp)
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