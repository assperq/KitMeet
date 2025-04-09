package com.digital.registration.presentation.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp


@Composable
fun BaseText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize : TextUnit = 16.sp,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text(text = text, modifier = modifier,
        color = color, fontSize = fontSize,
        fontWeight = fontWeight,
        fontFamily = FontFamily.SansSerif,
        textAlign = textAlign,

    )
}