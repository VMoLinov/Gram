package ru.molinov.gram.ui.fragments

import com.google.firebase.auth.PhoneAuthProvider
import ru.molinov.gram.MainActivity
import ru.molinov.gram.R
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
        AUTH.signInWithCredential(credential)
            .addOnSuccessListener {
                val uid = AUTH.currentUser?.uid.toString()
                val map = mutableMapOf<String, Any>()
                map[USER_ID] = uid
                map[USER_PHONE] = phoneNumber
                map[USER_NAME] = USER.username.ifEmpty { uid }
                pushData(map)
            }
            .addOnFailureListener { showToast(it.message.toString()) }
    }

    private fun pushData(map: MutableMap<String, Any>) {
        REFERENCE_DB.child(NODE_PHONES).child(map[USER_PHONE].toString()).setValue(map[USER_ID])
            .addOnSuccessListener { _ ->
                REFERENCE_DB.child(NODE_USERS).child(map[USER_ID].toString()).updateChildren(map)
                    .addOnSuccessListener {
                        showToast(getString(R.string.authorization_successful))
                        (activity as RegisterActivity).replaceActivity(MainActivity())
                    }
                    .addOnFailureListener { showToast(it.message.toString()) }
            }
            .addOnFailureListener { showToast(it.message.toString()) }
    }
}
