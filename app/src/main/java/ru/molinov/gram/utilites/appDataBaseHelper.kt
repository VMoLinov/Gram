package ru.molinov.gram.utilites

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import ru.molinov.gram.BuildConfig
import ru.molinov.gram.activities.RegisterActivity
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.models.UserModel

lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String
lateinit var REFERENCE_DB: DatabaseReference
lateinit var REFERENCE_STORAGE: StorageReference
lateinit var USER: UserModel

const val TYPE_TEXT = "text"

const val STORAGE_IMAGES = "users_images"

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phone_contacts"
const val NODE_MESSAGES = "messages"

const val USER_ID = "id"
const val USER_PHONE = "phone"
const val USER_NAME = "username"
const val USER_PHOTO_URL = "photoUrl"
const val USER_STATUS = "status"
const val USER_FULL_NAME = "fullName"
const val USER_BIO = "bio"
const val USER_TEXT = "text"
const val USER_TYPE = "type"
const val USER_FROM = "from"
const val USER_TIMESTAMP = "timestamp"

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    if (BuildConfig.DEBUG) AUTH.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
    REFERENCE_DB = Firebase.database(BuildConfig.FIREBASE_REFERENCE).reference
    REFERENCE_STORAGE = Firebase.storage.reference
    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
}

inline fun putUrlToDataBase(url: String, crossinline function: () -> Unit) {
    REFERENCE_DB.child(NODE_USERS).child(CURRENT_UID).child(USER_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putImageToStore(uri: Uri?, path: StorageReference, crossinline function: () -> Unit) {
    uri?.let { file ->
        path.putFile(file)
            .addOnSuccessListener { function() }
            .addOnFailureListener { showToast(it.message.toString()) }
    }
}

inline fun initUser(crossinline function: () -> Unit) {
    REFERENCE_DB.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                USER = snapshot.getUserModel()
                if (USER.username.isEmpty()) USER.username = CURRENT_UID
                function()
            }

            override fun onCancelled(error: DatabaseError) {
                MAIN_ACTIVITY.replaceActivity(RegisterActivity())
            }
        })
}

fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    REFERENCE_DB.child(NODE_PHONES).addListenerForSingleValueEvent(AppValueEventListener {
        it.children.forEach { snapshot ->
            arrayContacts.forEach { contact ->
                if (snapshot.key == contact.phone) {
                    REFERENCE_DB.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                        .child(snapshot.value.toString()).child(USER_ID)
                        .setValue(snapshot.value.toString())
                        .addOnFailureListener { exception -> showToast(exception.message.toString()) }
                    REFERENCE_DB.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                        .child(snapshot.value.toString()).child(USER_FULL_NAME)
                        .setValue(contact.fullName)
                        .addOnFailureListener { exception -> showToast(exception.message.toString()) }
                }
            }
        }
    })
}

fun sendMessage(message: String, receivingUserId: String, typeText: String, function: () -> Unit) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserId"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserId/$CURRENT_UID"
    val messageKey = REFERENCE_DB.child(refDialogUser).push().key
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[USER_FROM] = CURRENT_UID
    mapMessage[USER_TYPE] = typeText
    mapMessage[USER_TEXT] = message
    mapMessage[USER_TIMESTAMP] = ServerValue.TIMESTAMP
    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$message"] = mapMessage
    REFERENCE_DB.updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()
