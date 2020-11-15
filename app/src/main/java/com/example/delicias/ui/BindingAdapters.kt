package com.example.delicias.ui

import android.view.View
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("visible")
    fun setVisible(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("linear_layout_weight")
    fun setLayoutWeight(view: View, weight: Float) {
        val layoutParams = view.layoutParams as? LinearLayout.LayoutParams
        layoutParams?.let {
            it.weight = weight
            view.layoutParams = it
        }
    }
}