package ru.molinov.gram.ui.fragments.groups

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.ui.fragments.singlechat.holders.SingleChatBaseViewHolder

class GroupChatAdapter :
    ListAdapter<CommonModel, SingleChatBaseViewHolder>(SingleChatDiffCallback) {

    private object SingleChatDiffCallback : DiffUtil.ItemCallback<CommonModel>() {
        override fun areItemsTheSame(oldItem: CommonModel, newItem: CommonModel): Boolean =
            oldItem.timestamp == newItem.timestamp

        override fun areContentsTheSame(oldItem: CommonModel, newItem: CommonModel): Boolean =
            oldItem == newItem
    }

    private val messagesList = mutableListOf<CommonModel>()
    private val holdersList = mutableListOf<SingleChatBaseViewHolder>()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatBaseViewHolder =
        SingleChatBaseViewHolder.getHolder(parent, viewType)

    override fun onBindViewHolder(holder: SingleChatBaseViewHolder, position: Int) =
        holder.bind(currentList[position])

    override fun getItemCount(): Int = currentList.size

    override fun onViewAttachedToWindow(holder: SingleChatBaseViewHolder) {
        holder.attach(currentList[holder.absoluteAdapterPosition])
        holdersList.add(holder)
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: SingleChatBaseViewHolder) {
        holder.detach()
        holdersList.remove(holder)
        super.onViewDetachedFromWindow(holder)
    }

    fun onDestroy() = holdersList.forEach { it.detach() }
}
