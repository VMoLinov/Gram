package ru.molinov.gram.database

import android.net.Uri
import ru.molinov.gram.utilites.showToast

fun changeUserPhoto(uri: Uri?, onSuccess: (String) -> Unit) {
    val path = REFERENCE_STORAGE
        .child(STORAGE_IMAGES)
        .child(CURRENT_UID)
    putFile(uri, path) {
        getUrl(path) { url ->
            putUrl(url) {
                USER.photoUrl = url
                onSuccess(url)
            }
        }
    }
}

fun changeUsername(newUsername: String, onSuccess: () -> Unit) {
    REFERENCE_DB
        .child(NODE_USERNAMES)
        .child(USER.username)
        .removeValue()
        .addOnSuccessListener {
            REFERENCE_DB
                .child(NODE_USERNAMES)
                .child(newUsername)
                .setValue(CURRENT_UID)
            USER.username = newUsername
            REFERENCE_DB
                .child(NODE_USERS)
                .child(CURRENT_UID)
                .child(USER_NAME)
                .setValue(newUsername)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
}

fun changeUserFullName(fullName: String, onSuccess: () -> Unit) {
    REFERENCE_DB
        .child(NODE_USERS)
        .child(CURRENT_UID)
        .child(USER_FULL_NAME)
        .setValue(fullName)
        .addOnSuccessListener {
            USER.fullName = fullName
            onSuccess()
        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun changeUserBio(newBio: String, onSuccess: () -> Unit) {
    REFERENCE_DB
        .child(NODE_USERS)
        .child(CURRENT_UID)
        .child(USER_BIO)
        .setValue(newBio)
        .addOnSuccessListener {
            USER.bio = newBio
            onSuccess()
        }.addOnFailureListener { showToast(it.message.toString()) }
}
