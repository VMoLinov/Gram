package ru.molinov.gram.utilites

import android.net.Uri
import android.provider.ContactsContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import ru.molinov.gram.BuildConfig
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.models.User

lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String
lateinit var REFERENCE_DB: DatabaseReference
lateinit var REFERENCE_STORAGE: StorageReference
lateinit var USER: User

const val STORAGE_IMAGES = "users_images"

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phone_contacts"

const val USER_ID = "id"
const val USER_PHONE = "phone"
const val USER_NAME = "username"
const val USER_PHOTO_URL = "photoUrl"
const val USER_STATUS = "status"
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
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(User::class.java) ?: User()
            if (USER.username.isEmpty()) USER.username = CURRENT_UID
            function()
        })
}

fun initContacts() {
    if (checkPermission(READ_CONTACTS)) {
        val arrayContacts = arrayListOf<CommonModel>()
        val cursor = MAIN_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            while (it.moveToNext()) {
                val fullNameColumn = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val phoneColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val newModel = CommonModel()
                newModel.fullName = it.getString(fullNameColumn)
                newModel.phone = it.getString(phoneColumn).replace(Regex("[\\s,-]"), "")
                arrayContacts.add(newModel)
            }
        }
        cursor?.close()
        updatePhonesToDatabase(arrayContacts)
    }
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
                }
            }
        }
    })
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()
