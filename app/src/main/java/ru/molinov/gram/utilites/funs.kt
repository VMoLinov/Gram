package ru.molinov.gram.utilites

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.molinov.gram.R

fun Fragment.showToast(text: String) {
    Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.replaceActivity(activity: AppCompatActivity) {
    val intent = Intent(this, activity::class.java)
    startActivity(intent)
    this.finish()
}

fun AppCompatActivity.addFragment(fragment: Fragment) {
    supportFragmentManager
        .beginTransaction()
        .add(R.id.dataContainer, fragment)
        .commit()
}

fun AppCompatActivity.replaceFragment(fragment: Fragment) {
    supportFragmentManager
        .beginTransaction()
        .replace(R.id.dataContainer, fragment)
        .addToBackStack(null)
        .commit()
}

fun Fragment.replaceFragment(fragment: Fragment) {
    parentFragmentManager
        .beginTransaction()
        .replace(R.id.dataContainer, fragment)
        .addToBackStack(null)
        .commit()
}