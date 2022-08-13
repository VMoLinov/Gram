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
    var phone: String = "",
    var text: String = "",
    var type: String = "",
    var from: String = "",
    var timestamp: Long = 0,
    var imageUrl: String = ""
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == this.id
    }

    override fun hashCode(): Int = id.hashCode()
}
