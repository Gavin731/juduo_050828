package com.film.television.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.film.television.R
import com.film.television.adapter.PagedVideoAdapter
import com.film.television.databinding.LayoutListBinding
import com.film.television.utils.ApplicationData
import com.film.television.utils.Constants
import com.film.television.viewmodel.PagedVideoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeOtherFragment : BaseFragment<LayoutListBinding>() {
    private var category: String = ""
    private val pagedVideoViewModel: PagedVideoViewModel by activityViewModels()
    private val pagingAdapter: PagedVideoAdapter by lazy {
        PagedVideoAdapter(this, null).apply {
            viewLifecycleOwner.lifecycleScope.launch {
                loadStateFlow.collectLatest { loadStates ->
                    binding.root.isRefreshing = loadStates.refresh is LoadState.Loading
                }
            }
        }
    }
    private var adSdkInitialized = false
    private var isPageDataFilled = false

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutListBinding {
        return LayoutListBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        category = requireArguments().getString(Constants.KEY_CATEGORY)!!
        binding.root.setColorSchemeResources(R.color.theme_color)
        binding.root.setOnRefreshListener {
            pagingAdapter.refresh()
        }
        binding.recyclerView.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        adSdkInitialized = ApplicationData.initStatus.value == true
        if (adSdkInitialized && Constants.GLOBAL_AD_ENABLED && Constants.FEEDS_AD_ENABLED) {
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position % 7 == 6) {
                        3
                    } else {
                        1
                    }
                }
            }
        }
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.adapter = pagingAdapter
    }

    override fun onResume() {
        super.onResume()
        if (!isPageDataFilled) {
            isPageDataFilled = true
            require(category.isNotEmpty())
            viewLifecycleOwner.lifecycleScope.launch {
                pagedVideoViewModel.getPagedVideoFlow(adSdkInitialized, category, null, null, null)
                    .collectLatest { pagingData ->
                        pagingAdapter.submitData(pagingData)
                    }
            }
        }
    }

}