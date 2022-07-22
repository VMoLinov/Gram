package ru.molinov.gram.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ru.molinov.gram.MainActivity
import ru.molinov.gram.R
import ru.molinov.gram.databinding.FragmentChangeFullNameBinding
import ru.molinov.gram.utilites.*

class ChangeFullNameFragment :
    OptionsFragment<FragmentChangeFullNameBinding>(FragmentChangeFullNameBinding::inflate) {

    override fun onResume() {
        super.onResume()
        loadName()
        setTextWatcher()
    }

    private fun setTextWatcher() = with(binding) {
        val fullNameList = USER.fullName.split(" ")
        changeInputName.addTextChangedListener(AppTextWatcher {
            val string = it.toString()
            if (string.isNotEmpty() && string != fullNameList.first()
                || fullNameList.last() != changeInputLastName.text.toString()
            ) setHasOptionsMenu(true)
            else setHasOptionsMenu(false)
        })
        changeInputLastName.addTextChangedListener(AppTextWatcher {
            val string = it.toString()
            if (string != fullNameList.last() || fullNameList.first() != changeInputName.text.toString())
                setHasOptionsMenu(true)
            else setHasOptionsMenu(false)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as MainActivity).menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.changeNameConfirm) changeName()
        return true
    }

    private fun loadName() = with(binding) {
        val fullNameList = USER.fullName.split(" ")
        changeInputName.setText(fullNameList.first())
        changeInputLastName.setText(fullNameList.last())
    }

    private fun changeName() = with(binding) {
        val name = changeInputName.text.toString()
        val lastName = changeInputLastName.text.toString()
        if (name.isEmpty()) showToast(getString(R.string.settings_name_is_empty))
        else {
            val fullName = "$name $lastName"
            REFERENCE_DB.child(NODE_USERS).child(UID).child(USER_FULL_NAME).setValue(fullName)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast(getString(R.string.toast_data_update))
                        USER.fullName = fullName
                        parentFragmentManager.popBackStack()
                    } else showToast("Data update error: ${it.exception?.message}")
                }
        }
    }
}