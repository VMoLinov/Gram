package ru.molinov.gram.ui.fragments.mainlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.molinov.gram.R
import ru.molinov.gram.databinding.MainListItemBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.downloadAndSetImage

class MainListAdapter :
    ListAdapter<CommonModel, MainListAdapter.ListAdapterViewHolder>(ListAdapterDiffCallback) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapterViewHolder {
        return ListAdapterViewHolder(
            MainListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ListAdapterViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ListAdapterViewHolder(val binding: MainListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: CommonModel) = with(binding) {
            fullName.text = model.fullName
            lastMessage.text = model.lastMessage
            photo.downloadAndSetImage(model.photoUrl, R.drawable.ic_default_user)
        }
    }
}
