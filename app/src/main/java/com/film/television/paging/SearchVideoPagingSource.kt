package com.film.television.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.film.television.model.VideoBean
import com.film.television.model.VideoSearchBody
import com.film.television.repository.ContentRepository
import com.film.television.utils.Constants
import com.film.television.utils.DeviceUtil
import com.film.television.utils.TokenUtil

class SearchVideoPagingSource(private val title: String) : PagingSource<Int, VideoBean>() {
    override fun getRefreshKey(state: PagingState<Int, VideoBean>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoBean> {
        return try {
            val nextPageNumber = params.key ?: 1
            val token = TokenUtil.getToken()
            if (token == null) {
                return LoadResult.Error(RuntimeException("The token in SearchVideoPagingSource load method is null."))
            }
            val body = VideoSearchBody(
                Constants.VIDEO_SEARCH,
                DeviceUtil.getPackageName(),
                token,
                VideoSearchBody.Params(
                    title,
                    nextPageNumber,
                    Constants.PAGE_SIZE
                )
            )
            val resp = ContentRepository.searchVideo(body)
            if (resp.code == 200) {
                val nextKey = resp.data.pageNum + 1
                LoadResult.Page(
                    data = resp.data.records,
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
}