// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.construction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.pekphet.base.components.ZInfoRecorder
import com.pekphet.base.components.safe
import com.pekphet.base.construction.annotations.Binding
import com.pekphet.base.construction.annotations.VMBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

open class BaseFragment<T : ViewBinding, VM : BaseViewModel> : Fragment(), CoroutineScope by MainScope(), IContext {
    private var mViewBinding: T? = null
    protected val binding get() = mViewBinding!!

    protected val vm: VM by lazy {
        ViewModelProvider(requireActivity()).get(this::class.java.getAnnotation(VMBinding::class.java)!!.viewModelBinding.java) as VM
    }

    /**
     * @see 尽量避免使用这个ViewModel
     * @see 跨Activity使用这个ViewModel进行数据绑定和传递
     *
     */
    protected val gVM by lazy { requireActivity().globalViewModel<GlobalPagesViewModel>().value }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mViewBinding != null) {
            if (binding.root.parent == null) {
                binding.initBindVM()
                return binding.root
            } else {
                return null
            }
        } else {
            safe {
                val viewBindingKlz =
                    this::class.java.getAnnotation(Binding::class.java)!!.viewBinding
                mViewBinding = viewBindingKlz.java.getMethod("inflate", LayoutInflater::class.java)
                    .invoke(null, layoutInflater) as T
            }
            binding.initView()
            binding.initBindVM()
            initData()
        }
        return binding.root
    }

    /**
     * 初始化布局，不要异步加载控件
     */
    open fun T.initView() {}

    /**
     *  初始化数据，异步加载在这里处理
     */
    open fun initData() {}

    /**
     * 绑定view model控件, 每次切换回来会走一遍绑定事件
     */
    open fun T.initBindVM() {}

    override fun getContext() = activity

    override fun onDestroy() {
        super.onDestroy()
        ZInfoRecorder.e("f_lc", "onDestroy")
        cancel()
        mViewBinding = null
    }
}