package com.tent1s.android.petdiary.ui.main_activity.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View

import androidx.fragment.app.Fragment
import com.tent1s.android.petdiary.R
import com.tent1s.android.petdiary.databinding.FragmentDocumentsBinding


class DocumentsFragment : Fragment(R.layout.fragment_documents) {

    private var _binding: FragmentDocumentsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDocumentsBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}