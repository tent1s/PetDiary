package com.tent1s.android.petdiary

import android.app.Application
import android.content.Context
import com.tent1s.android.petdiary.datebase.PetDiaryDatabase
import com.tent1s.android.petdiary.datebase.PetsListDao
import com.tent1s.android.petdiary.repository.PetDiaryRepository
import timber.log.Timber

class PetDiaryApplication : Application() {

    lateinit var dateSource: PetsListDao

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        dateSource = PetDiaryDatabase.getInstance(applicationContext).petsListDao
    }

    private val _repository by lazy { PetDiaryRepository(dateSource) }
    val repository
        get() = _repository

}