package ru.molinov.gram.ui.fragments.singlechat.holders

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ru.molinov.gram.models.CommonModel

abstract class SingleChatBaseViewHolder(binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(model: CommonModel)
    open fun attach(model: CommonModel) {}
    open fun detach() {}
}
