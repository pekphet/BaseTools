// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.*

fun Long.toHMS(): String {
    val sec = this / 1000
    if (sec > 60 * 60) {
        return String.format("%02d:%02d:%02d", sec / 60 / 60, (sec / 60) % 60, sec % 60)
    } else {
        return String.format("%02d:%02d", sec / 60, sec % 60)
    }
}

fun Int.toHMS(): String {
    val sec = this / 1000
    if (sec > 60 * 60) {
        return String.format("%02d:%02d:%02d", sec / 60 / 60, (sec / 60) % 60, sec % 60)
    } else {
        return String.format("%02d:%02d", sec / 60, sec % 60)
    }
}

fun Long.toYMD() = SimpleDateFormat("yyyy/MM/dd").format(this)

fun Int.SecToHMS(): String {
    if (this > 60 * 60) {
        return String.format("%02d:%02d:%02d", this / 60 / 60, (this / 60) % 60, this % 60)
    } else {
        return String.format("%02d:%02d", this / 60, this % 60)
    }
}

fun getTimeYMMDDHHmm() = SimpleDateFormat("yyyyMMddHHmm").format(Date())

fun getTimeYMMDDHHmmss() = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
fun getTimeYMMDDHHmmssWithSp() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
fun getTimeYMMDDHHmmssWithUL() = SimpleDateFormat("yyyyMMdd-HH:mm:ss").format(Date())

/**
 * TZ time format to TimeStamp
 * e.g.: 2022-04-06T08:32:25.328Z
 */
//fun TZtoTs(tzTimeL: String) = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(tzTimeL.substring(0, tzTimeL.length - 4)).time
//fun TZtoTs(tzTimeL: String) = try {
//    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'").parse(tzTimeL)?.time
//} catch (ex: Exception) {
////    ex.printStackTrace()
//    System.currentTimeMillis()
//}

@SuppressLint("HardwareIds")
fun Context.deviceId() = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    ?: Build.ID

private val SIZE_CHANGE_VALUE = 0.9F
fun Long.B2KMG(): String {
    if (this / 1000F < SIZE_CHANGE_VALUE) return String.format("${this}B")
    else if (this / 1000F / 1000F < SIZE_CHANGE_VALUE) return String.format("%.2fK", this / 1000f)
    else if (this / 1000 / 1000 / 1000F < SIZE_CHANGE_VALUE) return String.format("%.2fM", this / 1000 / 1000F)
    else return String.format("%.2fG", this / 1000 / 1000 / 1000F)
}

fun Long.B2RKMG(): String {
    if (this / 1024F < SIZE_CHANGE_VALUE) return String.format("${this} B")
    else if (this / 1024F / 1024F < SIZE_CHANGE_VALUE) return String.format("%.2f K", this / 1024F)
    else if (this / 1024F / 1024F / 1024F < SIZE_CHANGE_VALUE) return String.format("%.2f M", this / 1024 / 1024F)
    else return String.format("%.2f G", this / 1024 / 1024 / 1024F)
}


fun Context.toSystemHome() {
    val homeIntent = Intent(Intent.ACTION_MAIN)
    homeIntent.addCategory(Intent.CATEGORY_HOME)
    startActivity(homeIntent)
}

fun gotoFloatViewPermission(ctx: Activity, requestCode: Int) {
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    intent.setData(Uri.parse("package:${ctx.packageName}"))
    ctx.startActivityForResult(intent, requestCode)
}
