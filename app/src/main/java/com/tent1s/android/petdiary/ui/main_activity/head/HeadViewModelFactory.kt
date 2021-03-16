package com.tent1s.android.petdiary.ui.main_activity.head

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class HeadViewModelFactory(private val petName: String) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HeadViewModel::class.java)) {
            return HeadViewModel(petName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}