package ru.molinov.gram.ui.fragments.chats.holders

import androidx.core.view.isVisible
import ru.molinov.gram.databinding.ItemMessageTextBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.asTime

class ChatTextViewHolder(val binding: ItemMessageTextBinding) :
    ChatBaseViewHolder(binding) {

    override fun bind(model: CommonModel) = with(binding) {
        if (model.isFromUser()) {
            userMessage.text = model.text
            userMessageTime.text = model.timestamp.asTime()
            blockUserMessage.isVisible = true
        } else {
            receivedMessage.text = model.text
            receivedMessageTime.text = model.timestamp.asTime()
            blockReceivedMessage.isVisible = true
        }
    }
}
