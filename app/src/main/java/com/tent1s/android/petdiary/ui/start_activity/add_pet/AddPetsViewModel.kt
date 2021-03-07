package com.tent1s.android.petdiary.ui.start_activity.add_pet

import android.net.Uri
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tent1s.android.petdiary.datebase.PetsList
import com.tent1s.android.petdiary.datebase.PetsListDao
import com.tent1s.android.petdiary.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*


class AddPetsViewModel(private val dataSource: PetsListDao) : ViewModel(){

    private var _isValid = false
    val isValid: Boolean
        get() = _isValid

    private var _name = ""
    val name: String
        get() = _name

    private var _imageUri : Uri? = null
    val imageUri: Uri?
        get() = _imageUri


    private var gender : String? = null

    private var breed : String? = null

    private var weight : String? = null

    private val _dayOfBirth = MutableLiveData("")
    val dayOfBirth: LiveData<String>
        get() = _dayOfBirth


    private val nameError = SingleLiveEvent<Boolean>()
    fun getNameError(): SingleLiveEvent<Boolean> {
        return nameError
    }

    private var _uniqueName = false
    val uniqueName: Boolean
        get() = _uniqueName

    private fun validation() {

        val isValidName = !TextUtils.isEmpty(name)
        _isValid = isValidName

        viewModelScope.launch {
            _uniqueName = get(name) == null
        }

        if (!isValid){
            nameError.postValue(true)
        }else{
            nameError.postValue(false)
        }
    }

    fun nameWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                _name = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {
                validation()
            }
        }
    }

    fun getBreed(newBreed : String){
        breed = newBreed
    }

    fun getWeight(newWeight : String){
        weight = newWeight
    }

    fun getGender(newGender : String){
        gender = newGender
    }

    fun getDate(newDayOfBirth : Long){
        _dayOfBirth.postValue(convertLongDateToString(newDayOfBirth))
    }

    fun getImageUri(newImageUri : Uri){
        _imageUri = newImageUri
    }

    private fun convertLongDateToString(newDayOfBirth: Long): String {
        val dateTime = Date(newDayOfBirth)
        val sm = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
        return sm.format(dateTime)
    }

    fun saveInfToDatabase(){
        viewModelScope.launch {
            insert(PetsList(name, gender, breed, weight, dayOfBirth.value))
        }
    }

    private suspend fun insert(pets: PetsList) {
        dataSource.insert(pets)
    }

    private suspend fun get(name: String) : PetsList? {
        return dataSource.get(name)
    }

}