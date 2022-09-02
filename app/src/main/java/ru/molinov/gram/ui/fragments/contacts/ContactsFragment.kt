package ru.molinov.gram.ui.fragments.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import ru.molinov.gram.R
import ru.molinov.gram.database.*
import ru.molinov.gram.databinding.FragmentContactsBinding
import ru.molinov.gram.databinding.ItemContactBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.ui.fragments.base.BaseOptionsFragment
import ru.molinov.gram.ui.fragments.singlechat.SingleChatFragment
import ru.molinov.gram.utilites.AppValueEventListener
import ru.molinov.gram.utilites.MAIN_ACTIVITY
import ru.molinov.gram.utilites.downloadAndSetImage
import ru.molinov.gram.utilites.replaceFragment

class ContactsFragment :
    BaseOptionsFragment<FragmentContactsBinding>(FragmentContactsBinding::inflate) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    private lateinit var refContacts: DatabaseReference
    private lateinit var refUser: DatabaseReference
    private var listenersMap = hashMapOf<DatabaseReference, AppValueEventListener>()

    override fun onResume() {
        super.onResume()
        MAIN_ACTIVITY.title = getString(R.string.contacts_title)
        initRecycleView()
    }

    private fun initRecycleView() {
        recyclerView = binding.contactsRecycleView
        refContacts = REFERENCE_DB.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(refContacts, CommonModel::class.java)
            .build()
        adapter = object : FirebaseRecyclerAdapter<CommonModel, ContactsHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                return ContactsHolder(
                    ItemContactBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            override fun onBindViewHolder(
                holder: ContactsHolder, position: Int, model: CommonModel
            ) {
                refUser = REFERENCE_DB.child(NODE_USERS).child(model.id)
                val listener = getAppValueEventListener(holder, model)
                refUser.addValueEventListener(listener)
                listenersMap[refUser] = listener
            }
        }
        recyclerView.adapter = adapter
        adapter.startListening()
    }

    private fun getAppValueEventListener(holder: ContactsHolder, model: CommonModel) =
        AppValueEventListener {
            val contact = it.getCommonModel()
            with(holder.binding) {
                if (contact.fullName.isEmpty()) contactFullName.text = model.fullName
                else contactFullName.text = contact.fullName
                contactStatus.text = contact.status
                contactPhoto
                    .downloadAndSetImage(contact.photoUrl, R.drawable.ic_default_user)
                holder.itemView.setOnClickListener {
                    replaceFragment(SingleChatFragment.newInstance(model))
                }
            }
        }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
        listenersMap.forEach { it.key.removeEventListener(it.value) }
    }

    class ContactsHolder(val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root)
}
