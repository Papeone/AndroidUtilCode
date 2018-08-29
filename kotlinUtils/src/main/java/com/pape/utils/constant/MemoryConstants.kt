package com.pape.utils.constant

import android.support.annotation.IntDef

/**
 * 类描述:The constants of memory.
 * @author chenzebang
 * @version v1.0
 * Date：2018/5/3 11:53
 */
object MemoryConstants {

    const val BYTE = 1
    const val KB = 1024
    const val MB = 1048576
    const val GB = 1073741824

    @IntDef(BYTE, KB, MB, GB)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Unit
}