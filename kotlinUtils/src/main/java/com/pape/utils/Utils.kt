package com.pape.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * Created by zhangzhanyong on 2018/4/19.
 */
object Utils {

    private var app: WeakReference<Application>? = null
    private val ACTIVITY_LIST = LinkedList<Activity>()

    private val mCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle) {
            setTopActivity(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            setTopActivity(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            setTopActivity(activity)
        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            ACTIVITY_LIST.remove(activity)
        }
    }

    /**
     * 工具初始化
     */
    fun init(app: Application) {
        this.app = WeakReference(app)
        app.registerActivityLifecycleCallbacks(mCallbacks)
    }

    fun getApp(): Application? {
        return app?.get()
    }

    internal fun setTopActivity(activity: Activity) {
//        if (activity.javaClass == PermissionUtils.PermissionActivity::class.java) return
        if (ACTIVITY_LIST.contains(activity)) {
            if (ACTIVITY_LIST.last != activity) {
                ACTIVITY_LIST.remove(activity)
                ACTIVITY_LIST.addLast(activity)
            }
        } else {
            ACTIVITY_LIST.addLast(activity)
        }
    }

    internal fun getActivityList(): LinkedList<Activity> {
        return ACTIVITY_LIST
    }

    internal fun getTopActivity(): Activity? {
        if (!ACTIVITY_LIST.isEmpty()) {
            val topActivity = ACTIVITY_LIST.last
            if (topActivity != null) {
                return topActivity
            }
        }
        // using reflect to get top activity
        try {
            @SuppressLint("PrivateApi")
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
            val activitiesField = activityThreadClass.getDeclaredField("mActivities")
            activitiesField.isAccessible = true
            val activities = activitiesField.get(activityThread) as Map<*, *> ?: return null
            for (activityRecord in activities.values) {
                val activityRecordClass = activityRecord?.javaClass
                activityRecordClass?.let {
                    val pausedField = it.getDeclaredField("paused")
                    pausedField.isAccessible = true
                    if (!pausedField.getBoolean(activityRecord)) {
                        val activityField = it.getDeclaredField("activity")
                        activityField.isAccessible = true
                        val activity = activityField.get(activityRecord) as Activity
                        Utils.setTopActivity(activity)
                        return activity
                    }
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }

        return null
    }

}