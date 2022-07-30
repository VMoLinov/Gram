package ru.molinov.gram.models

data class CommonModel(
    val id: String = "",
    var username: String = "",
    var bio: String = "",
    var fullName: String = "",
    var status: String = "",
    var photoUrl: String = "",
    var phone: String = ""
)
