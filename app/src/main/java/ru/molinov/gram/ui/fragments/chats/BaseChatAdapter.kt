package ru.molinov.gram.ui.fragments.chats

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.ui.fragments.chats.holders.ChatBaseViewHolder

class BaseChatAdapter :
    ListAdapter<CommonModel, ChatBaseViewHolder>(SingleChatDiffCallback) {

    private object SingleChatDiffCallback : DiffUtil.ItemCallback<CommonModel>() {
        override fun areItemsTheSame(oldItem: CommonModel, newItem: CommonModel): Boolean =
            oldItem.timestamp == newItem.timestamp

        override fun areContentsTheSame(oldItem: CommonModel, newItem: CommonModel): Boolean =
            oldItem == newItem
    }

    private val messagesList = mutableListOf<CommonModel>()
    private val holdersList = mutableListOf<ChatBaseViewHolder>()

    fun addItem(item: CommonModel, onSuccess: () -> Unit) {
        if (!messagesList.contains(item)) {
            messagesList.add(item)
            messagesList.sortBy { it.timestamp }
            submitList(messagesList)
            notifyItemInserted(currentList.indexOf(item))
        }
        onSuccess()
    }

    override fun getItemViewType(position: Int) = currentList[position].messageType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatBaseViewHolder =
        ChatBaseViewHolder.getHolder(parent, viewType)

    override fun onBindViewHolder(holder: ChatBaseViewHolder, position: Int) =
        holder.bind(currentList[position])

    override fun getItemCount(): Int = currentList.size

    override fun onViewAttachedToWindow(holder: ChatBaseViewHolder) {
        holder.attach(currentList[holder.absoluteAdapterPosition])
        holdersList.add(holder)
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: ChatBaseViewHolder) {
        holder.detach()
        holdersList.remove(holder)
        super.onViewDetachedFromWindow(holder)
    }

    fun onDestroy() = holdersList.forEach { it.detach() }
}
