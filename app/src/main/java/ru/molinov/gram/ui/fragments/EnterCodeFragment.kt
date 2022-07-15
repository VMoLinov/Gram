package ru.molinov.gram.ui.fragments

import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import ru.molinov.gram.databinding.FragmentEnterCodeBinding

class EnterCodeFragment :
    BaseFragment<FragmentEnterCodeBinding>(FragmentEnterCodeBinding::inflate) {

    override fun onStart() {
        super.onStart()
        binding.registerInputCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 6) {
                    verifyCode()
                }
            }
        })
    }

    private fun verifyCode() {
        Toast.makeText(requireContext(), "Работает", Toast.LENGTH_SHORT).show()
    }
}
