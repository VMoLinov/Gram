package ru.molinov.gram.ui.fragments.chats.group

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import ru.molinov.gram.R
import ru.molinov.gram.database.NODE_GROUPS
import ru.molinov.gram.database.NODE_MESSAGES
import ru.molinov.gram.database.REFERENCE_DB
import ru.molinov.gram.database.sendMessageToGroup
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.ui.fragments.chats.BaseChatFragment
import ru.molinov.gram.ui.fragments.chats.ChatsViewModelFactory
import ru.molinov.gram.utilites.MAIN_ACTIVITY
import ru.molinov.gram.utilites.showToast

class GroupChatFragment : BaseChatFragment() {

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        commonModel = arguments?.getParcelable(ARGS_KEY) ?: CommonModel()
        refMessages = REFERENCE_DB.child(NODE_GROUPS).child(commonModel.id).child(NODE_MESSAGES)
        viewModel = ViewModelProvider(
            this, ChatsViewModelFactory(MAIN_ACTIVITY.application, commonModel.id, refMessages)
        )[GroupChatViewModel::class.java]
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setSent() = with(binding) {
        btnSent.setOnClickListener {
            isSmoothScroll = true
            val enterMessage = message.text.toString()
            if (enterMessage.isEmpty()) showToast(getString(R.string.single_chat_enter_a_message))
            else sendMessageToGroup(enterMessage, commonModel.id) {
                message.setText(getString(R.string.app_empty_string))
            }
        }
    }

    companion object {
        private const val ARGS_KEY = "Groups chat ARGS"
        fun newInstance(model: CommonModel = CommonModel()): GroupChatFragment {
            val fragment = GroupChatFragment()
            val args = Bundle()
            args.putParcelable(ARGS_KEY, model)
            fragment.arguments = args
            return fragment
        }
    }
}