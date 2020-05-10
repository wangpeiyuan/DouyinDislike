package com.yuan.douyindislike.ktx

import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import com.yuan.douyindislike.Helper

/**
 *
 * Created by wangpeiyuan on 2020/5/9.
 */
const val SHARE_NAME = "like_data"
const val POS_X = "pos_x"
const val POS_Y = "pos_y"
fun Context.setLikePos(posX: Float, poxY: Float) {
    Helper.log("setLikePos ($posX , $poxY)")
    val edit = this.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE).edit()
    edit.putFloat(POS_X, posX)
    edit.putFloat(POS_Y, poxY)
    edit.apply()
}

fun Context.getLikePos(): FloatArray {
    val sharedPreferences = this.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)
    val posX = sharedPreferences.getFloat(POS_X, 0f)
    val posY = sharedPreferences.getFloat(POS_Y, 0f)
    return floatArrayOf(posX, posY)
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Context.sp2px(spValue: Float): Int {
    return (TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        spValue, resources.displayMetrics
    ) + 0.5f).toInt()
}