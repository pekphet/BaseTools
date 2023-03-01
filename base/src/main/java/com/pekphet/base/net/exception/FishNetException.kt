// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.net.exception

import com.pekphet.base.event.EventManager
import com.pekphet.base.event.GlobalEvent
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by fish on 18-2-27.
 */
class FishNetException(responseCode: Int, responseMsg: String = "") : Exception() {
    var mResponseCode = responseCode
    private var mMessage = ""
    val responseMessage = responseMsg

    fun getReasonMessage() = when (mResponseCode) {
        in 300..399 -> "${mResponseCode}重定向"
        400 -> "400 请求错误"
        401 -> "401 身份验证错误"
        403 -> "403 请求被禁止"
        404 -> "404 页面未找到"
        405 -> "405 请求方式错误"
        408 -> "408 页面超时"
        500 -> "500 服务器内部错误"
        501 -> "501 不可实现的请求"
        502 -> "502 网关错误"
        503 -> "503 服务不可用"
        504 -> "504 网关超时"
        else -> "${mResponseCode}未知错误"
    }

    fun setMsg(msg: String) {
        mMessage = msg
    }
}

fun makeFishNetException(respcode: Int, inputstream: InputStream): FishNetException {
    val exception = FishNetException(respcode)
    try {
        exception.setMsg(stream2Str(inputstream))
        return exception
    } catch (ex: Exception) {
//        ex.printStackTrace()
        return exception
    } finally {
        inputstream.closeQuiet()
    }
}

fun stream2Str(inputStream: InputStream): String {
    val reader = InputStreamReader(inputStream, "UTF-8")
    val buf = CharArray(1024)
    val sb = StringBuffer()
    var readCnt: Int
    do {
        readCnt = reader.read(buf)
        if (readCnt > 0)
            sb.append(buf, 0, readCnt)
    } while (readCnt >= 0)
    return sb.toString()
}

fun InputStream.closeQuiet() = try {
    close()
} catch (ex: Exception) {
//    ex.printStackTrace()
}

fun FishNetException.throwOut() {
    try {
        throw this
    } catch (ex: FishNetException) {
        ex.printStackTrace()
        EventManager.postEvent(GlobalEvent.HttpExceptionEvent, ex)
    }
}