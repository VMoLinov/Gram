package ru.molinov.gram.ui.fragments.chats.group.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import ru.molinov.gram.databinding.ItemAddContactsBinding
import ru.molinov.gram.ui.fragments.chats.group.AddContactsFragment

class AddContactsAdapter : GroupsBaseAdapter() {

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
}
