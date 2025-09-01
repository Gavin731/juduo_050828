package com.film.television.viewmodel

import androidx.lifecycle.ViewModel
import com.film.television.model.PagedVideoQueryBody
import com.film.television.model.VideoBean
import com.film.television.repository.ContentRepository
import com.film.television.utils.Constants
import com.film.television.utils.DeviceUtil

class VideoDetailViewModel : ViewModel() {

    suspend fun getRecommendation(token: String, category: String, genre: String?): List<VideoBean> {
        val resp = ContentRepository.queryPagedVideo(
            PagedVideoQueryBody(
                Constants.PAGED_VIDEO_QUERY,
                DeviceUtil.getPackageName(),
                token,
                PagedVideoQueryBody.Params(
                    1,
                    Constants.PAGE_SIZE,
                    category,
                    genre,
                    null,
                    null
                )
            )
        )
        return if (resp.code == 200) {
            resp.data.records
        } else {
            emptyList()
        }
    }

}