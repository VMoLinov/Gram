package ru.molinov.gram.ui.fragments.chats.group.adapters

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.molinov.gram.R
import ru.molinov.gram.databinding.ItemAddContactsBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.ui.fragments.chats.group.AddContactsFragment
import ru.molinov.gram.utilites.downloadAndSetImage

abstract class GroupsBaseAdapter :
    ListAdapter<CommonModel, GroupsBaseAdapter.AddContactsViewHolder>(ListAdapterDiffCallback) {

    private object ListAdapterDiffCallback : DiffUtil.ItemCallback<CommonModel>() {
        override fun areItemsTheSame(oldItem: CommonModel, newItem: CommonModel): Boolean =
            oldItem.timestamp == newItem.timestamp

        override fun areContentsTheSame(oldItem: CommonModel, newItem: CommonModel): Boolean =
            oldItem == newItem
    }

    private var list = mutableListOf<CommonModel>()

    fun updateList(item: CommonModel) {
        list.add(item)
        submitList(list)
        notifyItemInserted(list.size)
    }

    override fun getItemCount(): Int = currentList.size

    abstract override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddContactsViewHolder

    override fun onBindViewHolder(holder: AddContactsViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class AddContactsViewHolder(val binding: ItemAddContactsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: CommonModel) = with(binding) {
            fullName.text = model.fullName
            lastMessage.text = model.lastMessage
            photo.downloadAndSetImage(model.photoUrl, R.drawable.ic_default_user)
            isChoice.isVisible = AddContactsFragment.listContacts.contains(model)
        }
    }
}
