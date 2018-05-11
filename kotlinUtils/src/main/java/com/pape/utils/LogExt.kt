package com.pape.utils

import android.annotation.SuppressLint
import android.os.Environment
import android.support.annotation.IntDef
import android.support.annotation.IntRange
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorService

/**
 * 功能描述：
 * Created by Administrator on 2018/5/7.
 */
object LogExt {
    const val V = Log.VERBOSE
    const val D = Log.DEBUG
    const val I = Log.INFO
    const val W = Log.WARN
    const val E = Log.ERROR
    const val A = Log.ASSERT

    @IntDef(value = *[V, D, I, W, E, A])
    @Retention(AnnotationRetention.SOURCE)
    annotation class TYPE

    private val T = charArrayOf('V', 'D', 'I', 'W', 'E', 'A')

    private val FILE = 0x10
    private val JSON = 0x20
    private val XML = 0x30

    private val FILE_SEP = System.getProperty("file.separator")
    private val LINE_SEP = System.getProperty("line.separator")
    private val TOP_CORNER = "┌"
    private val MIDDLE_CORNER = "├"
    private val LEFT_BORDER = "│ "
    private val BOTTOM_CORNER = "└"
    private val SIDE_DIVIDER = "────────────────────────────────────────────────────────"
    private val MIDDLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
    private val TOP_BORDER = TOP_CORNER + SIDE_DIVIDER + SIDE_DIVIDER
    private val MIDDLE_BORDER = MIDDLE_CORNER + MIDDLE_DIVIDER + MIDDLE_DIVIDER
    private val BOTTOM_BORDER = BOTTOM_CORNER + SIDE_DIVIDER + SIDE_DIVIDER
    private val MAX_LEN = 3000
    @SuppressLint("SimpleDateFormat")
    private val FORMAT = SimpleDateFormat("MM-dd HH:mm:ss.SSS ")
    private val NOTHING = "log nothing"
    private val NULL = "null"
    private val ARGS = "args"
    private val PLACEHOLDER = " "
    private val CONFIG = Config()
    private var sExecutor: ExecutorService? = null

    fun v(contents: Any) {

    }

    fun vTag(tag: String, contents: Any) {

    }

    private fun log(type: Int, tag: String, contents: Any) {
    }

    private fun input2File(input: String, filePath: String) {

    }


    class TagHead(var tag: String, var consoleHead: Array<String>, var fileHead: String)

    class Config {
        private var mDefaultDir: String? = null// The default storage directory of log.
        private var mDir: String? = null       // The storage directory of log.
        private var mFilePrefix = "util"// The file prefix of log.
        private var mLogSwitch = true  // The switch of log.
        private var mLog2ConsoleSwitch = true  // The logcat's switch of log.
        private var mGlobalTag: String? = null  // The global tag of log.
        private var mTagIsSpace = true  // The global tag is space.
        private var mLogHeadSwitch = true  // The head's switch of log.
        private var mLog2FileSwitch = false // The file's switch of log.
        private var mLogBorderSwitch = true  // The border's switch of log.
        private var mSingleTagSwitch = true  // The single tag of log.
        private var mConsoleFilter = V     // The console's filter of log.
        private var mFileFilter = V     // The file's filter of log.
        private var mStackDeep = 1     // The stack's deep of log.

        internal constructor() {
            if (mDefaultDir != null) return
            mDefaultDir = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                    && Utils.getApp()?.externalCacheDir != null)
                Utils.getApp()?.externalCacheDir.toString() + FILE_SEP + "log" + FILE_SEP
            else {
                Utils.getApp()?.cacheDir.toString() + FILE_SEP + "log" + FILE_SEP
            }
        }

        fun setLogSwitch(logSwitch: Boolean): Config {
            mLogSwitch = logSwitch
            return this
        }

        fun setConsoleSwitch(consoleSwitch: Boolean): Config {
            mLog2ConsoleSwitch = consoleSwitch
            return this
        }

        fun setGlobalTag(tag: String): Config {
            if (tag.isEmpty()) {
                mGlobalTag = ""
                mTagIsSpace = true
            } else {
                mGlobalTag = tag
                mTagIsSpace = false
            }
            return this
        }

        fun setLogHeadSwitch(logHeadSwitch: Boolean): Config {
            mLogHeadSwitch = logHeadSwitch
            return this
        }

        fun setLog2FileSwitch(log2FileSwitch: Boolean): Config {
            mLog2FileSwitch = log2FileSwitch
            return this
        }

        fun setDir(dir: String): Config {
            mDir = if (dir.isEmpty()) {
                null
            } else {
                if (dir.endsWith(FILE_SEP)) dir else dir + FILE_SEP
            }
            return this
        }

        fun setDir(dir: File?): Config {
            mDir = if (dir == null) null else dir.absolutePath + FILE_SEP
            return this
        }

        fun setFilePrefix(filePrefix: String): Config {
            mFilePrefix = if (filePrefix.isEmpty()) {
                "util"
            } else {
                filePrefix
            }
            return this
        }

        fun setBorderSwitch(borderSwitch: Boolean): Config {
            mLogBorderSwitch = borderSwitch
            return this
        }

        fun setSingleTagSwitch(singleTagSwitch: Boolean): Config {
            mSingleTagSwitch = singleTagSwitch
            return this
        }

        fun setConsoleFilter(@TYPE consoleFilter: Int): Config {
            mConsoleFilter = consoleFilter
            return this
        }

        fun setFileFilter(@TYPE fileFilter: Int): Config {
            mFileFilter = fileFilter
            return this
        }

        fun setStackDeep(@IntRange(from = 1) stackDeep: Int): Config {
            mStackDeep = stackDeep
            return this
        }

        override fun toString(): String {
            return ("switch: " + mLogSwitch
                    + LINE_SEP + "console: " + mLog2ConsoleSwitch
                    + LINE_SEP + "tag: " + (if (mTagIsSpace) "null" else mGlobalTag)
                    + LINE_SEP + "head: " + mLogHeadSwitch
                    + LINE_SEP + "file: " + mLog2FileSwitch
                    + LINE_SEP + "dir: " + (if (mDir == null) mDefaultDir else mDir)
                    + LINE_SEP + "filePrefix: " + mFilePrefix
                    + LINE_SEP + "border: " + mLogBorderSwitch
                    + LINE_SEP + "singleTag: " + mSingleTagSwitch
                    + LINE_SEP + "consoleFilter: " + T[mConsoleFilter - V]
                    + LINE_SEP + "fileFilter: " + T[mFileFilter - V]
                    + LINE_SEP + "stackDeep: " + mStackDeep)
        }

    }
}