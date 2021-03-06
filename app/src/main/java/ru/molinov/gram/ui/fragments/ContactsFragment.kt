package ru.molinov.gram.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import ru.molinov.gram.R
import ru.molinov.gram.databinding.ContactItemBinding
import ru.molinov.gram.databinding.FragmentContactsBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.utilites.*

class ContactsFragment :
    OptionsFragment<FragmentContactsBinding>(FragmentContactsBinding::inflate) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    private lateinit var refContacts: DatabaseReference
    private lateinit var refUser: DatabaseReference

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
                    ContactItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            override fun onBindViewHolder(
                holder: ContactsHolder, position: Int, model: CommonModel
            ) {
                refUser = REFERENCE_DB.child(NODE_USERS).child(model.id)
                refUser.addValueEventListener(AppValueEventListener {
                    val contact = it.getCommonModel()
                    with(holder.binding) {
                        contactFullName.text = contact.fullName
                        contactStatus.text = contact.status
                        contactPhoto
                            .downloadAndSetImage(contact.photoUrl, R.drawable.ic_default_user)
                    }
                })
            }
        }
        recyclerView.adapter = adapter
        adapter.startListening()
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }

    class ContactsHolder(val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root)
}
