package com.pape.utils

import android.os.Build
import android.support.v4.util.LongSparseArray
import android.support.v4.util.SimpleArrayMap
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import android.util.SparseLongArray

/**
 * 功能描述：
 * Created by Administrator on 2018/5/4.
 */
object ObjectExt {

    fun isEmpty(obj: Any?): Boolean {
        return when (obj) {
            null -> false
            is CharSequence -> obj.toString().isEmpty()
            is Array<*> -> obj.isEmpty()
            is Collection<*> -> obj.isEmpty()
            is Map<*, *> -> obj.isEmpty()
            is SimpleArrayMap<*, *> -> obj.isEmpty
            is SparseArray<*> -> obj.size() == 0
            is SparseBooleanArray -> obj.size() == 0
            is SparseIntArray -> obj.size() == 0
            is SparseLongArray -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                obj.size() == 0
            } else {
                return false
            }
            is LongSparseArray<*> -> obj.size() == 0
            else -> false
        }
    }

    fun isNotEmpty(obj: Any?): Boolean = !isEmpty(obj)

    fun equals(obj1: Any?, obj2: Any?): Boolean {
        return obj1 == obj2
    }

    fun <T> requireNonNull(obj: T, message: String): T {
        return obj ?: throw NullPointerException(message)
    }

    fun <T> getOrDefault(obj: T, defaultObj: T): T = obj ?: defaultObj

    fun hashCode(o: Any?): Int {
        return o?.hashCode() ?: 0
    }
}