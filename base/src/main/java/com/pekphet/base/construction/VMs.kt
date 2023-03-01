// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.construction

import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.pekphet.base.components.ZInfoRecorder

/**
 * @see     ViewModel用法
 * Use:     [BaseFragment] + [BaseViewModel]
 * Sample:  [SampleBaseFragment] + [SampleBaseViewModel]
 *
 * @see     [BaseActivity]持有[ViewModel]方式:
 *          1.此Activity内传递
 * @doc     [ViewModelProvider.get] ViewModelProvider(this@BaseActivity).get(VM::class.java)
 * @doc     [ViewModelStoreOwner.VM] 扩展方法直接拿VM
 *
 *          2.App全局传递（需处理Activity销毁时的控件绑定）
 * @doc     [globalViewModel]  直接用BaseActivity获取
 * @see     推荐使用[GlobalPagesViewModel]统一处理 (BaseFragment内置gVM为此对象)
 *
 * @see     局部传递和全局传递的ViewModel尽量分离使用,不要使用同一个ViewModel
 */
interface VMDoc

/**
 * [BaseActivity] [BaseFragment] [ViewModelStoreOwner]直接获取ViewModel
 */
inline fun <reified VM : BaseViewModel> ViewModelStoreOwner.VM(): VM {
    return ViewModelProvider(this)[VM::class.java]
}

/**
 * Lazy Function of [ViewModelStoreOwner.VM]
 */
inline fun <reified VM : BaseViewModel> ViewModelStoreOwner.LazyVM(): Lazy<VM> {
    return lazy{ ViewModelProvider(this)[VM::class.java] }
}

/**
 * 获取全局ViewModel
 */
inline fun <reified VM : ViewModel> ComponentActivity.globalViewModel(): Lazy<VM> {
    val factoryPromize = { ViewModelProvider.AndroidViewModelFactory.getInstance(application) }
    return ViewModelLazy(VM::class, { globalViewModelStore }, factoryPromize)
}

/**
 * Default Function of [ComponentActivity.globalViewModel]
 */
fun ComponentActivity.defaultGlobalVM() = globalViewModel<GlobalPagesViewModel>()

val globalViewModelStore by lazy { ViewModelStore() }


fun TextView.vmBind(owner: LifecycleOwner, data: LiveData<String>) {
    data.observe(owner) {
        text = it
        ZInfoRecorder.e("MID_CHG", "change to $it")
    }
}

fun <T : Any?> LiveData<T>.bind(owner: LifecycleOwner, observer: Observer<in T>) {
    observe(owner, observer)
}

fun <T : Any?> LiveData<T>.unbind(observer: Observer<in T>) {
    removeObserver(observer)
}

fun clearOwnerBinds(data: LiveData<*>, owner: LifecycleOwner) {
    data.removeObservers(owner)
}

