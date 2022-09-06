package com.example.game

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    val textLiveData = MutableLiveData("")
    private val listLiveData = ArrayList<String>().toMutableStateList()
    val list : List<String> get() = listLiveData
    private val messages = ArrayList<String>()
    fun setMessage(text : String) {
        viewModelScope.launch {
            textLiveData.value = text
        }
    }

    fun addMessage(text : String) {
        viewModelScope.launch {
            listLiveData.add(text)
        }
    }
}