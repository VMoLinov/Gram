package ru.molinov.gram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ru.molinov.gram.databinding.ActivityMainBinding
import ru.molinov.gram.ui.fragments.ChatsFragment
import ru.molinov.gram.ui.objects.AppDrawer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    private lateinit var appDrawer: AppDrawer

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
        setSupportActionBar(toolbar)
        appDrawer.create()
        supportFragmentManager.beginTransaction()
            .add(R.id.dataContainer, ChatsFragment()).commit()
    }

    private fun initProperties() {
        toolbar = binding.mainToolbar
        appDrawer = AppDrawer(this, toolbar)
    }
}
