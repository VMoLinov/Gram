package ru.molinov.gram.ui.settings

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import ru.molinov.gram.R
import ru.molinov.gram.database.AUTH
import ru.molinov.gram.database.USER
import ru.molinov.gram.database.changeUserPhoto
import ru.molinov.gram.database.getUserPhoto
import ru.molinov.gram.databinding.FragmentSettingsBinding
import ru.molinov.gram.ui.fragments.base.BaseOptionsFragment
import ru.molinov.gram.utilites.*

class SettingsFragment :
    BaseOptionsFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

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
            initUserPhoto(photoUrl)
        }
        settingsBtnChangeUsername.setOnClickListener { replaceFragment(ChangeUsernameFragment()) }
        settingsBtnChangeBio.setOnClickListener { replaceFragment(ChangeBioFragment()) }
        settingsChangePhoto.setOnClickListener { startCrop() }
    }

    private fun initUserPhoto(photoUrl: String) {
        if (photoUrl.isBlank()) getUserPhoto { setUserPhoto(it) }
        else setUserPhoto(photoUrl)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_menu_exit -> {
                AppStates.updateState(AppStates.OFFLINE)
                AUTH.signOut()
                restartActivity()
                return true
            }
            R.id.settings_menu_change_name -> replaceFragment(ChangeFullNameFragment())
        }
        return true
    }

    private fun setUserPhoto(url: String) {
        binding.settingsUserPhoto.downloadAndSetImage(url, R.drawable.ic_default_user)
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            changeUserPhoto(uri) {
                setUserPhoto(it)
                updateDrawerHeader()
                showToast(getString(R.string.app_toast_data_update))
            }
        } else showToast(result.error.toString())
    }

    private fun startCrop() {
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.OFF)
                setRequestedSize(250, 250)
                setAspectRatio(1, 1)
                setCropShape(CropImageView.CropShape.OVAL)
                setBorderCornerLength(0f)
                setBorderCornerThickness(0f)
            }
        )
    }
}
