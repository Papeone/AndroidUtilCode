package com.pape.utils

import android.Manifest.permission.WRITE_SETTINGS
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresPermission
import android.util.DisplayMetrics
import android.view.Surface
import android.view.WindowManager

/**
 * 功能描述：
 * Created by Administrator on 2018/5/4.
 */

object ScreenExt {
    fun getScreenWidth(): Int {
        val wm = Utils.getApp()?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                ?: return Utils.getApp()?.resources?.displayMetrics?.widthPixels
                ?: 0
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.x
    }

    fun getScreenHeight(): Int {
        val wm = Utils.getApp()?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                ?: return Utils.getApp()?.resources?.displayMetrics?.heightPixels
                ?: 0
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.y
    }

    fun getScreenDensityDpi(): Int {
        return Utils.getApp()?.resources?.displayMetrics?.densityDpi ?: 0
    }

    fun setFullScreen(activity: Activity) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    fun setLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun setPortrait(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun isLandscape(): Boolean {
        return Utils.getApp()?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    fun isPortrait(): Boolean {
        return Utils.getApp()?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    fun getScreenRotation(activity: Activity): Int {
        when (activity.windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 -> return 0
            Surface.ROTATION_90 -> return 90
            Surface.ROTATION_180 -> return 180
            Surface.ROTATION_270 -> return 270
            else -> return 0
        }
    }

    fun screenShot(activity: Activity, isDeleteStatusBar: Boolean = false): Bitmap {
        val decorView = activity.window.decorView
        decorView.isDrawingCacheEnabled = true
        decorView.buildDrawingCache()
        val bmp = decorView.drawingCache
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        val ret: Bitmap
        ret = if (isDeleteStatusBar) {
            val resources = activity.resources
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            val statusBarHeight = resources.getDimensionPixelSize(resourceId)
            Bitmap.createBitmap(
                    bmp,
                    0,
                    statusBarHeight,
                    dm.widthPixels,
                    dm.heightPixels - statusBarHeight
            )
        } else {
            Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels)
        }
        decorView.destroyDrawingCache()
        return ret
    }

    fun isScreenLock(): Boolean {
        val km = Utils.getApp()?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return km != null && km.inKeyguardRestrictedInputMode()
    }

    @RequiresPermission(WRITE_SETTINGS)
    fun setSleepDuration(duration: Int) {
        Settings.System.putInt(
                Utils.getApp()?.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT,
                duration
        )
    }

    fun getSleepDuration(): Int {
        return try {
            Settings.System.getInt(
                    Utils.getApp()?.contentResolver,
                    Settings.System.SCREEN_OFF_TIMEOUT
            )
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            -123
        }
    }

    fun isTablet(): Boolean {
        val screenLayout = Utils.getApp()?.resources?.configuration?.screenLayout
        return if (screenLayout != null) {
            screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
        } else {
            false
        }
    }
}