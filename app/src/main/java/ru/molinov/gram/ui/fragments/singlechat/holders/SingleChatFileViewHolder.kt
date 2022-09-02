package ru.molinov.gram.ui.fragments.singlechat.holders

import android.os.Environment
import android.view.View
import androidx.core.view.isVisible
import ru.molinov.gram.database.getFile
import ru.molinov.gram.databinding.ItemMessageFileBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.WRITE_FILES
import ru.molinov.gram.utilites.asTime
import ru.molinov.gram.utilites.checkPermission
import ru.molinov.gram.utilites.showToast
import java.io.File

class SingleChatFileViewHolder(val binding: ItemMessageFileBinding) :
    SingleChatBaseViewHolder(binding) {

    override fun bind(model: CommonModel) = with(binding) {
        if (model.isFromUser()) bindUser(model)
        else bindReceived(model)
    }

    private fun ItemMessageFileBinding.bindUser(model: CommonModel) {
        userFileName.text = model.text
        userMessageTime.text = model.timestamp.asTime()
        blockUserMessage.isVisible = true
    }

    private fun ItemMessageFileBinding.bindReceived(model: CommonModel) {
        receivedFileName.text = model.text
        receivedMessageTime.text = model.timestamp.asTime()
        blockReceivedMessage.isVisible = true
    }

    override fun attach(model: CommonModel) = with(binding) {
        if (model.isFromUser()) userFileOpen.setOnClickListener { clickToBtnFile(model) }
        else receivedFileOpen.setOnClickListener { clickToBtnFile(model) }
    }

    private fun ItemMessageFileBinding.clickToBtnFile(model: CommonModel) {
        val from = model.isFromUser()
        progressBarVisible(from)
        try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                model.text
            )
            if (checkPermission(WRITE_FILES)) {
                file.createNewFile()
                getFile(file, model.fileUrl) { progressBarInvisible(from) }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        } finally {
            progressBarInvisible(from)
        }
    }

    private fun ItemMessageFileBinding.progressBarVisible(isFromUser: Boolean) {
        if (isFromUser) {
            userFileOpen.visibility = View.INVISIBLE
            userProgressBar.isVisible = true
        } else {
            receivedFileOpen.visibility = View.INVISIBLE
            receivedProgressBar.isVisible = true
        }
    }

    private fun ItemMessageFileBinding.progressBarInvisible(isFromUser: Boolean) {
        if (isFromUser) {
            userFileOpen.visibility = View.VISIBLE
            userProgressBar.isVisible = false
        } else {
            receivedFileOpen.visibility = View.VISIBLE
            receivedProgressBar.isVisible = false
        }
    }

    override fun detach() {
        binding.userFileOpen.setOnClickListener(null)
        binding.receivedFileOpen.setOnClickListener(null)
    }
}
