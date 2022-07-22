package ru.molinov.gram.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ru.molinov.gram.MainActivity
import ru.molinov.gram.R
import ru.molinov.gram.databinding.FragmentChangeUsernameBinding
import ru.molinov.gram.utilites.*

class ChangeUsernameFragment :
    OptionsFragment<FragmentChangeUsernameBinding>(FragmentChangeUsernameBinding::inflate) {

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as MainActivity).menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.changeNameConfirm) checkField()
        return true
    }

    private fun checkField() {
        val newUsername = binding.changeUsername.text.toString()
        REFERENCE_DB.child(NODE_USERNAMES).addListenerForSingleValueEvent(AppValueEventListener {
            if (it.hasChild(newUsername)) showToast(getString(R.string.settings_change_username_already_exists))
            else changeUsername(newUsername)
        })
    }

    private fun changeUsername(newUsername: String) {
        REFERENCE_DB.child(NODE_USERNAMES).child(USER.username).removeValue().addOnSuccessListener {
            REFERENCE_DB.child(NODE_USERNAMES).child(newUsername).setValue(UID)
            USER.username = newUsername
            REFERENCE_DB.child(NODE_USERS).child(UID).child(USER_NAME).setValue(newUsername)
                .addOnCompleteListener {
                    showToast(getString(R.string.toast_data_update))
                    parentFragmentManager.popBackStack()
                }
        }
    }
}
