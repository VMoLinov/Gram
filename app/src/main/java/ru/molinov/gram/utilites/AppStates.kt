package ru.molinov.gram.utilites

enum class AppStates(val state: String) {
    ONLINE("online"),
    OFFLINE("seen recently"),
    TYPING("typing...");

    companion object {
        fun updateState(appStates: AppStates) {
            val state = appStates.state
            REFERENCE_DB.child(NODE_USERS).child(CURRENT_UID).child(USER_STATUS)
                .setValue(state)
                .addOnSuccessListener { USER.status = state }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
    }
}
