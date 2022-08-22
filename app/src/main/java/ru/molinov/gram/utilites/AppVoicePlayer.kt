package ru.molinov.gram.utilites

import android.media.MediaPlayer
import ru.molinov.gram.database.getFile
import java.io.File

class AppVoicePlayer {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var file: File

    fun play(messageKey: String, fileUrl: String, onSuccess: () -> Unit) {
        file = File(MAIN_ACTIVITY.filesDir, messageKey)
        if (file.exists() && file.length() > 0 && file.isFile) {
            startPlay { onSuccess() }
        } else {
            file.createNewFile()
            getFile(file, fileUrl) {
                startPlay { onSuccess() }
            }
        }
    }

    private fun startPlay(onSuccess: () -> Unit) {
        try {
            mediaPlayer.setDataSource(file.absolutePath)
            mediaPlayer.prepare()
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {
                stop { onSuccess() }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    fun stop(onSuccess: () -> Unit) {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
        } catch (e: Exception) {
            showToast(e.message.toString())
        } finally {
            onSuccess()
        }
    }

    fun release() = mediaPlayer.release()

    fun init() {
        mediaPlayer = MediaPlayer()
    }
}
