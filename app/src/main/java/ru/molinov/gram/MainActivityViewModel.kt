package ru.molinov.gram

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.molinov.gram.database.initFirebase
import ru.molinov.gram.database.initUser
import ru.molinov.gram.utilites.AppStates
import ru.molinov.gram.utilites.MAIN_ACTIVITY
import ru.molinov.gram.utilites.READ_CONTACTS
import ru.molinov.gram.utilites.initContacts

class MainActivityViewModel : ViewModel() {

    fun initApp(onSuccess: () -> Unit) {
        initFirebase()
        initUser {
            CoroutineScope(Dispatchers.IO).launch { initContacts() }
            onSuccess()
        }
    }

    fun updateState(state: AppStates) {
        AppStates.updateState(state)
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(MAIN_ACTIVITY, READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED
        ) initContacts()
    }
}
