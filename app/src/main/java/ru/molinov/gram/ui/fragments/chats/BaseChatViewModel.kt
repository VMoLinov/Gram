package ru.molinov.gram.ui.fragments.chats

import android.app.Application
import android.net.Uri
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import ru.molinov.gram.R
import ru.molinov.gram.database.*
import ru.molinov.gram.models.UserModel
import ru.molinov.gram.ui.fragments.mainlist.MainListFragment
import ru.molinov.gram.utilites.*

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
        listenerToolbar = AppValueEventListener { onSuccess(it.getUserModel()) }
        refUser.addValueEventListener(listenerToolbar)
    }

    fun onPause() {
        refUser.removeEventListener(listenerToolbar)
        refMessages.removeEventListener(listenerRecycler)
    }

    fun onDestroy() {
        voiceRecorder.releaseRecorder()
        adapter.onDestroy()
    }

    fun initRecyclerView(onSuccess: (Int) -> Unit) {
        listenerRecycler = AppChildEventListener {
            adapter.addItem(it.getCommonModel()) { onSuccess(adapter.itemCount) }
        }
        refMessages.limitToLast(messagesCount).addChildEventListener(listenerRecycler)
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

    val actionMenuProvider = object : MenuProvider {

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.single_chat_action_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.clear -> clearChat(id) {
                    showToast(getApplication<Application>().getString(R.string.single_chat_cleared))
                    replaceFragment(MainListFragment())
                }
                R.id.delete -> deleteChat(id) {
                    showToast(getApplication<Application>().getString(R.string.single_chat_deleted))
                    replaceFragment(MainListFragment())
                }
            }
            return true
        }
    }
}
