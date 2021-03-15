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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tent1s.android.petdiary.R
import com.tent1s.android.petdiary.databinding.FragmentStartPetsAddBinding
import com.tent1s.android.petdiary.datebase.PetDiaryDatabase
import com.tent1s.android.petdiary.utils.hideKeyboard
import com.tent1s.android.petdiary.utils.shortToast
import timber.log.Timber
import java.io.*


private const val CAMERA_CODE = 1
private const val GALLERY_CODE = 0
private const val CAMERA_PHOTO_NAME = "temporaryPhoto"
private const val REQUEST_STORAGE_PERMISSION = 355

class AddPetsFragment : Fragment(R.layout.fragment_start_pets_add) {

    private var _binding: FragmentStartPetsAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var addPetsViewModel: AddPetsViewModel
    private lateinit var viewModelFactory: AddPetsViewModelFactory

    private lateinit var photoFile: File


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStartPetsAddBinding.bind(view)

        val application = requireNotNull(this.activity).application
        val dataSource = PetDiaryDatabase.getInstance(application).petsListDao

        viewModelFactory = AddPetsViewModelFactory(dataSource)
        addPetsViewModel =
                ViewModelProvider(this, viewModelFactory).get(AddPetsViewModel::class.java)






        binding.floatingActionButtonPetDone.setOnClickListener {
            if (addPetsViewModel.isValid && addPetsViewModel.uniqueName) {

                if (addPetsViewModel.imageUri.value != null) saveImageFile(addPetsViewModel.imageUri.value!!)

                addPetsViewModel.saveInfToDatabase()

                val navController = binding.root.findNavController()
                navController.navigate(R.id.action_addPetsFragment_to_petsListFragment)

            }else{
                requireContext().shortToast("Введите кличку!")
                binding.nameField.error = "Поле пустое или такая кличка уже есть!"
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
            builder.setTitleText("Выбор даты")
            val picker = builder.build()
            val fragmentManager = (activity as FragmentActivity).supportFragmentManager

            picker.show(fragmentManager, picker.toString())


            picker.addOnPositiveButtonClickListener {
                addPetsViewModel.getDate(it)
            }

        }

        binding.petAvatar.setOnClickListener {
            activity?.hideKeyboard()
            val items = arrayOf("Фото из галереи", "Сфотографировать")

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
        intent.type = "image/*"
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
                        requireContext().shortToast("Без прав невозможно сохранить!")
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

        val items = listOf("Самец", "Самка")
        val genderAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_item, items)
        (binding.genderInput.editText as? AutoCompleteTextView)?.setAdapter(genderAdapter)

        binding.genderInput.editText!!.doOnTextChanged { text, _, _, _ ->
            addPetsViewModel.getGender(text.toString())
        }

        addPetsViewModel.dayOfBirth.observe(viewLifecycleOwner){
            binding.dateInput.text = Editable.Factory.getInstance().newEditable(it)
        }

        addPetsViewModel.getNameError().observe(viewLifecycleOwner){
            if (it) {
                binding.nameField.error = "Поле пустое или такая кличка уже есть!"
            }else{
                binding.nameField.error = null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        try {
            photoFile.delete()
        }catch (ex: UninitializedPropertyAccessException ){
            Timber.e("File cant del")
        }
    }
}