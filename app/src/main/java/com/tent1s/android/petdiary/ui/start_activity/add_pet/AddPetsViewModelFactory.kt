package com.tent1s.android.petdiary.ui.start_activity.add_pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tent1s.android.petdiary.datebase.PetsListDao


class AddPetsViewModelFactory(private val dataSource: PetsListDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPetsViewModel::class.java)) {
            return AddPetsViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}