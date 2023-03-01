// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.security.crypto.EncryptedFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class IOHandler(val threadName: String = "IO-HANDLER") {
    private val mHandlerThread: HandlerThread = HandlerThread(threadName)
    private lateinit var mIOHandler: Handler
    private var mFileOutputStream: FileOutputStream? = null
    private var mFileInputStream: FileInputStream? = null
    private lateinit var mFile: EncryptedFile

    fun initWriteFile(ctx: Context, file: File) {
        try {
            mFile = DecryptFile.getEncryptFile(ctx, file)
            mFileOutputStream = mFile.openFileOutput()
            mHandlerThread.start()
            mIOHandler = Handler(mHandlerThread.looper)
        } catch (e: Exception) {
//            e.printStackTrace()
        }
    }

    fun write(buffer: ByteArray, size: Int) {
        checkFile()
        mIOHandler.post {
            if (mFileOutputStream != null) {
                try {
                    mFileOutputStream!!.write(buffer, 0, size)
                    mFileOutputStream!!.flush()
                } catch (e: IOException) {
//                    e.printStackTrace()
                }
            }
        }
    }

    fun writeStr(str: String) {
        str.toByteArray().run {
            write(this, size)
        }
    }

    private fun checkFile() {
        if (!this::mFile.isInitialized) {
            throw RuntimeException("File is NOT Initialized!!")
        }
    }

    fun stopWrite() {
        checkFile()
        mIOHandler.post {
            if (mFileOutputStream != null) {
                try {
                    mFileOutputStream!!.close()
                } catch (e: IOException) {
//                    e.printStackTrace()
                } finally {
                    mFileOutputStream = null
                    releaseThread()
                }
            }
        }
    }

    private fun releaseThread() {
        Handler(Looper.getMainLooper()).postDelayed({ mHandlerThread.looper.quit() }, 1000)
    }

}
