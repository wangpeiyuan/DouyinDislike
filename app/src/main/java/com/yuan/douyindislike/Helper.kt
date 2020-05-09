package com.yuan.douyindislike

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log


/**
 *
 * Created by wangpeiyuan on 2020/5/9.
 */
object Helper {

    fun addExpression(activity: Activity, listener: OnOpenServiceListener) {
        if (isServiceEnabled(activity)) {
            openService(activity, listener)
        } else {
            showOpenServiceDialog(activity, listener)
        }
    }

    private fun showOpenServiceDialog(activity: Activity, listener: OnOpenServiceListener) {
        AlertDialog.Builder(activity)
            .setCancelable(false)
            .setTitle(activity.getString(R.string.open_service_title))
            .setMessage(activity.getString(R.string.open_service_msg))
            .setPositiveButton(activity.getString(R.string.open_service_confirm)) { dialog, _ ->
                dialog.cancel()
                openService(activity, listener)
            }
            .setNegativeButton(activity.getString(R.string.open_service_cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    /**
     * 检查服务是否开启。
     */
    fun isServiceEnabled(context: Context): Boolean {
        return isAccessibilitySettingsOn(context)
    }

    private fun isAccessibilitySettingsOn(context: Context): Boolean {
        var accessibilityEnabled = 0
        val service: String =
            context.packageName + "/" + DislikeAccessibilityService::class.java.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                context.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
//            log("accessibilityEnabled = $accessibilityEnabled")
        } catch (e: SettingNotFoundException) {
            log("Error finding setting, default accessibility to not found: " + e.message)
        }
        val mStringColonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
//            log("***ACCESSIBILITY IS ENABLED*** -----------------")
            val settingValue = Settings.Secure.getString(
                context.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
//                    log("-------------- > accessibilityService :: $accessibilityService $service")
                    if (accessibilityService.equals(service, ignoreCase = true)) {
//                        log("We've found the correct setting - accessibility is switched on!")
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun openService(context: Context, listener: OnOpenServiceListener) {
        (context.applicationContext as Application).registerActivityLifecycleCallbacks(
            object : ActivityLifecycleCallbacksAdapter() {
                override fun onActivityResumed(activity: Activity) {
                    if (context.javaClass == activity.javaClass) {
                        (context.applicationContext as Application).unregisterActivityLifecycleCallbacks(
                            this
                        )
                        listener.onResult(isServiceEnabled(context))
                    }
                }
            })
        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    fun log(msg: String) {
        Log.d("Service", msg)
    }

    /**
     * 打开服务的回调。
     */
    interface OnOpenServiceListener {
        /**
         * 打开服务的回调。
         *
         * @param isOpening 服务是否开启。
         */
        fun onResult(isOpening: Boolean)
    }
}