package com.film.television.model

import androidx.recyclerview.widget.DiffUtil

data class VideoAdBean(val videoBean: VideoBean?, val adBean: AdBean?) {
    object AdBean

    companion object : DiffUtil.ItemCallback<VideoAdBean>() {
        override fun areItemsTheSame(
            oldItem: VideoAdBean,
            newItem: VideoAdBean
        ): Boolean {
            return if (oldItem.videoBean != null) {
                if (newItem.videoBean != null) {
                    VideoBean.areItemsTheSame(oldItem.videoBean, newItem.videoBean)
                } else {
                    false
                }
            } else {
                newItem.adBean != null
            }
        }

        override fun areContentsTheSame(
            oldItem: VideoAdBean,
            newItem: VideoAdBean
        ): Boolean {
            return if (oldItem.videoBean != null) {
                VideoBean.areContentsTheSame(oldItem.videoBean, newItem.videoBean!!)
            } else {
                true
            }
        }

    }
}
