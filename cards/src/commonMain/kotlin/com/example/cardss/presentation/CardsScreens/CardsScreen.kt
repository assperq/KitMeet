package com.example.cardss.presentation.CardsScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digital.supabaseclients.SupabaseManager
import com.example.cardss.CardsViewModel
import com.example.cardss.domain.CardsRepositoryImpl
import com.example.cardss.presentation.SwipeTracker
import kotlinx.coroutines.launch

@Composable
fun CardsScreen(
    viewModel: CardsViewModel,
    onProfileClick: (String) -> Unit,
    swipeTracker: SwipeTracker
) {
    val profilesState = viewModel.profiles.collectAsState()
    val acceptedProfilesState = viewModel.acceptedProfiles.collectAsState()
    val rejectedProfilesState = viewModel.rejectedProfiles.collectAsState()
    val swipedToday = viewModel.cardsSwipedToday.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    var showAcceptedDialog by remember { mutableStateOf(false) }
    var showRejectedDialog by remember { mutableStateOf(false) }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        sheetBackgroundColor = Color.White,
        sheetContent = {
            FilterBottomSheet(
                initialGender = "Оба",
                initialCourse = null,
                initialSpecialization = "Любая"
            ) { gender, course, specialization ->
                viewModel.loadProfiles(gender, course, specialization)
                scope.launch { bottomSheetState.hide() }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEDE7F6))
        ) {
            TopBar(
                onAcceptedClick = {
                    viewModel.loadAcceptedProfiles()
                    showAcceptedDialog = true
                },
                onRejectedClick = {
                    viewModel.loadRejectedProfiles()
                    showRejectedDialog = true
                },
                onFilterClick = {
                    scope.launch { bottomSheetState.show() }
                },
                cardsSwiped = swipedToday.value
            )

            if (showAcceptedDialog) {
                ProfilesDialog(
                    title = "Принятые профили",
                    profiles = acceptedProfilesState.value,
                    onDismiss = { showAcceptedDialog = false },
                    onRemoveProfile = { profile ->
                        viewModel.removeProfileFromList(profile, "accepted")
                    }
                )
            }

            if (showRejectedDialog) {
                ProfilesDialog(
                    title = "Отклонённые профили",
                    profiles = rejectedProfilesState.value,
                    onDismiss = { showRejectedDialog = false },
                    onRemoveProfile = { profile ->
                        viewModel.removeProfileFromList(profile, "rejected")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SwipeableCardStack(
                profiles = profilesState.value,
                onSwipeLeft = { profile -> viewModel.rejectProfile(profile) },
                onSwipeRight = { profile -> viewModel.acceptProfile(profile) },
                onCardClick = { profile -> onProfileClick(profile.user_id) }
            )
        }
    }
}

enum class SwipeDirection {
    LEFT, RIGHT
}

