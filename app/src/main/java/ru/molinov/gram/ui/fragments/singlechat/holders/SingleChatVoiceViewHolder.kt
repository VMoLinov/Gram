package ru.molinov.gram.ui.fragments.singlechat.holders

import androidx.core.view.isVisible
import ru.molinov.gram.databinding.ItemMessageVoiceBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.AppVoicePlayer
import ru.molinov.gram.utilites.asTime

class SingleChatVoiceViewHolder(val binding: ItemMessageVoiceBinding) :
    SingleChatBaseViewHolder(binding) {

    private val voicePlayer = AppVoicePlayer()

    override fun bind(model: CommonModel) = with(binding) {
        if (model.isFromUser()) {
            userMessageTime.text = model.timestamp.asTime()
            blockUserMessage.isVisible = true
        } else {

            receivedMessageTime.text = model.timestamp.asTime()
            blockReceivedMessage.isVisible = true
        }
    }

    override fun attach(model: CommonModel) = with(binding) {
        voicePlayer.init()
        if (model.isFromUser()) {
            userMessagePlay.setOnClickListener { play ->
                play.isVisible = false
                userMessageStop.isVisible = true
                userMessageStop.setOnClickListener { stop ->
                    stop {
                        stop.setOnClickListener(null)
                        play.isVisible = true
                        stop.isVisible = false
                    }
                }
                play(model) {
                    play.isVisible = true
                    userMessageStop.isVisible = false
                }
            }
        } else {
            receivedMessagePlay.setOnClickListener { play ->
                play.isVisible = false
                receivedMessageStop.isVisible = true
                receivedMessageStop.setOnClickListener { stop ->
                    stop {
                        stop.setOnClickListener(null)
                        play.isVisible = true
                        stop.isVisible = false
                    }
                }
                play(model) {
                    play.isVisible = true
                    receivedMessageStop.isVisible = false
                }
            }
        }
    }

    override fun detach() = with(binding) {
        userMessagePlay.setOnClickListener(null)
        receivedMessagePlay.setOnClickListener(null)
        voicePlayer.release()
    }

    private fun play(model: CommonModel, onSuccess: () -> Unit) {
        voicePlayer.play(model.id, model.fileUrl) { onSuccess() }
    }

    private fun stop(onSuccess: () -> Unit) = voicePlayer.stop { onSuccess() }
}
