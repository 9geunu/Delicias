package com.example.delicias.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap

class AnimationOnClickListener @JvmOverloads internal constructor(
    context: Context,
    private val view: View,
    private val map: View,
    private val naverMap: NaverMap,
    private val interpolator: Interpolator? = null) : View.OnClickListener {

    private val animatorSet = AnimatorSet()
    private val height: Int
    private var isRestaurantDetailInfoShown = false

    init {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
    }

    override fun onClick(v: View?) {
        isRestaurantDetailInfoShown = !isRestaurantDetailInfoShown

        view.visibility = View.GONE

        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()

        val cameraUpdate = CameraUpdate.scrollBy(PointF(0F, map.measuredHeight*(0.4F)))
            .animate(CameraAnimation.None)
        naverMap.moveCamera(cameraUpdate)

        val animator =
            ObjectAnimator.ofFloat(map, "translationY",
                (if (isRestaurantDetailInfoShown) map.measuredHeight*(-0.4F) else 0F).toFloat())

        animator.duration = 500
        if (interpolator != null) {
            animator.interpolator = interpolator
        }
        animatorSet.play(animator)

        animator.start()
    }

    fun onMapClick(){
        if (!isRestaurantDetailInfoShown)
            return

        isRestaurantDetailInfoShown = !isRestaurantDetailInfoShown

        view.visibility = View.VISIBLE

        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()

        val cameraUpdate = CameraUpdate.scrollBy(PointF(0F, map.measuredHeight*(-0.4F)))
            .animate(CameraAnimation.None)
        naverMap.moveCamera(cameraUpdate)

        val animator =
            ObjectAnimator.ofFloat(map, "translationY", 0F)

        animator.duration = 500
        if (interpolator != null) {
            animator.interpolator = interpolator
        }
        animatorSet.play(animator)

        animator.start()
    }
}