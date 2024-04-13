package com.silvia.easybizzmanager3.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _email = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val email: LiveData<String> = _email

    fun setEmail(email:String){
        _email.value = email
    }
}