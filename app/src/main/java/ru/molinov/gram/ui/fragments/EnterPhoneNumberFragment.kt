package ru.molinov.gram.ui.fragments

import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import ru.molinov.gram.MainActivity
import ru.molinov.gram.R
import ru.molinov.gram.activities.RegisterActivity
import ru.molinov.gram.databinding.FragmentEnterPhoneNumberBinding
import ru.molinov.gram.utilites.AUTH
import ru.molinov.gram.utilites.replaceActivity
import ru.molinov.gram.utilites.replaceFragment
import ru.molinov.gram.utilites.showToast
import java.util.concurrent.TimeUnit

class EnterPhoneNumberFragment :
    BaseFragment<FragmentEnterPhoneNumberBinding>(FragmentEnterPhoneNumberBinding::inflate) {

    private lateinit var phoneNumber: String
    private lateinit var callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()
        callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast(getString(R.string.register_authorization_successful))
                        (activity as RegisterActivity).replaceActivity(MainActivity())
                    } else showToast(it.exception?.message.toString())
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(EnterCodeFragment(phoneNumber, id))
            }
        }
        binding.registerBtnNext.setOnClickListener { sendCode() }
    }

    private fun sendCode() {
        if (binding.registerInputPhoneNumber.text.isEmpty()) {
            showToast(getString(R.string.register_enter_phone))
        } else {
            authUser()
        }
    }

    private fun authUser() {
        phoneNumber = binding.registerInputPhoneNumber.text.toString()
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(AUTH)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity as RegisterActivity)
                .setCallbacks(callback)
                .build()
        )
    }
}
