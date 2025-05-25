package com.digital.settings.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kitmeet.settings.generated.resources.Res
import kitmeet.settings.generated.resources.ic_setting_arrow
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingElement(
    text : String,
    onElementClick : () -> Unit,
    borderColor : Color = Color(146, 146, 146),
    textColor : Color = Color.Black
) {
    Box(
        modifier = Modifier
            .width(250.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .background(Color.White)
            .clickable(onClick = onElementClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(10.dp).clip(RoundedCornerShape(70.dp))
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )

            Image(painter = painterResource(Res.drawable.ic_setting_arrow), contentDescription = null)
        }

    }
}