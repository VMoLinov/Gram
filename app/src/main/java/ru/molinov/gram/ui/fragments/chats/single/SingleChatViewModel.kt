package ru.molinov.gram.ui.fragments.chats.single

import android.app.Application
import com.google.firebase.database.DatabaseReference
import ru.molinov.gram.ui.fragments.chats.BaseChatViewModel

class SingleChatViewModel(
    application: Application,
    private val id: String,
    private val refMessages: DatabaseReference
) : BaseChatViewModel(application, id, refMessages) {


}
