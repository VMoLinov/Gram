package ru.molinov.gram.ui.fragments

import android.widget.Toast
import ru.molinov.gram.R
import ru.molinov.gram.databinding.FragmentEnterPhoneNumberBinding

class EnterPhoneNumberFragment :
    BaseFragment<FragmentEnterPhoneNumberBinding>(FragmentEnterPhoneNumberBinding::inflate) {

    override fun onStart() {
        super.onStart()
        binding.registerBtnNext.setOnClickListener { sendCode() }
    }

    private fun sendCode() {
        if (binding.registerInputPhoneNumber.text.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.register_enter_phone),
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            parentFragmentManager.beginTransaction()
                .replace(R.id.registerDataContainer, EnterCodeFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
