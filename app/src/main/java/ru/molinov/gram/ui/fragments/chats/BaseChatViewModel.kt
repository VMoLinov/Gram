package ru.molinov.gram.ui.fragments.chats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import ru.molinov.gram.models.CommonModel

class BaseChatViewModel(application: Application) : AndroidViewModel(application) {

    val liveData: MutableLiveData<List<CommonModel>> = MutableLiveData(mutableListOf())
    lateinit var refMessages: DatabaseReference
}
