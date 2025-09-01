package com.film.television.adapter

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.film.television.databinding.ItemVideoBinding
import com.film.television.model.VideoBean
import com.film.television.utils.ImageLoader
import com.film.television.utils.RouteUtil

class VideoViewHolder(
    private val context: Context?,
    private val fragment: Fragment?,
    private val activity: FragmentActivity?,
    private val binding: ItemVideoBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(videoBean: VideoBean) {
        when {
            context != null -> {
                ImageLoader.loadRoundedCornerImage(context, videoBean.imageUrl, 8f, binding.cover)
                binding.root.setOnClickListener {
                    RouteUtil.goToVideoDetailActivity(context, videoBean)
                }
            }

            fragment != null -> {
                ImageLoader.loadRoundedCornerImage(fragment, videoBean.imageUrl, 8f, binding.cover)
                binding.root.setOnClickListener {
                    RouteUtil.goToVideoDetailActivity(fragment.requireContext(), videoBean)
                }
            }

            activity != null -> {
                ImageLoader.loadRoundedCornerImage(activity, videoBean.imageUrl, 8f, binding.cover)
                binding.root.setOnClickListener {
                    RouteUtil.goToVideoDetailActivity(activity, videoBean)
                }
            }
        }
        binding.rating.text = "${videoBean.rating?:"0"}分"
        binding.name.text = videoBean.title
    }
}