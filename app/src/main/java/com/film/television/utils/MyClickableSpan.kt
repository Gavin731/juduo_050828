package com.film.television.utils

import android.text.TextPaint
import android.text.style.ClickableSpan

abstract class MyClickableSpan : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.setColor(ds.linkColor)
    }

}