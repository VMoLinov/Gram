package ru.molinov.gram.database

import android.net.Uri
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.TYPE_GROUP
import ru.molinov.gram.utilites.showToast

fun changeUserPhoto(uri: Uri?, onSuccess: (String) -> Unit) {
    val path = REFERENCE_STORAGE
        .child(STORAGE_IMAGES)
        .child(CURRENT_UID)
    putFile(uri, path) {
        getUrl(path) { url ->
            putUrl(url) {
                USER.photoUrl = url
                onSuccess(url)
            }
        }
    }
}

fun createGroup(
    name: String, uri: Uri?, list: List<CommonModel>, onSuccess: () -> Unit
) {
    val keyGroup = REFERENCE_DB.child(NODE_GROUPS).push().key.toString()
    val pathGroup = REFERENCE_DB.child(NODE_GROUPS).child(keyGroup)
    val pathStorage = REFERENCE_STORAGE.child(GROUPS_IMAGE).child(keyGroup)
    val mapData = hashMapOf<String, Any>()
    mapData[USER_ID] = keyGroup
    mapData[USER_FULL_NAME] = name
    mapData[USER_PHOTO_URL] = "empty"
    val mapMembers = hashMapOf<String, Any>()
    list.forEach { model -> mapMembers[model.id] = USER_MEMBER }
    mapMembers[CURRENT_UID] = USER_CREATOR
    mapData[NODE_MEMBERS] = mapMembers
    pathGroup.updateChildren(mapData)
        .addOnSuccessListener {
            if (uri != Uri.EMPTY) {
                putFile(uri, pathStorage) {
                    getUrl(pathStorage) {
                        pathGroup.child(USER_PHOTO_URL).setValue(it)
                            .addOnSuccessListener {
                                addGroupToMainList(mapData, list) { onSuccess() }
                            }
                            .addOnFailureListener { exception -> showToast(exception.message.toString()) }
                    }
                }
            } else addGroupToMainList(mapData, list) { onSuccess() }
        }
        .addOnFailureListener { exception -> showToast(exception.message.toString()) }
}

fun addGroupToMainList(
    mapData: HashMap<String, Any>, list: List<CommonModel>, onSuccess: () -> Unit
) {
    val path = REFERENCE_DB.child(NODE_MAIN_LIST)
    val map = hashMapOf<String, Any>()
    map[USER_ID] = mapData[USER_ID].toString()
    map[USER_TYPE] = TYPE_GROUP
    list.forEach { path.child(it.id).child(map[USER_ID].toString()).updateChildren(map) }
    path.child(CURRENT_UID).child(map[USER_ID].toString()).updateChildren(map)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun changeUsername(newUsername: String, onSuccess: () -> Unit) {
    REFERENCE_DB
        .child(NODE_USERNAMES)
        .child(USER.username)
        .removeValue()
        .addOnSuccessListener {
            REFERENCE_DB
                .child(NODE_USERNAMES)
                .child(newUsername)
                .setValue(CURRENT_UID)
            USER.username = newUsername
            REFERENCE_DB
                .child(NODE_USERS)
                .child(CURRENT_UID)
                .child(USER_NAME)
                .setValue(newUsername)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
}

fun changeUserFullName(fullName: String, onSuccess: () -> Unit) {
    REFERENCE_DB
        .child(NODE_USERS)
        .child(CURRENT_UID)
        .child(USER_FULL_NAME)
        .setValue(fullName)
        .addOnSuccessListener {
            USER.fullName = fullName
            onSuccess()
        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun changeUserBio(newBio: String, onSuccess: () -> Unit) {
    REFERENCE_DB
        .child(NODE_USERS)
        .child(CURRENT_UID)
        .child(USER_BIO)
        .setValue(newBio)
        .addOnSuccessListener {
            USER.bio = newBio
            onSuccess()
        }.addOnFailureListener { showToast(it.message.toString()) }
}
