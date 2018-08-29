package com.pape.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import java.util.*

/**
 * 功能描述：
 * Created by Administrator on 2018/5/4.
 */
object ServiceExt {

    fun getAllRunningServices(): Set<String> {
        val am: ActivityManager? = Utils.getApp()?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = am?.getRunningServices(0x7FFFFFFF) ?: return Collections.emptySet()
        if (info.size == 0) return Collections.emptySet()
        return info.mapTo(HashSet<String>()) { it.service.className }
    }

    fun startService(className: String) {
        try {
            startService(Class.forName(className))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startService(cls: Class<*>) {
        Utils.getApp()?.startService(Intent(Utils.getApp(), cls))
    }

    fun stopService(className: String) {
        try {
            stopService(Class.forName(className))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopService(cls: Class<*>) {
        Utils.getApp()?.stopService(Intent(Utils.getApp(), cls))
    }

    fun unbindService(conn: ServiceConnection) {
        Utils.getApp()?.unbindService(conn)
    }

    fun isServiceRunning(className: String): Boolean {
        val am = Utils.getApp()?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager ?: return false
        val info = am.getRunningServices(0x7FFFFFFF) ?: return false
        if (info.size == 0) return false
        return info.any { className == it.service.className }
    }

    fun isServiceRunning(cls: Class<*>): Boolean {
        return isServiceRunning(cls.name)
    }

}