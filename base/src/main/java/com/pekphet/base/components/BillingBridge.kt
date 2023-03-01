// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import android.app.Activity
import android.content.Context
import com.pekphet.base.components.ZInfoRecorder
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.functions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.typeOf


/**
 * It's used for GoogleBilling cross in different modules
 * Using KOTLIN Reflect Invoke
 */
@Deprecated("Can't Be Used")
object BillingBridge {

    private val mBillingWrapperKlz = Class.forName("com.lenovo.yogapaper.row.billing.BillingWrapper")
    private val mBillingWrapper by lazy { mBillingWrapperKlz.kotlin.objectInstance }

    private val mBillingCacheKlz = Class.forName("com.lenovo.yogapaper.row.billing.BillingCache")
    private val mBillingCache by lazy { mBillingCacheKlz.kotlin.objectInstance }

    /**
     * 初始化支付sdk
     * 拉取历史支付记录
     */
    fun init(context: Context) {
        KTReflector.refInvoke<Unit>(mBillingWrapper, "init", context)
    }

    /**
     *   连接到google pay的回调
     */
    fun setOnConnected(cb: (Boolean) -> Unit) {
        KTReflector.refInvoke<Unit>(mBillingWrapper, "setOnConnected", cb)
    }

    /**
     *   连接到google pay失败的回调
     */
    fun setOnDisConnected(cb: () -> Unit) {
        KTReflector.refInvoke<Unit>(mBillingWrapper, "setOnDisConnected", cb)
    }

    /**
     *   连接到google pay出错的回调
     */
    fun setOnErrorConnected(cb: (Int) -> Unit) {
        KTReflector.refInvoke<Unit>(mBillingWrapper, "setOnErrorConnected", cb)
    }

    /**
     * String -> Purchase
     * 购买成功回调
     */
    fun setOnSucceed(callback: (MutableList<String>) -> Unit) {
        KTReflector.refInvoke<Unit>(mBillingWrapper, "setOnSucceedWrapper", callback)
    }

    /**
     * 取消购买回调
     */
    fun setOnCanceled(callback: () -> Unit) {
        KTReflector.refInvoke<Unit>(mBillingWrapper, "setOnCanceled", callback)
    }

    /**
     * 查询订单回调
     */
    fun queryPurchases(scope: CoroutineScope, complete: () -> Unit) {
        KTReflector.refInvoke<Unit>(mBillingWrapper, "queryPurchases", scope, complete)
    }

    /**
     * 拉取商品列表
     */
    suspend fun loadProductsFromIDS(skuList: List<String>) {
        KTReflector.refSuspendInvoke<Unit>(mBillingWrapper, "loadProductsFromIDS", skuList)
    }

    fun launchBilling(activity: Activity, ID: String) {
        KTReflector.refInvoke<Unit>(mBillingWrapper, "launchByProductID", activity, ID)
    }

    /**
     * ID获取商品名称
     */
    fun getTitleById(id: String) = KTReflector.refInvoke<String>(mBillingCache, "getTitleById", id)

    /**
     * ID获取商品描述
     */
    fun getDescById(id: String) = KTReflector.refInvoke<String>(mBillingCache, "getDescById", id)

    /**
     * ID获取商品价格
     */
    fun getFormattedPrice(id: String) = KTReflector.refInvoke<String>(mBillingCache, "getFormattedPrice", id)

    /**
     * 核销
     */
    suspend fun dealPurchase(purchaseToken: String) = KTReflector.refSuspendInvoke<Unit>(mBillingWrapper, "dealPurchase", purchaseToken)

}

fun reflectIsPRCEnv(): Boolean {
    val appCfg = Class.forName("com.lenovo.lcui.einkclient.manager.AppConfig").kotlin.objectInstance ?: return true
    val isPrc = appCfg::class.functions.first { it.name == "isPRCEnv" }.call(appCfg) as? Boolean
    return isPrc != false
}

object KTReflector {
    val TAG = "KTReflector"
    inline fun <reified T> refInvoke(caller: Any?, methodName: String, vararg params: Any): T? {
        if (reflectIsPRCEnv()) return null
        if (caller == null) {
            throw RuntimeException("Caller is NULL!!!!")
        }
        val functions = caller::class.functions.filter { it.name == methodName }
        if (functions.isEmpty()) {
            throw RuntimeException("NO METHOD NAME")
        }
        if (functions[0].parameters.size - 1 != params.size) {
            ZInfoRecorder.e(TAG, "${functions[0].parameters.size}")
            ZInfoRecorder.e(TAG, "parameters size not right!")
            return null
        }
        if (functions[0].returnType != typeOf<T>()) {
            ZInfoRecorder.e(TAG, "result type not ${T::class::simpleName}")
        }
        ZInfoRecorder.e(TAG, "PARAMSIZE:${functions[0].parameters.size}")
        functions[0].valueParameters.forEach { ZInfoRecorder.e(TAG, "pre call: ${it.index}, param:${it.name}: ${it.type}, ${it.isVararg}") }
        functions[0].parameters.forEach { ZInfoRecorder.e(TAG, "pre call: ${it.index}, param:${it.name}: ${it.type}, ${it.isVararg}") }
        return functions[0].call(caller, *params) as? T
    }

    inline suspend fun <reified T> refSuspendInvoke(caller: Any?, methodName: String, vararg params: Any): T? {
        if (reflectIsPRCEnv()) return null
        if (caller == null) {
            throw RuntimeException("Caller is NULL!!!!")
        }
        val functions = caller::class.functions.filter { it.name == methodName }
        if (functions.isEmpty()) {
            throw RuntimeException("NO METHOD NAME")
        }
        if (functions[0].parameters.size - 1 != params.size) {
            ZInfoRecorder.e(TAG, "${functions[0].parameters.size}")
            ZInfoRecorder.e(TAG, "parameters size not right!")
            return null
        }
        if (functions[0].returnType != typeOf<T>()) {
            ZInfoRecorder.e(TAG, "result type not ${T::class::simpleName}")
        }
        ZInfoRecorder.e(TAG, "PARAMSIZE:${functions[0].parameters.size}")
        functions[0].valueParameters.forEach { ZInfoRecorder.e(TAG, "pre call: ${it.index}, param:${it.name}: ${it.type}, ${it.isVararg}") }
        functions[0].parameters.forEach { ZInfoRecorder.e(TAG, "pre call: ${it.index}, param:${it.name}: ${it.type}, ${it.isVararg}") }
        return functions[0].callSuspend(caller, *params) as? T
    }

}

data class StringListPack(val data: List<String>)
