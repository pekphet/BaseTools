// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.view

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat

fun Activity.setLightStatusBar() = setStatusBar()

fun Activity.setDarkStatusBar() = setStatusBar(resources.getColor(android.R.color.background_dark, null), false)

private fun Activity.setStatusBar(color: Int = Color.WHITE, isLight: Boolean = true) {
    window.statusBarColor = color
    WindowCompat.setDecorFitsSystemWindows(window, true)
    ViewCompat.getWindowInsetsController(window.decorView)?.apply {
        isAppearanceLightStatusBars = isLight
    }
    if (isLight) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR  //实现状态栏文字颜色为暗色
    }
}

fun Activity.transluteStatusbar(isDarkStatusContent: Boolean) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.decorView.systemUiVisibility =View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.statusBarColor = Color.TRANSPARENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val controller = window.insetsController
        if (isDarkStatusContent) {
            controller?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            controller?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
        }
    }
}