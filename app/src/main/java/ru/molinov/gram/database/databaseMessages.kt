package ru.molinov.gram.database

import android.net.Uri
import com.google.firebase.database.ServerValue
import ru.molinov.gram.utilites.TYPE_MESSAGE_TEXT
import ru.molinov.gram.utilites.getFileNameFromUrl
import ru.molinov.gram.utilites.showToast

fun sendMessageAsText(message: String, receivingUserId: String, onSuccess: () -> Unit) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserId"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserId/$CURRENT_UID"
    val messageKey = REFERENCE_DB.child(refDialogUser).push().key
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[USER_FROM] = CURRENT_UID
    mapMessage[USER_TYPE] = TYPE_MESSAGE_TEXT
    mapMessage[USER_TEXT] = message
    mapMessage[USER_ID] = messageKey.toString()
    mapMessage[USER_TIMESTAMP] = ServerValue.TIMESTAMP
    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage
    REFERENCE_DB.updateChildren(mapDialog)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageAsFile(
    receivingUserId: String, fileUrl: Any, uri: Uri?, messageKey: String, typeMessage: Int
) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserId"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserId/$CURRENT_UID"
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[USER_FROM] = CURRENT_UID
    mapMessage[USER_TYPE] = typeMessage
    mapMessage[USER_ID] = messageKey
    mapMessage[USER_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[USER_FILE_URL] = fileUrl
    mapMessage[USER_TEXT] = getFileNameFromUrl(uri)
    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage
    REFERENCE_DB.updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun saveToMainList(id: String, type: String) {
    val refUser = "$NODE_MAIN_LIST/$CURRENT_UID/$id"
    val refReceived = "$NODE_MAIN_LIST/$id/$CURRENT_UID"
    val mapUser = hashMapOf<String, Any>()
    val mapReceived = hashMapOf<String, Any>()
    mapUser[USER_ID] = id
    mapUser[USER_TYPE] = type
    mapReceived[USER_ID] = CURRENT_UID
    mapReceived[USER_TYPE] = type
    val commonMap = hashMapOf<String, Any>()
    commonMap[refUser] = mapUser
    commonMap[refReceived] = mapReceived
    REFERENCE_DB
        .updateChildren(commonMap)
        .addOnFailureListener { showToast(it.message.toString()) }
}
