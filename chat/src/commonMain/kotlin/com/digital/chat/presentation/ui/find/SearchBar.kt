import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.digital.chat.presentation.ui.BaseText
import kitmeet.chat.generated.resources.Res
import kitmeet.chat.generated.resources.ic_find
import org.jetbrains.compose.resources.painterResource
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.platform.LocalFocusManager
import kitmeet.chat.generated.resources.ic_close

@Composable
fun SearchBar(
    text: String,
    onTextChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    focusRequester: FocusRequester
) {
    val interaction = remember { MutableInteractionSource() }
    val isFocused by interaction.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isFocused) { onFocusChange(isFocused) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF929292), RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Transparent)
            .clickable { focusRequester.requestFocus() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            IconButton(onClick = {
                onTextChange("")
                if (isFocused) {
                    focusManager.clearFocus()
                } else {
                    focusRequester.requestFocus()
                }
            }) {
                Icon(
                    painterResource(
                        if (!isFocused) Res.drawable.ic_find else Res.drawable.ic_close
                    ),
                    contentDescription = null,
                    tint = Color.Gray
                )
            }


            Spacer(Modifier.width(8.dp))

            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                interactionSource = interaction,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .background(Color.Transparent),
                placeholder = {
                    BaseText(
                        "Поиск",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFADADAD)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor   = Color.Transparent
                ),
                singleLine = true
            )
        }
    }
}
