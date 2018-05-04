package com.pape.utils

import java.io.Closeable
import java.io.IOException

/**
 * 类描述:
 * @author chenzebang@kungeek.com
 * @version v3.2
 * Date：2018/5/3 15:15
 */
class CloseUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        /**
         * Close the io stream.
         *
         * @param closeables closeables
         */
        fun closeIO(vararg closeables: Closeable) {
            closeables
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
        fun closeIOQuietly(vararg closeables: Closeable) {
            closeables
                    .forEach {
                        try {
                            it.close()
                        } catch (ignored: IOException) {
                        }
                    }
        }
    }
}