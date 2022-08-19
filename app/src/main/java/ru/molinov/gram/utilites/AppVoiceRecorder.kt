package ru.molinov.gram.utilites

import android.media.MediaRecorder
import android.os.Build
import java.io.File

class AppVoiceRecorder {

    private val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(MAIN_ACTIVITY)
    } else MediaRecorder()
    private lateinit var file: File
    private lateinit var id: String

    fun startRecording(contactId: String) {
        try {
            id = contactId
            createFileForRecord()
            prepareMediaRecorder()
            mediaRecorder.start()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    private fun prepareMediaRecorder() {
        mediaRecorder.apply {
            reset()
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            setOutputFile(file.absolutePath)
            prepare()
        }
    }

    private fun createFileForRecord() {
        file = File(MAIN_ACTIVITY.filesDir, id)
        file.createNewFile()
    }

    fun stopRecording(onSuccess: (File, String) -> Unit) {
        try {
            mediaRecorder.stop()
            onSuccess(file, id)
        } catch (e: Exception) {
            showToast(e.message.toString())
            file.delete()
        }
    }

    fun releaseRecorder() {
        try {
            mediaRecorder.release()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }
}
