package com.tent1s.android.petdiary.ui.start_activity.pets_list


import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tent1s.android.petdiary.PetDiaryApplication
import com.tent1s.android.petdiary.R
import com.tent1s.android.petdiary.databinding.FragmentHeadBinding
import com.tent1s.android.petdiary.databinding.FragmentStartPetsListBinding
import com.tent1s.android.petdiary.datebase.PetDiaryDatabase
import java.io.File


class PetsListFragment : Fragment(R.layout.fragment_start_pets_list) {

    private var _binding: FragmentStartPetsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var petsListViewModel: PetsListViewModel
    private lateinit var viewModelFactory: PetsListViewModelFactory



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentStartPetsListBinding.bind(view)

        val myRepository = (requireActivity().application as PetDiaryApplication).repository

        val application = requireNotNull(this.activity).application
        val dataSource = PetDiaryDatabase.getInstance(application).petsListDao

        viewModelFactory = PetsListViewModelFactory(myRepository, dataSource)

        petsListViewModel =
                ViewModelProvider(this, viewModelFactory).get(PetsListViewModel::class.java)




        binding.floatingActionButtonPetAdd.setOnClickListener {
            val navController = binding.root.findNavController()
            navController.navigate(R.id.action_petsListFragment_to_addPetsFragment)
        }




        val adapter = PetsListAdapter({

            MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Удалить ${it.name}?")
                    .setNegativeButton("ДА") { _, _ ->
                        petsListViewModel.startDelItemDatabase(it.name)

                        deleteExternalStoragePrivateFile(it.name)

                    }
                    .setPositiveButton("НЕТ") { _, _ -> }
                    .show()

            true

        }, {

            val navController = binding.root.findNavController()
            navController.navigate(
                    PetsListFragmentDirections.actionPetsListFragmentToMainActivity(
                            it.name
                    )
            )

        }, requireContext())
        binding.listOfPets.adapter = adapter

        petsListViewModel.pets.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()){
                    binding.imageEmptyList.visibility = View.GONE
                }else{
                    binding.imageEmptyList.visibility = View.VISIBLE
                }
                adapter.submitList(it)
            }
        }
    }


    private fun deleteExternalStoragePrivateFile(name: String) {
        val file = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$name.jpg")
        file.delete()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}