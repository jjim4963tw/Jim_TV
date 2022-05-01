package com.jim.tv

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideoViewModel: ViewModel() {
    val nowSelectedID = MutableLiveData<String>()
}