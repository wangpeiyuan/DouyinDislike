package com.yuan.douyindislike.ktx

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler

/**
 *
 * Created by wangpeiyuan on 2020/5/13.
 */
fun AccessibilityService.back() {
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
}

fun AccessibilityService.openNotifications() {
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
}

fun AccessibilityService.takeScreenshot() {
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
}

fun AccessibilityService.lockScreen() {
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
}

fun AccessibilityService.gestureOnScreen(
    path: Path,
    startTime: Long = 0,
    duration: Long = 100,
    callback: AccessibilityService.GestureResultCallback,
    handler: Handler? = null
) {
    /**
     * 参数path：笔画路径
     * 参数startTime：时间 (以毫秒为单位)，从手势开始到开始笔划的时间，非负数
     * 参数duration：笔划经过路径的持续时间(以毫秒为单位)，非负数
     */
    val gesture = GestureDescription.Builder()
        .addStroke(GestureDescription.StrokeDescription(path, startTime, duration))
        .build()
    /**
     * 参数GestureDescription：手势的描述，如果要实现模拟，首先要描述你的要模拟的手势嘛
     * 参数GestureResultCallback：翻译过来就是手势的回调，手势模拟执行以后回调结果
     * 参数handler：大部分情况我们不用的话传空就可以了
     */
    dispatchGesture(gesture, callback, handler)
}

fun AccessibilityService.clickOnScreen(
    x: Float,
    y: Float,
    callback: AccessibilityService.GestureResultCallback,
    handler: Handler? = null
) {
    val path = Path()
    path.moveTo(x, y)
    gestureOnScreen(path, callback = callback, handler = handler)
}