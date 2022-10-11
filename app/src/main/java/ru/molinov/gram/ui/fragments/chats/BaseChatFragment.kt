package ru.molinov.gram.ui.fragments.chats

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.molinov.gram.R
import ru.molinov.gram.database.*
import ru.molinov.gram.databinding.FragmentChatBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.models.UserModel
import ru.molinov.gram.ui.fragments.base.BaseOptionsFragment
import ru.molinov.gram.ui.fragments.mainlist.MainListFragment
import ru.molinov.gram.utilites.*

abstract class BaseChatFragment :
    BaseOptionsFragment<FragmentChatBinding>(FragmentChatBinding::inflate) {

    private lateinit var viewModel: BaseChatViewModel
    private lateinit var toolbar: ViewGroup
    private lateinit var listenerToolbar: AppValueEventListener
    private lateinit var listenerRecycler: ChildEventListener
    private lateinit var receivingUser: UserModel
    private lateinit var refUser: DatabaseReference
    private lateinit var voiceRecorder: AppVoiceRecorder
    private lateinit var adapter: BaseChatAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    internal lateinit var refMessages: DatabaseReference
    internal lateinit var commonModel: CommonModel
    internal var isSmoothScroll = true
    private var isScrolling = false
    private var messagesCount = 10
    private val loadAttach = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            uploadFile(result.uriContent, commonModel.id, TYPE_MESSAGE_IMAGE)
            isSmoothScroll = true
        } else showToast(result.error.toString())
    }
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uploadFile(it, commonModel.id, TYPE_MESSAGE_FILE) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[BaseChatViewModel::class.java]
        viewModel.liveData.observe(viewLifecycleOwner) {

        }
        toolbar = MAIN_ACTIVITY.toolbar.findViewById(R.id.toolbarInfo)
        refUser = REFERENCE_DB.root.child(NODE_USERS).child(commonModel.id)
    }

    override fun onResume() {
        super.onResume()
        initToolbar()
        initRecyclerView()
        initFields()
    }

    override fun onPause() {
        super.onPause()
        toolbar.isVisible = false
        refUser.removeEventListener(listenerToolbar)
        refMessages.removeEventListener(listenerRecycler)
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceRecorder.releaseRecorder()
        adapter.onDestroy()
    }

    private fun initToolbar() {
        toolbar.isVisible = true
        listenerToolbar = AppValueEventListener {
            receivingUser = it.getUserModel()
            initToolbarInfo()
        }
        refUser.addValueEventListener(listenerToolbar)
    }

    private fun initToolbarInfo() {
        toolbar.apply {
            findViewById<TextView>(R.id.toolbarFullName).text =
                receivingUser.fullName.ifEmpty { commonModel.fullName }
            findViewById<CircleImageView>(R.id.toolbarImage).downloadAndSetImage(
                receivingUser.photoUrl,
                R.drawable.ic_default_user
            )
            findViewById<TextView>(R.id.toolbarUserStatus).text = receivingUser.status
        }
    }

    private fun initRecyclerView() = with(binding) {
        adapter = BaseChatAdapter()
        val layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.adapter = adapter
        chatRecyclerView.layoutManager = layoutManager
        chatRecyclerView.setHasFixedSize(true)
        chatRecyclerView.isNestedScrollingEnabled = false
        listenerRecycler = AppChildEventListener {
            adapter.addItem(it.getCommonModel()) { swipeRefresh.isRefreshing = false }
            if (isSmoothScroll) chatRecyclerView.smoothScrollToPosition(adapter.itemCount)
        }
        refMessages.limitToLast(messagesCount).addChildEventListener(listenerRecycler)
        chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isScrolling && dy < 0 && layoutManager.findFirstVisibleItemPosition() <= ITEMS_PRE_LOAD) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        })
        swipeRefresh.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        isSmoothScroll = false
        isScrolling = false
        messagesCount += 10
        refMessages.removeEventListener(listenerRecycler)
        refMessages.limitToLast(messagesCount).addChildEventListener(listenerRecycler)
    }

    @Suppress("DEPRECATION")
    private fun initFields() {
        setHasOptionsMenu(true)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.root)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        voiceRecorder = AppVoiceRecorder()
        binding.btnAttach.setOnClickListener { attach() }
        setSent()
        setMessage()
        setVoice()
    }

    private fun attach() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.bottomSheet.apply {
            btnAttachImage.setOnClickListener { attachImage() }
            btnAttachFile.setOnClickListener { getContent.launch("*/*") }
        }
    }

    private fun attachImage() {
        loadAttach.launch(options {
            setGuidelines(CropImageView.Guidelines.OFF)
            setRequestedSize(250, 250)
            setAspectRatio(1, 1)
            setBorderCornerLength(0f)
            setBorderCornerThickness(0f)
        })
    }

    abstract fun setSent()

    private fun setMessage() = with(binding) {
        message.addTextChangedListener(AppTextWatcher {
            if (it.isEmpty()) {
                btnSent.isVisible = false
                btnAttach.isVisible = true
                btnVoice.isVisible = true
                changeMessageViewConstraints(btnAttach.id)
            } else {
                btnSent.isVisible = true
                btnAttach.isVisible = false
                btnVoice.isVisible = false
                changeMessageViewConstraints(btnSent.id)
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setVoice() = with(binding) {
        CoroutineScope(Dispatchers.IO).launch {
            btnVoice.setOnTouchListener { _, motionEvent ->
                if (checkPermission(RECORD_AUDIO)) {
                    if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                        recording.isVisible = true
                        btnVoice.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                        )
                        voiceRecorder.startRecording(commonModel.id)
                    } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                        recording.isVisible = false
                        btnVoice.colorFilter = null
                        voiceRecorder.stopRecording { file, contactId ->
                            uploadFile(Uri.fromFile(file), contactId, TYPE_MESSAGE_VOICE)
                            isSmoothScroll = true
                        }
                    }
                }
                true
            }
        }
    }

    private fun changeMessageViewConstraints(targetId: Int) {
        ConstraintSet().apply {
            clone(binding.constraint)
            connect(binding.message.id, ConstraintSet.END, targetId, ConstraintSet.START)
            applyTo(binding.constraint)
        }
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "requireActivity().menuInflater.inflate(R.menu.single_chat_action_menu, menu)",
            "ru.molinov.gram.R"
        )
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.single_chat_action_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> clearChat(commonModel.id) {
                showToast(getString(R.string.single_chat_cleared))
                replaceFragment(MainListFragment())
            }
            R.id.delete -> deleteChat(commonModel.id) {
                showToast(getString(R.string.single_chat_deleted))
                replaceFragment(MainListFragment())
            }
        }
        return true
    }

    companion object {
        private const val ITEMS_PRE_LOAD = 3
    }
}
