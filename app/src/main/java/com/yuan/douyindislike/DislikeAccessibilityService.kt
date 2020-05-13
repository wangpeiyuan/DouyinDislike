package com.yuan.douyindislike

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.yuan.douyindislike.Helper.log
import com.yuan.douyindislike.ktx.getLikePos


/**
 *抖音取消喜欢
 *抖音包名：com.ss.android.ugc.aweme
 *喜欢列表：
 *1.点击第一 item 进入视频页，点击取消喜欢 > 返回 > 列表自动刷新，再次点击第一个 item。。。
 *2.视频已被删除，点击 item 弹出，点击删除按钮，自动刷新列表，再次点击第一个 item
 * Created by wangpeiyuan on 2020/5/9.
 */
class DislikeAccessibilityService : AccessibilityService() {

    private var isInMain = true
    private var isInVideoDetail = false
    private var isInFollow = false

    /**
     * 中断服务的回调
     */
    override fun onInterrupt() {

    }

    /**
     * 监听窗口变化的回调
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        log(event.toString())
        //根据事件回调类型进行处理
        val eventType = event.eventType

        //获取窗口节点（根节点）
        val nodeInfo: AccessibilityNodeInfo? = rootInActiveWindow ?: return

        //当窗口的状态发生改变时
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (isInVideoDetail(event)) {
                isInVideoDetail = true
                isInMain = false
                isInFollow = false
            } else if (isInMain(event)) {
                isInMain = true
                isInVideoDetail = false
                isInFollow = false
            } else if (isInFollow(event)) {
                isInFollow = true
                isInMain = false
                isInVideoDetail = false
            }
            log(
                "WINDOW_STATE_CHANGED:> isInMain = $isInMain;isInVideoDetail = $isInVideoDetail;" +
                        "isInFollow = $isInFollow"
            )
        }

        //如果选中 喜欢 tab
        if (isInMain) {
            if (isShowDeleteDialog(event)) {
                val findDeleteButton = findDeleteButton(nodeInfo)
                findDeleteButton?.let {
                    log("click delete")
                    nodeClick(it)
                }
            } else if (isLikeSelected(nodeInfo)) {
                val videoNode = findFistVideoNode(nodeInfo)
                videoNode?.let {
                    log("click video")
                    nodeClick(it)
                }
            }
        } else if (isInVideoDetail) {
            val likePos = application.getLikePos()
            if (likePos[0] == 0f && likePos[1] == 0f) return
            log("disLikeClick: (${likePos[0]},${likePos[1]})")
            dispatchGestureClick(likePos[0], likePos[1])
        } else if (isInFollow) {
            val findFollowedButton = findFollowedButton(nodeInfo)
            if (findFollowedButton != null) {
                log("click followed")
                nodeClick(findFollowedButton)
            }
        }
    }

    private fun isInMain(event: AccessibilityEvent): Boolean {
        return event.className == "com.ss.android.ugc.aweme.main.MainActivity"
    }

    private fun isInVideoDetail(event: AccessibilityEvent): Boolean {
        return event.className == "com.ss.android.ugc.aweme.detail.ui.DetailActivity"
    }

    private fun isInFollow(event: AccessibilityEvent): Boolean {
        return event.className == "com.ss.android.ugc.aweme.following.ui.FollowRelationTabActivity"
    }

    private fun isShowDeleteDialog(event: AccessibilityEvent): Boolean {
        return event.text.isNotEmpty() && event.text[0] == "视频已失效"
    }

    private fun isLikeSelected(nodeInfo: AccessibilityNodeInfo?): Boolean {
        if (nodeInfo != null) {
            val nodeList = nodeInfo.findAccessibilityNodeInfosByText("喜欢")
            if (nodeList != null && nodeList.isNotEmpty()) {
                for (childNodeInfo in nodeList) {
                    if ("android.widget.TextView" == childNodeInfo.className && childNodeInfo.isSelected) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun findFistVideoNode(nodeInfo: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (nodeInfo != null) {
            val nodeList = nodeInfo.findAccessibilityNodeInfosByText("视频1")
            if (nodeList != null && nodeList.isNotEmpty()) {
                for (childNodeInfo in nodeList) {
                    if ("android.widget.ImageView" == childNodeInfo.className) {
                        return childNodeInfo
                    }
                }
            }
        }
        return null
    }

    private fun findDeleteButton(nodeInfo: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (nodeInfo != null) {
            val nodeList = nodeInfo.findAccessibilityNodeInfosByText("删除")
            if (nodeList != null && nodeList.isNotEmpty()) {
                for (childNodeInfo in nodeList.reversed()) {
                    if ("android.widget.TextView" == childNodeInfo.className) {
                        return childNodeInfo
                    }
                }
            }
        }
        return null
    }

    private fun findFollowedButton(nodeInfo: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (nodeInfo != null) {
            val nodeList = nodeInfo.findAccessibilityNodeInfosByText("已关注")
            if (nodeList != null && nodeList.isNotEmpty()) {
                for (childNodeInfo in nodeList) {
                    if ("android.widget.TextView" == childNodeInfo.className) {
                        return childNodeInfo
                    }
                }
            }
        }
        return null
    }

    private fun dispatchGestureClick(x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)
        path.lineTo(x, y)
        /**
         * 参数GestureDescription：翻译过来就是手势的描述，如果要实现模拟，首先要描述你的腰模拟的手势嘛
         * 参数GestureResultCallback：翻译过来就是手势的回调，手势模拟执行以后回调结果
         * 参数handler：大部分情况我们不用的话传空就可以了
         */
        dispatchGesture(
            /**
             * 参数path：笔画路径
             * 参数startTime：时间 (以毫秒为单位)，从手势开始到开始笔划的时间，非负数
             * 参数duration：笔划经过路径的持续时间(以毫秒为单位)，非负数
             */
            GestureDescription.Builder()
                .addStroke(StrokeDescription(path, 300, 100)).build(),
            object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    back()
                }
            }, null
        )
    }

    private fun nodeClick(nodeInfo: AccessibilityNodeInfo) {
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    private fun nodeScrollForward(nodeInfo: AccessibilityNodeInfo) {
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
    }

    private fun back() {
        performGlobalAction(GLOBAL_ACTION_BACK)
    }
}