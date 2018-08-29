package com.pape.utils

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.view.ViewCompat
import android.support.v4.widget.TextViewCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast

/**
 * author：huangchen on 2018/8/28 21:30
 * email：huangchen@yonghui.cn
 * desc  : utils about toast
 */
object ToastUtils {


    private const val COLOR_DEFAULT = -0x1000001
    private val HANDLER = Handler(Looper.getMainLooper())

    private var sToast: Toast? = null
    private var sGravity = -1
    private var sXOffset = -1
    private var sYOffset = -1
    private var sBgColor = COLOR_DEFAULT
    private var sBgResource = -1
    private var sMsgColor = COLOR_DEFAULT
    private var sMsgTextSize = -1

    /**
     * Set the gravity.
     *
     * @param gravity The gravity.
     * @param xOffset X-axis offset, in pixel.
     * @param yOffset Y-axis offset, in pixel.
     */
    fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        sGravity = gravity
        sXOffset = xOffset
        sYOffset = yOffset
    }

    /**
     * Set the color of background.
     *
     * @param backgroundColor The color of background.
     */
    fun setBgColor(@ColorInt backgroundColor: Int) {
        sBgColor = backgroundColor
    }

    /**
     * Set the resource of background.
     *
     * @param bgResource The resource of background.
     */
    fun setBgResource(@DrawableRes bgResource: Int) {
        sBgResource = bgResource
    }

    /**
     * Set the color of message.
     *
     * @param msgColor The color of message.
     */
    fun setMsgColor(@ColorInt msgColor: Int) {
        sMsgColor = msgColor
    }

    /**
     * Set the text size of message.
     *
     * @param textSize The text size of message.
     */
    fun setMsgTextSize(textSize: Int) {
        sMsgTextSize = textSize
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param text The text.
     */
    fun showShort(text: CharSequence) {
        show(text, Toast.LENGTH_SHORT)
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param resId The resource id for text.
     */
    fun showShort(@StringRes resId: Int) {
        show(resId, Toast.LENGTH_SHORT)
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param resId The resource id for text.
     * @param args  The args.
     */
    fun showShort(@StringRes resId: Int, vararg args: Any) {
        if (args.isEmpty()) {
            show(resId, Toast.LENGTH_SHORT)
        } else {
            show(resId, Toast.LENGTH_SHORT, *args)
        }
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param format The format.
     * @param args   The args.
     */
    fun showShort(format: String, vararg args: Any) {
        if (args.isEmpty()) {
            show(format, Toast.LENGTH_SHORT)
        } else {
            show(format, Toast.LENGTH_SHORT, *args)
        }
    }

    /**
     * Show the toast for a long period of time.
     *
     * @param text The text.
     */
    fun showLong(text: CharSequence) {
        show(text, Toast.LENGTH_LONG)
    }

    /**
     * Show the toast for a long period of time.
     *
     * @param resId The resource id for text.
     */
    fun showLong(@StringRes resId: Int) {
        show(resId, Toast.LENGTH_LONG)
    }

    /**
     * Show the toast for a long period of time.
     *
     * @param resId The resource id for text.
     * @param args  The args.
     */
    fun showLong(@StringRes resId: Int, vararg args: Any) {
        if (args.isEmpty()) {
            show(resId, Toast.LENGTH_SHORT)
        } else {
            show(resId, Toast.LENGTH_LONG, *args)
        }
    }

    /**
     * Show the toast for a long period of time.
     *
     * @param format The format.
     * @param args   The args.
     */
    fun showLong(format: String, vararg args: Any) {
        if (args.isEmpty()) {
            show(format, Toast.LENGTH_SHORT)
        } else {
            show(format, Toast.LENGTH_LONG, *args)
        }
    }

    /**
     * Show custom toast for a short period of time.
     *
     * @param layoutId ID for an XML layout resource to load.
     */
    fun showCustomShort(@LayoutRes layoutId: Int): View? {
        val view = getView(layoutId)
        show(view, Toast.LENGTH_SHORT)
        return view
    }

    /**
     * Show custom toast for a long period of time.
     *
     * @param layoutId ID for an XML layout resource to load.
     */
    fun showCustomLong(@LayoutRes layoutId: Int): View? {
        val view = getView(layoutId)
        show(view, Toast.LENGTH_LONG)
        return view
    }

    /**
     * Cancel the toast.
     */
    fun cancel() {
        if (sToast != null) {
            sToast!!.cancel()
            sToast = null
        }
    }

    private fun show(@StringRes resId: Int, duration: Int) {
        show(Utils.app.resources.getText(resId).toString(), duration)
    }

    private fun show(@StringRes resId: Int, duration: Int, vararg args: Any) {
        show(String.format(Utils.app.resources.getString(resId), *args), duration)
    }

    private fun show(format: String, duration: Int, vararg args: Any) {
        show(text = String.format(format, *args), duration = duration)
    }

    private fun show(text: CharSequence, duration: Int) {
        HANDLER.post {
            cancel()
            sToast = Toast.makeText(Utils.app, text, duration)
            val tvMessage = sToast!!.view.findViewById<TextView>(android.R.id.message)
            val msgColor = tvMessage.currentTextColor
            //it solve the font of toast
            TextViewCompat.setTextAppearance(tvMessage, android.R.style.TextAppearance)
            if (sMsgColor != COLOR_DEFAULT) {
                tvMessage.setTextColor(sMsgColor)
            } else {
                tvMessage.setTextColor(msgColor)
            }
            if (sMsgTextSize != -1) {
                tvMessage.textSize = sMsgTextSize.toFloat()
            }
            if (sGravity != -1 || sXOffset != -1 || sYOffset != -1) {
                sToast!!.setGravity(sGravity, sXOffset, sYOffset)
            }
            setBg(tvMessage)
            sToast!!.show()
        }
    }

    private fun show(view: View?, duration: Int) {
        HANDLER.post {
            cancel()
            sToast = Toast(Utils.app)
            sToast!!.view = view
            sToast!!.duration = duration
            if (sGravity != -1 || sXOffset != -1 || sYOffset != -1) {
                sToast!!.setGravity(sGravity, sXOffset, sYOffset)
            }
            setBg()
            sToast!!.show()
        }
    }

    private fun setBg() {
        val toastView = sToast!!.view
        if (sBgResource != -1) {
            toastView.setBackgroundResource(sBgResource)
        } else if (sBgColor != COLOR_DEFAULT) {
            val background = toastView.background
            if (background != null) {
                background.colorFilter = PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN)
            } else {
                ViewCompat.setBackground(toastView, ColorDrawable(sBgColor))
            }
        }
    }

    private fun setBg(tvMsg: TextView) {
        val toastView = sToast!!.view
        if (sBgResource != -1) {
            toastView.setBackgroundResource(sBgResource)
            tvMsg.setBackgroundColor(Color.TRANSPARENT)
        } else if (sBgColor != COLOR_DEFAULT) {
            val tvBg = toastView.background
            val msgBg = tvMsg.background
            if (tvBg != null && msgBg != null) {
                tvBg.colorFilter = PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN)
                tvMsg.setBackgroundColor(Color.TRANSPARENT)
            } else if (tvBg != null) {
                tvBg.colorFilter = PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN)
            } else if (msgBg != null) {
                msgBg.colorFilter = PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN)
            } else {
                toastView.setBackgroundColor(sBgColor)
            }
        }
    }

    private fun getView(@LayoutRes layoutId: Int): View? {
        val inflate = Utils.app.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as? LayoutInflater
        return inflate?.inflate(layoutId, null)
    }
}