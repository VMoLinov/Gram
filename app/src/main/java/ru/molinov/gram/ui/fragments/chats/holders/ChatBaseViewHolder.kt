package ru.molinov.gram.ui.fragments.chats.holders

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

abstract class ChatBaseViewHolder(binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(model: CommonModel)
    open fun attach(model: CommonModel) {}
    open fun detach() {}

    companion object {
        fun getHolder(parent: ViewGroup, viewType: Int): ChatBaseViewHolder {
            return when (viewType) {
                TYPE_MESSAGE_TEXT -> {
                    ChatTextViewHolder(
                        ItemMessageTextBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                        )
                    )
                }
                TYPE_MESSAGE_IMAGE -> {
                    ChatImageViewHolder(
                        ItemMessageImageBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                        )
                    )
                }
                TYPE_MESSAGE_VOICE -> {
                    ChatVoiceViewHolder(
                        ItemMessageVoiceBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                        )
                    )
                }
                TYPE_MESSAGE_FILE -> {
                    ChatFileViewHolder(
                        ItemMessageFileBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                        )
                    )
                }
                else -> {
                    ChatTextViewHolder(
                        ItemMessageTextBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                        )
                    )
                }
            }
        }
    }
}
