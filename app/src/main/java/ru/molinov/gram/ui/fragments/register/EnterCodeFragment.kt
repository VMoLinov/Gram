package ru.molinov.gram.ui.fragments.register

import android.os.Bundle
import android.view.View
import com.google.firebase.auth.PhoneAuthProvider
import ru.molinov.gram.R
import ru.molinov.gram.database.*
import ru.molinov.gram.databinding.FragmentEnterCodeBinding
import ru.molinov.gram.ui.fragments.BaseFragment
import ru.molinov.gram.utilites.*

class EnterCodeFragment :
    BaseFragment<FragmentEnterCodeBinding>(FragmentEnterCodeBinding::inflate) {

    private lateinit var phoneNumber: String
    private lateinit var id: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments?.getStringArray(ARGS)
        args?.let {
            phoneNumber = it.first()
            id = it.last()
        }
    }

    override fun onStart() {
        super.onStart()
        MAIN_ACTIVITY.title = phoneNumber
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
                REFERENCE_DB.child(NODE_USERS).child(uid)
                    .addListenerForSingleValueEvent(AppValueEventListener {
                        if (!it.hasChild(USER_NAME)) map[USER_NAME] = USER.username.ifEmpty { uid }
                        pushData(map)
                    })
            }
            .addOnFailureListener { showToast(it.message.toString()) }
    }

    private fun pushData(map: MutableMap<String, Any>) {
        REFERENCE_DB.child(NODE_PHONES).child(map[USER_PHONE].toString()).setValue(map[USER_ID])
            .addOnSuccessListener { _ ->
                REFERENCE_DB.child(NODE_USERS).child(map[USER_ID].toString()).updateChildren(map)
                    .addOnSuccessListener {
                        showToast(getString(R.string.register_authorization_successful))
                        hideKeyboard()
                        restartActivity()
                    }
                    .addOnFailureListener { showToast(it.message.toString()) }
            }
            .addOnFailureListener { showToast(it.message.toString()) }
    }

    companion object {
        private const val ARGS = "Enter Code Fragment ARGS"
        fun newInstance(number: String, id: String): EnterCodeFragment {
            val bundle = Bundle()
            bundle.putStringArray(ARGS, arrayOf(number, id))
            val fragment = EnterCodeFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
