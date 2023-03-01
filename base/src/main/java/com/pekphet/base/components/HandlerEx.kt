// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import android.os.Handler
import android.os.Looper
import com.pekphet.base.B

fun Handler.delay(ms: Long, runnable: () -> Unit) = postDelayed(runnable, ms)

object HandlerEx {
    private val _H = Handler(Looper.getMainLooper())
    fun delay(ms: Long, runnable: B) = _H.postDelayed(runnable, ms)
}
