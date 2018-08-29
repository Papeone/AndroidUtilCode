package com.pape.utils

import java.io.Closeable
import java.io.IOException

/**
 * author：huangchen on 2018/8/28 19:16
 * email：huangchen@yonghui.cn
 * desc：utils about IO close
 *
 */

/**
 * Close the io stream.
 *
 * @param closeables closeables
 */
fun <T : Closeable> closeIO(vararg closeables: T?) {
    if (closeables.isEmpty()) return
    closeables.filterNotNull()
            .forEach {
                try {
                    it.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
}


/**
 * Close the io stream quietly.
 *
 * @param closeables closeables
 */
fun <T : Closeable> closeIOQuietly(vararg closeables: T?) {
    if (closeables.isEmpty()) return
    closeables.filterNotNull()
            .forEach {
                try {
                    it.close()
                } catch (ignored: IOException) {
                }
            }
}

