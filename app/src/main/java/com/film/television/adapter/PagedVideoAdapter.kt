package com.film.television.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.film.television.databinding.ItemAdBinding
import com.film.television.databinding.ItemVideoBinding
import com.film.television.model.VideoAdBean
import com.film.television.utils.UIUtil

class PagedVideoAdapter(private val fragment: Fragment?, private val activity: FragmentActivity?) :
    PagingDataAdapter<VideoAdBean, RecyclerView.ViewHolder>(VideoAdBean) {

    override fun getItemViewType(position: Int): Int {
        val item = peek(position) ?: return super.getItemViewType(position)
        return if (item.videoBean != null) {
            return VIEW
        } else {
            return AD
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW -> {
                when {
                    fragment != null -> {
                        VideoViewHolder(
                            null,
                            fragment,
                            null,
                            ItemVideoBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        )
                    }

                    activity != null -> {
                        VideoViewHolder(
                            null,
                            null,
                            activity,
                            ItemVideoBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            )
                        )
                    }

                    else -> {
                        throw RuntimeException("PagedVideoAdapter's unknown host.")
                    }
                }
            }

            AD -> {
                val act = when {
                    fragment != null -> fragment.requireActivity()
                    activity != null -> activity
                    else -> throw RuntimeException("PagedVideoAdapter's unknown host.")
                }
                AdViewHolder(
                    act,
                    ItemAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }

            else -> {
                throw RuntimeException("PagedVideoAdapter's unknown view type.")
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        when (holder) {
            is VideoViewHolder -> holder.bind(item.videoBean!!)
            is AdViewHolder -> holder.bind(UIUtil.feedsWidth)
        }
    }

    companion object {
        private const val VIEW = 1
        private const val AD = 2
    }
}