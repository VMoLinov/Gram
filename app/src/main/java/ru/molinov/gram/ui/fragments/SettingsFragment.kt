package ru.molinov.gram.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import de.hdodenhof.circleimageview.CircleImageView
import ru.molinov.gram.R
import ru.molinov.gram.databinding.FragmentSettingsBinding
import ru.molinov.gram.ui.activities.RegisterActivity
import ru.molinov.gram.utilites.*

class SettingsFragment :
    OptionsFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
    }

    private fun initFields() = with(binding) {
        USER.apply {
            settingsUserName.text = username
            settingsBio.text = bio
            settingsFullName.text = fullName
            settingsUserStatus.text = status
            settingsPhoneNumber.text = phone
            initUserPhoto()
        }
        settingsBtnChangeUsername.setOnClickListener { replaceFragment(ChangeUsernameFragment()) }
        settingsBtnChangeBio.setOnClickListener { replaceFragment(ChangeBioFragment()) }
        settingsChangePhoto.setOnClickListener { startCrop() }
    }

    private fun initUserPhoto() {
        if (USER.photoUrl.isBlank()) {
            REFERENCE_STORAGE.child(STORAGE_IMAGES).child(CURRENT_UID).downloadUrl
                .addOnSuccessListener {
                    val url = it.toString()
                    setPhoto(url)
                    REFERENCE_DB.child(NODE_USERS).child(CURRENT_UID).child(USER_PHOTO_URL)
                        .setValue(url)
                }
        } else setPhoto(USER.photoUrl)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_menu_exit -> {
                AUTH.signOut()
                MAIN_ACTIVITY.replaceActivity(RegisterActivity())
                return true
            }
            R.id.settings_menu_change_name -> replaceFragment(ChangeFullNameFragment())
        }
        return true
    }

    private fun setPhoto(url: String) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.ic_default_user)
            .into(binding.settingsUserPhoto)
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let {
                REFERENCE_STORAGE.child(STORAGE_IMAGES).child(CURRENT_UID).putFile(it)
                    .addOnSuccessListener { uploadTask ->
                        uploadTask.storage.downloadUrl.addOnSuccessListener { uri ->
                            val url = uri.toString()
                            setPhoto(url)
                            REFERENCE_DB.child(NODE_USERS).child(CURRENT_UID).child(USER_PHOTO_URL)
                                .setValue(url)
                            USER.photoUrl = url
                            showToast(getString(R.string.toast_data_update))
                        }
                    }
            }
        } else showToast(result.error.toString())
    }

    private fun startCrop() {
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.OFF)
                setRequestedSize(600, 600)
                setAspectRatio(1, 1)
                setCropShape(CropImageView.CropShape.OVAL)
                setBorderCornerLength(0f)
                setBorderCornerThickness(0f)
            }
        )
    }
}
