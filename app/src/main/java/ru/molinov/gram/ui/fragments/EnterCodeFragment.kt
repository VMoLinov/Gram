package ru.molinov.gram.ui.fragments

import com.google.firebase.auth.PhoneAuthProvider
import ru.molinov.gram.MainActivity
import ru.molinov.gram.databinding.FragmentEnterCodeBinding
import ru.molinov.gram.ui.activities.RegisterActivity
import ru.molinov.gram.utilites.*

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
        AUTH.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val map = mutableMapOf<String, Any>()
                map[USER_ID] = UID
                map[USER_PHONE] = phoneNumber
                map[USER_NAME] = UID
                REFERENCE_DB.child(NODE_USERS).child(UID).updateChildren(map)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("Аутентификация выполнена успешно")
                            (activity as RegisterActivity).replaceActivity(MainActivity())
                        } else showToast("Ошибка авторизации")
                    }
            } else showToast(it.exception?.message.toString())
        }
    }
}
