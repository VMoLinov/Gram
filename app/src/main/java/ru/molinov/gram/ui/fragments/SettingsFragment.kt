package ru.molinov.gram.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ru.molinov.gram.MainActivity
import ru.molinov.gram.R
import ru.molinov.gram.databinding.FragmentSettingsBinding
import ru.molinov.gram.ui.activities.RegisterActivity
import ru.molinov.gram.utilites.AUTH
import ru.molinov.gram.utilites.USER
import ru.molinov.gram.utilites.replaceActivity
import ru.molinov.gram.utilites.replaceFragment

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
        }
        settingsBtnChangeUsername.setOnClickListener { replaceFragment(ChangeUsernameFragment()) }
        settingsBtnChangeBio.setOnClickListener { replaceFragment(ChangeBioFragment()) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_menu_exit -> {
                AUTH.signOut()
                (activity as MainActivity).replaceActivity(RegisterActivity())
                return true
            }
            R.id.settings_menu_change_name -> replaceFragment(ChangeFullNameFragment())
        }
        return true
    }
}
