package ru.molinov.gram.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ru.molinov.gram.MainActivity
import ru.molinov.gram.R
import ru.molinov.gram.databinding.FragmentChangeNameBinding
import ru.molinov.gram.utilites.*

class ChangeNameFragment :
    BaseFragment<FragmentChangeNameBinding>(FragmentChangeNameBinding::inflate) {

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as MainActivity).menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.changeNameConfirm) changeName()
        return true
    }

    private fun changeName() = with(binding) {
        val name = settingsChangeName.text.toString()
        val lastName = settingsChangeLastName.text.toString()
        if (name.isEmpty()) showToast(getString(R.string.settings_name_is_empty))
        else {
            val fullName = "$name $lastName"
            refDb.child(NODE_USERS).child(uid).child(USER_FULL_NAME).setValue(fullName)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast(getString(R.string.data_update))
                        user.fullName = fullName
                        parentFragmentManager.popBackStack()
                    } else showToast("Data update error: ${it.exception?.message}")
                }
        }
    }
}
