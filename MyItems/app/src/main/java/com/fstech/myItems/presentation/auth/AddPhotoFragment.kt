package store.msolapps.flamingo.presentation.auth

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentAddPhotoBinding
import java.io.ByteArrayOutputStream
import java.io.IOException


const val STORAGE_PERMISSION_CODE = 543

@AndroidEntryPoint
class AddPhotoFragment : BaseFragment() {
    private var _binding: FragmentAddPhotoBinding? = null
    private var email: String? = null
    private var name: String? = null
    private lateinit var filesAssistant: FilesAssistant
    private var selectPhotoUri: Uri? = null
    private val PICK_FROM_GALLERY = 1
    var bitmap: Bitmap? = null
    private val binding get() = _binding!!
    private val viewModel: AddPhotoViewModel by activityViewModels()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("name")!!
            email = it.getString("email")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPhotoBinding.inflate(inflater, container, false)
        binding.imageView6.setImageDrawable(resources.getDrawable(R.drawable.photo_placeholder))
        selectPhotoUri =
            Uri.parse("android.resource://store.msolapps.flamingo/drawable/photo_placeholder")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonSignUp.setOnClickListener {
            val imagePart = if (bitmap != null) {
                bitmapToMultipart(bitmap!!)
//                    convertImage(selectPhotoUri)
            } else if (selectPhotoUri != null) {
                prepareImagePart(requireContext(), selectPhotoUri!!)
            } else {
                null
            }
            viewModel.updateProfile(
                name ?: "",
                email ?: "",
                image = imagePart,
                birth_date = "2021-02-09"
            )
        }
        binding.constraintLayout3.setOnClickListener { openPickupPhotoRoutine() }
        binding.imageView6.setOnClickListener { openPickupPhotoRoutine() }
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

    private fun setupObservers() {
        viewModel.isLoad.observe(viewLifecycleOwner) {
            if (it) showLoading() else hideLoading()
        }
        viewModel.error.observe(viewLifecycleOwner) {
            Log.e("AddPhotoFragment", it)
        }
        viewModel.updateProfile.observe(viewLifecycleOwner) {
            findNavController()
                .navigate(AddPhotoFragmentDirections.actionAddPhotoFragmentToMapFragment())
        }
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
        if (requestCode == STORAGE_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            showFileChooser()
        }

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
                    .into(binding.imageView6)
            } catch (e: IOException) {
                e.printStackTrace()
            }
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
        bitmap.compress(CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
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

    private fun renderPhotoToScreen(selectPhotoUri: Uri) {
        try {
            binding.imageView6.setImageURI(selectPhotoUri)
            binding.tvUploadPhoto.visibility = View.GONE
        } catch (e: IOException) {
            e.printStackTrace()
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


}