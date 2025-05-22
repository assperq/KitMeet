package com.digital.chat.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.digital.chat.domain.Message
import com.digital.chat.presentation.getTimeFromInstant

@Composable
fun LeftMessage(message : Message) {
    Box(modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Box(
                modifier = Modifier
                    .width(213.dp)
                    .background(
                        color = Color(217,217,217),
                        shape = AbsoluteRoundedCornerShape(
                            topLeft = 8.dp,
                            topRight = 8.dp,
                            bottomRight = 8.dp,
                            bottomLeft = 0.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                BaseText(message.content, maxLines = 99, modifier = Modifier.fillMaxWidth())
            }
            BaseText(
                getTimeFromInstant(message.createdAt),
            )
        }
    }
}

@Composable
fun RightMessage(message : Message) {
    Box(modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .width(213.dp)
                    .background(
                        color = Color(205, 205, 205),
                        shape = AbsoluteRoundedCornerShape(
                            topLeft = 8.dp,
                            topRight = 8.dp,
                            bottomRight = 0.dp,
                            bottomLeft = 8.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                BaseText(message.content, maxLines = 99, modifier = Modifier.fillMaxWidth())
            }
            BaseText(
                getTimeFromInstant(message.createdAt),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
    }
}