package com.tent1s.android.petdiary.ui.main_activity.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tent1s.android.petdiary.R
import com.tent1s.android.petdiary.databinding.FragmentEventsBinding
import com.tent1s.android.petdiary.ui.main_activity.events.EventsViewModel

class PhotoFragment : Fragment() {

    private lateinit var photoViewModel: PhotoViewModel
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoViewModel =
                ViewModelProvider(this).get(PhotoViewModel::class.java)
        _binding = FragmentEventsBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}