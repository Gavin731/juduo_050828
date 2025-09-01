package com.film.television.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.film.television.databinding.ItemVideoBinding
import com.film.television.model.VideoBean

class VideoAdapter(
    private val context: Context?,
    private val fragment: Fragment?,
    private val activity: FragmentActivity?,
    private val data: List<VideoBean>
) :
    RecyclerView.Adapter<VideoViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return when {
            context != null -> {
                VideoViewHolder(
                    context,
                    null,
                    null,
                    binding
                )
            }

            fragment != null -> {
                VideoViewHolder(
                    null,
                    fragment,
                    null,
                    binding
                )
            }

            activity != null -> {
                VideoViewHolder(
                    null,
                    null,
                    activity,
                    binding
                )
            }

            else -> {
                throw RuntimeException("VideoAdapter with null host.")
            }
        }
    }

    override fun onBindViewHolder(
        holder: VideoViewHolder,
        position: Int
    ) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

}