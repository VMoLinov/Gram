package ru.molinov.gram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

open class BaseFragment<T : ViewBinding>(
    private val bindingFactory: (inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean) -> T
) : Fragment() {

    private var _binding: T? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = bindingFactory(inflater, container, false).also { _binding = it }.root

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
