package com.film.television.fragment

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.film.television.activity.AboutUsActivity
import com.film.television.activity.TeenModeActivity
import com.film.television.databinding.FragmentMineBinding
import com.film.television.utils.AdUtil
import com.film.television.utils.ApplicationData
import com.film.television.utils.CacheUtil
import com.film.television.utils.Constants
import com.film.television.utils.Formatter
import com.film.television.utils.UIUtil
import kotlinx.coroutines.launch

class MineFragment : BaseFragment<FragmentMineBinding>() {
    private var canShowInterstitialAd = true
    private var isFeedsAdLoaded = false

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMineBinding {
        return FragmentMineBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        binding.teenMode.setOnClickListener {
            startActivity(Intent(requireContext(), TeenModeActivity::class.java))
        }
        binding.aboutUs.setOnClickListener {
            startActivity(Intent(requireContext(), AboutUsActivity::class.java))
        }
        binding.supportApp.setOnClickListener {
            if (ApplicationData.initStatus.value == true && Constants.GLOBAL_AD_ENABLED && Constants.VIDEO_AD_ENABLED) {
                AdUtil.showRewardVideoAd(requireActivity())
            }
        }
        binding.clearCache.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                CacheUtil.clearCache(requireContext())
                setCacheSize()
            }
        }
    }

    private suspend fun setCacheSize() {
        val cacheSizeBytes = CacheUtil.getCacheSize(requireContext())
        binding.cacheSize.text = Formatter.formatFileSize(requireContext(), cacheSizeBytes)
    }

    override fun onResume() {
        super.onResume()
        if (canShowInterstitialAd && ApplicationData.initStatus.value == true && Constants.GLOBAL_AD_ENABLED && Constants.INTERSTITIAL_AD_ENABLED) {
            canShowInterstitialAd = false
            AdUtil.showInterstitialAd(requireActivity())
        }
        viewLifecycleOwner.lifecycleScope.launch {
            setCacheSize()
        }
        if (!isFeedsAdLoaded) {
            isFeedsAdLoaded = true
            if (ApplicationData.initStatus.value == true && Constants.GLOBAL_AD_ENABLED && Constants.FEEDS_AD_ENABLED) {
                AdUtil.showFeedsAd(requireActivity(), binding.adContainer, UIUtil.feedsWidth, 0)
            }
        }
    }
}