package ru.molinov.gram.utilites

import ru.molinov.gram.database.*

enum class AppStates(val status: String) {
    ONLINE("online"),
    OFFLINE("seen recently");
//    TYPING("typing...");

    companion object {
        fun updateState(appStates: AppStates) {
            if (AUTH.currentUser != null) {
                val state = appStates.status
                REFERENCE_DB.child(NODE_USERS).child(CURRENT_UID).child(USER_STATUS)
                    .setValue(state)
                    .addOnSuccessListener { USER.status = state }
                    .addOnFailureListener { showToast(it.message.toString()) }
            }
        }
    }
}
