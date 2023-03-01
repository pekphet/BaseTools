// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.view

import android.app.Activity
import android.content.Context
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.drawerlayout.widget.DrawerLayout
import com.pekphet.base.components.io
import com.pekphet.base.components.safe
import com.pekphet.base.components.ui
import kotlinx.coroutines.CoroutineScope

fun View.setClickEventOnce(onClick: (View) -> Unit) = setOnClickListener {
    onClick(it)
    setOnClickListener(null)
}

infix fun TextView.textOrEmptyHide(content: String?) {
    if (content.isNullOrEmpty()) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        text = content
    }
}

fun View.showDuration(keepTimeSec: Long) {
    visibility = View.VISIBLE
    postDelayed({
        visibility = View.GONE
    }, keepTimeSec)
}

//Load a view into this view group by layout id
fun ViewGroup.loadLayoutRes(@LayoutRes layoutId: Int) =
    LayoutInflater.from(context).inflate(layoutId, this)

//Load a view for return by layout id
fun ViewGroup.loadViewByLayoutRes(@LayoutRes layoutId: Int) =
    LayoutInflater.from(context).inflate(layoutId, this, false)

fun Activity.fullscreen() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
}

fun Context.toast(msg: String) {
    if (msg.isNotBlank()) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.toast(@StringRes msgId: Int) =
    safe { Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show() }

fun Context.getPXByDP(dp: Int) = resources.displayMetrics.density * dp

fun TextView.setDrawableLeft(@DrawableRes srcId: Int) =
    setCompoundDrawablesRelativeWithIntrinsicBounds(srcId, 0, 0, 0)

infix operator fun <T> T.invoke(block: T.() -> Unit) = apply(block)

infix suspend fun <T> T.suspendCall(block: suspend T.() -> Unit) = apply { this.block() }

fun View.showATime(
    keepTimeSec: Long = 1000,
    hideTimeSec: Long = keepTimeSec,
    showFirst: Boolean = true,
    repeat: (View) -> Boolean = { false }
): View {
    visibility = if (showFirst) View.VISIBLE else View.GONE
    postDelayed({
        visibility = if (showFirst) View.GONE else View.VISIBLE
        if (repeat(this))
            showATime(keepTimeSec, hideTimeSec, !showFirst, repeat)
    }, if (showFirst) keepTimeSec else hideTimeSec)
    return this
}

infix fun View.showOrHide(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}

infix fun View.showOrTransparent(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.INVISIBLE
}

fun DrawerLayout.open(gravity: Int = Gravity.LEFT) {
    if (!isDrawerOpen(gravity))
        openDrawer(gravity)
}

fun DrawerLayout.close(gravity: Int = Gravity.LEFT) {
    if (isDrawerOpen(gravity))
        closeDrawer(gravity)
}

fun DrawerLayout.addOnDrawerOpened(cb: ()->Unit) {
    addDrawerListener(object : DrawerLayout.DrawerListener {
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        }

        override fun onDrawerOpened(drawerView: View) {
            cb()
        }

        override fun onDrawerClosed(drawerView: View) {
        }

        override fun onDrawerStateChanged(newState: Int) {
        }
    })
}

/**
 * 带有全局延迟的点击（不推荐用）
 */
private const val CLICK_BLOCK_TIME = 300
private var mClickTime = 0L
infix fun View.clickBlockQuick(onClick: (View) -> Unit) = setOnClickListener {
    if (System.currentTimeMillis() - mClickTime > CLICK_BLOCK_TIME) {
        onClick(it)
    }
    mClickTime = System.currentTimeMillis()
}

private const val L_CLICK_BLOCK_TIME = 300L
infix fun View.setOnClickWithLock(onClick: (View) -> Unit) {
    setOnClickListener {
        isEnabled = false
        onClick(it)
        it.postDelayed({ isEnabled = true }, L_CLICK_BLOCK_TIME)
    }
}

fun View.setOnClickWithBlockingLock(scope: CoroutineScope, background: suspend () -> Unit, uiBlock: () -> Unit) {
    setOnClickListener {
        isEnabled = false
        scope.io {
            background()
            scope.ui {
                uiBlock()
                it.postDelayed({ isEnabled = true }, L_CLICK_BLOCK_TIME)
            }
        }
    }
}


fun ImageView.loadGif(res: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val src = ImageDecoder.createSource(context.resources, res)
        val drawable = ImageDecoder.decodeDrawable(src)
        (drawable as? AnimatedImageDrawable)?.start()
        setImageDrawable(drawable)

    } else {
        setImageResource(res)
    }
}
