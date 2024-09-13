package com.example.konok.ui.myBooks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyBooksViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is my books Fragment"
    }
    val text: LiveData<String> = _text
}