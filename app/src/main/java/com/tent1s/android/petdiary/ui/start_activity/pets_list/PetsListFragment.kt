package com.tent1s.android.petdiary.ui.start_activity.pets_list


import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tent1s.android.petdiary.PetDiaryApplication
import com.tent1s.android.petdiary.R
import com.tent1s.android.petdiary.databinding.FragmentEventsBinding
import com.tent1s.android.petdiary.databinding.FragmentHeadBinding
import com.tent1s.android.petdiary.databinding.FragmentStartPetsListBinding
import com.tent1s.android.petdiary.datebase.PetDiaryDatabase
import com.tent1s.android.petdiary.ui.main_activity.events.EventsViewModel
import kotlinx.coroutines.flow.collect
import java.io.File


class PetsListFragment : Fragment(R.layout.fragment_start_pets_list) {


    private val petsListViewModel: PetsListViewModel by viewModels{
        viewModelFactory
    }

    private val binding : FragmentStartPetsListBinding by viewBinding {
        FragmentStartPetsListBinding.bind(it.requireView())
    }



    private lateinit var viewModelFactory: PetsListViewModelFactory



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myRepository = (requireActivity().application as PetDiaryApplication).repository

        val application = requireNotNull(this.activity).application
        val dataSource = PetDiaryDatabase.getInstance(application).petsListDao

        viewModelFactory = PetsListViewModelFactory(myRepository, dataSource)


        binding.petAddFloatingActionButton.setOnClickListener {
            val navController = binding.root.findNavController()
            navController.navigate(R.id.action_petsListFragment_to_addPetsFragment)
        }




        val adapter = PetsListAdapter({

            MaterialAlertDialogBuilder(requireContext())
                    .setTitle("${getString(R.string.delete_alert_dialog)} ${it.name}?")
                    .setNegativeButton(getString(R.string.yes_alert_dialog)) { _, _ ->
                        petsListViewModel.startDelItemDatabase(it.name)

                        deleteExternalStoragePrivateFile(it.name)

                    }
                    .setPositiveButton(getString(R.string.no_alert_dialog)) { _, _ -> }
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
        binding.petsListRecyclerView.adapter = adapter

        lifecycleScope.launchWhenStarted {
            petsListViewModel.pets.collect {
                binding.emptyListImage.isVisible = it.isEmpty()
                adapter.submitList(it)
            }
        }
    }


    private fun deleteExternalStoragePrivateFile(name: String) {
        File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$name.jpg").delete()
    }


}