package ru.molinov.gram.ui.fragments.singlechat.holders

import androidx.core.view.isVisible
import ru.molinov.gram.databinding.MessageImageItemBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.asTime
import ru.molinov.gram.utilites.downloadAndSetImage

class SingleChatImageViewHolder(val binding: MessageImageItemBinding) :
    SingleChatBaseViewHolder(binding) {

    override fun bind(model: CommonModel) = with(binding) {
        if (model.isFromUser()) {
            userMessage.downloadAndSetImage(model.fileUrl)
            userMessageTime.text = model.timestamp.asTime()
            blockUserMessage.isVisible = true
        } else {
            receivedMessage.downloadAndSetImage(model.fileUrl)
            receivedMessageTime.text = model.timestamp.asTime()
            blockReceivedMessage.isVisible = true
        }
    }
}
