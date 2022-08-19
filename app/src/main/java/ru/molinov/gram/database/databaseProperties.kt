package ru.molinov.gram.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import ru.molinov.gram.BuildConfig
import ru.molinov.gram.models.UserModel
import ru.molinov.gram.ui.fragments.register.EnterPhoneNumberFragment
import ru.molinov.gram.utilites.addFragment

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    if (BuildConfig.DEBUG) AUTH.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
    REFERENCE_DB = Firebase.database(BuildConfig.FIREBASE_REFERENCE).reference
    REFERENCE_STORAGE = Firebase.storage.reference
    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
}

inline fun initUser(crossinline onSuccess: () -> Unit) {
    REFERENCE_DB.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                USER = snapshot.getUserModel()
                if (USER.username.isEmpty()) USER.username = CURRENT_UID
                onSuccess()
            }

            override fun onCancelled(error: DatabaseError) {
                addFragment(EnterPhoneNumberFragment())
            }
        })
}
