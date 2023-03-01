// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.net.ex

import com.pekphet.base.components.ZInfoRecorder
import com.pekphet.base.net.ResponseJson
import com.pekphet.base.net.exception.FishNetException
import com.pekphet.base.net.exception.throwOut
import com.pekphet.base.net.makeResponseJson

class SimpleExRequester(url: String, method: ExRequestMethod, contentType: ContentType? = null) :
    IExRequester(url, method, contentType) {

    override suspend fun syncRequest(): ResponseJson {
        val startSt = System.currentTimeMillis()
        try {
            getConnection().apply {
                if (responseCode / 100 != 2) {
                    ZInfoRecorder.e("COST-FNErr", "+${System.currentTimeMillis() - startSt}ms---SYNC REQ: ${url}")
                    ZInfoRecorder.e("COST-FNErr", "$responseCode: $responseMessage")
                    FishNetException(responseCode, responseMessage).throwOut()
                    return FishNetException(responseCode).makeResponseJson()
                }
                ZInfoRecorder.e("COST", "$responseCode : ${System.currentTimeMillis() - startSt}ms---SYNC REQ: ${url}")
                return ResponseJson(stream2Str(inputStream))
            }
        }catch (ex: Exception) {
//            ex.printStackTrace()
            return ex.makeResponseJson()
        }
    }

    override suspend fun retry() = syncRequest()
}