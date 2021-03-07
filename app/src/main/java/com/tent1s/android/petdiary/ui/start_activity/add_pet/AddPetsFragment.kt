package com.tent1s.android.petdiary.ui.start_activity.add_pet


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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

class AddPetsFragment : Fragment() {

    private var _binding: FragmentStartPetsAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var addPetsViewModel: AddPetsViewModel
    private lateinit var viewModelFactory: AddPetsViewModelFactory



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_start_pets_add,
                container,
                false
        )


        val application = requireNotNull(this.activity).application
        val dataSource = PetDiaryDatabase.getInstance(application).petsListDao

        viewModelFactory = AddPetsViewModelFactory(dataSource)
        addPetsViewModel =
                ViewModelProvider(this, viewModelFactory).get(AddPetsViewModel::class.java)

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.floatingActionButtonPetDone.setOnClickListener {
            if (addPetsViewModel.isValid && addPetsViewModel.uniqueName) {

                if (addPetsViewModel.imageUri != null) saveImageFile(addPetsViewModel.imageUri!!)

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

        binding.imageView.setOnClickListener {
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


    }



    private fun selectImageInAlbum() {
        val packageManager = requireActivity().packageManager
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, GALLERY_CODE)
        }
    }

    private fun takePhoto() {
        val packageManager = requireActivity().packageManager
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CAMERA_CODE)
        }
    }






    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {

                    val bundle: Bundle = data!!.extras!!
                    val bmp = bundle["data"] as Bitmap?
                    val resized = Bitmap.createScaledBitmap(bmp!!, 500, 500, true)

                    Glide
                        .with(this)
                        .load(resized)
                        .into(binding.imageView)

                    addPetsViewModel.getImageUri(covertBitmapToUri(requireContext(),resized)!!)

                }
                GALLERY_CODE -> {

                    val imageURI: Uri? = data?.data

                    Glide
                        .with(this)
                        .load(imageURI)
                        .into(binding.imageView)

                    addPetsViewModel.getImageUri(imageURI!!)
                }
            }
        }
    }


    private fun covertBitmapToUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun saveImageFile(uri: Uri){
        val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
        val file = File(requireActivity().getExternalFilesDir(null), "${addPetsViewModel.name}.jpg")
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
    }
}