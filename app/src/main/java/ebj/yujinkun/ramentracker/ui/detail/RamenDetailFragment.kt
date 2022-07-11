package ebj.yujinkun.ramentracker.ui.detail

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.View.OnFocusChangeListener
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import ebj.yujinkun.ramentracker.R
import ebj.yujinkun.ramentracker.data.models.Ramen
import ebj.yujinkun.ramentracker.databinding.FragmentRamenDetailBinding
import ebj.yujinkun.ramentracker.util.DateUtils
import ebj.yujinkun.ramentracker.util.Resource
import ebj.yujinkun.ramentracker.util.SoftKeyboardUtils
import timber.log.Timber
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class RamenDetailFragment : Fragment() {
    private lateinit var binding: FragmentRamenDetailBinding
    private val viewModel: RamenDetailViewModel by viewModels()
    private var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRamenDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        parseArguments(arguments)
        initViews()
        viewModel.getSaveRamenLiveData().observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Success -> handleSaveSuccess(resource.data)
                is Resource.Error -> handleSaveError(resource.error)
                is Resource.Loading -> handleSaveLoading()
            }

        })
        viewModel.getDeleteRamenLiveData().observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Success -> handleDeleteSuccess(resource.data)
                is Resource.Error -> handleDeleteError(resource.error)
                is Resource.Loading -> handleDeleteLoading()
            }
        })
        viewModel.dataUpdatedLiveData.observe(viewLifecycleOwner, { hasUnsavedChanges: Boolean ->
            if (hasUnsavedChanges) {
                showSaveChangesButton()
            } else {
                hideSaveChangesButton()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()
    }

    override fun onDestroyView() {
        SoftKeyboardUtils.hideSoftKeyboard(requireActivity())
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar_ramen_detail, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_favorite)
            .setIcon(if (viewModel.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
        menu.findItem(R.id.action_delete).isVisible = viewModel.isDeletable
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (id == R.id.action_favorite) {
            handleFavoriteAction()
            return true
        }
        if (id == R.id.action_delete) {
            handleDeleteAction()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SELECT_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val uri = data.data
                    Timber.i("Received uri: %s", uri)
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver,
                            uri
                        )
                        viewModel.updateSelectedPhoto(bitmap)
                        updateRamenPhoto(bitmap)
                    } catch (e: IOException) {
                        Timber.e(e, "Get bitmap error")
                    }
                } else {
                    Timber.e("data is null")
                }
            }
        } else if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val extras = data!!.extras
                val bitmap = extras!!["data"] as Bitmap?
                viewModel.updateSelectedPhoto(bitmap)
                updateRamenPhoto(bitmap)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleSelectImage()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please allow storage permission to proceed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun parseArguments(args: Bundle?) {
        val ramen = args?.let {
            RamenDetailFragmentArgs.fromBundle(args).ramen
        }
        viewModel.initValues(ramen)
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        val actionBar = activity.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setTitle("")

    }

    private fun initViews() {
        initShopEditText()
        initLocationEditText()
        initRamenNameEditText()
        initCommentsEditText()
        initDateField()
        initRamenImage()
        binding.fab.setOnClickListener { view: View? -> viewModel.saveRamen() }
    }

    private fun initShopEditText() {
        val editText = binding.shop.editText
        editText!!.setText(viewModel.shop)
        binding.shop.isHintAnimationEnabled = true
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewModel.shop = s.toString()
            }
        })
    }

    private fun initLocationEditText() {
        val editText = binding.location.editText
        editText!!.setText(viewModel.location)
        binding.location.isHintAnimationEnabled = true
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewModel.location = s.toString()
            }
        })
    }

    private fun initRamenNameEditText() {
        val editText = binding.ramenName.editText
        editText!!.setText(viewModel.ramenName)
        binding.ramenName.isHintAnimationEnabled = true
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewModel.ramenName = s.toString()
            }
        })
    }

    private fun initCommentsEditText() {
        val editText = binding.comments.editText
        editText!!.setText(viewModel.comments)
        binding.comments.isHintAnimationEnabled = true
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewModel.comments = s.toString()
            }
        })
    }

    private fun initDateField() {
        binding.date.editText!!.setText(
            DateUtils.formatDate(
                viewModel.date,
                DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_PRETTY
            )
        )
        binding.date.editText!!.inputType = InputType.TYPE_NULL
        binding.date.editText!!.onFocusChangeListener =
            OnFocusChangeListener { v: View?, hasFocus: Boolean ->
                if (hasFocus) {
                    handleDateClicked()
                }
            }
        binding.date.editText!!.setOnClickListener { v: View? -> handleDateClicked() }
    }

    private fun handleDateClicked() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = DateUtils.getDate(viewModel.date)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                handleDatePicked(
                    year,
                    month,
                    dayOfMonth
                )
            },
            calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.show()
    }

    private fun handleDatePicked(year: Int, month: Int, dayOfMonth: Int) {
        viewModel.date = DateUtils.formatDate(year, month, dayOfMonth)
        binding.date.editText!!.setText(
            DateUtils.formatDate(
                viewModel.date,
                DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_PRETTY
            )
        )
    }

    private fun handleFavoriteAction() {
        viewModel.isFavorite = !viewModel.isFavorite
        requireActivity().invalidateOptionsMenu()
    }

    private fun handleDeleteAction() {
        viewModel.deleteRamen()
    }

    private fun handleDeleteLoading() {
        Timber.i("Deleting")
    }

    private fun handleDeleteSuccess(ramen: Ramen) {
        Timber.i("Delete success: %s", ramen)
        NavHostFragment.findNavController(this).navigateUp()
    }

    private fun handleDeleteError(error: Throwable) {
        Timber.e(error, "Delete error")
        Toast.makeText(requireContext(), "Delete error", Toast.LENGTH_SHORT).show()
    }

    private fun handleSaveLoading() {
        Timber.i("Saving...")
        showProgressDialog("Saving...", false)
    }

    private fun handleSaveSuccess(ramen: Ramen) {
        Timber.i("Save success: %s", ramen)
        hideProgressDialog()
        NavHostFragment.findNavController(this).navigateUp()
    }

    private fun handleSaveError(error: Throwable) {
        Timber.e(error, "Save error")
        hideProgressDialog()
        Toast.makeText(requireContext(), "Save error", Toast.LENGTH_SHORT).show()
    }

    private fun onBackPressed() {
        val contentsUpdated = viewModel.dataUpdatedLiveData.value ?: false
        if (contentsUpdated) {
            showConfirmationDialog()
        } else {
            navigateUp()
        }
    }

    private fun showConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(activity)
            .setMessage("Are you sure you want to discard your changes?")
            .setPositiveButton("Yes") { dialog: DialogInterface?, which: Int -> navigateUp() }
            .setNegativeButton("No") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .create()
        alertDialog.show()
    }

    private fun showUpdatePhotoDialog() {
        val alertDialog = AlertDialog.Builder(requireActivity())
            .setTitle(R.string.update_image)
            .setItems(R.array.update_photo) { dialog: DialogInterface?, which: Int ->
                when (which) {
                    0 -> handleTakePhoto()
                    1 -> handleSelectImage()
                    else -> Timber.e("Unknown item selected")
                }
            }
            .setNegativeButton(R.string.cancel) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .create()
        alertDialog.show()
    }

    private fun handleTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE)
    }

    private fun handleSelectImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Image"),
                SELECT_IMAGE_REQUEST_CODE
            )
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun initRamenImage() {
        binding!!.ramenImagePlaceholder.setOnClickListener { view: View? -> showUpdatePhotoDialog() }
        binding!!.ramenImage.setOnClickListener { view: View? -> showUpdatePhotoDialog() }
        val photoUri = viewModel!!.photoUri
        if (!TextUtils.isEmpty(photoUri)) {
            Glide.with(requireActivity())
                .load(photoUri)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Timber.e(e, "Error loading image")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding!!.ramenImagePlaceholder.visibility = View.GONE
                        binding!!.addPhotoIcon.visibility = View.GONE
                        binding!!.ramenImage.visibility = View.VISIBLE
                        return false
                    }
                })
                .into(binding!!.ramenImage)
        }
    }

    private fun updateRamenPhoto(bitmap: Bitmap?) {
        Timber.i("Update ramen photo")
        binding.ramenImagePlaceholder.visibility = View.GONE
        binding.addPhotoIcon.visibility = View.GONE
        binding.ramenImage.setImageBitmap(bitmap)
        binding.ramenImage.visibility = View.VISIBLE
    }

    private fun showSaveChangesButton() {
        binding.fab.visibility = View.VISIBLE
    }

    private fun hideSaveChangesButton() {
        binding.fab.visibility = View.GONE
    }

    private fun showProgressDialog(message: String, cancelable: Boolean) {
        if (dialog == null) {
            dialog = ProgressDialog(requireActivity())
        }
        dialog!!.setCancelable(cancelable)
        dialog!!.setMessage(message)
        dialog!!.show()
    }

    private fun hideProgressDialog() {
        dialog!!.dismiss()
    }

    private fun navigateUp() {
        NavHostFragment.findNavController(this).navigateUp()
    }

    companion object {
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1
        private const val SELECT_IMAGE_REQUEST_CODE = 100
        private const val TAKE_PHOTO_REQUEST_CODE = 200
    }
}