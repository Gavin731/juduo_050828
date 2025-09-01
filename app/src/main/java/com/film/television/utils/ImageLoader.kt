package com.film.television.utils

import android.content.Context
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

object ImageLoader {

    fun loadRoundedCornerImage(
        context: Context,
        url: String?,
        roundingRadiusDp: Float,
        imageView: ImageView
    ) {
        Glide.with(context)
            .load(url)
            .transform(CenterCrop(), RoundedCorners(UIUtil.dp2px(context, roundingRadiusDp)))
            .into(imageView)
    }

    fun loadRoundedCornerImage(
        activity: FragmentActivity,
        url: String?,
        roundingRadiusDp: Float,
        imageView: ImageView
    ) {
        Glide.with(activity)
            .load(url)
            .transform(CenterCrop(), RoundedCorners(UIUtil.dp2px(activity, roundingRadiusDp)))
            .into(imageView)
    }

    fun loadRoundedCornerImage(
        fragment: Fragment,
        url: String?,
        roundingRadiusDp: Float,
        imageView: ImageView
    ) {
        Glide.with(fragment)
            .load(url)
            .transform(
                CenterCrop(),
                RoundedCorners(UIUtil.dp2px(fragment.requireContext(), roundingRadiusDp))
            )
            .into(imageView)
    }

    fun loadImage(context: Context, url:String?, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .into(imageView)
    }

    fun loadImage(fragment: Fragment, url:String?, imageView: ImageView) {
        Glide.with(fragment)
            .load(url)
            .into(imageView)
    }

    fun loadImage(activity: FragmentActivity, url:String?, imageView: ImageView) {
        Glide.with(activity)
            .load(url)
            .into(imageView)
    }

}