package com.film.television.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.film.television.model.VideoAdBean
import com.film.television.paging.PagedVideoPagingSource
import com.film.television.utils.Constants
import kotlinx.coroutines.flow.Flow

class PagedVideoViewModel : ViewModel() {

    fun getPagedVideoFlow(
        adSdkInitialized: Boolean,
        category: String?,
        genre: String?,
        region: String?,
        yearCategory: String?
    ): Flow<PagingData<VideoAdBean>> {
        return Pager(PagingConfig(Constants.PAGE_SIZE, initialLoadSize = Constants.PAGE_SIZE * 2)) {
            PagedVideoPagingSource(adSdkInitialized, category, genre, region, yearCategory)
        }.flow
    }

}