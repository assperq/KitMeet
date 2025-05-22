package com.digital.chat.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DateDivider(date: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            color = Color.LightGray,
            thickness = 1.dp
        )

        Text(
            text = date,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 12.sp
        )

        Divider(
            modifier = Modifier.weight(1f),
            color = Color.LightGray,
            thickness = 1.dp
        )
    }
}