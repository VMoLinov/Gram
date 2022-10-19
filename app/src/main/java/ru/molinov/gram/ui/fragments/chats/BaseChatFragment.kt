package ru.molinov.gram.ui.fragments.chats

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.molinov.gram.R
import ru.molinov.gram.databinding.FragmentChatBinding
import ru.molinov.gram.models.CommonModel
import ru.molinov.gram.models.UserModel
import ru.molinov.gram.ui.fragments.base.BaseOptionsFragment
import ru.molinov.gram.utilites.*

abstract class BaseChatFragment :
    BaseOptionsFragment<FragmentChatBinding>(FragmentChatBinding::inflate) {

    private lateinit var toolbar: ViewGroup
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    internal lateinit var refMessages: DatabaseReference
    internal lateinit var viewModel: BaseChatViewModel
    internal lateinit var commonModel: CommonModel
    internal var isSmoothScroll = true
    private var isScrolling = false
    private val loadAttach = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            viewModel.sendFile(result.uriContent, TYPE_MESSAGE_IMAGE)
            isSmoothScroll = true
        } else showToast(result.error.toString())
    }
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { viewModel.sendFile(it, TYPE_MESSAGE_FILE) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this, ChatsViewModelFactory(MAIN_ACTIVITY.application, commonModel.id, refMessages)
        )[BaseChatViewModel::class.java]
        toolbar = MAIN_ACTIVITY.toolbar.findViewById(R.id.toolbarInfo)
    }

    override fun onResume() {
        super.onResume()
        initToolbar()
        initRecyclerView()
        initFields()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
        toolbar.isVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun initToolbar() {
        toolbar.isVisible = true
        viewModel.initToolbar { initToolbarInfo(it) }
    }

    private fun initToolbarInfo(receivingUser: UserModel) {
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
        chatRecyclerView.adapter = viewModel.adapter
        val layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.layoutManager = layoutManager
        chatRecyclerView.setHasFixedSize(true)
        chatRecyclerView.isNestedScrollingEnabled = false
        viewModel.initRecyclerView {
            swipeRefresh.isRefreshing = false
            if (isSmoothScroll) chatRecyclerView.smoothScrollToPosition(it)
        }
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
        viewModel.updateData()
    }

    private fun initFields() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(viewModel.actionMenuProvider)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.root)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
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
                        viewModel.startRecording()
                    } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                        recording.isVisible = false
                        btnVoice.colorFilter = null
                        viewModel.stopRecording { isSmoothScroll = true }
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

    companion object {
        private const val ITEMS_PRE_LOAD = 3
    }
}
