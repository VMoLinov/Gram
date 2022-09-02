package ru.molinov.gram.ui.fragments.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.molinov.gram.R
import ru.molinov.gram.databinding.ItemAddContactsBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.downloadAndSetImage

class AddContactsAdapter :
    ListAdapter<CommonModel, AddContactsAdapter.AddContactsViewHolder>(ListAdapterDiffCallback) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsViewHolder {
        val holder = AddContactsViewHolder(
            ItemAddContactsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        holder.binding.apply {
            root.setOnClickListener {
                isChoice.isVisible = !isChoice.isVisible
                if (isChoice.isVisible) {
                    AddContactsFragment.listContacts.add(currentList[holder.absoluteAdapterPosition])
                } else {
                    AddContactsFragment.listContacts.remove(currentList[holder.absoluteAdapterPosition])
                }
            }
        }
        return holder
    }

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
