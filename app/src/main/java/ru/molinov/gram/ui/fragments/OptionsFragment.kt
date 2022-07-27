package ru.molinov.gram.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import ru.molinov.gram.utilites.MAIN_ACTIVITY

open class OptionsFragment<T : ViewBinding>(
    bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> T
) : BaseFragment<T>(bindingFactory) {

    override fun onStart() {
        super.onStart()
        MAIN_ACTIVITY.appDrawer.lockDrawer()
    }

    override fun onStop() {
        super.onStop()
        MAIN_ACTIVITY.appDrawer.unlockDrawer()
    }

    /** update user's data on navigation drawer header */
    fun updateDrawerHeader() {
        MAIN_ACTIVITY.appDrawer.updateHeader()
    }
}
