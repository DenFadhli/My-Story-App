package com.submission.mystoryapp.ui.story

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.submission.mystoryapp.R
import com.submission.mystoryapp.databinding.FragmentPostBinding
import com.submission.mystoryapp.di.ViewModelFactory
import com.submission.mystoryapp.ui.main.MainActivity
import com.submission.mystoryapp.ui.viewmodel.MainViewModel
import com.submission.mystoryapp.utils.MediaUtils.createTemporaryFile
import com.submission.mystoryapp.utils.MediaUtils.reduceFileImage
import com.submission.mystoryapp.utils.MediaUtils.rotateBitmap
import com.submission.mystoryapp.utils.MediaUtils.uriToFile
import com.submission.mystoryapp.utils.Result
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding
    private var getLoc: Location? = null
    private var getPathFile: File? = null

    private lateinit var photoPath: String
    private lateinit var mainViewModel: MainViewModel
    private lateinit var flpc: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        val uriFile = arguments?.get("selected_image")
        if (uriFile != null) {
            val uri: Uri = uriFile as Uri
            val isBackCamera = arguments?.get("isBackCamera") as Boolean
            val result = rotateBitmap(
                BitmapFactory.decodeFile(uri.path),
                isBackCamera
            )
            getPathFile = uri.toFile()
            binding?.ivPostPreview!!.setImageBitmap(result)
        }

        setupModel()
        binding?.apply {
            btnCamera.setOnClickListener {
                openCamera()
            }
            btnGallery.setOnClickListener {
                openGallery()
            }
            btnUpload.setOnClickListener {
                upload()
            }
            flpc = LocationServices.getFusedLocationProviderClient(requireContext())
            setUploadLocation()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireContext().packageManager)

        createTemporaryFile(requireContext()).also { file ->
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.submission.mystoryapp",
                file
            )
            photoPath = file.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val myFile = File(photoPath)
            getPathFile = myFile

            val bitmap = BitmapFactory.decodeFile(getPathFile?.path)
            binding?.ivPostPreview!!.setImageBitmap(bitmap)
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            getPathFile = myFile
            binding?.ivPostPreview!!.setImageURI(selectedImg)
        }
    }

    private fun withLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            flpc.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this.getLoc = location
                } else {
                    binding?.btnLocEnable!!.isChecked = true
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.invalid_location),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val locationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            withLocation()
        }
    }

    private fun setUploadLocation() {
        binding?.btnLocEnable?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (allPermissionsGranted()) {
                    withLocation()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                getLoc = null
            }
        }
    }

    private fun upload() {
        mainViewModel.getUser().observe(viewLifecycleOwner) { user ->
            val token = "Bearer " + user.token
            if (getPathFile != null) {
                val file = reduceFileImage(getPathFile as File)
                val descriptionText = binding?.edPostDesc!!.text
                val lat = getLoc?.latitude ?: 0.0
                val lon = getLoc?.longitude ?: 0.0
                if (!descriptionText.isNullOrEmpty()) {
                    val description = descriptionText.toString().toRequestBody("text/plain".toMediaType())
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestImageFile
                    )
                    val latBody = lat.toString().toRequestBody("multipart/form-data".toMediaType())
                    val lonBody = lon.toString().toRequestBody("multipart/form-data".toMediaType())
                    mainViewModel.uploadStory(token, imageMultipart, description, latBody, lonBody).observe(viewLifecycleOwner) { response ->
                        when(response) {
                            is Result.Loading -> showLoading(true)
                            is Result.Success -> {
                                showLoading(false)
                                val message = response.data.message
                                when {
                                    message!!.contains("Story created successfully") ->
                                        Toast.makeText(requireContext(), resources.getString(R.string.success_create_story), Toast.LENGTH_SHORT).show()
                                }
                                Log.d("CreateStory: ", "response: ${response.data}")
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            is Result.Error -> {
                                showLoading(false)
                                Toast.makeText(requireContext(), response.error, Toast.LENGTH_SHORT).show()
                                Log.d("CreateStory: ", "response: ${response.error}")
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), R.string.blank_description_create_story, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), R.string.blank_image_create_story, Toast.LENGTH_SHORT).show()
            }
            Log.d("cek token createStory", token)
        }
    }

    private fun setupModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding?.btnCamera?.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding?.btnGallery?.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding?.ivPostPreview?.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding?.edPostDesc?.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding?.btnUpload?.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        fun newInstance() = PostFragment()
    }
}