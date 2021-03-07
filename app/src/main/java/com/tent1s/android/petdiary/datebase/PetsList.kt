package com.tent1s.android.petdiary.datebase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "pets_table")
data class PetsList(
        @PrimaryKey(autoGenerate = false)
        var name: String = "",

        @ColumnInfo(name = "gender")
        var gender:  String? = null,

        @ColumnInfo(name = "breed")
        var breed: String? = null,

        @ColumnInfo(name = "weight")
        var weight: String? = null,

        @ColumnInfo(name = "date_of_birth")
        var DateOfBirth: String? = null,

)