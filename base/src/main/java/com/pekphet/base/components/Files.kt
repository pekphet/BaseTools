// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toFile
import com.pekphet.base.components.ZInfoRecorder
import java.io.File
import java.security.MessageDigest
import kotlin.experimental.and

@Throws(Exception::class)
fun Uri.getFileName(ctx: Context): String = when (scheme) {
    ContentResolver.SCHEME_FILE -> toFile().name
    ContentResolver.SCHEME_CONTENT -> {
        val cursor = ctx.contentResolver.query(this, null, null, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val displayName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            cursor.close()
            displayName
        } ?: path?.split(File.pathSeparatorChar)?.last() ?: ""
    }
    else -> {
        path?.split(File.pathSeparatorChar)?.last() ?: ""
    }
}

fun Uri.getFileSize(ctx: Context): Long = when (scheme) {
    ContentResolver.SCHEME_FILE -> toFile().length()
    ContentResolver.SCHEME_CONTENT -> {
        val cursor = ctx.contentResolver.query(this, null, null, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val size = it.getLong(it.getColumnIndexOrThrow(OpenableColumns.SIZE))
            cursor.close()
            size
        } ?: -1L
    }
    else -> {
        -1L
    }
}

@Throws(Exception::class)
fun Uri.getSHA256(ctx: Context): String? {
    val inputStream = ctx.contentResolver.openInputStream(this) ?: return null
    val sha256 = MessageDigest.getInstance("SHA256")
    val buffer = ByteArray(128 * 1024)
    var cnt: Int
    while (inputStream.read(buffer).also { cnt = it } > -1) {
        sha256.update(buffer, 0, cnt)
    }
    val result = sha256.digest().map { String.format("%02x", it and 0xff.toByte()) }.reduce { acc, s -> "$acc$s" }
    ZInfoRecorder.e("upload sha256", result)
    return result
}


const val REGEX_GENERATE_COPY = " copy \\((\\d+)\\)"

fun generateFileName(fileNameList: List<String>, name: String): String {
    val childList = fileNameList.map { it.getSimpleName() }
    var oriName = name.getSimpleName()
    if (!childList.contains(oriName)) return name
    val checkRegex = Regex(REGEX_GENERATE_COPY)
    if (checkRegex.toPattern().matcher(oriName).find()) {
        val tmp = oriName.split(checkRegex)
        oriName = tmp[0]
    }
    val namePatterns = Regex("^$REGEX_GENERATE_COPY$").toPattern()
    val numberList = mutableListOf<Int>()
    childList.forEach {
        val matcher = namePatterns.matcher(it.replace(oriName, "").getSimpleName())
        if (matcher.find()) numberList.add((matcher.group(1) ?: "0").toInt())
    }
    if (numberList.isEmpty()) return name.replace(name.getSimpleName(), "${name.getSimpleName()} copy (1)")
    numberList.sortWith(Comparator { first, sec -> sec - first })
    val targetNum = numberList[0] + 1
    val generateName = name.replace(name.getSimpleName(), "$oriName copy ($targetNum)")
    ZInfoRecorder.e("GenerateName", generateName)
    return generateName
}

fun String.currentFileName() = split("/").last()

fun String.getSimpleName(): String {
    if (contains('.'))
        return substring(0, indexOfLast { it == '.' })
    else
        return this
}

fun String.getSuffix() = split(".").last()

fun String.inFolderPath(folderPath: String): Boolean? {
    val folder = File(folderPath)
    try {
        if (folder.isDirectory) {
            folder.listFiles()?.map {
                if (it.name == this) return true
            }
            return false
        }
        return null
    } catch (ex: Exception) {
//        ex.printStackTrace()
        return null
    }
}