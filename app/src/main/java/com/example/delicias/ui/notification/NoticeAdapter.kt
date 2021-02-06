package com.example.delicias.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.delicias.R
import com.example.delicias.domain.Notice


class NoticeAdapter() : ListAdapter<Notice, NoticeAdapter.ViewHolder>(
    diffUtil
) {
    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Notice>() {
            override fun areContentsTheSame(oldItem: Notice, newItem: Notice) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Notice, newItem: Notice) =
                oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_notice, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notice = getItem(position)

        with(holder) {
            title.text = notice.title
            date.text = notice.date
            body.text = notice.body

            layout.setOnClickListener {
                if (detailLayout.isVisible) {
                    collapse(detailLayout)
                    arrow.rotation = 0f
                }
                else {
                    expand(detailLayout)
                    arrow.rotation = 180f
                }
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var layout: LinearLayout = itemView.findViewById(R.id.ll_notice_layout)
        var detailLayout: LinearLayout = itemView.findViewById(R.id.ll_notice_detail)
        var title: TextView = itemView.findViewById(R.id.tv_notice_title)
        var date: TextView = itemView.findViewById(R.id.tv_notice_date)
        var body: TextView = itemView.findViewById(R.id.tv_notice_body)
        var arrow: ImageView = itemView.findViewById(R.id.iv_arrow)
    }

    private fun expand(v: View) {
        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) WindowManager.LayoutParams.WRAP_CONTENT
                    else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toLong()
        v.startAnimation(a)
    }

    private fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toLong()
        v.startAnimation(a)
    }
}