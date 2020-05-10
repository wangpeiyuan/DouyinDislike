package com.yuan.douyindislike.ktx

import android.text.Html
import android.widget.TextView
import androidx.core.text.HtmlCompat

/**
 *
 * Created by wangpeiyuan on 2020/5/10.
 */
fun TextView.setHtmlText(html: String) {
    this.text = HtmlCompat.fromHtml(
        html, HtmlCompat.FROM_HTML_MODE_LEGACY,
        Html.ImageGetter { source ->
            val drawable = context.resources.getDrawable(source.toInt(), null)
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            drawable
        },
        null
    )
}