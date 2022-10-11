package ru.molinov.gram.ui.fragments.chats

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import ru.molinov.gram.database.*
import ru.molinov.gram.models.UserModel
import ru.molinov.gram.utilites.AppChildEventListener
import ru.molinov.gram.utilites.AppValueEventListener
import ru.molinov.gram.utilites.AppVoiceRecorder
import ru.molinov.gram.utilites.TYPE_MESSAGE_VOICE

open class BaseChatViewModel(
    application: Application,
    private val id: String,
    private val refMessages: DatabaseReference
) : AndroidViewModel(application) {

    var adapter: BaseChatAdapter = BaseChatAdapter()
        private set
    private val refUser: DatabaseReference = REFERENCE_DB.root.child(NODE_USERS).child(id)
    private val voiceRecorder: AppVoiceRecorder = AppVoiceRecorder()
    private var messagesCount = 10
    private lateinit var listenerToolbar: AppValueEventListener
    private lateinit var listenerRecycler: ChildEventListener

    fun initToolbar(onSuccess: (UserModel) -> Unit) {
        listenerToolbar = AppValueEventListener {
            val receivingUser = it.getUserModel()
            onSuccess(receivingUser)
        }
        refUser.addValueEventListener(listenerToolbar)
    }

    fun onPause() {
        refUser.removeEventListener(listenerToolbar)
        refMessages.removeEventListener(listenerRecycler)
    }

    fun initRecyclerView(onSuccess: (Int) -> Unit) {
        listenerRecycler = AppChildEventListener {
            adapter.addItem(it.getCommonModel()) { onSuccess(adapter.itemCount) }
        }
        refMessages.limitToLast(messagesCount).addChildEventListener(listenerRecycler)
    }

    fun onDestroy() {
        voiceRecorder.releaseRecorder()
        adapter.onDestroy()
    }

    fun updateData() {
        messagesCount += 10
        refMessages.removeEventListener(listenerRecycler)
        refMessages.limitToLast(messagesCount).addChildEventListener(listenerRecycler)
    }

    fun startRecording() = voiceRecorder.startRecording(id)
    fun stopRecording(onSuccess: () -> Unit) {
        voiceRecorder.stopRecording { file, contactId ->
            sendFile(Uri.fromFile(file), TYPE_MESSAGE_VOICE, contactId)
            onSuccess()
        }
    }

    fun sendFile(uri: Uri?, type: Int, idForLoad: String = id) =
        uploadFile(uri, idForLoad, type)
}
