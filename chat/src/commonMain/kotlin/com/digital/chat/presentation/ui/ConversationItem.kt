package com.digital.chat.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digital.chat.domain.Message
import com.digital.chat.presentation.getDateFromInstant
import com.example.profile.data.Profile
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun ConversationItem(
    user: Profile,
    lastMessage: Message?,
    isUnread: Boolean,
    isCurrentUserFirst : Boolean,
    onClick : () -> Unit
) {
    Row(Modifier.clickable(true, onClick = onClick)) {
        KamelImage(
            resource = {
                asyncPainterResource(user.main_photo)
            },
            contentDescription = "Profile photo",
            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(50.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.padding(horizontal = 10.dp))
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f,)) {
                    BaseText(
                        user.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.padding(vertical = 4.dp))
                    val text = if (isCurrentUserFirst) ("Ты: " + lastMessage?.content) else lastMessage?.content ?: ""
                    BaseText(text, fontSize = 18.sp, overflow = TextOverflow.Ellipsis)
                }

                Column(modifier = Modifier.wrapContentWidth(), horizontalAlignment = Alignment.End) {
                    BaseText(
                        getDateFromInstant(lastMessage!!.createdAt).toString(),
                        fontSize = 10.sp
                    )
                    Spacer(Modifier.padding(vertical = 4.dp))
                    if (isUnread) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(
                                    color = Color(0xFF7F265B),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
            Spacer(Modifier.padding(vertical = 4.dp))
            Divider(
                color = Color(0xFFBEBEBE),
                thickness = 1.dp
            )
        }
    }
    Spacer(Modifier.padding(vertical = 6.dp))
}


