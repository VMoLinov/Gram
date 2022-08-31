package ru.molinov.gram.ui.fragments.mainlist

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import ru.molinov.gram.R
import ru.molinov.gram.database.*
import ru.molinov.gram.databinding.FragmentMainListBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.ui.fragments.base.BaseFragment
import ru.molinov.gram.utilites.AppValueEventListener
import ru.molinov.gram.utilites.MAIN_ACTIVITY

class MainListFragment : BaseFragment<FragmentMainListBinding>(FragmentMainListBinding::inflate) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainListAdapter
    private val refMainList = REFERENCE_DB.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val refUsers = REFERENCE_DB.child(NODE_USERS)
    private val refMessages = REFERENCE_DB.child(NODE_MESSAGES).child(CURRENT_UID)
    private var listItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        MAIN_ACTIVITY.title = getString(R.string.chats_title)
        MAIN_ACTIVITY.appDrawer.unlockDrawer()
        initFields()
    }

    private fun initFields() {
        setHasOptionsMenu(true)
        recyclerView = binding.recyclerView
        adapter = MainListAdapter()
        refMainList.addListenerForSingleValueEvent(AppValueEventListener {
            listItems = it.children.map { map -> map.getCommonModel() }
            listItems.forEach { model ->
                refUsers.child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { nModel ->
                        val newModel = nModel.getCommonModel()
                        refMessages.child(newModel.id).limitToLast(1)
                            .addListenerForSingleValueEvent(AppValueEventListener { snapshot ->
                                val tempList = snapshot.children.last().getCommonModel()
                                newModel.lastMessage = tempList.text
                                newModel.fullName.ifEmpty { newModel.phone }
                                adapter.updateList(newModel)
                            })
                    })
            }
        })
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.single_chat_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {

            }
            R.id.delete -> {

            }
        }
        return true
    }
}
