// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.construction.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pekphet.base.construction.BaseViewModel

class SampleBaseViewModel : BaseViewModel(){

    /**
     * 定义内部操作的LiveData，以及初始值
     */
    private val _sampleString = MutableLiveData<String>().apply { value = "default_value" }

    /**
     * 外部使用这个公共数据做observe绑定
     */
    val sample: LiveData<String>  = _sampleString

    /**
     * 暴露给外部修改值的方法
     */
    fun changeSample(newContent: String) {
        changeWithPost(_sampleString, newContent)
        return
    }
}