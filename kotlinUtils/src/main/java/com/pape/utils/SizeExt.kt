package com.pape.utils

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

/**
 * 功能描述：
 * Created by Administrator on 2018/5/4.
 */
object SizeExt {
    private val scale: Float by lazy { Utils.getApp()?.resources?.displayMetrics?.density ?: 0f }
    private val fontScale: Float by lazy { Utils.getApp()?.resources?.displayMetrics?.scaledDensity ?: 0f }

    fun dp2px(dpValue: Float): Int = (dpValue * scale + 0.5f).toInt()

    fun px2dp(pxValue: Float): Int = (pxValue / scale + 0.5f).toInt()

    fun sp2px(dpValue: Float): Int = (dpValue * fontScale + 0.5f).toInt()

    fun px2sp(pxValue: Float): Int = (pxValue / fontScale + 0.5f).toInt()

    fun applyDimension(value: Float, unit: Int): Float {
        val metrics = Utils.getApp()?.resources?.displayMetrics ?: return 0f
        return when (unit) {
            TypedValue.COMPLEX_UNIT_PX -> value
            TypedValue.COMPLEX_UNIT_DIP -> value * metrics.density
            TypedValue.COMPLEX_UNIT_SP -> value * metrics.scaledDensity
            TypedValue.COMPLEX_UNIT_PT -> value * metrics.xdpi * (1.0f / 72)
            TypedValue.COMPLEX_UNIT_IN -> value * metrics.xdpi
            TypedValue.COMPLEX_UNIT_MM -> value * metrics.xdpi * (1.0f / 25.4f)
            else -> 0f
        }
    }

    fun forceGetViewSize(view: View, onGetSize: (params: View) -> Unit) {
        view.post({ onGetSize(view) })
    }

    fun getMeasuredWidth(view: View) = measureView(view)[0]

    fun getMeasuredHeight(view: View) = measureView(view)[1]

    fun measureView(view: View): IntArray {
        val lp = view.layoutParams ?: ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        val widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width)
        val heightSpec = if (lp.height > 0) {
            View.MeasureSpec.makeMeasureSpec(lp.height, View.MeasureSpec.EXACTLY);
        } else {
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(widthSpec, heightSpec)
        return intArrayOf(view.measuredWidth, view.measuredHeight)
    }
}
