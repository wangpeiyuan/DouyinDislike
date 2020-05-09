package com.yuan.douyindislike

import android.content.Context

/**
 *
 * Created by wangpeiyuan on 2020/5/9.
 */
const val SHARE_NAME = "like_data"
const val POS_X = "pos_x"
const val POS_Y = "pos_y"
fun Context.setLikePos(posX: Float, poxY: Float) {
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