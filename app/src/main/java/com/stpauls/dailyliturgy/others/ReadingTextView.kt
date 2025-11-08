package com.stpauls.dailyliturgy.others

import android.content.Context
import android.util.AttributeSet
import com.stpauls.dailyliturgy.R


class ReadingTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.ReadingTextView,
            0, 0
        )
        try {
            val fontSize =
                a.getDimension(R.styleable.ReadingTextView_font_size, 0f).toInt()
            this.textSize = fontSize.toFloat()
        } finally {
            a.recycle()
        }
    }
}