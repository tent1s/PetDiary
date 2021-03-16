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
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.tent1s.android.petdiary.R
import com.tent1s.android.petdiary.databinding.FragmentDocumentsBinding
import com.tent1s.android.petdiary.databinding.FragmentHeadBinding
import com.tent1s.android.petdiary.ui.start_activity.pets_list.PetsListViewModelFactory
import timber.log.Timber
import java.io.File

private lateinit var name : String

class HeadFragment : Fragment(R.layout.fragment_head), Postman {

    private lateinit var headViewModel: HeadViewModel

    private var activity: Activity? = null
    private lateinit var viewModelFactory: HeadViewModelFactory
    private var _binding: FragmentHeadBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHeadBinding.bind(view)


        viewModelFactory = HeadViewModelFactory(name)
        headViewModel =
                ViewModelProvider(this, viewModelFactory).get(HeadViewModel::class.java)


        super.onAttach(requireContext())
        if (context is Activity) activity = context as Activity


        binding.petName.text = headViewModel.petName


        val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${headViewModel.petName}.jpg")

        if (file.exists()){
            Glide
                    .with(requireContext())
                    .load(file)
                    .centerCrop()
                    .into(binding.petAvatar)
        }
    }

    override fun mailToFragment(petName: String) {
        name = petName
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}