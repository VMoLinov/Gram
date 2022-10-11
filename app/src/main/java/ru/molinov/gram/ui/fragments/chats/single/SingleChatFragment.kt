package ru.molinov.gram.ui.fragments.chats.single

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import ru.molinov.gram.R
import ru.molinov.gram.database.*
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.ui.fragments.chats.BaseChatFragment
import ru.molinov.gram.ui.fragments.chats.ChatsViewModelFactory
import ru.molinov.gram.utilites.MAIN_ACTIVITY
import ru.molinov.gram.utilites.TYPE_CHAT
import ru.molinov.gram.utilites.showToast

class SingleChatFragment : BaseChatFragment() {

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        commonModel = arguments?.getParcelable(ARGS_KEY) ?: CommonModel()
        refMessages = REFERENCE_DB.child(NODE_MESSAGES).child(CURRENT_UID).child(commonModel.id)
        viewModel = ViewModelProvider(
            this, ChatsViewModelFactory(MAIN_ACTIVITY.application, commonModel.id, refMessages)
        )[SingleChatViewModel::class.java]
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setSent() = with(binding) {
        btnSent.setOnClickListener {
            isSmoothScroll = true
            val enterMessage = message.text.toString()
            if (enterMessage.isEmpty()) showToast(getString(R.string.single_chat_enter_a_message))
            else sendMessageAsText(enterMessage, commonModel.id) {
                saveToMainList(commonModel.id, TYPE_CHAT)
                message.setText(getString(R.string.app_empty_string))
            }
        }
    }

    companion object {
        private const val ARGS_KEY = "Single Chat ARGS"
        fun newInstance(model: CommonModel = CommonModel()): SingleChatFragment {
            val fragment = SingleChatFragment()
            val args = Bundle()
            args.putParcelable(ARGS_KEY, model)
            fragment.arguments = args
            return fragment
        }
    }
}
