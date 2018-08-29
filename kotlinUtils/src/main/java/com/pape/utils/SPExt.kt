package com.pape.utils

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.util.SimpleArrayMap

/**
 * 功能描述：
 * Created by Administrator on 2018/5/4.
 */

class SPExt private constructor(spName: String? = "spUtils") {
    val sp: SharedPreferences? = Utils.getApp()?.getSharedPreferences(spName, Context.MODE_PRIVATE)

    companion object {
        private val SP_UTILS_MAP: SimpleArrayMap<String, SPExt> = SimpleArrayMap()

        fun getInstance(spName: String? = ""): SPExt? {
            val name = if (spName == null || spName.isEmpty()) "spUtils" else spName
            var spUtils: SPExt? = SP_UTILS_MAP.get(name)
            if (spUtils == null) {
                spUtils = SPExt(name)
                SP_UTILS_MAP.put(name, spUtils)
            }
            return spUtils
        }
    }


    fun put(key: String, value: Any, isCommit: Boolean = false) {
        val editor: SharedPreferences.Editor? = sp?.edit()
        when (value) {
            is String -> editor?.putString(key, value)
            is Int -> editor?.putInt(key, value)
            is Long -> editor?.putLong(key, value)
            is Float -> editor?.putFloat(key, value)
            is Boolean -> editor?.putBoolean(key, value)
            else -> return
        }
        if (isCommit) editor?.commit() else editor?.apply()
    }

    fun put(key: String, value: Set<String>, isCommit: Boolean = false) {
        if (isCommit) sp?.edit()?.putStringSet(key, value)?.commit()
        else sp?.edit()?.putStringSet(key, value)?.apply()
    }

    fun getString(key: String, defValue: String = ""): String? = sp?.getString(key, defValue)

    fun getInt(key: String, defValue: Int = -1): Int? = sp?.getInt(key, defValue)

    fun getLong(key: String, defValue: Long = -1L): Long? = sp?.getLong(key, defValue)

    fun getFloat(key: String, defValue: Float = -1f): Float? = sp?.getFloat(key, defValue)

    fun getBoolean(key: String, defValue: Boolean = false): Boolean? = sp?.getBoolean(key, defValue)

    fun getStringSet(key: String, defValue: MutableSet<String> = mutableSetOf()): Set<String>? = sp?.getStringSet(key, defValue)

    fun getAll(): MutableMap<String, *>? = sp?.all

    operator fun contains(key: String) = sp?.contains(key) ?: false

    fun remove(key: String, isCommit: Boolean = false) {
        if (isCommit) sp?.edit()?.remove(key)?.commit()
        else sp?.edit()?.remove(key)?.apply()
    }

    fun clear(isCommit: Boolean = false) {
        if (isCommit) sp?.edit()?.clear()?.commit()
        else sp?.edit()?.clear()?.apply()
    }
}
