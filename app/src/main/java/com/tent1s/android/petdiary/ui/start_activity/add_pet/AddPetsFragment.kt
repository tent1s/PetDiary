package com.tent1s.android.petdiary.ui.start_activity.add_pet


import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tent1s.android.petdiary.R
import com.tent1s.android.petdiary.databinding.FragmentStartPetsAddBinding
import com.tent1s.android.petdiary.databinding.FragmentStartPetsListBinding
import com.tent1s.android.petdiary.datebase.PetDiaryDatabase
import com.tent1s.android.petdiary.ui.start_activity.pets_list.PetsListViewModel
import com.tent1s.android.petdiary.utils.hideKeyboard
import com.tent1s.android.petdiary.utils.shortToast
import timber.log.Timber
import java.io.*




class AddPetsFragment : Fragment(R.layout.fragment_start_pets_add) {

    private val CAMERA_CODE = 1
    private val GALLERY_CODE = 0
    private val CAMERA_PHOTO_NAME = "temporaryPhoto"
    private val REQUEST_STORAGE_PERMISSION = 355


    private val addPetsViewModel: AddPetsViewModel by viewModels{
        viewModelFactory
    }

    private val binding : FragmentStartPetsAddBinding by viewBinding {
        FragmentStartPetsAddBinding.bind(it.requireView())
    }

    private lateinit var viewModelFactory: AddPetsViewModelFactory

    private lateinit var photoFile: File


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val dataSource = PetDiaryDatabase.getInstance(application).petsListDao

        viewModelFactory = AddPetsViewModelFactory(dataSource)





        binding.floatingActionButtonPetDone.setOnClickListener {
            if (addPetsViewModel.isValid && addPetsViewModel.uniqueName) {

                if (addPetsViewModel.imageUri.value != null) saveImageFile(addPetsViewModel.imageUri.value!!)

                addPetsViewModel.saveInfToDatabase()

                val navController = binding.root.findNavController()
                navController.navigate(R.id.action_addPetsFragment_to_petsListFragment)

            }else{
                requireContext().shortToast(getString(R.string.text_view_pet_name_empty))
                binding.nameField.error = getString(R.string.text_field_pet_name_error)
            }
        }


        binding.nameInput.addTextChangedListener(addPetsViewModel.nameWatcher())

        binding.breedField.editText!!.doOnTextChanged { text, _, _, _ ->
            addPetsViewModel.getBreed(text.toString())
        }

        binding.weightField.editText!!.doOnTextChanged { text, _, _, _ ->
            addPetsViewModel.getWeight(text.toString())
        }

        binding.dateInput.setOnClickListener {
            activity?.hideKeyboard()

            val builder = MaterialDatePicker.Builder.datePicker()
            builder.setTitleText(getString(R.string.choose_date))
            val picker = builder.build()
            val fragmentManager = (activity as FragmentActivity).supportFragmentManager

            picker.show(fragmentManager, picker.toString())


            picker.addOnPositiveButtonClickListener {
                addPetsViewModel.getDate(it)
            }

        }

        binding.petAvatar.setOnClickListener {
            activity?.hideKeyboard()
            val items = arrayOf(getString(R.string.choose_photo_gallery), getString(R.string.take_photo))

            MaterialAlertDialogBuilder(requireContext())
                    .setItems(items) { _, which ->
                        when (which) {
                            GALLERY_CODE -> selectImageInAlbum()
                            CAMERA_CODE -> takePhoto()
                        }
                    }
                    .show()
        }

        addPetsViewModel.imageUri.observe(viewLifecycleOwner){
            it?.let { notNullUri -> setImageOnLayout(notNullUri) }
        }

    }





    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)

        intent.apply {  type = "image/*"}

        try {
            startActivityForResult(intent, GALLERY_CODE)
        } catch (ex: ActivityNotFoundException) {
            requireContext().shortToast("Невозможно открыть галерею!")
        }
    }

    private fun takePhoto() {
        checkForPermission()
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getTemporaryPhotoFile()
        val fileProvider = FileProvider.getUriForFile(requireContext(), "com.tent1s.android.petdiary.fileprovider", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        try {
            startActivityForResult(takePictureIntent, CAMERA_CODE)
        } catch (ex: ActivityNotFoundException) {
            requireContext().shortToast("Невозможно открыть камеру!")
        }
    }

    private fun getTemporaryPhotoFile(): File {
        val storageDirectory = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(CAMERA_PHOTO_NAME, ".jpg", storageDirectory)
    }

    private fun checkForPermission() {
        if (ContextCompat.checkSelfPermission(
                        requireActivity().applicationContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION
            )
        }
    }

    private fun setImageOnLayout(URI : Uri){
        Glide
                .with(this)
                .load(URI)
                .centerCrop()
                .into(binding.petAvatar)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    val photoUri = Uri.fromFile(photoFile)
                    try {


                        addPetsViewModel.getImageUri(photoUri)

                    } catch (ex: RuntimeException) {
                        requireContext().shortToast(getString(R.string.not_have_permission))
                        return
                    }
                }
                GALLERY_CODE -> {

                    val imageURI: Uri = data!!.data!!

                    addPetsViewModel.getImageUri(imageURI)
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun saveImageFile(uri: Uri){
        val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
        val file = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${addPetsViewModel.name}.jpg")
        try {
            val os: OutputStream = FileOutputStream(file)
            val data = ByteArray(inputStream!!.available())
            inputStream.read(data)
            os.write(data)
            inputStream.close()
            os.close()
        } catch (e: IOException) {
            Timber.w("Error writing $file")
        }
    }



    override fun onStart() {
        super.onStart()

        val items = listOf(getString(R.string.male), getString(R.string.female))
        val genderAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_item, items)
        (binding.genderInput.editText as AutoCompleteTextView).setAdapter(genderAdapter)

        (binding.genderInput.editText as AutoCompleteTextView).doOnTextChanged { text, _, _, _ ->
            addPetsViewModel.getGender(text.toString())
        }

        addPetsViewModel.dayOfBirth.observe(viewLifecycleOwner){
            binding.dateInput.text = Editable.Factory.getInstance().newEditable(it)
        }

        addPetsViewModel.getNameError().observe(viewLifecycleOwner){
            if (it) {
                binding.nameField.error = getString(R.string.text_field_pet_name_error)
            }else{
                binding.nameField.error = null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            photoFile.delete()
        }catch (ex: UninitializedPropertyAccessException ){
            Timber.e("File cant del")
        }
    }
}