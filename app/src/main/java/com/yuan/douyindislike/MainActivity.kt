package com.yuan.douyindislike

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.yuan.douyindislike.ktx.getLikePos
import com.yuan.douyindislike.ktx.setHtmlText
import com.yuan.douyindislike.ktx.toast
import com.yuan.douyindislike.view.FloatWindow
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var mFloatWindow: FloatWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()
        initView()
    }

    private fun initView() {
        btn_open_service.setOnClickListener {
            openService()
        }
        btn_like_pos.setOnClickListener {
            showOrHideFloatWidow()
        }
    }

    private fun initData() {
        tv_instructions_for_use.setHtmlText(
            getString(
                R.string.instructions_for_use,
                R.drawable.ic_my_location,
                R.drawable.ic_my_location
            )
        )
        updateServiceStatus(Helper.isServiceEnabled(this))
        val likePos = getLikePos()
        if (likePos[0] == 0f && likePos[1] == 0f) {
            btn_open_service.isSelected = true
            btn_like_pos.isSelected = false
        } else {
            btn_open_service.isSelected = false
            btn_like_pos.isSelected = true
        }
    }

    private fun openService() {
        val likePos = getLikePos()
        if (likePos[0] == 0f && likePos[1] == 0f) {
            toast(getString(R.string.open_service_condition))
            return
        }
        Helper.addExpression(this, object : Helper.OnOpenServiceListener {
            override fun onResult(isOpening: Boolean) {
                updateServiceStatus(isOpening)
            }
        })
    }

    private fun updateServiceStatus(isOpen: Boolean) {
        if (isOpen) {
            btn_open_service.text = getString(R.string.close_service)
        } else {
            btn_open_service.text = getString(R.string.open_service)
        }
    }

    private fun updateLikePos(isShow: Boolean) {
        if (isShow) {
            btn_like_pos.text = getString(R.string.location_like_pos_hide)
        } else {
            btn_like_pos.text = getString(R.string.location_like_pos_show)
        }
    }

    private fun showOrHideFloatWidow() {
        if (mFloatWindow == null || !mFloatWindow!!.isShow()) {
            requestShowFloatWindow()
        } else {
            mFloatWindow!!.hideFloatWindow()
            updateLikePos(mFloatWindow!!.isShow())
        }
    }

    private fun requestShowFloatWindow() {
        if (!Settings.canDrawOverlays(applicationContext)) {
            //启动Activity让用户授权
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 10)
            return
        } else {
            showFloatWindow()
        }
    }

    private fun showFloatWindow() {
        if (mFloatWindow == null) {
            mFloatWindow = FloatWindow(this)
        }
        val likePos = getLikePos()
        if (likePos[0] == 0f && likePos[1] == 0f) {
            mFloatWindow?.showFloatWindow()
        } else {
            mFloatWindow?.showFloatWindow(likePos[0].toInt(), likePos[1].toInt())
        }
        updateLikePos(mFloatWindow!!.isShow())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && Settings.canDrawOverlays(applicationContext)) {
            showFloatWindow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mFloatWindow?.hideFloatWindow()
    }
}
