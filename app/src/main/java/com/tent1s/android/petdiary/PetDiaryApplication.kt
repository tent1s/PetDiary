package com.tent1s.android.petdiary

import android.app.Application
import android.content.Context
import com.tent1s.android.petdiary.repository.PetDiaryRepository
import timber.log.Timber

class PetDiaryApplication : Application() {

    lateinit var application: Context

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        application = applicationContext
    }

    private val _repository by lazy { PetDiaryRepository(application as Application) }
    val repository
        get() = _repository

}