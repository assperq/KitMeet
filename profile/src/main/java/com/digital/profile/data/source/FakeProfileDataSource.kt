package com.digital.profile.data.source

import com.digital.profile.domain.model.Profile

class FakeProfileDataSource {
    fun getMockProfile() = Profile(
        id = 1,
        name = "Артём Шины Валерьевич",
        age = 19,
        group = "ИСП-304",
        position = "DevOps, SRE",
        lookingFor = "Ищу разработчиков",
        aboutMe = "Привет! Меня зовут...",
        photos = listOf("photo1", "photo2", "photo3", "photo4", "photo5"),
        mainPhoto = "main_photo"
    )
}