// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.net.ex

import android.content.Context
import android.net.Uri
import com.pekphet.base.event.EventManager
import com.pekphet.base.event.TipEvent
import com.pekphet.base.net.ResponseJson
import com.pekphet.base.net.exception.FishNetException
import com.pekphet.base.net.exception.throwOut
import com.pekphet.base.net.makeResponseJson
import com.pekphet.base.components.*
import java.io.File
import java.net.URLEncoder

class FileExRequester(url: String, method: ExRequestMethod, contentType: ContentType?) :
    IExRequester(url, method, contentType) {


    fun makeFileHeader(ctx: Context, fileUri: Uri, fileName: String, folderId: String, fileId: String? = null): FileExRequester {
        val fileNameFixed = URLEncoder.encode(fileName, Charsets.UTF_8.toString())
//        val fileNameFixed = fileName
        if (fileId == null) {
            val sha256 = fileUri.getSHA256(ctx) ?: return this
            val headerVal = XFileHeaderUpload(fileNameFixed, sha256, fileUri.getFileSize(ctx), XFileParent(folderId))
            addHeader("x-file-metadata", headerVal.toJson())
        } else {
            val sha256 = fileUri.getSHA256(ctx) ?: return this
            val headerVal = XFileHeaderUpdate(fileNameFixed, sha256, fileUri.getFileSize(ctx), XFileParent(folderId), fileId)
            addHeader("x-file-metadata", headerVal.toJson())
        }
        return this
    }

    suspend fun uploadFile(ctx: Context, fileUri: Uri, fileName: String, progressCallback: (Float) -> Unit = { _ -> }): ResponseJson {
        val inputStream = ctx.contentResolver.openInputStream(fileUri)!!
        val fileNameFixed = URLEncoder.encode(fileName, Charsets.UTF_8.toString())
        ZInfoRecorder.e("UDS_UPLOAD", "META:URI:$fileUri, NAME:$fileName, URL:$mUrl")
//        val fileNameFixed = fileName
        mUrl += "?fileName=$fileNameFixed"
        try {
            getConnection().apply {
                val fileSize = fileUri.getFileSize(ctx)
                val os = outputStream
                os.write("--$BOUNDARY\r\n".toByteArray(Charsets.UTF_8))
                val header = "Content-Disposition: form-data; name=\"${fileNameFixed.removeSuffix()}\"; filename=\"$fileNameFixed\"; fileName=\"$fileNameFixed\"\r\n"
                ZInfoRecorder.i("File-UPLOAD", header)
                os.write(header.toByteArray(Charsets.UTF_8))
                os.write("\r\n".toByteArray(Charsets.UTF_8))
                val buffer = ByteArray(128 * 1024)
                var cnt = 0
                var total = 0
                while (inputStream.read(buffer).also { cnt = it } > -1) {
                    os.write(buffer, 0, cnt)
                    total += cnt
                    if (fileSize > 0)
                        progressCallback(1f * total / fileSize)
                }
                os.write("\r\n".toByteArray(Charsets.UTF_8))
                os.write("--$BOUNDARY--\r\n".toByteArray(Charsets.UTF_8))
                os.flush()
                os.close()
                ZInfoRecorder.e("resp errmsg", responseMessage)
                if (responseCode / 100 != 2) {
                    ZInfoRecorder.e("File-UPLOAD", "RESP ERR: $responseCode")
                    FishNetException(responseCode).throwOut()
                    return FishNetException(responseCode).makeResponseJson()
                }
                return ResponseJson(stream2Str(this.inputStream))
            }
        } catch (ex: Exception) {
//            ex.printStackTrace()
            EventManager.postEvent(TipEvent.SvcError)
            return ex.makeResponseJson()
        } finally {
            inputStream.close()
        }
    }

    /**
     * @see 下载uds文件到临时文件
     * @return File: 临时文件
     * @return NULL: 文件下载失败
     */
    suspend fun getTempFile(ctx: Context, fileName: String, progressCallback: (Float) -> Unit = { _ -> }): File? {
        val tmpFile = File(ctx.cacheDir, "download_tmp/$fileName")
        tmpFile.generateNewFile()
        try {
            getConnection().apply {
                if (responseCode / 100 != 2) {
                    ZInfoRecorder.e("getTempFile->resp", "respcode: $responseCode")
                    try {
                        FishNetException(responseCode, responseMessage).throwOut()
                    } catch (ex: Exception) {
                        FishNetException(responseCode).throwOut()
                    }
                    return null
                } else {
                    ZInfoRecorder.e("getTempFile->get_file_resp_header", headerFields.toJson())
                    val fileSize = (headerFields.get("content-length")?.firstOrNull() ?: "0").toInt()
                    val buffer = ByteArray(128 * 1024)
                    var cnt = 0
                    var total = 0
                    val os = tmpFile.outputStream()
                    while (inputStream.read(buffer).also { cnt = it } > -1) {
                        total += cnt
                        os.write(buffer, 0, cnt)
                        if (fileSize != 0)
                            progressCallback(1F * total / fileSize)
                    }
                    safe { os.close() }
                    ZInfoRecorder.e("getTempFile->get_file_OK", "size: ${tmpFile.length()}")
                }
                return tmpFile
            }
        } catch (ex: Exception) {
//            ex.printStackTrace()
            return null
        }
    }

    fun String.removeSuffix(): String {
        if (startsWith(".")) return this
        return if (contains('.'))
            subSequence(0, lastIndexOf('.')).toString()
        else
            this
    }

    @Deprecated("Not supported function")
    override suspend fun syncRequest(): ResponseJson {
        throw RuntimeException("Not supported function")
    }

    @Deprecated("Not supported function")
    override suspend fun retry() = syncRequest()

    data class XFileHeaderUpload(val file_name: String, val hash: String, val size_in_bytes: Long, val parent: XFileParent, val partition_id: String = "eink_tablet_partition")
    data class XFileHeaderUpdate(val file_name: String, val hash: String, val size_in_bytes: Long, val parent: XFileParent, val resource_id: String, val partition_id: String = "eink_tablet_partition")
    data class XFileParent(val id: String)
}

fun File.generateNewFile() {
    if (!parentFile!!.exists()) {
        parentFile?.mkdirs()
    }
    if (exists()) {
        delete()
    }
    createNewFile()
}