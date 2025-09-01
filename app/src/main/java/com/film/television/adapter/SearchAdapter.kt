package com.film.television.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.film.television.adapter.SearchAdapter.SearchViewHolder
import com.film.television.databinding.ItemSearchBinding
import com.film.television.model.VideoBean
import com.film.television.utils.ApplicationData
import com.film.television.utils.ImageLoader
import com.film.television.utils.RouteUtil

class SearchAdapter(private val activity: FragmentActivity) :
    PagingDataAdapter<VideoBean, SearchViewHolder>(VideoBean) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
        return SearchViewHolder(
            ItemSearchBinding.inflate(LayoutInflater.from(activity), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: SearchViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    inner class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(videoBean: VideoBean) {
            ImageLoader.loadRoundedCornerImage(activity, videoBean.imageUrl, 8f, binding.cover)
            binding.title.text = videoBean.title
            val rating = try {
                videoBean.rating?.toFloat()
            } catch (_: NumberFormatException) {
                null
            }
            if (rating == null || rating == 0f) {
                binding.rating.visibility = View.GONE
            } else {
                binding.rating.visibility = View.VISIBLE
                binding.rating.text = "${rating}分"
            }
            val data = ApplicationData.generalVideoInfoData.value
            if (data == null) {
                binding.cat.visibility = View.GONE
            } else {
                binding.cat.visibility = View.VISIBLE
                val tags = mutableListOf<String>()
                data.yearCategories.get(videoBean.releaseYear)?.asString?.let {
                    tags.add(it)
                }
                data.categories.get(videoBean.category)?.asString?.let {
                    tags.add(it)
                }
                data.genres.get(videoBean.genre)?.asString?.let {
                    tags.add(it)
                }
                binding.cat.text = "类型：${tags.joinToString(" | ")}"
            }
            binding.actors.text =
                "主演：${videoBean.actors.takeIf { !it.isNullOrEmpty() } ?: "未知"}"
            binding.root.setOnClickListener {
                RouteUtil.goToVideoDetailActivity(activity, videoBean)
            }
        }
    }

}