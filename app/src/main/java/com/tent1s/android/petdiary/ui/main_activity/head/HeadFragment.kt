package com.tent1s.android.petdiary.ui.main_activity.head

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.tent1s.android.petdiary.R
import com.tent1s.android.petdiary.databinding.FragmentDocumentsBinding
import com.tent1s.android.petdiary.databinding.FragmentEventsBinding
import com.tent1s.android.petdiary.databinding.FragmentHeadBinding
import com.tent1s.android.petdiary.ui.main_activity.events.EventsViewModel
import com.tent1s.android.petdiary.ui.start_activity.pets_list.PetsListViewModelFactory
import timber.log.Timber
import java.io.File

private lateinit var name : String

class HeadFragment : Fragment(R.layout.fragment_head), Postman {



    private lateinit var viewModelFactory: HeadViewModelFactory

    private val headViewModel: HeadViewModel by viewModels{
        viewModelFactory
    }

    private val binding : FragmentHeadBinding by viewBinding {
        FragmentHeadBinding.bind(it.requireView())
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewModelFactory = HeadViewModelFactory(name)




        binding.petNameTextView.text = headViewModel.petName


        val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${headViewModel.petName}.jpg")

        if (file.exists()){
            Glide
                    .with(requireContext())
                    .load(file)
                    .centerCrop()
                    .into(binding.petAvatarImage)
        }
    }

    override fun mailToFragment(petName: String) {
        name = petName
    }


}