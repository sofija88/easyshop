package com.example.myapplication.utils

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

class SpaceItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildLayoutPosition(view) % 2 != 0) {
            outRect.top = 50
            outRect.bottom = -50

        }else outRect.top = 0
    }
}