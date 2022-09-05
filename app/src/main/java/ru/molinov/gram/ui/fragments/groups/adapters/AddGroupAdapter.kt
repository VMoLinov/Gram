package ru.molinov.gram.ui.fragments.groups.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.molinov.gram.databinding.ItemAddContactsBinding

class AddGroupAdapter : GroupsBaseAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsViewHolder {
        return AddContactsViewHolder(
            ItemAddContactsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }
}
