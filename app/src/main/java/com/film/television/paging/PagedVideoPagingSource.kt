package com.film.television.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.film.television.model.PagedVideoQueryBody
import com.film.television.model.VideoAdBean
import com.film.television.model.VideoBean
import com.film.television.repository.ContentRepository
import com.film.television.utils.Constants
import com.film.television.utils.DeviceUtil
import com.film.television.utils.TokenUtil

class PagedVideoPagingSource(
    private val adSdkInitialized: Boolean,
    private val category: String?,
    private val genre: String?,
    private val region: String?,
    private val yearCategory: String?
) : PagingSource<Int, VideoAdBean>() {
    override fun getRefreshKey(state: PagingState<Int, VideoAdBean>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoAdBean> {
        return try {
            val nextPageNumber = params.key ?: 1
            val token = TokenUtil.getToken()
            if (token == null) {
                return LoadResult.Error(RuntimeException("The token in PagedVideoPagingSource load method is null."))
            }
            val body = PagedVideoQueryBody(
                Constants.PAGED_VIDEO_QUERY,
                DeviceUtil.getPackageName(),
                token,
                PagedVideoQueryBody.Params(
                    nextPageNumber,
                    Constants.PAGE_SIZE,
                    category,
                    genre,
                    region,
                    yearCategory
                )
            )
            val resp = ContentRepository.queryPagedVideo(body)
            if (resp.code == 200) {
                val nextKey = resp.data.pageNum + 1
                LoadResult.Page(
                    data = processVideoBeanList(resp.data.records),
                    prevKey = null,
                    nextKey = if (nextKey <= resp.data.totalPages) nextKey else null
                )
            } else {
                LoadResult.Error(RuntimeException("Error code: ${resp.code}, and message: ${resp.message}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun processVideoBeanList(data: List<VideoBean>): List<VideoAdBean> {
        return if (adSdkInitialized && Constants.GLOBAL_AD_ENABLED && Constants.FEEDS_AD_ENABLED) {
            data.foldIndexed(mutableListOf<VideoAdBean>()) { index, acc, t ->
                acc.apply {
                    add(VideoAdBean(t, null))
                    if (index % 6 == 5) {
                        add(VideoAdBean(null, VideoAdBean.AdBean))
                    }
                }
            }
        } else {
            data.map { VideoAdBean(it, null) }
        }
    }
}