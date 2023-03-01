// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import com.pekphet.base.Sample
import kotlin.reflect.KClass

fun Activity.goto(
    activityClz: Class<out Activity>,
    autoFinish: Boolean = false,
    flag: Int? = null,
    bundle: Bundle? = null
) {
    val intent = Intent(this, activityClz)
    if (flag != null) {
        intent.addFlags(flag)
    }
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    startActivity(intent)
    if (autoFinish) {
        finish()
    }
}

fun Activity.goto(activityKlz: KClass<out Activity>, autoFinish: Boolean = false, flag: Int? = null, bundle: Bundle? = null) =
    goto(activityKlz.java, autoFinish, flag, bundle)

fun Activity.gotoWithResult(
    activityClz: Class<out Activity>,
    reqCode: Int,
    flag: Int? = null,
    bundle: Bundle? = null
) {
    val intent = Intent(this, activityClz)
    if (flag != null) {
        intent.addFlags(flag)
    }
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    startActivityForResult(intent, reqCode)
}

fun Activity.gotoWithResult(activityKlz: KClass<out Activity>, reqCode: Int, flag: Int? = null, bundle: Bundle? = null) =
    gotoWithResult(activityKlz.java, reqCode, flag, bundle)


fun Context.getVersionName() = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA).versionName

/**
 * 定制化分享方法，做sample方法用
 */
@Sample
fun Activity.shareSingleFile(activity: Activity, fileUri: Uri) {
    ZInfoRecorder.e("share", fileUri.path)
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_STREAM, fileUri)
    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.type = activity.contentResolver.getType(fileUri)
    intent.putExtra(Intent.EXTRA_TITLE, "分享文件:${fileUri.getFileName(activity.applicationContext)}")
//    intent.putExtra(Intent.EXTRA_TEXT, "分享文件：1231231")
    activity.startActivity(Intent.createChooser(intent, "分享文件:${fileUri.getFileName(activity.applicationContext)}"))
}

fun Activity.restartTask() {
    val intent = packageManager.getLaunchIntentForPackage(packageName)?:return
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}