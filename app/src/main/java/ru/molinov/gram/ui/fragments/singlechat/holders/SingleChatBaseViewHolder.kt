package ru.molinov.gram.ui.fragments.singlechat.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ru.molinov.gram.databinding.ItemMessageFileBinding
import ru.molinov.gram.databinding.ItemMessageImageBinding
import ru.molinov.gram.databinding.ItemMessageTextBinding
import ru.molinov.gram.databinding.ItemMessageVoiceBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.TYPE_MESSAGE_FILE
import ru.molinov.gram.utilites.TYPE_MESSAGE_IMAGE
import ru.molinov.gram.utilites.TYPE_MESSAGE_TEXT
import ru.molinov.gram.utilites.TYPE_MESSAGE_VOICE

abstract class SingleChatBaseViewHolder(binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(model: CommonModel)
    open fun attach(model: CommonModel) {}
    open fun detach() {}

    companion object {
        fun getHolder(parent: ViewGroup, viewType: Int): SingleChatBaseViewHolder {
            return when (viewType) {
                TYPE_MESSAGE_TEXT -> {
                    SingleChatTextViewHolder(
                        ItemMessageTextBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                        )
                    )
                }
                TYPE_MESSAGE_IMAGE -> {
                    SingleChatImageViewHolder(
                        ItemMessageImageBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                        )
                    )
                }
                TYPE_MESSAGE_VOICE -> {
                    SingleChatVoiceViewHolder(
                        ItemMessageVoiceBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                        )
                    )
                }
                TYPE_MESSAGE_FILE -> {
                    SingleChatFileViewHolder(
                        ItemMessageFileBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                        )
                    )
                }
                else -> {
                    SingleChatTextViewHolder(
                        ItemMessageTextBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                        )
                    )
                }
            }
        }
    }
}
