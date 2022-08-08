package ru.molinov.gram.ui.fragments

import ru.molinov.gram.R
import ru.molinov.gram.database.NODE_USERNAMES
import ru.molinov.gram.database.REFERENCE_DB
import ru.molinov.gram.database.USER
import ru.molinov.gram.database.changeUsernameInDatabase
import ru.molinov.gram.databinding.FragmentChangeUsernameBinding
import ru.molinov.gram.utilites.AppTextWatcher
import ru.molinov.gram.utilites.AppValueEventListener
import ru.molinov.gram.utilites.MAIN_ACTIVITY
import ru.molinov.gram.utilites.showToast

class ChangeUsernameFragment :
    BaseChangeFragment<FragmentChangeUsernameBinding>(FragmentChangeUsernameBinding::inflate) {

    override fun onResume() {
        super.onResume()
        initField()
    }

    private fun initField() {
        binding.changeUsername.setText(USER.username)
        binding.changeUsername.addTextChangedListener(AppTextWatcher {
            if (it.isNotEmpty() && it.toString() != USER.username) setHasOptionsMenu(true)
            else setHasOptionsMenu(false)
        })
    }

    override fun changeCheck() {
        val newUsername = binding.changeUsername.text.toString()
        changeUsername(newUsername)
    }

    private fun changeUsername(newUsername: String) {
        REFERENCE_DB.child(NODE_USERNAMES).addListenerForSingleValueEvent(AppValueEventListener {
            if (it.hasChild(newUsername)) showToast(MAIN_ACTIVITY.getString(R.string.settings_change_username_already_exists))
            else changeUsernameInDatabase(newUsername) {
                updateDrawerHeader()
                showToast(getString(R.string.app_toast_data_update))
                parentFragmentManager.popBackStack()
            }
        })
    }
}
