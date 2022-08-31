package ru.molinov.gram.ui.settings

import ru.molinov.gram.R
import ru.molinov.gram.database.USER
import ru.molinov.gram.database.changeUserFullName
import ru.molinov.gram.databinding.FragmentChangeFullNameBinding
import ru.molinov.gram.ui.fragments.base.BaseChangeFragment
import ru.molinov.gram.utilites.AppTextWatcher
import ru.molinov.gram.utilites.showToast

class ChangeFullNameFragment :
    BaseChangeFragment<FragmentChangeFullNameBinding>(FragmentChangeFullNameBinding::inflate) {

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

    private fun loadName() = with(binding) {
        val fullNameList = USER.fullName.split(" ")
        changeInputName.setText(fullNameList.first())
        changeInputLastName.setText(fullNameList.last())
    }

    override fun changeCheck(): Unit = with(binding) {
        val name = changeInputName.text.toString()
        val lastName = changeInputLastName.text.toString()
        if (name.isEmpty()) showToast(getString(R.string.settings_name_is_empty))
        else changeUserFullName("$name $lastName") {
            updateDrawerHeader()
            showToast(getString(R.string.app_toast_data_update))
            parentFragmentManager.popBackStack()
        }
    }
}
