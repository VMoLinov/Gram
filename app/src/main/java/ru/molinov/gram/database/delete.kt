package ru.molinov.gram.database

import ru.molinov.gram.utilites.showToast

fun clearChat(id: String, onSuccess: () -> Unit) {
    REFERENCE_DB
        .child(NODE_MESSAGES)
        .child(CURRENT_UID)
        .child(id)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {
            REFERENCE_DB
                .child(NODE_MESSAGES)
                .child(id)
                .child(CURRENT_UID)
                .removeValue()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
}

fun deleteChat(id: String, onSuccess: () -> Unit) {
    REFERENCE_DB
        .child(NODE_MAIN_LIST)
        .child(CURRENT_UID)
        .child(id)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { clearChat(id) { onSuccess() } }
}
