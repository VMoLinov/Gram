package ru.molinov.gram.utilites

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import ru.molinov.gram.BuildConfig
import ru.molinov.gram.ui.models.User

lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String
lateinit var REFERENCE_DB: DatabaseReference
lateinit var REFERENCE_STORAGE: StorageReference
lateinit var USER: User

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val USER_ID = "uid"
const val USER_PHONE = "phone"
const val USER_NAME = "username"
const val USER_FULL_NAME = "fullName"
const val USER_BIO = "bio"

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    if (BuildConfig.DEBUG) AUTH.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
    REFERENCE_DB = Firebase.database(BuildConfig.FIREBASE_REFERENCE).reference
    REFERENCE_STORAGE = Firebase.storage.reference
    USER = User()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
}
