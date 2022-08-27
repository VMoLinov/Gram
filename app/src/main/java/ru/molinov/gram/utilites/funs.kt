package ru.molinov.gram.utilites

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.molinov.gram.MainActivity
import ru.molinov.gram.R
import ru.molinov.gram.database.AUTH
import ru.molinov.gram.database.setPhones
import ru.molinov.gram.models.CommonModel
import java.text.SimpleDateFormat
import java.util.*

fun showToast(text: String) {
    Toast.makeText(MAIN_ACTIVITY, text, Toast.LENGTH_SHORT).show()
}

fun restartActivity() {
    val intent = Intent(MAIN_ACTIVITY, MainActivity::class.java)
    MAIN_ACTIVITY.startActivity(intent)
    MAIN_ACTIVITY.finish()
}

fun addFragment(fragment: Fragment) {
    MAIN_ACTIVITY.supportFragmentManager
        .beginTransaction()
        .add(R.id.dataContainer, fragment)
        .commit()
}

fun replaceFragment(fragment: Fragment) {
    MAIN_ACTIVITY.supportFragmentManager
        .beginTransaction()
        .replace(R.id.dataContainer, fragment)
        .addToBackStack(null)
        .commit()
}

fun ImageView.downloadAndSetImage(url: String) {
    Glide.with(this)
        .load(url)
        .transform(CenterInside(), RoundedCorners(20))
        .into(this)
}

fun ImageView.downloadAndSetImage(url: String, placeholder: Int) {
    Glide.with(this)
        .load(url)
        .placeholder(placeholder)
        .into(this)
}

fun hideKeyboard() {
    val imm: InputMethodManager =
        MAIN_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(MAIN_ACTIVITY.window.decorView.windowToken, 0)
}

fun initContacts() {
    if (checkPermission(READ_CONTACTS)) {
        val arrayContacts = arrayListOf<CommonModel>()
        val cursor = MAIN_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            while (it.moveToNext()) {
                val fullNameColumn = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val phoneColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val newModel = CommonModel()
                newModel.fullName = it.getString(fullNameColumn)
                newModel.phone = it.getString(phoneColumn).replace(Regex("[\\s,-]"), "")
                arrayContacts.add(newModel)
            }
        }
        cursor?.close()
        if (AUTH.currentUser != null) setPhones(arrayContacts)
    }
}

fun Long.asTime(): String {
    val time = Date(this)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}

fun getFileNameFromUrl(uri: Uri?): String {
    uri?.let {
        var result = ""
        val cursor = MAIN_ACTIVITY.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                result = cursor.getString(index)
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        } finally {
            cursor?.close()
        }
        return result
    }
    return ""
}
