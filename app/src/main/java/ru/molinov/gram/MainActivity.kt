package ru.molinov.gram

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ru.molinov.gram.database.AUTH
import ru.molinov.gram.databinding.ActivityMainBinding
import ru.molinov.gram.ui.fragments.mainlist.MainListFragment
import ru.molinov.gram.ui.fragments.register.EnterPhoneNumberFragment
import ru.molinov.gram.ui.objects.AppDrawer
import ru.molinov.gram.utilites.AppStates
import ru.molinov.gram.utilites.MAIN_ACTIVITY
import ru.molinov.gram.utilites.addFragment

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    internal lateinit var toolbar: Toolbar
    internal lateinit var appDrawer: AppDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MAIN_ACTIVITY = this
        toolbar = binding.mainToolbar
        viewModel.initApp { initFunc() }
    }

    private fun initFunc() {
        appDrawer = AppDrawer()
        setSupportActionBar(toolbar)
        if (AUTH.currentUser != null) {
            appDrawer.create()
            addFragment(MainListFragment())
        } else addFragment(EnterPhoneNumberFragment())
    }

    override fun onStart() {
        super.onStart()
        if (AUTH.currentUser != null) viewModel.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        if (AUTH.currentUser != null) viewModel.updateState(AppStates.OFFLINE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.checkPermission()
    }
}
