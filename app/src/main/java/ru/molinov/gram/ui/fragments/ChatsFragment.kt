package ru.molinov.gram.ui.fragments

import ru.molinov.gram.R
import ru.molinov.gram.databinding.FragmentChatsBinding
import ru.molinov.gram.utilites.MAIN_ACTIVITY

class ChatsFragment : BaseFragment<FragmentChatsBinding>(FragmentChatsBinding::inflate) {

    override fun onResume() {
        super.onResume()
        MAIN_ACTIVITY.title = getString(R.string.chats_title)
    }
}
