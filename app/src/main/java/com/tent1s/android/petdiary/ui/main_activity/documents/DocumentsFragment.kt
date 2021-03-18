package com.tent1s.android.petdiary.ui.main_activity.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.tent1s.android.petdiary.R
import com.tent1s.android.petdiary.databinding.FragmentDocumentsBinding
import com.tent1s.android.petdiary.databinding.FragmentEventsBinding
import com.tent1s.android.petdiary.ui.main_activity.events.EventsViewModel


class DocumentsFragment : Fragment(R.layout.fragment_documents) {

    private val documentsViewModel: DocumentsViewModel by viewModels()

    private val binding : FragmentDocumentsBinding by viewBinding {
        FragmentDocumentsBinding.bind(it.requireView())
    }
}