// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.construction

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.pekphet.base.components.safe
import com.pekphet.base.view.transluteStatusbar
import com.pekphet.base.construction.annotations.AActivityViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

open class BaseActivity<T : ViewBinding> : AppCompatActivity(), CoroutineScope by MainScope(), IContext {
    protected lateinit var mViewBinding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadBinding()
        transluteStatusbar(false)
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadBinding() {
        safe {
            val viewBindingKlz =
                this::class.java.getAnnotation(AActivityViewBinding::class.java)!!.viewBinding
            mViewBinding = viewBindingKlz.java.getMethod("inflate", LayoutInflater::class.java)
                .invoke(null, layoutInflater) as T
            setContentView(mViewBinding.root)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun getContext() = this
}