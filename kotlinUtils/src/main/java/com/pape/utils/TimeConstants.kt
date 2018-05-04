package com.pape.utils

import android.support.annotation.IntDef

/**
 * 功能描述：
 * Created by Administrator on 2018/4/17.
 */
class TimeConstants {
    companion object TimeConstants {
        const val MSEC: Int = 1
        const val SEC: Int = 1000
        const val MIN: Int = 60000
        const val HOUR: Int = 3600000
        const val DAY: Int = 86400000
    }

    @IntDef(TimeConstants.MSEC, TimeConstants.SEC, TimeConstants.MIN, TimeConstants.HOUR, TimeConstants.DAY)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Unit
}


