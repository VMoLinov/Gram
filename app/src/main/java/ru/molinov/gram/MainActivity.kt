package ru.molinov.gram

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.molinov.gram.activities.RegisterActivity
import ru.molinov.gram.databinding.ActivityMainBinding
import ru.molinov.gram.ui.fragments.ChatsFragment
import ru.molinov.gram.ui.objects.AppDrawer
import ru.molinov.gram.utilites.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    internal lateinit var toolbar: Toolbar
    internal lateinit var appDrawer: AppDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MAIN_ACTIVITY = this
        initFirebase()
        initUser {
            CoroutineScope(Dispatchers.IO).launch { initContacts() }
            initProperties()
            initFunc()
        }
    }

    private fun initFunc() {
        if (AUTH.currentUser != null) {
            setSupportActionBar(toolbar)
            appDrawer.create()
            addFragment(ChatsFragment())
        } else replaceActivity(RegisterActivity())
    }

    private fun initProperties() {
        toolbar = binding.mainToolbar
        appDrawer = AppDrawer()
    }

    override fun onStart() {
        super.onStart()
        if (AUTH.currentUser != null) AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        if (AUTH.currentUser != null) AppStates.updateState(AppStates.OFFLINE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat
                .checkSelfPermission(MAIN_ACTIVITY, READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            initContacts()
        }
    }
}
