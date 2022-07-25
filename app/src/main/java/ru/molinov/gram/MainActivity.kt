package ru.molinov.gram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import ru.molinov.gram.databinding.ActivityMainBinding
import ru.molinov.gram.ui.activities.RegisterActivity
import ru.molinov.gram.ui.fragments.ChatsFragment
import ru.molinov.gram.ui.models.User
import ru.molinov.gram.ui.objects.AppDrawer
import ru.molinov.gram.utilites.*

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
        if (AUTH.currentUser != null) {
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
        initUser()
    }

    private fun initUser() {
        REFERENCE_DB.child(NODE_USERS).child(CURRENT_UID)
            .addListenerForSingleValueEvent(AppValueEventListener {
                USER = it.getValue(User::class.java) ?: User()
            })
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let {
                REFERENCE_STORAGE.child("users_images").child(CURRENT_UID).putFile(it)
                    .addOnSuccessListener {
                        showToast(getString(R.string.toast_data_update))
                    }
            }
        } else showToast(result.error.toString())
    }

    fun startCrop() {
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setAspectRatio(1, 1)
            }
        )
    }
}
