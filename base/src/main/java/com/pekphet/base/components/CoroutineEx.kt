// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import android.os.Handler
import android.os.Looper
import com.pekphet.base.B
import com.pekphet.base.TypedSync
import kotlinx.coroutines.*

private val _CoroutineHandler = Handler(Looper.getMainLooper())

infix fun <T> Deferred<T>.then(block: (T) -> Unit): Job {
    return MainScope().launch {
        block(this@then.await())
    }
}

infix fun CoroutineScope.ui(main: B) {
    launch(Dispatchers.Main) { main() }
}

infix fun CoroutineScope.main(blk: B) {
    launch(Dispatchers.Main) { blk() }
}

infix fun CoroutineScope.bg(blk: TypedSync<CoroutineScope>) {
    launch { blk() }
}

infix fun CoroutineScope.io(blk: TypedSync<CoroutineScope>) {
    launch(Dispatchers.IO) { blk() }
}

fun CoroutineScope.count(repeat: Int = -1, delayMs: Long, stopFlag: Boolean, cb: B) {
    launch {
        var rep = repeat
        while (!stopFlag && rep-- != 0) {
            ui(cb)
            delay(delayMs)
        }
    }
}

fun CoroutineScope.delayDo(delayMs: Long, cb: B) = launch {
    delay(delayMs)
    ui(cb)
}