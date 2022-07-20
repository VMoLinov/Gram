package ru.molinov.gram.utilites

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.molinov.gram.BuildConfig

lateinit var auth: FirebaseAuth
lateinit var refDb: DatabaseReference

const val NODE_USERS = "users"
const val USER_ID = "uid"
const val USER_PHONE = "phone"
const val USER_NAME = "username"

fun initFirebase() {
    auth = FirebaseAuth.getInstance()
    if (BuildConfig.DEBUG) auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
    refDb = Firebase.database(BuildConfig.FIREBASE_REFERENCE).reference
}
