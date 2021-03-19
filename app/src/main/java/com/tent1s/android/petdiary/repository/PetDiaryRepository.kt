package com.tent1s.android.petdiary.repository

import android.app.Application
import com.tent1s.android.petdiary.datebase.PetDiaryDatabase
import com.tent1s.android.petdiary.datebase.PetsList
import com.tent1s.android.petdiary.datebase.PetsListDao
import kotlinx.coroutines.flow.Flow

class PetDiaryRepository(private val dataSource: PetsListDao) {



    val listPets: Flow<List<PetsList>>
        get() = dataSource.getAllPats()

}