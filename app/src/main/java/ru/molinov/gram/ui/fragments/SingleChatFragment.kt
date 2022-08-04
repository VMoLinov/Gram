package ru.molinov.gram.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView
import ru.molinov.gram.R
import ru.molinov.gram.databinding.FragmentSingleChatBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.models.UserModel
import ru.molinov.gram.utilites.*

class SingleChatFragment :
    OptionsFragment<FragmentSingleChatBinding>(FragmentSingleChatBinding::inflate) {

    private lateinit var toolbar: ViewGroup
    private lateinit var listenerToolbar: AppValueEventListener
    private lateinit var receivingUser: UserModel
    private lateinit var refUser: DatabaseReference
    private lateinit var model: CommonModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = arguments?.getParcelable(ARGS_KEY) ?: CommonModel()
        toolbar = MAIN_ACTIVITY.toolbar.findViewById(R.id.toolbarInfo)
        refUser = REFERENCE_DB.root.child(NODE_USERS).child(model.id)
    }

    override fun onResume() {
        super.onResume()
        toolbar.isVisible = true
        listenerToolbar = AppValueEventListener {
            receivingUser = it.getUserModel()
            initToolbar()
        }
        refUser.addValueEventListener(listenerToolbar)
        binding.singleChatBtnSent.setOnClickListener {
            val message = binding.singleChatMessage.text.toString()
            if (message.isEmpty()) showToast("Enter a message")
            else sendMessage(message, model.id, TYPE_TEXT) {
                binding.singleChatMessage.setText("")
            }
        }
    }

    private fun initToolbar() {
        if (receivingUser.fullName.isEmpty()) {
            toolbar.findViewById<TextView>(R.id.toolbarFullName).text = model.fullName
        } else {
            toolbar.findViewById<TextView>(R.id.toolbarFullName).text = receivingUser.fullName
        }
        toolbar.findViewById<CircleImageView>(R.id.toolbarImage)
            .downloadAndSetImage(receivingUser.photoUrl, R.drawable.ic_default_user)
        toolbar.findViewById<TextView>(R.id.toolbarUserStatus).text = receivingUser.status
    }

    override fun onPause() {
        super.onPause()
        toolbar.isVisible = false
        refUser.removeEventListener(listenerToolbar)
    }

    companion object {
        private const val ARGS_KEY = "SINGLE_CHAT_ARGS_KEY"
        fun newInstance(model: CommonModel = CommonModel()): SingleChatFragment {
            val fragment = SingleChatFragment()
            val args = Bundle()
            args.putParcelable(ARGS_KEY, model)
            fragment.arguments = args
            return fragment
        }
    }
}
