package ru.molinov.gram.ui.fragments.chats.group

import android.app.Application
import com.google.firebase.database.DatabaseReference
import ru.molinov.gram.ui.fragments.chats.BaseChatViewModel

class GroupChatViewModel(
    application: Application,
    id: String,
    refMessages: DatabaseReference
) : BaseChatViewModel(application, id, refMessages) {


}
