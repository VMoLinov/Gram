package ru.molinov.gram.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.molinov.gram.database.CURRENT_UID

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
    var messageType: Int = 0,
    var lastMessageType: Int = 0,
    var from: String = "",
    var timestamp: Long = 0,
    var fileUrl: String = "",
    var lastMessage: String = ""
) : Parcelable {

    fun isFromUser(): Boolean = from == CURRENT_UID

    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == this.id
    }

    override fun hashCode(): Int = id.hashCode()
}
