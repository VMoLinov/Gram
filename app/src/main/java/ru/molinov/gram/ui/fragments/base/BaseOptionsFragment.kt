package ru.molinov.gram.ui.fragments.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import ru.molinov.gram.utilites.MAIN_ACTIVITY

/** Fragments with locked drawer menu */
open class BaseOptionsFragment<T : ViewBinding>(
    bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> T
) : BaseFragment<T>(bindingFactory) {

    override fun onStart() {
        super.onStart()
        MAIN_ACTIVITY.appDrawer.lockDrawer()
    }

    /** update user's data on navigation drawer header */
    fun updateDrawerHeader() = MAIN_ACTIVITY.appDrawer.updateHeader()
}
