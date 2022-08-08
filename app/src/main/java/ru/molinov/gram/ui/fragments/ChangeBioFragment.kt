package ru.molinov.gram.ui.fragments

import ru.molinov.gram.R
import ru.molinov.gram.database.USER
import ru.molinov.gram.database.changeUserBio
import ru.molinov.gram.databinding.FragmentChangeBioBinding
import ru.molinov.gram.utilites.AppTextWatcher
import ru.molinov.gram.utilites.showToast

class ChangeBioFragment :
    BaseChangeFragment<FragmentChangeBioBinding>(FragmentChangeBioBinding::inflate) {

    override fun onResume() {
        super.onResume()
        initField()
    }

    private fun initField() {
        binding.changeInputBio.setText(USER.bio)
        binding.changeInputBio.addTextChangedListener(AppTextWatcher {
            if (it.isNotEmpty() || it.toString() != USER.bio) setHasOptionsMenu(true)
            else setHasOptionsMenu(false)
        })
    }

    override fun changeCheck() {
        val newBio = binding.changeInputBio.text.toString()
        changeUserBio(newBio) {
            updateDrawerHeader()
            showToast(getString(R.string.app_toast_data_update))
            parentFragmentManager.popBackStack()
        }
    }
}
