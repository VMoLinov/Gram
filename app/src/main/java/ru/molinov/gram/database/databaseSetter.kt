package ru.molinov.gram.database

import android.net.Uri
import com.google.firebase.storage.StorageReference
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.AppValueEventListener
import ru.molinov.gram.utilites.showToast

fun setPhones(arrayContacts: ArrayList<CommonModel>) {
    REFERENCE_DB
        .child(NODE_PHONES)
        .addListenerForSingleValueEvent(AppValueEventListener {
            it.children.forEach { snapshot ->
                arrayContacts.forEach { contact ->
                    if (snapshot.key == contact.phone) {
                        REFERENCE_DB
                            .child(NODE_PHONES_CONTACTS)
                            .child(CURRENT_UID)
                            .child(snapshot.value.toString())
                            .child(USER_ID)
                            .setValue(snapshot.value.toString())
                            .addOnFailureListener { exception -> showToast(exception.message.toString()) }
                        REFERENCE_DB
                            .child(NODE_PHONES_CONTACTS)
                            .child(CURRENT_UID)
                            .child(snapshot.value.toString())
                            .child(USER_FULL_NAME)
                            .setValue(contact.fullName)
                            .addOnFailureListener { exception -> showToast(exception.message.toString()) }
                    }
                }
            }
        })
}

inline fun putUrl(url: String, crossinline onSuccess: () -> Unit) {
    REFERENCE_DB
        .child(NODE_USERS)
        .child(CURRENT_UID)
        .child(USER_PHOTO_URL)
        .setValue(url)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putFile(uri: Uri?, path: StorageReference, crossinline onSuccess: () -> Unit) {
    uri?.let { file ->
        path.putFile(file)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { showToast(it.message.toString()) }
    }
}

fun uploadFile(uri: Uri?, contactId: String, typeMessage: Int) {
    val messageKey = getMessageKey(contactId)
    val path = REFERENCE_STORAGE
        .child(FILES)
        .child(messageKey)
    putFile(uri, path) {
        getUrl(path) { url ->
            putUrl(url) {
                sendMessageAsFile(contactId, url, messageKey, typeMessage)
            }
        }
    }
}
