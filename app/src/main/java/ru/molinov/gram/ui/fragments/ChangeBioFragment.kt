package ru.molinov.gram.ui.fragments

import ru.molinov.gram.R
import ru.molinov.gram.databinding.FragmentChangeBioBinding
import ru.molinov.gram.utilites.*

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
        REFERENCE_DB.child(NODE_USERS).child(UID).child(USER_BIO)
            .setValue(newBio)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast(getString(R.string.toast_data_update))
                    USER.bio = newBio
                    parentFragmentManager.popBackStack()
                } else showToast(it.exception?.message.toString())
            }
    }
}
