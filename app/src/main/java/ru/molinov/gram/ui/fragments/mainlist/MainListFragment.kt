package ru.molinov.gram.ui.fragments.mainlist

import androidx.recyclerview.widget.RecyclerView
import ru.molinov.gram.R
import ru.molinov.gram.database.*
import ru.molinov.gram.databinding.FragmentMainListBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.ui.fragments.base.BaseFragment
import ru.molinov.gram.utilites.AppValueEventListener
import ru.molinov.gram.utilites.MAIN_ACTIVITY
import ru.molinov.gram.utilites.TYPE_CHAT
import ru.molinov.gram.utilites.TYPE_GROUP

class MainListFragment : BaseFragment<FragmentMainListBinding>(FragmentMainListBinding::inflate) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainListAdapter
    private val refMainList = REFERENCE_DB.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val refUsers = REFERENCE_DB.child(NODE_USERS)
    private val refMessages = REFERENCE_DB.child(NODE_MESSAGES).child(CURRENT_UID)
    private var listItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        MAIN_ACTIVITY.title = getString(R.string.main_title)
        MAIN_ACTIVITY.appDrawer.unlockDrawer()
        initFields()
    }

    private fun initFields() {
        recyclerView = binding.recyclerView
        adapter = MainListAdapter()
        refMainList.addListenerForSingleValueEvent(AppValueEventListener {
            listItems = it.children.map { map -> map.getCommonModel() }
            listItems.forEach { model ->
                when (model.type) {
                    TYPE_CHAT -> showChat(model)
                    TYPE_GROUP -> showGroup(model)
                }
            }
        })
        recyclerView.adapter = adapter
    }

    private fun showGroup(model: CommonModel) {
        REFERENCE_DB
            .child(NODE_GROUPS)
            .child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { nModel ->
                val newModel = nModel.getCommonModel()
                REFERENCE_DB
                    .child(NODE_GROUPS)
                    .child(model.id)
                    .child(NODE_MESSAGES)
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener { snapshot ->
                        newModel.lastMessage = if (snapshot.hasChildren()) {
                            snapshot.children.last().getCommonModel().text
                        } else getString(R.string.single_chat_cleared)
                        newModel.type = TYPE_GROUP
                        adapter.updateList(newModel)
                    })
            })
    }

    private fun showChat(model: CommonModel) {
        refUsers
            .child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { nModel ->
                val newModel = nModel.getCommonModel()
                refMessages
                    .child(newModel.id)
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener { snapshot ->
                        newModel.lastMessage = if (snapshot.hasChildren()) {
                            snapshot.children.last().getCommonModel().text
                        } else {
                            getString(R.string.single_chat_cleared)
                        }
                        newModel.fullName.ifEmpty { newModel.phone }
                        newModel.type = TYPE_CHAT
                        adapter.updateList(newModel)
                    })
            })
    }
}
