// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object DecryptFile {

    //decrypt voice file
    fun decryptFile(context: Context, file: String, finished: (String) -> Unit) {
        val f = File(file)
        val tmpFile = File(context.cacheDir, "t.tmp")
        tmpFile.parentFile?.mkdirs()
        try {
            tmpFile.createNewFile()
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            var mEncFile = EncryptedFile.Builder(
                context,
                f,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            Thread(Runnable {
                try {
                    val tmpFos = FileOutputStream(tmpFile)
                    val fis: FileInputStream = mEncFile.openFileInput()
                    var readCnt: Int
                    val buffer = ByteArray(1024 * 32)
                    while (true) {
                        readCnt = fis.read(buffer)
                        if (readCnt <= -1) {
                            break
                        }
                        tmpFos.write(buffer, 0, readCnt)
                    }
                    tmpFos.close()
                    fis.close()
                    Handler(Looper.getMainLooper()).post { finished(tmpFile.absolutePath) }
                } catch (e: Exception) {
//                    e.printStackTrace()
                }
            }).start()
        } catch (e: Exception) {
//            e.printStackTrace()
        }
    }

    fun deleteTMPFile(context: Context) {
        File(context.cacheDir, "tvf.tmp").delete()
        File(context.cacheDir, "imf.tmp").delete()
    }

    fun getEncryptFile(context: Context, file: File): EncryptedFile {
        file.parentFile?.mkdirs()
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        return encryptedFile
    }

    fun syncDecryptCommonFile(context: Context, file: File): File {
        val tmpFile = File(context.cacheDir, "imf.tmp")
        val f = file
        tmpFile.parentFile?.mkdirs()
        try {
            tmpFile.createNewFile()
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val mEncFile = EncryptedFile.Builder(
                context,
                f,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            val tmpFos = FileOutputStream(tmpFile)
            val fis: FileInputStream = mEncFile.openFileInput()
            var readCnt: Int
            val buffer = ByteArray(1024 * 32)
            while (true) {
                readCnt = fis.read(buffer)
                if (readCnt <= -1) {
                    break
                }
                tmpFos.write(buffer, 0, readCnt)
            }
            tmpFos.close()
            fis.close()
        } catch (e: Exception) {
//            e.printStackTrace()
        }
        return tmpFile
    }


}