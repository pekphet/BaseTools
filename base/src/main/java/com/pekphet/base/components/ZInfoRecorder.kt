// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import android.content.Context
import android.os.Build
import android.os.Process
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter

object ZInfoRecorder {

    private var mLogLevel = LogLevel.Verbose
    private val DEFAULT_CRASH_LOG_PATH =
        "./"
    private var mCrashRecordEnable = false


    @JvmStatic
    fun init(level: LogLevel) {
        mLogLevel = level
    }

    @JvmStatic
    fun crashRecordEnable(enable: Boolean) {
        mCrashRecordEnable = enable
    }

    @JvmStatic
    fun v(tag: String, content: String?) {
//        if (mLogLevel.level <= LogLevel.Verbose.level)
//            Log.v(tag, content ?: "empty")
    }

    @JvmStatic
    fun d(tag: String, content: String?) {
//        if (mLogLevel.level <= LogLevel.Debug.level)
//            Log.d(tag, content ?: "empty")
    }

    @JvmStatic
    fun i(tag: String, content: String?) {
//        if (mLogLevel.level <= LogLevel.Info.level)
//            Log.i(tag, content ?: "empty")
    }

    @JvmStatic
    fun w(tag: String, content: String?) {
//        if (mLogLevel.level <= LogLevel.Warning.level)
//            Log.w(tag, content ?: "empty")
    }

    @JvmStatic
    fun e(tag: String, content: String?) {
        if (mLogLevel.level <= LogLevel.Error.level)
            Log.e(tag, content ?: "empty")
    }

    fun recordCrashLog(exception: Throwable): String {
        if (mCrashRecordEnable) {
            val file = File(DEFAULT_CRASH_LOG_PATH, "${getTimeYMMDDHHmmssWithUL()}.log")
            try {
                file.parentFile.mkdirs()
                val writer = StringWriter()
                val pw = PrintWriter(writer)
                pw.write("\nCrash Time:${getTimeYMMDDHHmmssWithSp()}")
                pw.write("\n**********Device Info**********\n")
                pw.write("ANDROID VERSION: ${Build.VERSION.SDK_INT}")
                pw.write("\n**********Exception Stack Trace**********\n")
                exception.printStackTrace(pw)
                pw.write("\n**********Cause By**********\n")
                val cause = exception.cause
                cause?.printStackTrace(pw)
                FileOutputStream(file).apply {
                    write(writer.toString().toByteArray())
                    flush()
                    close()
                }
            } catch (ex: Exception) {
                System.exit(0)
            }
            return file.absolutePath
        } else {
            return ""
        }
    }

    enum class LogLevel(val level: Int) {
        Verbose(1),
        Debug(2),
        Info(3),
        Warning(4),
        Error(5),
        Close(100)
    }

    class UncaughtExceptionHandlerImpl(val ctx: Context, path: String = DEFAULT_CRASH_LOG_PATH) :
        Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            recordCrashLog(e)
            Process.killProcess(Process.myPid())
            System.exit(0)
            throw e
//            Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(t, e)
        }
    }
}