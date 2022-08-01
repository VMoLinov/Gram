package ru.molinov.gram.ui.fragments

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.isVisible
import ru.molinov.gram.R
import ru.molinov.gram.databinding.FragmentSingleChatBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.MAIN_ACTIVITY

class SingleChatFragment :
    OptionsFragment<FragmentSingleChatBinding>(FragmentSingleChatBinding::inflate) {

    private lateinit var toolbarInfo: ViewGroup

    override fun onResume() {
        super.onResume()
        toolbarInfo = MAIN_ACTIVITY.toolbar.findViewById(R.id.toolbarInfo)
        toolbarInfo.isVisible = true
    }

    override fun onPause() {
        super.onPause()
        toolbarInfo.isVisible = false
    }

    companion object {
        const val ARGS_KEY = "SINGLE_CHAT_ARGS_KEY"
        fun newInstance(contact: CommonModel): SingleChatFragment {
            val fragment = SingleChatFragment()
            val args = Bundle()
            args.putParcelable(ARGS_KEY, contact)
            fragment.arguments = args
            return fragment
        }
    }
}
