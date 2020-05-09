package com.yuan.douyindislike

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import java.lang.reflect.Field


/**
 *
 * Created by wangpeiyuan on 2020/5/9.
 */
class FloatWindow(private val context: Context) : View.OnTouchListener {
    private var mWindowParams: WindowManager.LayoutParams
    private var mWindowManager: WindowManager

    private var mFloatLayout: View =
        LayoutInflater.from(context).inflate(R.layout.layout_float_window, null, false)

    private var mInViewX = 0f
    private var mInViewY = 0f
    private var mDownInScreenX = 0f
    private var mDownInScreenY = 0f
    private var mInScreenX = 0f
    private var mInScreenY = 0f

    private val mSysHeight = getSysBarHeight()

    init {
        mFloatLayout.setOnTouchListener(this)

        mWindowParams = WindowManager.LayoutParams()
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= 26) { //8.0新特性
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        mWindowParams.format = PixelFormat.RGBA_8888
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mWindowParams.gravity = Gravity.START or Gravity.TOP
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return floatLayoutTouch(event)
    }

    private fun floatLayoutTouch(motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                // 获取相对View的坐标，即以此View左上角为原点
                mInViewX = motionEvent.x
                mInViewY = motionEvent.y
                // 获取相对屏幕的坐标，即以屏幕左上角为原点
                mDownInScreenX = motionEvent.rawX
                mDownInScreenY = motionEvent.rawY - mSysHeight
                mInScreenX = motionEvent.rawX
                mInScreenY = motionEvent.rawY - mSysHeight
            }
            MotionEvent.ACTION_MOVE -> {
                // 更新浮动窗口位置参数
                mInScreenX = motionEvent.rawX
                mInScreenY = motionEvent.rawY - mSysHeight
                mWindowParams.x = (mInScreenX - mInViewX).toInt()
                mWindowParams.y = (mInScreenY - mInViewY).toInt()
                // 手指移动的时候更新小悬浮窗的位置
                mWindowManager.updateViewLayout(mFloatLayout, mWindowParams)
            }
            MotionEvent.ACTION_UP -> {
                context.setLikePos(mInScreenX, mInScreenY)
            }
        }
        return true
    }

    fun showFloatWindow() {
        if (mFloatLayout.parent == null) {
            val metrics = DisplayMetrics()
            //默认固定位置，靠屏幕右边缘的中间
            mWindowManager.defaultDisplay.getMetrics(metrics)
            mWindowParams.x = metrics.widthPixels
            mWindowParams.y = metrics.heightPixels / 2 - mSysHeight
            mWindowManager.addView(mFloatLayout, mWindowParams)
        }
    }

    fun hideFloatWindow() {
        if (mFloatLayout.parent != null) mWindowManager.removeView(mFloatLayout)
    }

    // 获取系统状态栏高度
    private fun getSysBarHeight(): Int {
        val c: Class<*>
        val obj: Any
        val field: Field
        val x: Int
        var sbar = 0
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c.newInstance()
            field = c.getField("status_bar_height")
            x = field.get(obj).toString().toInt()
            sbar = context.resources.getDimensionPixelSize(x)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
        return sbar
    }
}