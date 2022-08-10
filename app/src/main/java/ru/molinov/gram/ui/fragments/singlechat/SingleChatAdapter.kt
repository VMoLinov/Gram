package ru.molinov.gram.ui.fragments.singlechat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.molinov.gram.database.CURRENT_UID
import ru.molinov.gram.databinding.MessageItemBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.asTime

class SingleChatAdapter :
    ListAdapter<CommonModel, SingleChatAdapter.SingleChatViewHolder>(SingleChatDiffCallback) {

    private object SingleChatDiffCallback : DiffUtil.ItemCallback<CommonModel>() {
        override fun areItemsTheSame(oldItem: CommonModel, newItem: CommonModel): Boolean =
            oldItem.timestamp == newItem.timestamp

        override fun areContentsTheSame(oldItem: CommonModel, newItem: CommonModel): Boolean =
            oldItem == newItem
    }

    private val messagesList = mutableListOf<CommonModel>()

    fun addItem(item: CommonModel) {
        if (!messagesList.contains(item)) {
            messagesList.add(item)
            messagesList.sortBy { it.timestamp }
            submitList(messagesList)
            notifyItemInserted(currentList.indexOf(item))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatViewHolder {
        return SingleChatViewHolder(
            MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SingleChatViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

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
