package ru.molinov.gram.ui.fragments

import ru.molinov.gram.R
import ru.molinov.gram.databinding.FragmentEnterPhoneNumberBinding
import ru.molinov.gram.utilites.replaceFragment
import ru.molinov.gram.utilites.showToast

class EnterPhoneNumberFragment :
    BaseFragment<FragmentEnterPhoneNumberBinding>(FragmentEnterPhoneNumberBinding::inflate) {

    override fun onStart() {
        super.onStart()
        binding.registerBtnNext.setOnClickListener { sendCode() }
    }

    private fun sendCode() {
        if (binding.registerInputPhoneNumber.text.isEmpty()) {
            showToast(getString(R.string.register_enter_phone))
        } else {
            replaceFragment(EnterCodeFragment())
        }
    }
}
