package ru.molinov.gram.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommonModel(
    val id: String = "",
    var username: String = "",
    var bio: String = "",
    var fullName: String = "",
    var status: String = "",
    var photoUrl: String = "",
    var phone: String = ""
) : Parcelable
