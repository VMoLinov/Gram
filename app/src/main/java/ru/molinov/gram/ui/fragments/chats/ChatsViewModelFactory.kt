package ru.molinov.gram.ui.fragments.chats

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DatabaseReference

class ChatsViewModelFactory(
    private val application: Application,
    private val id: String,
    private val refMessages: DatabaseReference
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            Application::class.java,
            String::class.java,
            DatabaseReference::class.java
        ).newInstance(application, id, refMessages)
    }
}
