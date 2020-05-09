package com.yuan.douyindislike

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var mFloatWindow: FloatWindow? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateServiceStatus(Helper.isServiceEnabled(this))

        btn_open_service.setOnClickListener {
            Helper.addExpression(this, object : Helper.OnOpenServiceListener {
                override fun onResult(isOpening: Boolean) {

                    updateServiceStatus(isOpening)
                }
            })
        }

        btn_like_pos.setOnClickListener {
            requestShowFloatWindow()
        }
    }

    private fun updateServiceStatus(isOpen: Boolean) {
        if (isOpen) {
            btn_open_service.text = getString(R.string.close_service)
        } else {
            btn_open_service.text = getString(R.string.open_service)
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
        mFloatWindow?.showFloatWindow()
    }

    override fun onDestroy() {
        super.onDestroy()
        mFloatWindow?.hideFloatWindow()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && Settings.canDrawOverlays(applicationContext)) {
            showFloatWindow()
        }
    }
}
