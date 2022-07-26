package ru.molinov.gram.ui.fragments

import android.view.*
import androidx.viewbinding.ViewBinding
import ru.molinov.gram.R
import ru.molinov.gram.utilites.MAIN_ACTIVITY
import ru.molinov.gram.utilites.hideKeyboard

open class BaseChangeFragment<T : ViewBinding>(bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> T) :
    OptionsFragment<T>(bindingFactory) {

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        MAIN_ACTIVITY.menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.changeNameConfirm) changeCheck()
        return true
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    open fun changeCheck() {}
}
