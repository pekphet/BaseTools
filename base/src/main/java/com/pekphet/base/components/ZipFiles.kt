// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import android.content.Context
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipFiles {

    /**
     * @param ctx
     * @param zipName Zip file name
     * @param files  File:List Of files need compressed ; String:File's name
     * @return zip file
     */
    fun compress(ctx: Context, targetFile: String, files: MutableMap<String,File>): File {
        val zipFile = File(ctx.cacheDir, "$targetFile.zip")
        if (!zipFile.parentFile.exists()) zipFile.parentFile.mkdirs()
        if (zipFile.exists()) zipFile.delete()
        zipFile.createNewFile()
        val zipOS = ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile)))
        for( f in files){
            val fileInput = FileInputStream(f.value)
            val bufferedInput = BufferedInputStream(fileInput)
            val entry = ZipEntry(f.key)
            zipOS.putNextEntry(entry)
            bufferedInput.copyTo(zipOS,1024)
            bufferedInput.close()
        }
        zipOS.closeEntry()
        zipOS.close()
        return zipFile
    }

}