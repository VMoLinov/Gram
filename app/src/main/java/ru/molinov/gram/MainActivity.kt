package ru.molinov.gram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ru.molinov.gram.databinding.ActivityMainBinding
import ru.molinov.gram.ui.activities.RegisterActivity
import ru.molinov.gram.ui.fragments.ChatsFragment
import ru.molinov.gram.ui.objects.AppDrawer
import ru.molinov.gram.utilites.addFragment
import ru.molinov.gram.utilites.auth
import ru.molinov.gram.utilites.initFirebase
import ru.molinov.gram.utilites.replaceActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    internal lateinit var appDrawer: AppDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        initProperties()
        initFunc()
    }

    private fun initFunc() {
        if (auth.currentUser != null) {
            setSupportActionBar(toolbar)
            appDrawer.create()
            addFragment(ChatsFragment())
        } else {
            replaceActivity(RegisterActivity())
        }
    }

    private fun initProperties() {
        toolbar = binding.mainToolbar
        appDrawer = AppDrawer(this, toolbar)
        initFirebase()
    }
}
