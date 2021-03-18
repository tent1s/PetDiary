package com.tent1s.android.petdiary.ui.main_activity.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.tent1s.android.petdiary.R
import com.tent1s.android.petdiary.databinding.FragmentDocumentsBinding
import com.tent1s.android.petdiary.databinding.FragmentEventsBinding

class EventsFragment : Fragment(R.layout.fragment_events) {


    private val eventsViewModel: EventsViewModel by viewModels()

    private val binding : FragmentEventsBinding by viewBinding {
        FragmentEventsBinding.bind(it.requireView())
    }


}