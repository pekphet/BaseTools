// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.net.ex

import com.pekphet.base.components.ZInfoRecorder
import com.pekphet.base.net.ResponseJson
import com.pekphet.base.net.exception.FishNetException
import com.pekphet.base.net.exception.throwOut
import com.pekphet.base.net.makeResponseJson

class PostExRequester(url: String, contentType: ContentType, val isPatch: Boolean = false) :
    IExRequester(url, if (isPatch) ExRequestMethod.PATCH else ExRequestMethod.POST, contentType) {
    private var mBody = ""

    fun setJsonBody(json: String): PostExRequester {
        if (mContentType == ContentType.AppJson) {
            mBody = json
        }
        return this
    }

    fun addFormBody(key: String, value: String) {
        if (value.isNotEmpty() && mContentType == ContentType.Form) {
            if (mBody.isEmpty()) {
                mBody = "$key=$value"
            } else {
                mBody = "$mBody&$key=$value"
            }
        }
    }

    fun setFormBody(param: MutableMap<String, String>) {
        mBody = map2Param(param)
    }

    override suspend fun syncRequest(): ResponseJson {
        try {
            getConnection().apply {
//            if (isPatch) {
//                setRequestProperty("X-HTTP-Method-Override", "PATCH")
//            }
                if (mBody.isNotEmpty()) {
                    ZInfoRecorder.e("POST", "body: $mBody")
                    outputStream.write(mBody.toByteArray(Charsets.UTF_8))
                }
                ZInfoRecorder.e("POST", "url: $url")
                ZInfoRecorder.e("POST", "murl: $mUrl")
                connect()
                if (responseCode / 100 != 2) {
                    ZInfoRecorder.e("resp", "respCode: $responseCode")
                    ZInfoRecorder.e("message", responseMessage)
                    try {
                        ZInfoRecorder.e("message inputstream", stream2Str(inputStream))
                    } catch (ex: Exception) {
//                        ex.printStackTrace()
                    }
                    FishNetException(responseCode, responseMessage).throwOut()
                    return FishNetException(responseCode).makeResponseJson()
//                throw FishNetException(responseCode)
                }
                return ResponseJson(stream2Str(inputStream))
            }
        } catch (ex: Exception) {
//            ex.printStackTrace()
            return ex.makeResponseJson()
        }
    }

    override suspend fun retry() = syncRequest()
}