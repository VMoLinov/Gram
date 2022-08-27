package ru.molinov.gram.ui.fragments.singlechat.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ru.molinov.gram.databinding.MessageFileItemBinding
import ru.molinov.gram.databinding.MessageImageItemBinding
import ru.molinov.gram.databinding.MessageTextItemBinding
import ru.molinov.gram.databinding.MessageVoiceItemBinding
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
                TYPE_MESSAGE_VOICE -> {
                    SingleChatVoiceViewHolder(
                        MessageVoiceItemBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                        )
                    )
                }
                TYPE_MESSAGE_FILE -> {
                    SingleChatFileViewHolder(
                        MessageFileItemBinding.inflate(
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
    }
}
