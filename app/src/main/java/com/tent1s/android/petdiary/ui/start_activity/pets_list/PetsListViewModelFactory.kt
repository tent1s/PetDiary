package com.tent1s.android.petdiary.ui.start_activity.pets_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tent1s.android.petdiary.datebase.PetsListDao
import com.tent1s.android.petdiary.repository.PetDiaryRepository


class PetsListViewModelFactory(private val myRepository: PetDiaryRepository, private val dateSource: PetsListDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetsListViewModel::class.java)) {
            return PetsListViewModel(myRepository, dateSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}