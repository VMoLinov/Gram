package ru.molinov.gram.ui.fragments.singlechat

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
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
    private var isSmoothScroll = true
    private var isScrolling = false
    private var messagesCount = 10

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contact = arguments?.getParcelable(ARGS_KEY) ?: CommonModel()
        toolbar = MAIN_ACTIVITY.toolbar.findViewById(R.id.toolbarInfo)
        refUser = REFERENCE_DB.root.child(NODE_USERS).child(contact.id)
        refMessages = REFERENCE_DB.child(NODE_MESSAGES).child(CURRENT_UID).child(contact.id)
    }

    override fun onResume() {
        super.onResume()
        initToolbar()
        initRecyclerView()
    }

    private fun initRecyclerView() = with(binding) {
        val adapter = SingleChatAdapter()
        singleChatRecyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext())
        singleChatRecyclerView.layoutManager = layoutManager
        singleChatRecyclerView.setHasFixedSize(true)
        singleChatRecyclerView.isNestedScrollingEnabled = false
        listenerRecycler = AppChildEventListener {
            adapter.addItem(it.getCommonModel()) { swipeRefresh.isRefreshing = false }
            if (isSmoothScroll) singleChatRecyclerView.smoothScrollToPosition(adapter.itemCount)
        }
        refMessages.limitToLast(messagesCount).addChildEventListener(listenerRecycler)
        singleChatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isScrolling && dy < 0 && layoutManager.findFirstVisibleItemPosition() <= ITEMS_PRE_LOAD) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        })
        swipeRefresh.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        isSmoothScroll = false
        isScrolling = false
        messagesCount += 10
        refMessages.removeEventListener(listenerRecycler)
        refMessages.limitToLast(messagesCount).addChildEventListener(listenerRecycler)
    }

    private fun initToolbar() {
        toolbar.isVisible = true
        listenerToolbar = AppValueEventListener {
            receivingUser = it.getUserModel()
            initToolbarInfo()
        }
        refUser.addValueEventListener(listenerToolbar)
        binding.singleChatBtnSent.setOnClickListener {
            isSmoothScroll = true
            val message = binding.singleChatMessage.text.toString()
            if (message.isEmpty()) showToast(getString(R.string.single_chat_enter_a_message))
            else sendMessage(message, contact.id, TYPE_TEXT) {
                binding.singleChatMessage.setText(getString(R.string.app_empty_string))
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
        private const val ITEMS_PRE_LOAD = 3
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
