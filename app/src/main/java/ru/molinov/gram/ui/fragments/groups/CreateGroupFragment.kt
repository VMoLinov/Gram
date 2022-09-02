package ru.molinov.gram.ui.fragments.groups

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.molinov.gram.R
import ru.molinov.gram.database.*
import ru.molinov.gram.databinding.FragmentCreateGroupBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.ui.fragments.base.BaseOptionsFragment
import ru.molinov.gram.utilites.AppValueEventListener
import ru.molinov.gram.utilites.MAIN_ACTIVITY
import ru.molinov.gram.utilites.replaceFragment
import ru.molinov.gram.utilites.showToast

class CreateGroupFragment :
    BaseOptionsFragment<FragmentCreateGroupBinding>(FragmentCreateGroupBinding::inflate) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddContactsAdapter
    private lateinit var listContacts: List<CommonModel>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listContacts = arguments?.getParcelableArrayList<CommonModel>(ARGS)?.toList() ?: listOf()
    }

    override fun onResume() {
        super.onResume()
        MAIN_ACTIVITY.title = getString(R.string.create_group)
        initFields()
        binding.createGroupBtnDone.setOnClickListener { showToast("Click") }
    }

    override fun onDestroy() {
        super.onDestroy()
        AddContactsFragment.listContacts.clear()
    }

    private fun initFields() {
        recyclerView = binding.recyclerView
        adapter = AddContactsAdapter()
        recyclerView.adapter = adapter
        listContacts.forEach {
            adapter.updateList(it)
        }
        binding.createGroupInputName.requestFocus()
    }

    companion object {
        private const val ARGS = "Create Group Fragment ARGS"
        fun newInstance(list: List<CommonModel>): CreateGroupFragment {
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARGS, ArrayList(list))
            val fragment = CreateGroupFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
