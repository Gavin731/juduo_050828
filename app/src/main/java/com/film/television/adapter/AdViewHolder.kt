package com.film.television.adapter

import android.app.Activity
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.film.television.databinding.ItemAdBinding
import com.film.television.utils.AdUtil

class AdViewHolder(private val activity: Activity, private val binding: ItemAdBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private var bound = false

    fun bind(width: Int) {
        if (!bound) {
            bound = true
            AdUtil.showFeedsAd(activity, binding.adContainer, width, 0)
        }
    }

}