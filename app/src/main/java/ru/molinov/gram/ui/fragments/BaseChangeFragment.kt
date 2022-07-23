package ru.molinov.gram.ui.fragments

import android.view.*
import androidx.viewbinding.ViewBinding
import ru.molinov.gram.MainActivity
import ru.molinov.gram.R

open class BaseChangeFragment<T : ViewBinding>(bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> T) :
    OptionsFragment<T>(bindingFactory) {

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as MainActivity).menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.changeNameConfirm) changeCheck()
        return true
    }

    open fun changeCheck() {}
}
