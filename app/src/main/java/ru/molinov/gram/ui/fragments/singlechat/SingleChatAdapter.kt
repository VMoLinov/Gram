package ru.molinov.gram.ui.fragments.singlechat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ru.molinov.gram.database.CURRENT_UID
import ru.molinov.gram.databinding.MessageImageItemBinding
import ru.molinov.gram.databinding.MessageTextItemBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.TYPE_MESSAGE_IMAGE
import ru.molinov.gram.utilites.TYPE_MESSAGE_TEXT
import ru.molinov.gram.utilites.asTime
import ru.molinov.gram.utilites.downloadAndSetImage

class SingleChatAdapter :
    ListAdapter<CommonModel, SingleChatAdapter.SingleChatBaseViewHolder>(SingleChatDiffCallback) {

    private object SingleChatDiffCallback : DiffUtil.ItemCallback<CommonModel>() {
        override fun areItemsTheSame(oldItem: CommonModel, newItem: CommonModel): Boolean =
            oldItem.timestamp == newItem.timestamp

        override fun areContentsTheSame(oldItem: CommonModel, newItem: CommonModel): Boolean =
            oldItem == newItem
    }

    private val messagesList = mutableListOf<CommonModel>()

    fun addItem(item: CommonModel, onSuccess: () -> Unit) {
        if (!messagesList.contains(item)) {
            messagesList.add(item)
            messagesList.sortBy { it.timestamp }
            submitList(messagesList)
            notifyItemInserted(currentList.indexOf(item))
        }
        onSuccess()
    }

    override fun getItemViewType(position: Int) = currentList[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatBaseViewHolder {
        return when (viewType) {
            TYPE_MESSAGE_TEXT -> {
                SingleChatTextViewHolder(
                    MessageTextItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            TYPE_MESSAGE_IMAGE -> {
                SingleChatImageViewHolder(
                    MessageImageItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> {
                SingleChatTextViewHolder(
                    MessageTextItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: SingleChatBaseViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    abstract inner class SingleChatBaseViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(model: CommonModel)
    }

    inner class SingleChatTextViewHolder(val binding: MessageTextItemBinding) :
        SingleChatBaseViewHolder(binding) {

        override fun bind(model: CommonModel) = with(binding) {
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

    inner class SingleChatImageViewHolder(val binding: MessageImageItemBinding) :
        SingleChatBaseViewHolder(binding) {

        override fun bind(model: CommonModel) = with(binding) {
            if (model.from == CURRENT_UID) {
                userMessage.downloadAndSetImage(model.fileUrl)
                userMessageTime.text = model.timestamp.asTime()
                blockUserMessage.isVisible = true
                blockReceivedMessage.isVisible = false
            } else {
                receivedMessage.downloadAndSetImage(model.fileUrl)
                receivedMessageTime.text = model.timestamp.asTime()
                blockReceivedMessage.isVisible = true
                blockUserMessage.isVisible = false
            }
        }
    }
}
