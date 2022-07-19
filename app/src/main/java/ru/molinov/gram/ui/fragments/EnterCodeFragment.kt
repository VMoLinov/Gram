package ru.molinov.gram.ui.fragments

import com.google.firebase.auth.PhoneAuthProvider
import ru.molinov.gram.MainActivity
import ru.molinov.gram.databinding.FragmentEnterCodeBinding
import ru.molinov.gram.ui.activities.RegisterActivity
import ru.molinov.gram.utilites.AppTextWatcher
import ru.molinov.gram.utilites.auth
import ru.molinov.gram.utilites.replaceActivity
import ru.molinov.gram.utilites.showToast

class EnterCodeFragment(private val phoneNumber: String, val id: String) :
    BaseFragment<FragmentEnterCodeBinding>(FragmentEnterCodeBinding::inflate) {

    override fun onStart() {
        super.onStart()
        (activity as RegisterActivity).title = phoneNumber
        binding.registerInputCode.addTextChangedListener(AppTextWatcher {
            if (it.length == 6) verifyCode()
        })
    }

    private fun verifyCode() {
        val code = binding.registerInputCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                showToast("Аутентификация выполнена успешно")
                (activity as RegisterActivity).replaceActivity(MainActivity())
            } else showToast(it.exception?.message.toString())
        }
    }
}
