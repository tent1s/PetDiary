package com.tent1s.android.petdiary.ui.start_activity.pets_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tent1s.android.petdiary.datebase.PetsList
import com.tent1s.android.petdiary.datebase.PetsListDao
import com.tent1s.android.petdiary.repository.PetDiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class PetsListViewModel(myRepository: PetDiaryRepository, private val  dateSource: PetsListDao) : ViewModel(){

    val pets: Flow<List<PetsList>> = myRepository.listPets

    fun startDelItemDatabase(name : String){
        viewModelScope.launch {
            del(name)
        }
    }

    private suspend fun del(key: String) {
        dateSource.del(key)
    }

}