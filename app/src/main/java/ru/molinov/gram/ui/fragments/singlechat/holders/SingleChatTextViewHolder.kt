package ru.molinov.gram.ui.fragments.singlechat.holders

import androidx.core.view.isVisible
import ru.molinov.gram.database.CURRENT_UID
import ru.molinov.gram.databinding.MessageTextItemBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.asTime

class SingleChatTextViewHolder(val binding: MessageTextItemBinding) :
    SingleChatBaseViewHolder(binding) {

    override fun bind(model: CommonModel) = with(binding) {
        if (model.from == CURRENT_UID) {
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
