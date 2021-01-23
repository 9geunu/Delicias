package com.example.delicias.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.delicias.R
import com.example.delicias.databinding.FragmentMapBinding
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap

class AnimationOnClickListener @JvmOverloads internal constructor(
    private val context: Context,
    private val view: View,
    private val map: View,
    private val naverMap: NaverMap,
    private val binding: FragmentMapBinding,
    private val interpolator: Interpolator? = null) : View.OnClickListener {

    private val animatorSet = AnimatorSet()
    private val height: Int
    var isRestaurantDetailInfoShown = false
    private var tvMapTitle: TextView
    private var ivSearchRestaurant: ImageView
    private var ivBackButton: ImageView

    init {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        val mapTitle: View = binding.mapTitle
        tvMapTitle = (mapTitle as ConstraintLayout).getViewById(R.id.tv_map_title) as TextView
        ivSearchRestaurant = mapTitle.getViewById(R.id.iv_search_restaurant) as ImageView
        ivBackButton = mapTitle.getViewById(R.id.iv_map_title_back_button) as ImageView
    }

    override fun onClick(v: View?) {
        isRestaurantDetailInfoShown = !isRestaurantDetailInfoShown

        view.visibility = View.INVISIBLE

        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()

        if (binding.restaurant != null) {
            val restaurant = binding.restaurant
            tvMapTitle.text = restaurant?.name
            tvMapTitle.setTextColor(Color.BLACK)
        }
        else
            tvMapTitle.text = "식당 정보를 가져올 수 없습니다."
        ivSearchRestaurant.visibility = View.INVISIBLE
        ivBackButton.visibility = View.VISIBLE

        val cameraUpdate = CameraUpdate.scrollBy(PointF(0F, map.measuredHeight*(0.4F)))
            .animate(CameraAnimation.None)
        naverMap.moveCamera(cameraUpdate)

        val animator1 =
            ObjectAnimator.ofFloat(map, "translationY",
                (if (isRestaurantDetailInfoShown) map.measuredHeight*(-0.4F) else 0F).toFloat())

        val animator2 =
            ObjectAnimator.ofFloat(binding.btnDefaultLocation, "translationY",
                (if (isRestaurantDetailInfoShown) map.measuredHeight*(-0.4F) else 0F).toFloat())

        animator1.duration = 500
        if (interpolator != null) {
            animator1.interpolator = interpolator
        }

        animator2.duration = 500
        if (interpolator != null) {
            animator2.interpolator = interpolator
        }

        animatorSet.playTogether(animator1, animator2)

        animator1.start()
        animator2.start()
    }

    fun onMapClick(){
        if (!isRestaurantDetailInfoShown)
            return

        isRestaurantDetailInfoShown = !isRestaurantDetailInfoShown

        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()

        tvMapTitle.text = context.getString(R.string.restaurant_map_title)
        tvMapTitle.setTextColor(ContextCompat.getColor(context, R.color.orange))
        ivSearchRestaurant.visibility = View.VISIBLE
        ivBackButton.visibility = View.INVISIBLE

        val cameraUpdate = CameraUpdate.scrollBy(PointF(0F, map.measuredHeight*(-0.4F)))
            .animate(CameraAnimation.None)
        naverMap.moveCamera(cameraUpdate)

        val animator1 =
            ObjectAnimator.ofFloat(map, "translationY", 0F)

        animator1.duration = 500
        if (interpolator != null) {
            animator1.interpolator = interpolator
        }

        val animator2 =
            ObjectAnimator.ofFloat(binding.btnDefaultLocation, "translationY", 0F)

        animator2.duration = 500
        if (interpolator != null) {
            animator2.interpolator = interpolator
        }

        animatorSet.playTogether(animator1, animator2)

        animator1.start()
        animator2.start()
    }
}