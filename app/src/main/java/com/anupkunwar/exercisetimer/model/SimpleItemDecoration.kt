package com.anupkunwar.exercisetimer.model

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView

class SimpleItemDecoration(height: Float, color: Int) :
    RecyclerView.ItemDecoration() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = color
        paint.strokeWidth = height
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 1 until parent.childCount) {
            val child = parent.getChildAt(i)
            c.drawLine(
                0f,
                child.top.toFloat(),
                parent.width.toFloat(),
                child.top.toFloat(),
                paint
            )
        }
    }
}