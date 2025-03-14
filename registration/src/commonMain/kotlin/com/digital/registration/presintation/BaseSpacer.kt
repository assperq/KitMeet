package com.digital.registration.presintation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun VerticalSpace(space : Dp = 8.dp)  {
    Spacer(modifier = Modifier.padding(vertical = space))
}

@Composable
internal fun HorizontalSpace(space : Dp = 8.dp)  {
    Spacer(modifier = Modifier.padding(horizontal = space))
}