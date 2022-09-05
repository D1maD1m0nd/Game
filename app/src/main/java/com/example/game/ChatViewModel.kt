package com.example.game

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    val textLiveData = MutableLiveData("")
    val listLiveData = MutableLiveData<MutableList<String>>()
    private val messages = ArrayList<String>()
    fun setMessage(text : String) {
        viewModelScope.launch {
            textLiveData.value = text
        }
    }

    fun addMessage(text : String) {
        viewModelScope.launch {
            messages.add(text)
            listLiveData.value = messages
        }
    }
}