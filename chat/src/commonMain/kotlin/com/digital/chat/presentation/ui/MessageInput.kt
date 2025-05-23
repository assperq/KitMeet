package com.digital.chat.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kitmeet.chat.generated.resources.Res
import kitmeet.chat.generated.resources.send_ic
import org.jetbrains.compose.resources.Resource
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun MessageInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier.clip(RoundedCornerShape(8.dp)).height(75.dp), elevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = Color(146, 146, 146),
                    shape = RoundedCornerShape(20.dp)
                )
                .background(Color.White)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(10.dp).clip(RoundedCornerShape(70.dp))
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.height(65.dp).weight(1f).background(Color.White),
                    placeholder = { BaseText("Твое сообщение",
                        fontWeight = FontWeight.ExtraBold, color = Color(173, 173, 173)) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    ),
                    maxLines = 3
                )

                IconButton(
                    onClick = onSend,
                    enabled = text.isNotBlank()
                ) {
                    Image(
                        painterResource(Res.drawable.send_ic),
                        contentDescription = "Отправить"
                    )
                }
            }
        }
    }
}