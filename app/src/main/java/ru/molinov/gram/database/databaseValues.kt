package ru.molinov.gram.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import ru.molinov.gram.models.UserModel

lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String
lateinit var REFERENCE_DB: DatabaseReference
lateinit var REFERENCE_STORAGE: StorageReference
lateinit var USER: UserModel

const val STORAGE_IMAGES = "users_images"
const val FILES = "messages_files"

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
const val USER_FILE_URL = "fileUrl"
