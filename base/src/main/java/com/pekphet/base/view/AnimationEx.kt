// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.view

import android.animation.Animator
import android.animation.TimeInterpolator
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.LinearInterpolator

fun ViewPropertyAnimator.end(onEnd: () -> Unit): ViewPropertyAnimator {
    setListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            onEnd()
        }
    })
    return this
}

fun View.isAnimating() = animation?.hasStarted() == true || !isEnabled

fun View.fade(
    duration: Long,
    show: Boolean = visibility == View.GONE,
    startAlpha: Float = if (show) 0f else 1f,
    endAlpha: Float = if (show) 1f else 0f,
    interpolator: TimeInterpolator = LinearInterpolator(),
    onEnd: () -> Unit = {}
) {
    if (isAnimating()) {
        return
    }
    alpha = startAlpha
    visibility = View.VISIBLE
    animate()
        .alpha(endAlpha)
        .setDuration(duration)
        .setInterpolator(interpolator)
        .end {
            this@fade.visibility = if (show) View.VISIBLE else View.GONE
            this@fade.animation = null
            isEnabled = true
            onEnd()
        }.start()
    isEnabled = false
}

fun View.animateTo(
    tarX: Float = x,
    tarY: Float = y,
    interpolator: TimeInterpolator = LinearInterpolator(),
    dur: Long,
    end: () -> Unit = {}
) {
    if (isAnimating())
        return
    animate()
        .translationX(tarX - left)
        .translationY(tarY - top)
        .setDuration(dur)
        .setInterpolator(interpolator)
        .end {
            isEnabled = true
            animation = null
            end()
        }.start()
    isEnabled = false
}

fun View.rotate(
    degree: Int,
    dur: Long,
    isRepeat: Boolean = false,
    isRightRotate: Boolean = true,
    interpolator: TimeInterpolator = LinearInterpolator(),
    end: () -> Unit = {}
) {
    if (isAnimating())
        return
    animate()
        .rotationBy(degree.toFloat() * if (isRightRotate) 1 else -1)
        .setDuration(dur)
        .setInterpolator(interpolator)
        .end {
            isEnabled = true
            animation = null
            end()
            if (isRepeat)
                post {
                    rotate(degree, dur, isRepeat, isRightRotate, interpolator, end)
                }
        }.start()
    isEnabled = false
}