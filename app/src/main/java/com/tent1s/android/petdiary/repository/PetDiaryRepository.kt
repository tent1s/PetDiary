package com.tent1s.android.petdiary.repository

import android.app.Application
import com.tent1s.android.petdiary.datebase.PetDiaryDatabase
import com.tent1s.android.petdiary.datebase.PetsList
import kotlinx.coroutines.flow.Flow

class PetDiaryRepository(application: Application) {

    private val dataSource = PetDiaryDatabase.getInstance(application).petsListDao

    val listPets: Flow<List<PetsList>>
        get() = dataSource.getAllPats()
}