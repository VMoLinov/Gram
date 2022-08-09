package ru.molinov.gram.ui.fragments.singlechat

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView
import ru.molinov.gram.R
import ru.molinov.gram.database.*
import ru.molinov.gram.databinding.FragmentSingleChatBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.models.UserModel
import ru.molinov.gram.ui.fragments.OptionsFragment
import ru.molinov.gram.utilites.*

class SingleChatFragment :
    OptionsFragment<FragmentSingleChatBinding>(FragmentSingleChatBinding::inflate) {

    private lateinit var toolbar: ViewGroup
    private lateinit var listenerToolbar: AppValueEventListener
    private lateinit var listenerRecycler: ChildEventListener
    private lateinit var receivingUser: UserModel
    private lateinit var refUser: DatabaseReference
    private lateinit var refMessages: DatabaseReference
    private lateinit var contact: CommonModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SingleChatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contact = arguments?.getParcelable(ARGS_KEY) ?: CommonModel()
        toolbar = MAIN_ACTIVITY.toolbar.findViewById(R.id.toolbarInfo)
        refUser = REFERENCE_DB.root.child(NODE_USERS).child(contact.id)
    }

    override fun onResume() {
        super.onResume()
        initToolbar()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = binding.singleChatRecyclerView
        adapter = SingleChatAdapter()
        recyclerView.adapter = adapter
        refMessages = REFERENCE_DB.child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        listenerRecycler = AppChildEventListener {
            adapter.addItem(it.getCommonModel())
            recyclerView.smoothScrollToPosition(adapter.itemCount)
        }
        refMessages.addChildEventListener(listenerRecycler)
    }

    private fun initToolbar() {
        toolbar.isVisible = true
        listenerToolbar = AppValueEventListener {
            receivingUser = it.getUserModel()
            initToolbarInfo()
        }
        refUser.addValueEventListener(listenerToolbar)
        binding.singleChatBtnSent.setOnClickListener {
            val message = binding.singleChatMessage.text.toString()
            if (message.isEmpty()) showToast("Enter a message")
            else sendMessage(message, contact.id, TYPE_TEXT) {
                binding.singleChatMessage.setText("")
            }
        }
    }

    private fun initToolbarInfo() {
        toolbar.apply {
            if (receivingUser.fullName.isEmpty()) {
                findViewById<TextView>(R.id.toolbarFullName).text = contact.fullName
            } else {
                findViewById<TextView>(R.id.toolbarFullName).text = receivingUser.fullName
            }
            findViewById<CircleImageView>(R.id.toolbarImage)
                .downloadAndSetImage(receivingUser.photoUrl, R.drawable.ic_default_user)
            findViewById<TextView>(R.id.toolbarUserStatus).text = receivingUser.status
        }
    }

    override fun onPause() {
        super.onPause()
        toolbar.isVisible = false
        refUser.removeEventListener(listenerToolbar)
        refMessages.removeEventListener(listenerRecycler)
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
