package com.film.television.fragment

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.film.television.R
import com.film.television.adapter.PagedVideoAdapter
import com.film.television.databinding.FragmentCategoryBinding
import com.film.television.model.GeneralVideoInfoResp
import com.film.television.utils.AdUtil
import com.film.television.utils.ApplicationData
import com.film.television.utils.Constants
import com.film.television.utils.UIUtil
import com.film.television.viewmodel.MainViewModel
import com.film.television.viewmodel.PagedVideoViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryFragment : BaseFragment<FragmentCategoryBinding>() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val pagedVideoViewModel: PagedVideoViewModel by activityViewModels()
    private val pagingAdapter: PagedVideoAdapter by lazy {
        PagedVideoAdapter(this, null).apply {
            viewLifecycleOwner.lifecycleScope.launch {
                loadStateFlow.collectLatest { loadStates ->
                    binding.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
                }
            }
        }
    }
    private var pagedVideoJob: Job? = null
    private var canShowInterstitialAd = true
    private var adSdkInitialized = false
    private var navBarInitialized = false
    private var pageInitialized = false

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCategoryBinding {
        return FragmentCategoryBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin =
                    insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars()).top
            }
            insets
        }
        binding.swipeRefresh.setColorSchemeResources(R.color.theme_color)
        binding.swipeRefresh.setOnRefreshListener {
            pagingAdapter.refresh()
        }
        binding.recyclerView.setHasFixedSize(true)
        adSdkInitialized = ApplicationData.initStatus.value == true
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
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

    override fun initObservation() {
        ApplicationData.generalVideoInfoData.observe(
            viewLifecycleOwner,
            object : Observer<GeneralVideoInfoResp.Data> {
                override fun onChanged(data: GeneralVideoInfoResp.Data) {
                    ApplicationData.generalVideoInfoData.removeObserver(this)
                    for (category in data.categories.entrySet()) {
                        binding.categories.addView(
                            getOptionView(
                                category.value.asString,
                                category.key
                            )
                        )
                    }
                    binding.categories.getChildAt(0).isSelected = true
                    binding.orders.addView(getOptionView("综合", null))
                    for (order in data.orders.entrySet()) {
                        binding.orders.addView(getOptionView(order.value.asString, order.key))
                    }
                    binding.orders.getChildAt(0).isSelected = true
                    binding.genres.addView(getOptionView("全部", null))
                    for (genre in data.genres.entrySet()) {
                        binding.genres.addView(getOptionView(genre.value.asString, genre.key))
                    }
                    binding.genres.getChildAt(0).isSelected = true
                    binding.regions.addView(getOptionView("全部", null))
                    for (region in data.regions.entrySet()) {
                        binding.regions.addView(
                            getOptionView(
                                region.value.asString,
                                region.key
                            )
                        )
                    }
                    binding.regions.getChildAt(0).isSelected = true
                    binding.yearCategories.addView(getOptionView("全部", null))
                    for (year in data.yearCategories.entrySet()) {
                        binding.yearCategories.addView(
                            getOptionView(
                                year.value.asString,
                                year.key
                            )
                        )
                    }
                    binding.yearCategories.getChildAt(0).isSelected = true
                    navBarInitialized = true
                }
            })
        mainViewModel.hotCategory.observe(viewLifecycleOwner) { category ->
            if (navBarInitialized) {
                for (categoryView in binding.categories.children) {
                    categoryView.isSelected = categoryView.tag?.toString() == category
                }
                binding.orders.getChildAt(0).isSelected = true
                binding.genres.getChildAt(0).isSelected = true
                binding.regions.getChildAt(0).isSelected = true
                binding.yearCategories.getChildAt(0).isSelected = true
                onOptionsChanged()
            }
        }
    }

    private fun getOptionView(option: String, tag: String?): View {
        val textView = TextView(requireContext())
        textView.textSize = 14f
        textView.setTextColor(
            ResourcesCompat.getColorStateList(
                resources,
                R.color.switchable_text_color,
                null
            )
        )
        textView.background = ResourcesCompat.getDrawable(resources, R.drawable.bg_options, null)
        textView.minWidth = UIUtil.dp2px(requireContext(), 57f)
        textView.minHeight = UIUtil.dp2px(requireContext(), 28f)
        val horizontalPadding = UIUtil.dp2px(requireContext(), 9f)
        val verticalPadding = UIUtil.dp2px(requireContext(), 6f)
        textView.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
        textView.gravity = Gravity.CENTER
        textView.text = option
        textView.tag = tag
        textView.layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            val marginHorizontal = UIUtil.dp2px(requireContext(), 7f)
            leftMargin = marginHorizontal
            rightMargin = marginHorizontal
        }
        textView.setOnClickListener {
            if (!textView.isSelected) {
                (textView.parent as ViewGroup).children.first { it.isSelected }.isSelected = false
                textView.isSelected = true
                val horizontalScrollView = textView.parent.parent as HorizontalScrollView
                val x = textView.left + textView.width / 2 - horizontalScrollView.width / 2
                horizontalScrollView.smoothScrollTo(x, 0)
                onOptionsChanged()
            }
        }
        return textView
    }

    private fun onOptionsChanged() {
        val category =
            (binding.categories.children.first { it.isSelected } as TextView).tag.toString()
        val genre = (binding.genres.children.first { it.isSelected } as TextView).tag?.toString()
        val region = (binding.regions.children.first { it.isSelected } as TextView).tag?.toString()
        val yearCategory =
            (binding.yearCategories.children.first { it.isSelected } as TextView).tag?.toString()
        pagedVideoJob?.cancel()
        pagedVideoJob = viewLifecycleOwner.lifecycleScope.launch {
            pagedVideoViewModel.getPagedVideoFlow(
                adSdkInitialized,
                category,
                genre,
                region,
                yearCategory
            )
                .collectLatest { pagingData ->
                    pagingAdapter.submitData(pagingData)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        if (canShowInterstitialAd && ApplicationData.initStatus.value == true && Constants.GLOBAL_AD_ENABLED && Constants.INTERSTITIAL_AD_ENABLED) {
            canShowInterstitialAd = false
            AdUtil.showInterstitialAd(requireActivity())
        }
        if (navBarInitialized && !pageInitialized) {
            pageInitialized = true
            onOptionsChanged()
        }
    }
}