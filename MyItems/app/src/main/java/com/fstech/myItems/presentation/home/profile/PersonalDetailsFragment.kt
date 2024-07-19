package store.msolapps.flamingo.presentation.home.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.msolapps.oscar.GeneralClasses.FilesAssistant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentPersonalDetailsBinding
import store.msolapps.flamingo.presentation.auth.STORAGE_PERMISSION_CODE
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.Calendar
import java.util.Date

class PersonalDetailsFragment : BaseFragment() {
    private var _binding: FragmentPersonalDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val PICK_FROM_GALLERY = 1
    private lateinit var filesAssistant: FilesAssistant
    var bitmap: Bitmap? = null
    private lateinit var file: File
    private var selectPhotoUri: Uri? = null
    private val viewModel: PersonalDetailsViewModel by activityViewModels()

    // Registers a photo picker activity launcher in single-select mode.
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            selectPhotoUri = uri
            renderPhotoToScreen(selectPhotoUri!!)
        } else {
//                        Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPersonalDetailsBinding.inflate(inflater, container, false)

        val isRtl = resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
        binding.userNameEdt.textDirection =
            if (isRtl) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR

        binding.userEmailEdt.textDirection =
            if (isRtl) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR

        binding.phoneNumberET.textDirection =
            if (isRtl) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR

        setupListeners()
        viewModel.getUserData()
        setupObservers()

        return binding.root
    }

    fun setupObservers() {
        viewModel.updateProfile.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                Toast.makeText(requireContext(), "Updated Successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }
        viewModel.isLoadData.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.isLoad.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.isLoadDeleteImage.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.profileData.observe(viewLifecycleOwner) {
            binding.userNameEdt.text = Editable.Factory.getInstance().newEditable(it.data.name)
            binding.userEmailEdt.text = Editable.Factory.getInstance().newEditable(it.data.email)
            binding.phoneNumberET.text = Editable.Factory.getInstance().newEditable(it.data.phone)
            if (it.data.birth_date != null) {
                binding.userDateEdt.text =
                    Editable.Factory.getInstance().newEditable(it.data.birth_date.toString())
            }
            Glide.with(this)
                .load(it.data.picture)
                .placeholder(R.drawable.photo_placeholder)
                .into(binding.profileImage)
        }
    }

    @SuppressLint("SetTextI18n")
    fun setupListeners() {
        binding.deleteIcon.setOnClickListener {
            viewModel.deleteImage()
            binding.profileImage.setImageDrawable(resources.getDrawable(R.drawable.photo_placeholder))
        }
        binding.userDateEdt.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    if (month + 1 < 10 && day < 10) {
                        binding.userDateEdt.setText("0$day/0${month + 1}/$year")
                    } else if (month + 1 < 10 && day >= 10) {
                        binding.userDateEdt.setText("$day/0${month + 1}/$year")
                    } else if (month + 1 >= 10 && day < 10) {
                        binding.userDateEdt.setText("0$day/${month + 1}/$year")
                    } else {
                        binding.userDateEdt.setText("$day/${month + 1}/$year")
                    }
                },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = Date().time
            datePickerDialog.show()
        }
        binding.profileImage.setOnClickListener {
            openPickupPhotoRoutine()
        }

        binding.saveBtn.setOnClickListener {
            val name = binding.userNameEdt.text
            val email = binding.userEmailEdt.text
            val dateOfBirth = binding.userDateEdt.text
            if (name!!.isNotEmpty() || email!!.isNotEmpty()) {
                val imagePart = if (bitmap != null) {
                    bitmapToMultipart(bitmap!!)
//                    convertImage(selectPhotoUri)
                } else if (selectPhotoUri != null) {
                    prepareImagePart(requireContext(), selectPhotoUri!!)
                } else {
                    null
                }
                viewModel.updateProfile(
                    name = name.toString(),
                    email = email.toString(),
                    image = imagePart,
                    birth_date = dateOfBirth.toString()
                )
            }
        }
        binding.backIc.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun renderPhotoToScreen(selectPhotoUri: Uri) {
        try {
            binding.profileImage.setImageURI(selectPhotoUri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun prepareImagePart(
        context: Context,
        imageUri: Uri,
        partName: String = "image"
    ): MultipartBody.Part? {
        return try {
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                val requestBody =
                    inputStream.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    partName,
                    "image.jpg",
                    requestBody
                ) // Adjust filename as needed
            }
        } catch (e: Exception) {
            // Handle exceptions (e.g., file not found)
            Log.e("prepareImagePart", "Error preparing image part: ${e.message}")
            null
        }
    }

    fun bitmapToMultipart(bitmap: Bitmap): MultipartBody.Part {
        val byteArray = bitmapToByteArray(bitmap)
        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            "image",
            "image.jpg",
            requestBody
        )
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    private fun showFileChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                pickUpPhotoForNewPhones()
            } catch (e: Exception) {
                pickUpPhotoForOldPhones()
            }
        } else {
            try {
                pickUpPhotoForOldPhones()
            } catch (e: Exception) {
                pickUpPhotoForNewPhones()
            }
        }
    }

    private fun pickUpPhotoForNewPhones() {
        try {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            val mimeType = "image/*"
            pickMedia.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.SingleMimeType(
                        mimeType
                    )
                )
            )
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickUpPhotoForOldPhones() {
        try {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 456)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 456 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            //for new phones
            selectPhotoUri = data.data!!
            renderPhotoToScreen(selectPhotoUri!!)
            //for old phones
            try {
                bitmap =
                    MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectPhotoUri)
                Glide.with(this)
                    .load(bitmap)
                    .placeholder(R.drawable.photo_placeholder)
                    .into(binding.profileImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == STORAGE_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            showFileChooser()
        }
    }

    private fun openPickupPhotoRoutine() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                val mimeType = "image/*"
                pickMedia.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.SingleMimeType(
                            mimeType
                        )
                    )
                )
            } else
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                )
                    requestForStoragePermissions(STORAGE_PERMISSION_CODE = STORAGE_PERMISSION_CODE)
                else
                    showFileChooser()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PICK_FROM_GALLERY -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY)
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
            }
        }
    }

    private fun requestForStoragePermissions(STORAGE_PERMISSION_CODE: Int) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_CODE
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}