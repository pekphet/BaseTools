// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.net.ex

import com.pekphet.base.components.ZInfoRecorder
import com.pekphet.base.components.toJson
import com.pekphet.base.net.ResponseJson
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager


abstract class IExRequester(
    private val url: String,
    val method: ExRequestMethod,
    val mContentType: ContentType? = null
) {
    protected val mHeader = mutableMapOf<String, String>()
    protected var mUrl: String = url

    fun addHeader(key: String, value: String) {
        if (mHeader.containsKey(key)) {
            mHeader[key] = "${mHeader[key]};$value"
        } else {
            setHeader(key, value)
        }
    }

    fun setHeader(key: String, value: String) {
        mHeader[key] = value
    }

    fun setUrlParam(urlParam: String) {
        mUrl = "$url?$urlParam".replace("??", "?")
    }

    fun setUrlParam(urlParam: MutableMap<String, String>) {
        setUrlParam(map2Param(urlParam))
    }

    protected suspend fun getConnection() =
        (URL(mUrl).openConnection() as HttpURLConnection).apply {
            ZInfoRecorder.e("open connection: $method", "$mUrl")
            if (mSSLContext != null && this is HttpsURLConnection) {
                sslSocketFactory = mSSLContext.socketFactory
            }
            connectTimeout = 3000
            readTimeout = 10000
            try {
                requestMethod = method.name
            } catch (ex: Exception) {
//                ex.printStackTrace()
            }
            if (mContentType != null) {
                setRequestProperty("Content-Type", mContentType.contentValue)
            }
            mHeader.map {
                setRequestProperty(it.key, it.value)
            }
            setRequestProperty("Charset", "UTF-8")
            setRequestProperty("Connection", "Keep-Alive")
            setRequestProperty("Accept", "*/*")
            setRequestProperty("Cache-Control", "no-cache")

            ZInfoRecorder.e("IEXRquester", "getConnect: $mUrl")
            ZInfoRecorder.e("IEXRquester", "getConnect: ${mHeader.toJson()}")
        }

    @Throws(Exception::class)
    abstract suspend fun syncRequest(): ResponseJson
    abstract suspend fun retry(): ResponseJson

    protected fun map2Param(param: MutableMap<String, String>) =
        param.map { "${it.key}=${URLEncoder.encode(it.value, Charsets.UTF_8.toString())}" }.reduce { ori, append -> "$ori&$append" }


    protected fun stream2Str(inputStream: InputStream): String {
        val reader = InputStreamReader(inputStream, "UTF-8")
        val buf = CharArray(1024)
        val sb = StringBuffer()
        var readCnt: Int
        do {
            readCnt = reader.read(buf)
            if (readCnt > 0)
                sb.append(buf, 0, readCnt)
        } while (readCnt >= 0)
        reader.close()
        ZInfoRecorder.e("stream2str", sb.toString())
        return sb.toString()
    }

    private val mSSLContext = SSLContext.getInstance("TLS").apply {
        init(null, arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(
                chian: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

            override fun checkServerTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }), SecureRandom())
    }
}

enum class ExRequestMethod {
    GET,
    POST,
    DELETE,
    HEAD,
    PATCH,
}

const val BOUNDARY = "fe65d6c01c6422f5"

enum class ContentType(val contentValue: String) {
    AppJson("application/json"),
    Form("application/x-www-form-urlencoded"),
    Multipart("multipart/form-data; boundary=$BOUNDARY"),
}
