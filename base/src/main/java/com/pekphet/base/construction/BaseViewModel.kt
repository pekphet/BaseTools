// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.construction

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pekphet.base.components.ZInfoRecorder

abstract class BaseViewModel : ViewModel() {

    protected fun <T> changeWithPost(liveData: MutableLiveData<T>, data: T) {
        liveData.postValue(data)
    }

    @MainThread
    protected fun <T> changeDirectly(liveData: MutableLiveData<T>, data: T) {
        liveData.value = data
    }

    override fun onCleared() {
        super.onCleared()
        ZInfoRecorder.e("VM", "${this::class.simpleName}: cleared")
    }
}