package com.tent1s.android.petdiary.datebase



import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [PetsList::class], version = 2, exportSchema = false)
abstract class PetDiaryDatabase : RoomDatabase() {


    abstract val petsListDao: PetsListDao



    companion object {

        private const val nameDatabase = "pets_database"

        @Volatile
        private var INSTANCE: PetDiaryDatabase? = null
        fun getInstance(context: Context): PetDiaryDatabase {

            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PetDiaryDatabase::class.java,
                            nameDatabase
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

    }
}
