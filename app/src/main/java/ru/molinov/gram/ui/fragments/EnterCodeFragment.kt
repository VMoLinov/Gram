package ru.molinov.gram.ui.fragments

import ru.molinov.gram.databinding.FragmentEnterCodeBinding
import ru.molinov.gram.utilites.AppTextWatcher
import ru.molinov.gram.utilites.showToast

class EnterCodeFragment :
    BaseFragment<FragmentEnterCodeBinding>(FragmentEnterCodeBinding::inflate) {

    override fun onStart() {
        super.onStart()
        binding.registerInputCode.addTextChangedListener(AppTextWatcher {
            if (it.length == 6) verifyCode()
        })
    }

    private fun verifyCode() {
        showToast("Работает")
    }
}
