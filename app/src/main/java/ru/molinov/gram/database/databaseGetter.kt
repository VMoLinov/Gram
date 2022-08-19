package ru.molinov.gram.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.storage.StorageReference
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.models.UserModel
import ru.molinov.gram.utilites.showToast

inline fun getUrl(path: StorageReference, crossinline onSuccess: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { onSuccess(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()

fun getUserPhoto(onSuccess: (String) -> Unit) {
    REFERENCE_STORAGE.child(STORAGE_IMAGES).child(CURRENT_UID).downloadUrl
        .addOnSuccessListener {
            val url = it.toString()
            REFERENCE_DB.child(NODE_USERS).child(CURRENT_UID).child(USER_PHOTO_URL)
                .setValue(url)
            onSuccess(url)
        }.addOnFailureListener { showToast(it.message.toString()) }
}