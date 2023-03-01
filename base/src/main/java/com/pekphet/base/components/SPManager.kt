// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringDef
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object LocalStorageManager {

    const val TYPE_SETTINGS = "settings"
    const val TYPE_APPLICATION = "application"
    const val TYPE_FILE_DATA = "f_data"
    const val TYPE_FOLDER_DATA = "folder_data"

    const val TYPE_TRANS_DATA = "t_data"

    const val _EMPTY_JSON_ARRAY_ = "[]"
    const val _EMPTY_JSON_ = "{}"
    const val _EMPTY_STR_ = ""

    fun putJson(
        context: Context,
        @LocalStorageType type: String,
        key: String,
        jsonContent: String
    ) {
        getSP(context, type).edit().putString(key, jsonContent).apply()
    }

    fun putString(
        context: Context,
        @LocalStorageType type: String,
        key: String,
        content: String
    ) {
        getSP(context, type).edit().putString(key, content).apply()
    }

    fun removeKey(context: Context, @LocalStorageType type: String, key: String) {
        getSP(context, type).edit().remove(key).apply()
    }

    fun putInt(context: Context, @LocalStorageType type: String, key: String, value: Int) {
        getSP(context, type).edit().putInt(key, value).apply()
    }

    fun putBoolean(context: Context, @LocalStorageType type: String, key: String, value: Boolean) {
        getSP(context, type).edit().putBoolean(key, value).apply()
    }

    fun getJsonOrEmpty(context: Context, @LocalStorageType type: String, key: String): String {
        return getSP(context, type).getString(key, _EMPTY_JSON_)!!
    }

    fun getJsonOrEmptyArray(context: Context, @LocalStorageType type: String, key: String): String {
        return getSP(context, type).getString(key, _EMPTY_JSON_ARRAY_)!!
    }

    fun getJsonOrNull(context: Context, @LocalStorageType type: String, key: String) =
        getSP(context, type).getString(key, null)

    fun getSpString(context: Context, @LocalStorageType type: String, key: String, default: String = _EMPTY_STR_) =
        getSP(context, type).getString(key, default) ?: default

    fun getSpBoolean(
        context: Context,
        @LocalStorageType type: String,
        key: String,
        default: Boolean = false
    ) =
        getSP(context, type).getBoolean(key, default)

    fun getSpInt(context: Context, @LocalStorageType type: String, key: String, default: Int = 0) =
        getSP(context, type).getInt(key, default)


    /**
     * Multiple insert in callback,
     * !!Needn't commit in callback
     */
    fun multiInsert(
        ctx: Context,
        @LocalStorageType type: String,
        callback: (SharedPreferences.Editor) -> Unit
    ) {
        getSP(ctx, type).edit().run {
            callback(this)
            apply()
        }
    }


    fun getSP(ctx: Context, @LocalStorageType type: String) = ctx.getSharedPreferences(type, Context.MODE_PRIVATE)

    fun getEncryptSP(ctx: Context, @LocalStorageType type: String): SharedPreferences {
        val masterKey = MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        return EncryptedSharedPreferences.create(
            ctx, type, masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

}

@StringDef(
    value = [
        LocalStorageManager.TYPE_SETTINGS,
        LocalStorageManager.TYPE_APPLICATION,
        LocalStorageManager.TYPE_FILE_DATA,
        LocalStorageManager.TYPE_FOLDER_DATA,
        LocalStorageManager.TYPE_TRANS_DATA,
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class LocalStorageType
