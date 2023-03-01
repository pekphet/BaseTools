// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.construction.sample

import com.pekphet.base.components.bg
import com.pekphet.base.construction.BaseFragment
import com.pekphet.base.construction.annotations.Binding
import com.pekphet.base.construction.annotations.VMBinding
import com.pekphet.base.construction.vmBind
import com.pekphet.base.databinding.FragmentSampleBinding
import kotlinx.coroutines.delay

@Binding(FragmentSampleBinding::class)
@VMBinding(SampleBaseViewModel::class)
class SampleBaseFragment : BaseFragment<FragmentSampleBinding, SampleBaseViewModel>() {

    override fun FragmentSampleBinding.initView() {
        tvTestMiddle.text = "init mid"
        tvTestBelow.text = "init below"
    }

    override fun FragmentSampleBinding.initBindVM() {
        tvTestMiddle.vmBind(this@SampleBaseFragment, vm.sample)
    }

    override fun initData() {
        bg {
            delay(2000)
            vm.changeSample("2000 yrs later")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}