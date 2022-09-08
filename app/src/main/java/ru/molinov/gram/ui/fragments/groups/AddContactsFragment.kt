package ru.molinov.gram.ui.fragments.groups

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import ru.molinov.gram.R
import ru.molinov.gram.database.*
import ru.molinov.gram.databinding.FragmentAddContactsBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.ui.fragments.base.BaseOptionsFragment
import ru.molinov.gram.ui.fragments.groups.adapters.AddContactsAdapter
import ru.molinov.gram.utilites.AppValueEventListener
import ru.molinov.gram.utilites.MAIN_ACTIVITY
import ru.molinov.gram.utilites.replaceFragment
import ru.molinov.gram.utilites.showToast

class AddContactsFragment :
    BaseOptionsFragment<FragmentAddContactsBinding>(FragmentAddContactsBinding::inflate) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddContactsAdapter
    private val refContactsList = REFERENCE_DB.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
    private val refUsers = REFERENCE_DB.child(NODE_USERS)
    private val refMessages = REFERENCE_DB.child(NODE_MESSAGES).child(CURRENT_UID)
    private var listItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        MAIN_ACTIVITY.title = getString(R.string.add_contacts_add_members)
        initFields()
        binding.addContactsButton.setOnClickListener {
            if (listContacts.isEmpty()) showToast(getString(R.string.add_contacts_add_members))
            else replaceFragment(CreateGroupFragment.newInstance(listContacts))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listContacts.clear()
    }

    private fun initFields() {
        recyclerView = binding.recyclerView
        adapter = AddContactsAdapter()
        refContactsList.addListenerForSingleValueEvent(AppValueEventListener {
            listItems = it.children.map { map -> map.getCommonModel() }
            listItems.forEach { model -> refUsers.child(model.id).getModel() }
        })
        recyclerView.adapter = adapter
    }

    private fun DatabaseReference.getModel() {
        addListenerForSingleValueEvent(AppValueEventListener { model ->
            val newModel = model.getCommonModel()
            refMessages
                .child(newModel.id)
                .limitToLast(1)
                .addListenerForSingleValueEvent(AppValueEventListener { snapshot ->
                    setLastMessage(newModel, snapshot)
                })
        })
    }

    private fun setLastMessage(newModel: CommonModel, snapshot: DataSnapshot) {
        newModel.lastMessage = if (snapshot.hasChildren()) {
            snapshot.children.last().getCommonModel().text
        } else getString(R.string.single_chat_cleared)
        newModel.fullName.ifEmpty { newModel.phone }
        adapter.updateList(newModel)
    }

    companion object {
        val listContacts = mutableListOf<CommonModel>()
    }
}
