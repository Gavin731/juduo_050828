package com.film.television.fragment

import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.film.television.R
import com.film.television.adapter.VideoAdapter
import com.film.television.databinding.FragmentHotBinding
import com.film.television.databinding.ItemHotBinding
import com.film.television.model.HomepageVideoResp
import com.film.television.utils.AdUtil
import com.film.television.utils.TokenUtil
import com.film.television.utils.UIUtil
import kotlinx.coroutines.launch
import androidx.fragment.app.viewModels
import com.film.television.utils.ApplicationData
import com.film.television.utils.Constants
import com.film.television.utils.DataStoreUtil
import com.film.television.viewmodel.HomeViewModel
import com.film.television.viewmodel.MainViewModel

class HotFragment : BaseFragment<FragmentHotBinding>() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var mRespData: HomepageVideoResp.Data? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHotBinding {
        return FragmentHotBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        binding.root.setColorSchemeResources(R.color.theme_color)
        binding.root.setOnRefreshListener {
            getData()
        }
    }

    override fun initData() {
        binding.root.isRefreshing = true
        getData()
    }

    private fun getData() {
        viewLifecycleOwner.lifecycleScope.launch {
            TokenUtil.getToken()?.let {
                val resp = homeViewModel.queryHomepageVideo(it)
                if (resp.code == 200) {
                    val respData = resp.data
                    if (respData != mRespData) {
                        binding.content.removeAllViews()
                        setContent(respData)
                        mRespData = respData
                    }
                }
            }
            binding.root.isRefreshing = false
        }
    }

    private fun setContent(data: HomepageVideoResp.Data) {
        val dp14: Int by lazy { UIUtil.dp2px(requireContext(), 14f) }
        val contentChildren = mutableListOf<ItemHotBinding>()
        if (data.tv.isNotEmpty()) {
            val tvBinding = ItemHotBinding.inflate(layoutInflater)
            tvBinding.title.text = getString(R.string.hot_tv)
            tvBinding.more.setOnClickListener {
                mainViewModel.setHotCategory("TV")
            }
            tvBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            val tvData = data.tv.takeIf { it.size <= 6 } ?: data.tv.subList(0, 6)
            tvBinding.recyclerView.adapter = VideoAdapter(null, this, null, tvData)
            binding.content.addView(tvBinding.root)
            contentChildren.add(tvBinding)
        }
        if (data.movie.isNotEmpty()) {
            val movieBinding = ItemHotBinding.inflate(layoutInflater)
            movieBinding.title.text = getString(R.string.hot_movie)
            movieBinding.more.setOnClickListener {
                mainViewModel.setHotCategory("MOVIE")
            }
            movieBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            val movieData = data.movie.takeIf { it.size <= 6 } ?: data.movie.subList(0, 6)
            movieBinding.recyclerView.adapter = VideoAdapter(null, this, null, movieData)
            binding.content.addView(movieBinding.root)
            contentChildren.add(movieBinding)
        }
        if (data.variety.isNotEmpty()) {
            val varietyBinding = ItemHotBinding.inflate(layoutInflater)
            varietyBinding.title.text = getString(R.string.hot_variety)
            varietyBinding.more.setOnClickListener {
                mainViewModel.setHotCategory("VARIETY")
            }
            varietyBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            val varietyData = data.variety.takeIf { it.size <= 6 } ?: data.variety.subList(0, 6)
            varietyBinding.recyclerView.adapter = VideoAdapter(null, this, null, varietyData)
            binding.content.addView(varietyBinding.root)
            contentChildren.add(varietyBinding)
        }
        if (data.anime.isNotEmpty()) {
            val animeBinding = ItemHotBinding.inflate(layoutInflater)
            animeBinding.title.text = getString(R.string.hot_anime)
            animeBinding.more.setOnClickListener {
                mainViewModel.setHotCategory("ANIME")
            }
            animeBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            val animeData = data.anime.takeIf { it.size <= 6 } ?: data.anime.subList(0, 6)
            animeBinding.recyclerView.adapter = VideoAdapter(null, this, null, animeData)
            binding.content.addView(animeBinding.root)
            contentChildren.add(animeBinding)
        }
        if (contentChildren.isNotEmpty()) {
            if (ApplicationData.initStatus.value == true && Constants.GLOBAL_AD_ENABLED && Constants.FEEDS_AD_ENABLED) {
                binding.nestedScrollView.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        binding.nestedScrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        for (b in contentChildren) {
                            if (isViewInViewport(b.root, .5f)) {
                                AdUtil.showFeedsAd(
                                    requireActivity(),
                                    b.adContainer,
                                    UIUtil.feedsWidth,
                                    0
                                ) {
                                    b.adContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                                        topMargin = dp14
                                    }
                                }
                            } else {
                                binding.nestedScrollView.viewTreeObserver.addOnScrollChangedListener(
                                    object : ViewTreeObserver.OnScrollChangedListener {
                                        override fun onScrollChanged() {
                                            if (isViewInViewport(b.root, .5f)) {
                                                binding.nestedScrollView.viewTreeObserver.removeOnScrollChangedListener(
                                                    this
                                                )
                                                AdUtil.showFeedsAd(
                                                    requireActivity(),
                                                    b.adContainer,
                                                    UIUtil.feedsWidth,
                                                    0
                                                ) {
                                                    b.adContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                                                        topMargin = dp14
                                                    }
                                                }
                                            }
                                        }
                                    })
                            }
                        }
                    }
                })
            }
            if (contentChildren.size >= 2 && ApplicationData.initStatus.value == true && Constants.GLOBAL_AD_ENABLED && Constants.INTERSTITIAL_AD_ENABLED) {
                lifecycleScope.launch {
                    if (!DataStoreUtil.getHotScrollViewInterstitialAdShown()) {
                        binding.nestedScrollView.viewTreeObserver.addOnScrollChangedListener(
                            object : ViewTreeObserver.OnScrollChangedListener {
                                override fun onScrollChanged() {
                                    if (isViewInViewport(contentChildren[1].root, 1f)) {
                                        binding.nestedScrollView.viewTreeObserver.removeOnScrollChangedListener(
                                            this
                                        )
                                        lifecycleScope.launch {
                                            DataStoreUtil.setHotScrollViewInterstitialAdShown()
                                        }
                                        AdUtil.showInterstitialAd(requireActivity())
                                    }
                                }
                            })
                    }
                }
            }
        }
    }

//    private fun isViewInViewPort(view: View): Boolean {
//        val location = IntArray(2)
//        view.getLocationOnScreen(location)
//        val viewRect = Rect(
//            location[0],
//            location[1],
//            location[0] + view.width,
//            location[1] + view.height
//        )
//        val displayRect = Rect()
//        view.getWindowVisibleDisplayFrame(displayRect)
//        return viewRect.intersect(displayRect)
//    }

    private fun isViewInViewport(view: View): Boolean {
        return view.getGlobalVisibleRect(Rect())
    }

    private fun isViewInViewport(view: View, minVisibleRatio: Float): Boolean {
        val rect = Rect()
        if (!view.getGlobalVisibleRect(rect)) return false
        val visibleArea: Float = rect.width().toFloat() * rect.height()
        val totalArea: Float = view.width.toFloat() * view.height
        val visibleRatio = visibleArea / totalArea
        return visibleRatio >= minVisibleRatio
    }

}