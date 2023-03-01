package com.pekphet.base.ui.dashboard

import com.pekphet.base.construction.BaseFragment
import com.pekphet.base.construction.vmBind
import com.pekphet.base.databinding.FragmentDashboardBinding

class DashboardFragment : BaseFragment<FragmentDashboardBinding, DashboardViewModel>() {

    override fun FragmentDashboardBinding.initView() {
    }

    override fun FragmentDashboardBinding.initBindVM() {
        textDashboard.vmBind(this@DashboardFragment, vm.text)
    }

    override fun initData() {
    }
}