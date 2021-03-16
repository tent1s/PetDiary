package com.tent1s.android.petdiary.datebase



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface PetsListDao {

    @Insert
    suspend fun insert(pets: PetsList)


    @Update
    suspend fun update(pets: PetsList)


    @Query("SELECT * from pets_table WHERE name = :key")
    suspend fun get(key: String): PetsList?

    @Query("DELETE FROM pets_table WHERE name = :key")
    suspend fun del(key: String)

    @Query("DELETE FROM pets_table")
    suspend fun clear()

    @Query("SELECT * FROM pets_table ORDER BY name DESC")
    fun getAllPats(): Flow<List<PetsList>>


}