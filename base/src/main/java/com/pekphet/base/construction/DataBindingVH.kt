// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.construction

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding


fun <DATA, V : ViewBinding> V.createVH(load: (V, DATA, Int) -> Unit) = object : DataBindingVH<DATA, V>(this) {
    override fun load(binding: V, data: DATA, position: Int) {
        load(binding, data, position)
    }
}

open class DataBindingVH<DATA, V : ViewBinding>(val binding: V) : ViewHolder(binding.root) {
    open fun load(binding: V, data: DATA, position: Int){}

}