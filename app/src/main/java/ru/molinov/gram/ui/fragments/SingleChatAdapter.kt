package ru.molinov.gram.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.molinov.gram.databinding.MessageItemBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.CURRENT_UID
import ru.molinov.gram.utilites.asTime

class SingleChatAdapter : RecyclerView.Adapter<SingleChatAdapter.SingleChatViewHolder>() {

    private var messagesList = emptyList<CommonModel>()

    fun setMessagesList(list: List<CommonModel>) {
        messagesList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatViewHolder {
        return SingleChatViewHolder(
            MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SingleChatViewHolder, position: Int) {
        holder.bind(messagesList[position])
    }

    override fun getItemCount(): Int = messagesList.size

    inner class SingleChatViewHolder(val binding: MessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: CommonModel) = with(binding) {
            if (model.from == CURRENT_UID) {
                userMessage.text = model.text
                userMessageTime.text = model.timestamp.asTime()
                blockUserMessage.isVisible = true
                blockReceivedMessage.isVisible = false
            } else {
                receivedMessage.text = model.text
                receivedMessageTime.text = model.timestamp.asTime()
                blockReceivedMessage.isVisible = true
                blockUserMessage.isVisible = false
            }
        }
    }
}
