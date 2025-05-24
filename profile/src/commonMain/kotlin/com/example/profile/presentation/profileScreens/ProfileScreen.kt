package com.example.profile.presentation.profileScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import com.example.profile.data.Profile
import com.example.profile.presentation.ProfileViewModel
import com.example.profile.presentation.pickImageFromGallery
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    profile: Profile,
    viewModel: ProfileViewModel,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var isExpanded by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var isOverflowing by remember { mutableStateOf(false) }
    var actualLineCount by remember { mutableStateOf(0) }
    var editingField by remember { mutableStateOf<String?>(null) }
    var newValue by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    val profile2 = viewModel.currentProfile.collectAsState().value

    fun resetImage() {
        selectedImage = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ProfileTopAppBar(
            profile = profile,
            viewModel = viewModel,
            showBackButton = showBackButton,
            onBackClick = onBackClick,
            isEditMode = isEditMode,
            onEditToggle = {
                if (isEditMode && profile2 != null) {
                    viewModel.viewModelScope.launch {
                        profile.group?.let {
                            viewModel.saveProfile(
                                userId = profile.user_id,
                                name = profile.name,
                                profession = profile.profession,
                                group = it,
                                mainPhoto = profile.main_photo,
                                galleryPhotos = profile.gallery_photos,
                                lookingFor = profile.looking_for,
                                aboutMe = profile.about_me,
                                gender = profile.gender,
                                age = profile.age,
                                status = profile.status,
                                specialty = profile.specialty
                            )
                        }
                    }
                }
                isEditMode = !isEditMode
            }
        )

        ProfileContent(
            profile = profile,
            scrollState = scrollState,
            isExpanded = isExpanded,
            isOverflowing = isOverflowing,
            actualLineCount = actualLineCount,
            isEditMode = isEditMode,
            editingField = editingField,
            newValue = newValue,
            onExpandedChange = { isExpanded = it },
            onActualLineCountChange = { actualLineCount = it },
            onOverflowingChange = { isOverflowing = it },
            onEditingFieldChange = { editingField = it },
            onNewValueChange = { newValue = it },
            onImageSelected = { selectedImage = it },
            showBackButton = showBackButton
        )

        selectedImage?.let { imageUrl ->
            ExpandedImageOverlay(
                imageUrl = imageUrl,
                onResetImage = { resetImage() },
            )
        }
    }
}