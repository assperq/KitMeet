package com.digital.chat.presentation.ui.find

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger.Companion.v
import com.digital.chat.presentation.getDateFromInstant
import com.digital.chat.presentation.ui.BaseText
import com.example.profile.data.Profile
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kitmeet.chat.generated.resources.Res
import kitmeet.chat.generated.resources.ic_delete
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FindElement(profile: Profile, navController: NavController, onLikeClick : () -> Unit = {}) {
    Card(onClick = {
            val userId = profile.user_id
            navController.navigate("profileDetails/$userId")
        },
        backgroundColor = Color.Transparent,
        modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(5.dp)),
        //border = BorderStroke(1.dp, Color.Black),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                KamelImage(
                    resource = { asyncPainterResource(profile.main_photo) },
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(50.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(8.dp))

                BaseText(
                    profile.name,
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
                            onLikeClick()
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = Color(127, 38, 91),
                        modifier = Modifier.size(21.dp)
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(6.dp))
}
