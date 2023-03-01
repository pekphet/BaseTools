// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import android.util.Log
import com.pekphet.base.components.ZInfoRecorder

/**
 * Debug tools.
 *
 */
object Debug {

    fun <T> costTimeWithResult(tag: String, function: () -> T): T {
        val startTs = System.currentTimeMillis()
        val result = function()
        Log.d(tag, "cost: ${System.currentTimeMillis() - startTs}ms")
        return result
    }

    fun costTime(tag: String, function: () -> Unit) {
        val startTs = System.currentTimeMillis()
        function()
        Log.d(tag, "cost: ${System.currentTimeMillis() - startTs}ms")
    }

    infix fun (() -> Unit).costTime(tag: String) {
        val startTs = System.currentTimeMillis()
        this()
        Log.d(tag, "cost: ${System.currentTimeMillis() - startTs}ms")
    }

    infix fun <R> (() -> R).withCostTime(tag: String): R {
        val startTs = System.currentTimeMillis()
        val ret = this()
        ZInfoRecorder.w(tag, "cost: ${System.currentTimeMillis() - startTs}ms")
        return ret
    }

    fun showStack() = try {
//        throw RuntimeException("ShowStack")
    } catch (ex: Exception) {
//        ex.printStackTrace()
    }

    inline fun <T> T.logE(logBlock: (T) -> String): T {
        ZInfoRecorder.e("DebugLogE", logBlock(this))
        return this
    }
}