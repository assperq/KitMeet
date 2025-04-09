package com.digital.registration.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun Container(
    content : @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().padding(top = 13.dp, start = 18.dp, end = 18.dp)) {
        content()
    }
}