package ru.molinov.gram.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import ru.molinov.gram.MainActivity

open class OptionsFragment<T : ViewBinding>(
    bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> T
) : BaseFragment<T>(bindingFactory) {

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).appDrawer.lockDrawer()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).appDrawer.unlockDrawer()
    }
}
