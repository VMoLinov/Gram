package ru.molinov.gram.ui.fragments.chats.group

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import ru.molinov.gram.R
import ru.molinov.gram.database.createGroup
import ru.molinov.gram.databinding.FragmentCreateGroupBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.ui.fragments.base.BaseOptionsFragment
import ru.molinov.gram.ui.fragments.chats.group.adapters.AddGroupAdapter
import ru.molinov.gram.ui.fragments.mainlist.MainListFragment
import ru.molinov.gram.utilites.MAIN_ACTIVITY
import ru.molinov.gram.utilites.getParticipants
import ru.molinov.gram.utilites.replaceFragment
import ru.molinov.gram.utilites.showToast

class CreateGroupFragment :
    BaseOptionsFragment<FragmentCreateGroupBinding>(FragmentCreateGroupBinding::inflate) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddGroupAdapter
    private lateinit var listContacts: List<CommonModel>
    private var imageUri = Uri.EMPTY
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            binding.createGroupPhoto.setImageURI(imageUri)
        } else showToast(result.error.toString())
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listContacts = arguments?.getParcelableArrayList<CommonModel>(ARGS)?.toList() ?: listOf()
    }

    override fun onResume() {
        super.onResume()
        MAIN_ACTIVITY.title = getString(R.string.create_group)
        initFields()
        binding.createGroupBtnDone.setOnClickListener {
            val nameGroup = binding.createGroupInputName.text.toString()
            if (nameGroup.isEmpty()) {
                showToast(getString(R.string.create_group_enter_name))
            } else {
                createGroup(nameGroup, imageUri, listContacts) {
                    showToast(getString(R.string.create_group_group_created))
                    replaceFragment(MainListFragment())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AddContactsFragment.listContacts.clear()
    }

    private fun initFields() {
        recyclerView = binding.recyclerView
        adapter = AddGroupAdapter()
        recyclerView.adapter = adapter
        listContacts.forEach { adapter.updateList(it) }
        with(binding) {
            createGroupInputName.requestFocus()
            createGroupCounts.text = getParticipants(listContacts.size)
            createGroupPhoto.setOnClickListener { startCrop() }
        }
    }

    private fun startCrop() {
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.OFF)
                setRequestedSize(250, 250)
                setAspectRatio(1, 1)
                setCropShape(CropImageView.CropShape.OVAL)
                setBorderCornerLength(0f)
                setBorderCornerThickness(0f)
            }
        )
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
